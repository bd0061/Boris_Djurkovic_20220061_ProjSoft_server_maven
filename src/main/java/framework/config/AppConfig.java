/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package framework.config;

import framework.DbEngine;

/**
 *
 * @author Djurkovic
 */
public class AppConfig {

    public static final int DEFAULT_DB_PORT_NUMBER = 3306;
    public static final String DEFAULT_DB_HOST = "localhost";
    public static final String DEFAULT_DB_USERNAME = "root";
    public static final String DEFAULT_DB_PASSWORD = "";
    public static final DbEngine DEFAULT_DB_ENGINE = DbEngine.MYSQL;
    public static final int DEFAULT_LISTENING = 9999;
    public static final boolean DEFAULT_ISDB = true;
    

    public String imeBaze;
    public String imeTestBaze;

    public int dbPortNumber = AppConfig.DEFAULT_DB_PORT_NUMBER;
    public String dbDomain = AppConfig.DEFAULT_DB_HOST;
    public String dbUsername = AppConfig.DEFAULT_DB_USERNAME;
    public String dbPassword = AppConfig.DEFAULT_DB_PASSWORD;
    public DbEngine dbEngine = AppConfig.DEFAULT_DB_ENGINE;
    public int listeningPort = AppConfig.DEFAULT_LISTENING;
    public boolean isDb = AppConfig.DEFAULT_ISDB;

    public AppConfig(String ib, String itb) {
        this.imeBaze = ib;
        this.imeTestBaze = itb;
    }

    private AppConfig(String ib, String itb, int dbp, String dbd, String u, String p, DbEngine e, int lp, boolean isDb) {
        this.imeBaze = ib;
        this.imeTestBaze = itb;
        this.dbPortNumber = dbp;
        this.dbDomain = dbd;
        this.dbUsername = u;
        this.dbPassword = p;
        this.dbEngine = e;
        this.listeningPort = lp;
        this.isDb = isDb;
    }

    public static class Builder {

        public int dbPortNumber = AppConfig.DEFAULT_DB_PORT_NUMBER;
        public String dbHost = AppConfig.DEFAULT_DB_HOST;
        public String dbUsername = AppConfig.DEFAULT_DB_USERNAME;
        public String dbPassword = AppConfig.DEFAULT_DB_PASSWORD;
        public DbEngine dbEngine = AppConfig.DEFAULT_DB_ENGINE;
        public int listeningPort = AppConfig.DEFAULT_LISTENING;
        public boolean isDb = AppConfig.DEFAULT_ISDB;

        public String imeBaze;
        public String imeTestBaze;

        public AppConfig build() {
            return new AppConfig(this.imeBaze, this.imeTestBaze, this.dbPortNumber, this.dbHost, this.dbUsername, this.dbPassword, this.dbEngine, this.listeningPort, this.isDb);
        }

        public Builder addImeBaze(String ib) {
            this.imeBaze = ib;
            return this;
        }

        public Builder addImeTestBaze(String itb) {
            this.imeTestBaze = itb;
            return this;
        }

        public Builder addDbHost(String d) {
            this.dbHost = d;
            return this;
        }

        public Builder addDbUsername(String u) {
            this.dbUsername = u;
            return this;
        }

        public Builder addDbPassword(String p) {
            this.dbPassword = p;
            return this;
        }

        public Builder addDbEngine(DbEngine e) {
            this.dbEngine = e;
            return this;
        }

        public Builder addListeningPort(int p) {
            this.listeningPort = p;
            return this;
        }

        public Builder addDbPortNumber(int port) {
            this.dbPortNumber = port;
            return this;
        }

        public Builder usingDatabase(boolean isDb) {
            this.isDb = isDb;
            return this;
        }
    }
}
