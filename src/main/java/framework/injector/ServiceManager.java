package framework.injector;

import framework.config.AppConfig;
import framework.model.MarkerRepository;
import framework.orm.EntityManager;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

public class ServiceManager {

    public static Object vratiOdgovarajuciServis(Class<?> s,AppConfig config,DIContainer container) throws Exception {
        Constructor<?> cs = s.getDeclaredConstructors()[0];
        Parameter[] ps = cs.getParameters();
        Object[] argumenti = new Object[ps.length];
        int i = 0;
        SimpleLogger.log(LogLevel.LOG_INFO, "Kreiram instancu servisa " + s.getSimpleName() + " i ubacujem potrebne repozitorijume");
        for(var p : ps) {
            Type t = p.getParameterizedType();
            SimpleLogger.log(LogLevel.LOG_INFO, "Potreban repozitorijum " + t);
            boolean visited = false;
            //dependency injection odgovarajuce implementacije repozitorijuma
            Object repoInstance = container.resolve(t);
            for(var f : repoInstance.getClass().getDeclaredFields()) {
                if(f.getType() == EntityManager.class) {
                    visited = true;
                    f.setAccessible(true);
                    f.set(repoInstance,new EntityManager(config));
                    SimpleLogger.log(LogLevel.LOG_INFO, s.getSimpleName() + ": Repozitorijumu " + repoInstance.getClass().getSimpleName() + " potreban EntityManager, uspesna injekcija");
                    break;
                }
            }
            if(!visited) {
                SimpleLogger.log(LogLevel.LOG_INFO, s.getSimpleName() + ": Repozitorijumu " + repoInstance.getClass().getSimpleName() + " nije potreban EntityManager");
            }
            argumenti[i++] = repoInstance;
            SimpleLogger.log(LogLevel.LOG_INFO, "Servisu " + s.getSimpleName() + " uspesno injectovan repozitorijum " + repoInstance.getClass().getSimpleName());

        }
        SimpleLogger.log(LogLevel.LOG_INFO, "Uspesno kreirana instanca servisa " + s.getSimpleName() + " i ubaceni potrebni repozitorijumi");
        return cs.newInstance(argumenti);
    }

    public void overiServise(Reflections scanner) throws Exception {
        Set<Class<? extends OpstiServis>> servisi = scanner.getSubTypesOf(OpstiServis.class);
        for (var s : servisi) {
            Field[] fs = s.getDeclaredFields();
            if(fs.length == 0) {
                throw new RuntimeException(s.getName() + ": Servis klasa mora da ima jedan ili vise repozitorijum polja (0 polja)");
            }
            int brojRepozitorijuma = 0;
            for(var f : fs) {
                if(MarkerRepository.class.isAssignableFrom(f.getType())) {
                    brojRepozitorijuma++;
                }
            }
            Constructor<?>[] cs = s.getDeclaredConstructors();
            if(cs.length != 1) {
                throw new RuntimeException(s.getName() + ": Servis klasa mora da ima tacno jedan konstruktor za injekciju repozitorijuma");
            }
            Parameter[] ps = cs[0].getParameters();
            if(brojRepozitorijuma != ps.length) {
                throw new RuntimeException(s.getName() + ": Broj parametara u konstruktoru i broj polja se ne poklapa");
            }
            for(var p : ps) {
                if(!MarkerRepository.class.isAssignableFrom(p.getType())) {
                    throw new RuntimeException(s.getSimpleName() + ": Servis klasa mora da ima konstruktor sa repozitorijum parametrima za injekciju (parametar koje nije repozitorijum)");
                }
            }
        }
    }
}
