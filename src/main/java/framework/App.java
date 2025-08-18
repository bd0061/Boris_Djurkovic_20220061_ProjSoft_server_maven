/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package framework;

import framework.config.AppConfig;
import framework.injector.ControllerManager;
import framework.injector.DIContainer;
import framework.orm.ConnectionPool;
import framework.orm.Entitet;
import framework.orm.EntityCache;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.reflections.Reflections;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;

/**
 *
 * @author Djurkovic
 */
public class App {

    private AppConfig config;
    private final DIContainer container;
    public void setAppConfig(AppConfig config) {
        this.config = config;
    }

    public App() {
        this.container = new DIContainer();
    }
    public DIContainer getContainer() {
        return container;
    }



    public App(AppConfig config) {
        this.config = config;
        this.container = new DIContainer();
    }

    private void overiKesirajModel(Reflections scanner) {
        Set<Class<? extends Entitet>> entiteti = scanner.getSubTypesOf(Entitet.class);
        for (var e : entiteti) {
            EntityCache.getMetadata(e);
        }

    }

    public void run(Class<?> clazz) {
        if(clazz == null) {
            throw new RuntimeException("Nije specifiran root paket za pretragu");
        }
        if(config == null) {
            throw new RuntimeException("Nije specifirana konfiguracija za pokretanje aplikacije.");
        }
        String imeRootPaketa = clazz.getPackage().getName();
        Reflections scanner = new Reflections(imeRootPaketa);


        SimpleLogger.log(LogLevel.LOG_INFO, "Overavam strukturu modela i kesiram metapodatke za entitete...");
        overiKesirajModel(scanner);
        SimpleLogger.log(LogLevel.LOG_INFO, "Kesiranje i overa entiteta uspesna.\n");

        SimpleLogger.log(LogLevel.LOG_INFO, "Overavam strukturu kontrolera i servisa i mapiram zahteve ka kontrolerima");
        ControllerManager.overiRutirajKontrolere(container,scanner,config);
        SimpleLogger.log(LogLevel.LOG_INFO, "Uspesno overena struktura kontrolera i mapirani zahtevi ka kontrolerima\n");

        if(config.isDb) {
            SimpleLogger.log(LogLevel.LOG_INFO, "Inicijalizujem database connection pool...");
            ConnectionPool.initialize(config);
            SimpleLogger.log(LogLevel.LOG_INFO, "Uspesno inicijalizovan connection pool");
        }
        else {
            SimpleLogger.log(LogLevel.LOG_WARN, "In memory sesija, ne pokrecem connection pool.");
        }


        SimpleLogger.log(LogLevel.LOG_INFO, "Pokrecem server, priprema thread poola...");
        ExecutorService executor = Executors.newCachedThreadPool();


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println();
            SimpleLogger.log(LogLevel.LOG_INFO, "Kraj rada, proces gasenja je u toku...");
            executor.shutdown();
            SimpleLogger.log(LogLevel.LOG_INFO, "Uspesno gasenje thread poola.");
            ConnectionPool.shutdown();
            SimpleLogger.log(LogLevel.LOG_INFO, "Server je ugasen.");
        }));


        try (ServerSocket ss = new ServerSocket(config.listeningPort)) {
            SimpleLogger.log(LogLevel.LOG_INFO, "Server je spreman za konekcije. Pokrecem osluskivanje mreze na portu " + config.listeningPort + "...");
            while (true) {
                Socket clientSocket = ss.accept();
                SimpleLogger.log(LogLevel.LOG_INFO, "Opsluzujem novog klijenta: " + "[" + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + "]");
                executor.submit(new RequestDispatcher(clientSocket,config,container));
            }
        } catch (IOException e) {
            SimpleLogger.log(LogLevel.LOG_FATAL, "Doslo je do ulazno-izlazne greske: " + e);
        }
    }

}
