package framework.injector;

import framework.model.MarkerRepository;
import framework.orm.ConnectionPool;
import framework.orm.EntityManager;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;

import java.lang.reflect.Field;
import java.sql.Connection;

public abstract class OpstiServis {

    //ukoliko framework detektuje db repozitorijume, ove funkcije ce obezbediti transaction management
    //u suprotnom su nop
    protected boolean transakcijaUToku = false;

    public final void pocniTransakciju() throws Exception {
        if(transakcijaUToku) {
            throw new Exception("transakcija je vec u toku");
        }

        Connection connection;
        try {
            connection = ConnectionPool.getConnection();
        }
        catch(RuntimeException e) {
            SimpleLogger.log(LogLevel.LOG_WARN, "Servis koristi in memory repozitorijume za koje transakciona obrada nije implementirana, ignorisem poziv na transakciju...");
            return;
        }
        transakcijaUToku = true;
        connection.setAutoCommit(false);
        SimpleLogger.log(LogLevel.LOG_INFO, "Pocinjem transakciju u " + getClass().getSimpleName() + ", trazim repozitorijume da overridujem konekcije");
        for(Field f : getClass().getDeclaredFields()) {
            if(MarkerRepository.class.isAssignableFrom(f.getType())) {
                f.setAccessible(true);
                MarkerRepository repo = (MarkerRepository) f.get(this);
                SimpleLogger.log(LogLevel.LOG_INFO, "Pronadjen repozitorijum " + repo.getClass().getSimpleName());
                boolean foundEm = false;
                for(Field entityManager : repo.getClass().getDeclaredFields()) {
                    if(entityManager.getType() == EntityManager.class) {
                        foundEm = true;
                        SimpleLogger.log(LogLevel.LOG_INFO, "Pronadjeno entitymanager polje za " + repo.getClass().getSimpleName());
                        entityManager.setAccessible(true);
                        Object em = entityManager.get(repo);
                        Field con = EntityManager.class.getDeclaredField("con");
                        con.setAccessible(true);
                        con.set(em, connection);
                        SimpleLogger.log(LogLevel.LOG_INFO, "Uspesno overridovana konekcija entitymanagera za " + repo.getClass().getSimpleName() + "\n");
                        break;
                    }
                }
                if(!foundEm) {
                    SimpleLogger.log(LogLevel.LOG_INFO, "Nije pronadjeno entitymanager polje za " + repo.getClass().getSimpleName() + "\n");
                }
            }
        }
    }

    public final void commitTransakcija() throws Exception {
        if(!transakcijaUToku) {
            SimpleLogger.log(LogLevel.LOG_WARN, "Pokusaj komitovanja nepostojece transakcije (in memory repozitorijum?), ignorisem...");
            return;
        }
        transakcijaUToku = false;
        SimpleLogger.log(LogLevel.LOG_INFO, "Komitujem transakciju u " + getClass().getSimpleName() + ", trazim repozitorijume da komitujem konekcije");
        boolean changedConnection = false;
        for(Field f : getClass().getDeclaredFields()) {
            if(MarkerRepository.class.isAssignableFrom(f.getType())) {
                f.setAccessible(true);
                MarkerRepository repo = (MarkerRepository) f.get(this);
                SimpleLogger.log(LogLevel.LOG_INFO, "Pronadjen repozitorijum " + repo.getClass().getSimpleName());
                boolean foundEm = false;
                for(Field entityManager : repo.getClass().getDeclaredFields()) {
                    if(entityManager.getType() == EntityManager.class) {
                        foundEm = true;
                        SimpleLogger.log(LogLevel.LOG_INFO, "Pronadjeno entitymanager polje za " + repo.getClass().getSimpleName());
                        entityManager.setAccessible(true);
                        Object em = entityManager.get(repo);
                        Field con = EntityManager.class.getDeclaredField("con");
                        con.setAccessible(true);
                        Connection c = (Connection) con.get(em);
                        if(!changedConnection) {
                            SimpleLogger.log(LogLevel.LOG_INFO, "Komitujem transakciju kroz dobijenu referencu konekcije (svi repozitorijumi je dele)");
                            if(c != null && !c.isClosed()) {
                                if(!c.getAutoCommit()) {
                                    c.commit();
                                    c.setAutoCommit(true);
                                    c.close();
                                }
                                changedConnection = true;
                                SimpleLogger.log(LogLevel.LOG_INFO, "Uspesno izvrsen komit transakcije za servis " + getClass().getSimpleName());
                            }
                            else {
                                throw new Exception("Greska pri komitovanju transakcije: neocekivano stanje konekcije");
                            }
                        }
                        con.set(em,null);

                        SimpleLogger.log(LogLevel.LOG_INFO, "Konekcija u entity manager referenci repozitorijuma " + repo.getClass().getSimpleName() + " uspesno vracen na null \n");
                        break;

                    }
                }
                if(!foundEm) {
                    SimpleLogger.log(LogLevel.LOG_INFO, "Nije pronadjeno entitymanager polje za " + repo.getClass().getSimpleName() + "\n");
                }
            }
        }
    }

    public final void rollbackTransakcija() throws Exception {
        if(!transakcijaUToku) {
            SimpleLogger.log(LogLevel.LOG_WARN, "Pokusaj rollbackovanja nepostojece transakcije (in memory repozitorijum?), ignorisem...");
            return;
        }
        transakcijaUToku = false;
        SimpleLogger.log(LogLevel.LOG_INFO, "Radim rollback za transakciju u " + getClass().getSimpleName() + ", trazim repozitorijume da komitujem konekcije");
        boolean changedConnection = false;
        for(Field f : getClass().getDeclaredFields()) {
            if(MarkerRepository.class.isAssignableFrom(f.getType())) {
                f.setAccessible(true);
                MarkerRepository repo = (MarkerRepository) f.get(this);
                SimpleLogger.log(LogLevel.LOG_INFO, "Pronadjen repozitorijum " + repo.getClass().getSimpleName());
                boolean foundEm = false;
                for(Field entityManager : repo.getClass().getDeclaredFields()) {
                    if(entityManager.getType() == EntityManager.class) {
                        foundEm = true;
                        SimpleLogger.log(LogLevel.LOG_INFO, "Pronadjeno entitymanager polje za " + repo.getClass().getSimpleName());
                        entityManager.setAccessible(true);
                        Object em = entityManager.get(repo);
                        Field con = EntityManager.class.getDeclaredField("con");
                        con.setAccessible(true);
                        Connection c = (Connection) con.get(em);
                        if(!changedConnection) {
                            SimpleLogger.log(LogLevel.LOG_INFO, "Roll backujem transakciju kroz dobijenu referencu konekcije (svi repozitorijumi je dele)");
                            if(c != null && !c.isClosed()) {
                                if(!c.getAutoCommit()) {
                                    c.rollback();
                                    c.setAutoCommit(true);
                                    c.close();
                                }
                                changedConnection = true;
                                SimpleLogger.log(LogLevel.LOG_INFO, "Uspesno izvrsen rollback transakcije za servis " + getClass().getSimpleName());
                            }
                            else {
                                throw new Exception("Greska pri rollbackovanju transakcije: neocekivano stanje konekcije");
                            }
                        }
                        con.set(em,null);
                        SimpleLogger.log(LogLevel.LOG_INFO, "Konekcija u entity manager referenci repozitorijuma " + repo.getClass().getSimpleName() + " uspesno vracen na null \n");
                        break;
                    }
                }
                if(!foundEm) {
                    SimpleLogger.log(LogLevel.LOG_INFO, "Nije pronadjeno entitymanager polje za " + repo.getClass().getSimpleName() + "\n");
                }
            }
        }
    }
}
