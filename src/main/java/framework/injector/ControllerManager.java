/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package framework.injector;

import framework.config.AppConfig;
import framework.injector.anotacije.Controller;
import framework.injector.anotacije.RequestHandler;
import framework.model.network.NetworkRequest;
import framework.model.network.NetworkResponse;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

/**
 *
 * @author Djurkovic
 */
public class ControllerManager {
    
    public static void isprazniRute() {
       tabelaRutiranja.clear();
       SimpleLogger.log(LogLevel.LOG_INFO, "Uspesno ispraznjena tabela rutiranja");
    }

    private static final Map<String, MethodInvoker> tabelaRutiranja = new HashMap<>();

    public static Object vratiOdgovarajuciKontroler(AppConfig config, DIContainer container, Class<?> k) throws Exception {
        SimpleLogger.log(LogLevel.LOG_INFO, "Kreiram instancu kontrolera tipa " + k.getSimpleName() + " i ubacujem mu potrebne servise");
        Field servisPolje = k.getDeclaredFields()[0];
        Object service = ServiceManager.vratiOdgovarajuciServis(servisPolje.getType(),config,container);
        Constructor<?> konst = k.getDeclaredConstructors()[0];

        SimpleLogger.log(LogLevel.LOG_INFO, "Uspesno kreirana instanca kontrolera tipa " + k.getSimpleName() + " i ubaceni potrebni servisi");
        return konst.newInstance(service);
    }


    public static void overiRutirajKontrolere(DIContainer container, Reflections scanner, AppConfig config) {
        Set<Class<?>> kontroleri = scanner.getTypesAnnotatedWith(Controller.class);
        ServiceManager sm = new ServiceManager();

        try {
            sm.overiServise(scanner);
            for (var k : kontroleri) {
                Field[] fields = k.getDeclaredFields();
                if(fields.length != 1 || !OpstiServis.class.isAssignableFrom(fields[0].getType())) {
                    throw new RuntimeException(k.getName() + ": Kontroleri moraju sadrzati samo jedno polje: odgovarajuc servis koji ce framework injectovati na pocetku");
                }
                Field servisPolje = fields[0];
                String prviDeo = k.getAnnotation(Controller.class).mapping();

                Constructor<?>[] konstruktori = k.getDeclaredConstructors();
                if(konstruktori.length != 1) {
                    throw new RuntimeException(k.getName() + ": Kontroleri moraju sadrzati samo jedan konstruktor za injekciju servisa");
                }
                Constructor<?> konst = konstruktori[0];
                Parameter[] parametri = konst.getParameters();
                if(parametri.length != 1 || !OpstiServis.class.isAssignableFrom(parametri[0].getType())) {
                    throw new RuntimeException(k.getName() + ": Kontrolerov konstruktor mora da sadrzi samo adekvatno anotiran servis");
                }
                if(parametri[0].getType() != servisPolje.getType()) {
                    throw new RuntimeException(k.getName() + ": Tipovi polja i konstruktora se ne pokalapju");
                }

                for(Method m : k.getDeclaredMethods()) {
                    if(m.isAnnotationPresent(RequestHandler.class)) {
                        String rt = m.getAnnotation(RequestHandler.class).requestType();
                        String imeMetode = prviDeo + rt;
                        if(tabelaRutiranja.containsKey(imeMetode)) {
                            throw new RuntimeException("Pokusaj redefinisanja rute zahteva " + imeMetode + ", vec postoji u " + tabelaRutiranja.get(imeMetode).controller.getName() + "." + tabelaRutiranja.get(imeMetode).method.getName());
                        }
                        SimpleLogger.log(LogLevel.LOG_INFO,"Uspesno rutiran zahtev " + imeMetode + " ka metodi " + m.getName() + " kontrolera " + k.getSimpleName() );
                        tabelaRutiranja.put(imeMetode,new MethodInvoker(k,m));
                    }
                }
                System.out.println();
            }
        }
        catch(Exception e) {
            throw new RuntimeException("Doslo je do grekse pri incijalizaciji kontrolera: " + e);
        }
       
        
    }
    
    
    public static NetworkResponse dispatch(NetworkRequest zahtev, AppConfig config, DIContainer container) {
        SimpleLogger.log(LogLevel.LOG_INFO, "Odredjujem korektan kontroler i metodu za obradu pristiglog zahteva...");
        MethodInvoker mi = tabelaRutiranja.get(zahtev.requestType);
        if(mi == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Odgovarajuci kontroler i metodu za obradu ne postoje za pristigli zahtev " + zahtev.requestType + ", abortiram zahtev...");
            return NetworkResponse.Neuspeh("Nije prepoznat tip zahteva: " + zahtev.requestType);
        }
        SimpleLogger.log(LogLevel.LOG_INFO, "Pronadjena je korektna metoda " + mi.method.getName() + " kontrolera tipa " + mi.controller.getSimpleName() + " u tabeli rutiranja");
        SimpleLogger.log(LogLevel.LOG_INFO, "Instanciram korektan kontroler za obradu pristiglog zahteva...");
        mi.method.setAccessible(true);
        Object kontroler;
        try {
            kontroler = ControllerManager.vratiOdgovarajuciKontroler(config,container,mi.controller);
        }
        catch(Exception e) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri instanciranju kontrolera: " + e);
            return NetworkResponse.Neuspeh("Doslo je do nepoznate greske pri obradi zahteva.");
        }

        try {
            //kljucna linija: na osnovu kljuca (vrsta sistemske operacije), pozivamo funkciju koja se vezuje za nju
            return (NetworkResponse) mi.method.invoke(kontroler, zahtev);
        } catch (Exception ex) {
            Throwable cause = ex.getCause();
            cause.printStackTrace();
            throw new RuntimeException("Doslo je do grekse pri dispecovanju kontrolera: " + ex);
        }
    }

    private static class MethodInvoker {

        final Class<?> controller;
        final Method method;

        MethodInvoker(Class<?> controller, Method method) {
            this.controller = controller;
            this.method = method;
        }
    }
}
