/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package framework.orm;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import framework.config.AppConfig;
import framework.DbEngine;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Djurkovic
 */
public final class ConnectionPool {

    private static HikariDataSource dataSource;
    private static boolean initialized = false;

    public static void initialize(AppConfig appconf) {
        initialize(appconf,false);
    }
    public static void initialize(AppConfig appconf, boolean isTest) {
        if (!initialized) {
            String ime = isTest ? appconf.imeTestBaze : appconf.imeBaze;
            if (ime == null) {
                throw new RuntimeException("Ime baze podataka nije specifirano");
            }
            HikariConfig config = new HikariConfig();

            config.setJdbcUrl(switch (appconf.dbEngine) {
                case DbEngine.MYSQL ->
                    "jdbc:mysql://" + appconf.dbDomain + ":" + appconf.dbPortNumber + "/" + ime;
                case DbEngine.POSTGRES ->
                    "jdbc:postgresql://" + appconf.dbDomain + ":" + appconf.dbPortNumber + "/" + ime;
                case DbEngine.SQL_SERVER ->
                    "jdbc:sqlserver://" + appconf.dbDomain + ":" + appconf.dbPortNumber + ";databaseName=" + ime;
            });
            config.setUsername(appconf.dbUsername);
            config.setPassword(appconf.dbPassword);
            config.setAutoCommit(true);

            config.setMaximumPoolSize(20);
            config.setMinimumIdle(2);

            config.setConnectionTimeout(30000);
            config.setMaxLifetime(1800000);

            dataSource = new HikariDataSource(config);

            initialized = true;
        }
    }
    public static void shutdown() {
        if(!initialized || dataSource == null) {
            SimpleLogger.log(LogLevel.LOG_WARN, "Pokusaj gasenja neinicijalizovanog connection poola");
            return;
        }
        dataSource.close();
        dataSource = null;
        SimpleLogger.log(LogLevel.LOG_INFO, "Uspesno gasenje connection poola");
        initialized = false;
    }


    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            throw new RuntimeException("Pokusaj uzimanja konekcije pre inicijalizacije thread poola");
        }
        return dataSource.getConnection();
    }
}
