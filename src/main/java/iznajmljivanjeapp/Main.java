/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iznajmljivanjeapp;

import framework.App;
import framework.config.AppConfig;
import framework.DbEngine;
import framework.injector.TypeToken;
import iznajmljivanjeapp.domain.*;
import iznajmljivanjeapp.repositories.Repository;
import iznajmljivanjeapp.repositories.dbrepositories.*;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;

/**
 *
 * @author Djurkovic
 */
public class Main {

    public static void main(String[] args) {

        SimpleLogger.LOG_SENSITIVITY = LogLevel.LOG_INFO;

        App app = new App();

        app.setAppConfig(new AppConfig.Builder()
                .addDbHost("localhost")
                .addDbEngine(DbEngine.MYSQL)
                .addDbPortNumber(3306)
                .addImeBaze("projektovanjesoftvera_seminarski")
                .addImeTestBaze("projektovanjesoftvera_seminarski_test")
                .addListeningPort(9999)
                .addDbUsername("root")
                .addDbPassword("")
                .build()
        );

        var container = app.getContainer();
        container.register(new TypeToken<Repository<Dozvola>>() {}.getType(), DozvolaDbRepository::new);
        container.register(new TypeToken<Repository<Vozac>>() {}.getType(), VozacDbRepository::new);
        container.register(new TypeToken<Repository<Vozilo>>() {}.getType(), VoziloDbRepository::new);
        container.register(new TypeToken<Repository<Zaposleni>>() {}.getType(), ZaposleniDbRepository::new);
        container.register(new TypeToken<Repository<Smena>>() {}.getType(), SmenaDbRepository::new);
        container.register(new TypeToken<Repository<StavkaIznajmljivanja>>() {}.getType(), StavkaIznajmljivanjaDbRepository::new);
        container.register(new TypeToken<Repository<TerminDezurstva>>() {}.getType(), TerminDezurstvaDbRepository::new);
        container.register(new TypeToken<Repository<Iznajmljivanje>>() {}.getType(), IznajmljivanjeDbRepository::new);

        app.run(Main.class);
    }
}
