/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package framework.orm;

import framework.DbEngine;
import framework.config.AppConfig;
import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.enumeracije.InsertBehaviour;
import framework.orm.anotacije.ImeKolone;

import java.lang.reflect.Field;
import java.sql.*;

import framework.orm.anotacije.kljuc.PrimarniKljuc;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;

import java.util.*;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import framework.orm.anotacije.kljuc.ManyToOne;
import framework.orm.anotacije.kljuc.OneToMany;
import framework.orm.anotacije.kljuc.SlozenKljuc;
import framework.orm.util.EnumConverter;

/**
 *
 * @author Djurkovic
 */
public final class EntityManager {

    public char quote;
    private String porukaMetode;
    private Connection con;
    private AppConfig config;

    public EntityManager(AppConfig config) {
        this.config = config;
        assert config != null;
        quote = config.dbEngine == DbEngine.MYSQL ? '`' : '"';
    }

    public boolean pocniTransakcija() {
        if (con != null) {
            return false;
        }
        try {
            con = ConnectionPool.getConnection();
            con.setAutoCommit(false);
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    public boolean rollbackTransakcija() {
        if (con == null) {
            return false;
        }
        try {
            if (!con.getAutoCommit()) {
                con.rollback();
                con.setAutoCommit(true);
                con.close();
                con = null;

            }
            return true;
        } catch (SQLException ex) {
            return false;
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.setAutoCommit(true);
                    con.close();
                    con = null;
                }
            } catch (SQLException ex) {
                return false;
            }
        }
    }

    public boolean komitTransakcija() {
        if (con == null) {
            return false;
        }
        try {
            if (!con.getAutoCommit()) {
                con.commit();
                con.setAutoCommit(true);
                con.close();
                con = null;

            }
            return true;
        } catch (SQLException ex) {
            return false;
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.setAutoCommit(true);
                    con.close();
                    con = null;
                }
            } catch (SQLException ex) {
                return false;
            }
        }
    }

    public static String vratiImeTabele(Class<? extends Entitet> clz) {
        return EntityCache.getMetadata(clz).imeTabele;
    }

    public static String vratiImePolja(Field f) {
        if (f.isAnnotationPresent(ImeKolone.class)) {
            return f.getAnnotation(ImeKolone.class).value();
        }
        if (f.isAnnotationPresent(ManyToOne.class)) {
            return f.getAnnotation(ManyToOne.class).joinColumn();
        }
        return f.getName();
    }

    public static Object vratiVrednostPolja(Class<?> polje, ResultSet rs, String imeKolone) throws Exception {
        if (polje.equals(Integer.class)) {
            return rs.getInt(imeKolone);
        } else if (polje.equals(Short.class)) {
            return rs.getShort(imeKolone);
        } else if (polje.equals(Long.class)) {
            return rs.getLong(imeKolone);
        } else if (polje.equals(Byte.class)) {
            return rs.getByte(imeKolone);
        } else if (polje.equals(java.util.Date.class)) {
            Date raw = rs.getTimestamp(imeKolone);
            return raw != null ? new Date(raw.getTime()) : null;
        } else if (polje.equals(String.class)) {
            return rs.getString(imeKolone);
        } else if (polje.equals(Character.class)) {
            return rs.getString(imeKolone) != null ? rs.getString(imeKolone).charAt(0) : null;
        } else if (polje.equals(Float.class)) {
            return rs.getFloat(imeKolone);
        } else if (polje.equals(Double.class)) {
            return rs.getDouble(imeKolone);
        } else if (polje.equals(Boolean.class)) {
            return rs.getBoolean(imeKolone);
        } else if (Enum.class.isAssignableFrom(polje)) {
            return EnumConverter.fromOrdinal((Class<Enum>) polje, rs.getInt(imeKolone));
        }
        throw new Exception("Nepodrzan tip polja: " + polje.getName());
    }

    public static Object extractId(Class<? extends Entitet> clazz, ResultSet rs, Set<String> columnNames) throws Exception {
        var m = EntityCache.getMetadata(clazz);
        if (!clazz.isAnnotationPresent(SlozenKljuc.class)) {

            Field kljuc = m.primarniKljucevi.get(0);
            Class<?> tipKljuca = kljuc.getType();
            String imeKolone = m.imeTabele + "_" + vratiImePolja(kljuc);
            if (!columnNames.contains(imeKolone)) {
                throw new Exception("Neocekivano ime kolone: " + imeKolone);
            }
            return vratiVrednostPolja(tipKljuca, rs, imeKolone);
        }
        Class<?> slozenKljuc = clazz.getAnnotation(SlozenKljuc.class).wrapper();
        Object key = slozenKljuc.getDeclaredConstructor().newInstance();
        for (Field f : slozenKljuc.getDeclaredFields()) {
            Class<?> tipPolja = f.getType();
            String ime = m.imeTabele + "_" + vratiImePolja(f);
            if (!columnNames.contains(ime)) {
                throw new Exception("Neocekivano ime kolone: " + ime);
            }
            f.setAccessible(true);
            f.set(key, vratiVrednostPolja(tipPolja, rs, ime));
        }
        return key;

    }


    public <T extends Entitet> List<T> vratiSve(Class<T> clazz) throws Exception {
        return vratiSveNoCriteria(clazz, new KriterijumWrapper(List.of(),KriterijumWrapper.DepthLevel.FULL));
    }

    public <T extends Entitet> List<T> vratiSveNoCriteria(Class<T> clazz, KriterijumWrapper w) throws Exception {
        QueryBuilder<T> qb = new QueryBuilder<>(clazz, config);
        List<T> result = new ArrayList<>();
        String joinQuery = qb.generateJoinString(w);

        boolean oneoff = false;
        try {
            if (con == null || con.isClosed()) {
                con = ConnectionPool.getConnection();
                con.setAutoCommit(true);
                oneoff = true;

            }
        } catch (SQLException ex) {
            porukaMetode = porukaMetode + "\n Neuspesno dobijanje konekcije";
            throw new Exception("Neuspesno dobijanje konekcije: " + ex);
        }
        try (Statement st = con.createStatement()) {
            
            ResultSet rs = st.executeQuery(joinQuery);
            while (rs.next()) {
                T root = qb.buildObjectGraph(rs,w);

                if (!result.contains(root)) {
                    result.add(root);
                }
            }
            rs.close();

        } catch (Exception ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri vracanju svih objekata: " + ex);
            throw ex;
        }
        finally {
            if (oneoff) {
                try {
                    if (con != null && !con.isClosed()) {
                        con.close();
                        con = null;
                        oneoff = false;
                    }

                } catch (SQLException ex) {
                    porukaMetode = porukaMetode + "\nNeuspesno zatvaranje konekcije: " + ex;
                    throw new Exception("Neuspesno zatvaranje konekcije: " + ex);
                }
            }
        }

        return result;
    }



    public <T extends Entitet> List<T> vratiSve(Class<T> clazz, List<KriterijumDescriptor> deskriptori) throws Exception {
        return vratiSve(clazz,new KriterijumWrapper(deskriptori, KriterijumWrapper.DepthLevel.FULL));
    }

    public <T extends Entitet> List<T> vratiSve(Class<T> clazz, KriterijumWrapper w) throws Exception {
        if(w.kds == null || w.kds.isEmpty()) {
            return vratiSveNoCriteria(clazz,w);
        }
        QueryBuilder<T> qb = new QueryBuilder<>(clazz, config);
        List<T> result = new ArrayList<>();
        String joinQuery = qb.generateJoinString(w);

        boolean oneoff = false;
        try {
            if (con == null || con.isClosed()) {
                con = ConnectionPool.getConnection();
                con.setAutoCommit(true);
                oneoff = true;

            }
        } catch (SQLException ex) {
            porukaMetode = porukaMetode + "\n Neuspesno dobijanje konekcije";
            throw new Exception("Neuspesno dobijanje konekcije: " + ex);
        }

        try (PreparedStatement ps = KriterijumDescriptor.procesirajKriterijume(con,joinQuery,w,config,clazz)) {
            if(ps == null) {
                throw new Exception("Neuspesno parsiranje kriterijuma");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                T root = qb.buildObjectGraph(rs,w);

                if (!result.contains(root)) {
                    result.add(root);
                }
            }
            rs.close();

        } catch (Exception ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri vracanju svih objekata: " + ex);
            throw ex;
        }
        finally {
            if (oneoff) {
                try {
                    if (con != null && !con.isClosed()) {
                        con.close();
                        con = null;
                        oneoff = false;
                    }

                } catch (SQLException ex) {
                    porukaMetode = porukaMetode + "\nNeuspesno zatvaranje konekcije: " + ex;
                    throw new Exception("Neuspesno zatvaranje konekcije: " + ex);
                }
            }
        }

        return result;
    }














    private static void ubaciUStatement(PreparedStatement st, Field f, Object value, int index) throws SQLException, IllegalAccessException {
        if (value == null) {
            st.setNull(index, Types.NULL);
            return;
        }
        if (f.isAnnotationPresent(ManyToOne.class)) {
            Class<?> fkType = f.getType();
            EntityCache.EntityMetadata fkCache = EntityCache.getMetadata((Class<? extends Entitet>) fkType);
            //preporuceno da nema slozenih spoljnih kljuceva...
            List<Field> kljucevi = fkCache.primarniKljucevi;
            if (kljucevi.size() > 1) {
                throw new RuntimeException("Slozeni spoljni kljucevi nisu podrzani");
            }
            //za slucaj jedne kolone primarnog kljuca,
            Field pk = kljucevi.get(0);
            pk.setAccessible(true);
            value = pk.get(value);
        }
        if (value instanceof Character) {
            st.setString(index, value.toString());
        } else if (value instanceof java.util.Date) {
            st.setDate(index, new java.sql.Date(((java.util.Date) value).getTime()));

        } else if (value instanceof Enum) {
            st.setInt(index, ((Enum) value).ordinal());
        } else {
            st.setObject(index, value);
        }
    }

    public void isprazniPoruku() {
        if (porukaMetode != null && !porukaMetode.isEmpty()) {
            SimpleLogger.log(LogLevel.LOG_INFO, "  ************** LOG BROKERA **************");
            System.out.println(porukaMetode);
            System.out.println();
            SimpleLogger.log(LogLevel.LOG_INFO, "************** KRAJ LOGA BROKERA **************");
        }
        porukaMetode = "";
    }

    @SafeVarargs
    public final <T extends Entitet> void kreirajEntitet(T... es) throws Exception {
        kreirajEntitet(InsertBehaviour.NORMAL,es);
    }
    @SafeVarargs
    public final <T extends Entitet> void kreirajEntitet(InsertBehaviour ib,T... es) throws Exception {
        for(T e : es) {
            if (!e.vrednosnaOgranicenja()) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri ubacivanju entiteta " + e + ": nezadovoljena vrednosna ogranicenja");
                throw new Exception("Greska pri ubacivanju entiteta " + e + ": nezadovoljena vrednosna ogranicenja");
            }
        }
        pamtiSlog(ib,es);
        SimpleLogger.log(LogLevel.LOG_INFO, "Uspesno ubacivanje entiteta " + Arrays.toString(es));
        isprazniPoruku();
    }

    @SafeVarargs
    public final <T extends Entitet> void obrisiEntitet(T... es) throws Exception {
        for(T e : es) {
            if (!e.ogranicenjaKljuc()) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri brisanju entiteta " + e + ": kljuc je nepotpun ili ima lose vrednosti");
                throw new Exception("Greska pri brisanju entiteta " + e + ": kljuc je nepotpun ili ima lose vrednosti");
            }
        }
        brisiSlog(es);
        SimpleLogger.log(LogLevel.LOG_INFO, "Uspesno brisanje entiteta " + Arrays.toString(es));
        isprazniPoruku();
    }

    public <T extends Entitet> void promeniEntitet(T e1, T e2) throws Exception {
        T[] arr = (T[]) java.lang.reflect.Array.newInstance(e1.getClass(), 1);
        arr[0] = e2;
        promeniEntitet(e1, arr);
    }

    public <T extends Entitet> void promeniEntitet(T e1) throws Exception {
        promeniEntitet(e1, e1);
    }

    public <T extends Entitet> void promeniEntitet(T e1, T[] e2s) throws Exception {
        for(T e2 : e2s) {
            if (!e1.promenaVrednosnaOgranicenja(e2, config)) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri promeni entiteta " + e1 + ": nezadovoljena vrednosna ogranicenja");

                isprazniPoruku();
                throw new Exception("Greska pri promeni entiteta " + e1 + ": nezadovoljena vrednosna ogranicenja");
            }
        }

        promeniSlog(e1, e2s);

        SimpleLogger.log(LogLevel.LOG_INFO, "Uspesna promena entiteta " + e1);
        isprazniPoruku();
    }

    //POTENCIJALNO OPASNA METODA (POTENCIJALNO RANJIVA NA SQL INJEKCIJE)
    //bitno je postarati se da sql parametar ne moze klijent da bira
    public void rawSQL(String sql) throws Exception {
        boolean oneoff = false;
        try {
            if (con == null || con.isClosed()) {
                con = ConnectionPool.getConnection();
                con.setAutoCommit(true);
                oneoff = true;
            }
        } catch (SQLException ex) {
            porukaMetode = porukaMetode + "\n Neuspesno dobijanje konekcije";
            throw new Exception("Neuspesno dobijanje konekcije");
        }

        try(Statement st = con.createStatement()) {
            st.executeUpdate(sql);
        }
    }





    private void pamtiSlog(Entitet... entiteti) throws Exception {
        pamtiSlog(InsertBehaviour.NORMAL,entiteti);
    }

    private void pamtiSlog(InsertBehaviour ib, Entitet... entiteti) throws Exception {
        if(entiteti.length == 0) return;
        EntityCache.EntityMetadata metadata = EntityCache.getMetadata(entiteti[0].getClass());
        List<String> atributi = entiteti[0].vratiListuImenaAtributa();
        StringBuilder upitSb;

        upitSb = new StringBuilder("INSERT").append(ib == InsertBehaviour.IGNORE_DUPS && config.dbEngine == DbEngine.MYSQL ? " IGNORE INTO " : " INTO ");

        upitSb.append(quote).append(metadata.imeTabele).append(quote).append("(");

        for (String atribut : atributi) {
            upitSb.append(quote).append(atribut).append(quote).append(",");
        }
        upitSb.delete(upitSb.length() - 1, upitSb.length());
        upitSb.append(") VALUES ");
        for(Entitet e : entiteti) {
            upitSb.append("(");
            for (String atribut : atributi) {
                upitSb.append("?,");
            }
            upitSb.delete(upitSb.length() - 1, upitSb.length());
            upitSb.append("),");
        }

        upitSb.delete(upitSb.length() - 1, upitSb.length());
        if(ib == InsertBehaviour.IGNORE_DUPS && config.dbEngine == DbEngine.POSTGRES) {
            upitSb.append(" ON CONFLICT DO NOTHING");
        }
        else if(ib == InsertBehaviour.OVERWRITE_DUPS) {
            if(config.dbEngine == DbEngine.MYSQL) {
                upitSb.append(" ON DUPLICATE KEY UPDATE ");
            }
            else if(config.dbEngine == DbEngine.POSTGRES) {
                upitSb.append(" ON CONFLICT(");
                for(var f : metadata.primarniKljucevi) {
                    upitSb.append(EntityManager.vratiImePolja(f)).append(",");
                }
                upitSb.delete(upitSb.length() - 1, upitSb.length());
                upitSb.append(") DO UPDATE SET ");
            }
            for(var f : metadata.svaPolja) {
                if(f.isAnnotationPresent(PrimarniKljuc.class) || f.isAnnotationPresent(OneToMany.class))
                    continue;
                if(config.dbEngine == DbEngine.MYSQL) {
                    upitSb.append(EntityManager.vratiImePolja(f)).append(" =  VALUES(").append(EntityManager.vratiImePolja(f)).append("),");
                }
                else if(config.dbEngine == DbEngine.POSTGRES) {
                    upitSb.append(EntityManager.vratiImePolja(f)).append(" =  EXCLUDED.").append(EntityManager.vratiImePolja(f)).append(",");
                }
            }
            upitSb.delete(upitSb.length() - 1, upitSb.length());
        }

        String upit = upitSb.toString();
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAA upit je: " + upit);
        boolean oneoff = false;
        try {
            if (con == null || con.isClosed()) {
                con = ConnectionPool.getConnection();
                con.setAutoCommit(true);
                oneoff = true;

            }
        } catch (SQLException ex) {
            porukaMetode = porukaMetode + "\n Neuspesno dobijanje konekcije";
            throw new Exception("Neuspesno dobijanje konekcije: " + ex);
        }

        String[] keyNames = new String[metadata.primarniKljucevi.size()];
        for(int i = 0; i < keyNames.length; i++) {
            keyNames[i] = EntityManager.vratiImePolja(metadata.primarniKljucevi.get(i));
        }


        try (PreparedStatement st = con.prepareStatement(upit,keyNames)) {
            AtomicInteger ia = new AtomicInteger(1);

            for(Entitet e : entiteti) {
                e.forEachValidField((field, value) -> {
                    int i = ia.get();
                    if (!field.isAnnotationPresent(OneToMany.class)) {
                        ubaciUStatement(st, field, value, i);
                        ia.incrementAndGet();
                    }

                });
            }
            st.executeUpdate();
            //moramo da kolone referiramo po indeksu iz nekog razloga
            //svi jdbc driveri (valjda) redosled kolona odredjuju na osnovu string imena kolona prosledjenom metodi prepareStatement
            //ne vidim drugi nacin da osiguram da za proizvoljan entitet primarni kljucevi budu popunjeni
            try(ResultSet krs = st.getGeneratedKeys()) {
                for(Entitet e : entiteti) {
                    if(krs.next()) {
                        for(int i = 0; i < metadata.primarniKljucevi.size(); i++) {
                            var k = metadata.primarniKljucevi.get(i);
                            k.setAccessible(true);
                            if(k.get(e) == null)
                            {
                                e.NapuniHelper(k,krs,i+1);
                            }

                        }
                    }
                }
            }

        }
        catch (SQLIntegrityConstraintViolationException e) {
            porukaMetode = porukaMetode + "\nNije uspesno zapamcen slog u bazi. " + e;
            throw e;
        }
        catch (SQLException | IllegalAccessException e) {
            porukaMetode = porukaMetode + "\nNije uspesno zapamcen slog u bazi. " + e;
            throw new Exception("Nije uspesno zapamcen slog u bazi. " + e);
        } finally {
            if (oneoff) {
                try {
                    if (con != null && !con.isClosed()) {
                        con.close();
                        con = null;
                        oneoff = false;
                    }

                } catch (SQLException ex) {
                    porukaMetode = porukaMetode + "\nNeuspesno zatvaranje konekcije: " + ex;
                    throw new Exception("Neuspesno zatvaranje konekcije: " + ex);
                }
            }

        }
        porukaMetode = porukaMetode + "\nUspesno zapamcen slog u bazi. ";
    }


    private void brisiSlog(Entitet... entities) throws Exception {

        if(entities.length == 0) return;
        EntityCache.EntityMetadata metadata = EntityCache.getMetadata(entities[0].getClass());
        List<String> atributi = entities[0].vratiListuImenaAtributa();

        StringBuilder upitSb = new StringBuilder("DELETE FROM ").append(quote).append(metadata.imeTabele).append(quote).append(" WHERE (");
        try {
            entities[0].forEachKey((field, value) -> upitSb.append(quote).append(EntityManager.vratiImePolja(field)).append(quote).append(","));
        } catch (IllegalAccessException | SQLException e) {
            porukaMetode = porukaMetode + "\nGreska pri formiranju upita: " + e;
            throw new Exception("Greska pri formiranju upita: " + e);
        }
        upitSb.delete(upitSb.length() - 1, upitSb.length());
        upitSb.append(") IN (");

        for(Entitet e : entities) {
            upitSb.append("(");
            for(var k : metadata.primarniKljucevi) {
                upitSb.append("?,");
            }
            upitSb.delete(upitSb.length() - 1, upitSb.length());
            upitSb.append("),");
        }
        upitSb.delete(upitSb.length() - 1, upitSb.length());
        upitSb.append(")");
        String upit = upitSb.toString();
        boolean oneoff = false;
        try {
            if (con == null || con.isClosed()) {
                con = ConnectionPool.getConnection();
                con.setAutoCommit(true);
                oneoff = true;

            }
        } catch (SQLException ex) {
            porukaMetode = porukaMetode + "\n Neuspesno dobijanje konekcije";
            throw new Exception("Neuspesno dobijanje konekcije");
        }
        try (PreparedStatement st = con.prepareStatement(upit)) {

            int atributiLength = atributi.size();
            AtomicInteger ia = new AtomicInteger(1);

            for(Entitet e : entities) {
                e.forEachKey((field, value) -> {
                    int i = ia.get();
                    if (!field.isAnnotationPresent(OneToMany.class)) {
                        ubaciUStatement(st, field, value, i);
                    }
                    ia.incrementAndGet();
                });
            }
            int rowsAffected = st.executeUpdate();
            if (rowsAffected == 0) {
                porukaMetode += "\nUpozorenje: delete upit nije uticao ni na jednu torku";
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            porukaMetode = porukaMetode + "\nNije uspesno zapamcen slog u bazi. " + e;
            throw e;
        } catch (SQLException | IllegalAccessException e) {
            porukaMetode = porukaMetode + "\nNije uspesno obrisan slog u bazi. " + e;
            throw new Exception("Nije uspesno obrisan slog u bazi: "  + e);
        } finally {
            if (oneoff) {
                try {
                    if (con != null && !con.isClosed()) {
                        con.close();
                        con = null;
                        oneoff = false;
                    }
                } catch (SQLException ex) {
                    porukaMetode = porukaMetode + "\nNeuspesno zatvaranje konekcije: " + ex;
                    throw new Exception("Neuspesno zatvaranje konekcije: " + ex);
                }
            }

        }
        porukaMetode = porukaMetode + "\nUspesno obrisan slog u bazi. ";
    }

    private void promeniSlog(Entitet odo1, Entitet[] odo2s) throws Exception {
        /* 
            odo1 - objekat cija se klasa koristi za tip, a atributi za nove vrednosti
            odo2 - objekat cije se vrednosti odnose na where klazulu
            cesto je dovoljan samo jedan odo za promenu ali kada imamo barem dva objekta
            mozemo da ucinimo upit tipa UPDATE nekatabela SET id = 999 where id = 1 koji ne bi bio moguc
            samo sa jednim objektom jer samo iz njega mozemo da izvucemo samo jednu vrednost id-a.
         */

        EntityCache.EntityMetadata metadata = EntityCache.getMetadata(odo1.getClass());
        List<String> atributi = odo1.vratiListuImenaAtributa();
        List<Field> polja = metadata.svaPolja;
        List<Field> kljucevi = EntityCache.getMetadata(odo2s[0].getClass()).primarniKljucevi;

        StringBuilder upitSb = new StringBuilder("UPDATE ");
        upitSb.append(quote).append(metadata.imeTabele).append(quote).append(" SET ");
        try {
            odo1.forEachValidField((field, value) -> {
                if(!field.isAnnotationPresent(OneToMany.class))
                    upitSb.append(quote).append(EntityManager.vratiImePolja(field)).append(quote).append("=").append("?,");
            });
        } catch (SQLException | IllegalAccessException e) {
            porukaMetode = porukaMetode + "\nNije uspesno promenjen slog u bazi. " + e;
            throw new Exception("Nije uspesno prmenjen slog u bazi: "  + e);
        }

        upitSb.delete(upitSb.length() - 1, upitSb.length());

        upitSb.append(" WHERE (");

        try {
            odo1.forEachKey((field, value) -> upitSb.append(quote).append(EntityManager.vratiImePolja(field)).append(quote).append(","));
        } catch (SQLException | IllegalAccessException e) {
            porukaMetode = porukaMetode + "\nNije uspesno promenjen slog u bazi. " + e;
            throw new Exception("Nije uspesno prmenjen slog u bazi: "  + e);

        }
        upitSb.delete(upitSb.length() - 1, upitSb.length());
        upitSb.append(") IN(");

        for(Entitet odo2 : odo2s) {
            upitSb.append("(");
            odo2.forEachKey((field, value) -> upitSb.append("?,"));
            upitSb.delete(upitSb.length() -1, upitSb.length());
            upitSb.append("),");
        }
        upitSb.delete(upitSb.length() - 1, upitSb.length());
        upitSb.append(")");

        String upit = upitSb.toString();
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA " + upit);
        boolean oneoff = false;
        try {
            if (con == null || con.isClosed()) {
                con = ConnectionPool.getConnection();
                con.setAutoCommit(true);
                oneoff = true;

            }
        } catch (SQLException ex) {
            porukaMetode = porukaMetode + "\n Neuspesno dobijanje konekcije";
            throw new Exception("Neuspesno dobijanje konekcije: " + ex);
        }
        try (PreparedStatement st = con.prepareStatement(upit)) {

            int atributiLength = atributi.size();
            AtomicInteger ia = new AtomicInteger(0);

            odo1.forEachValidField((field, value) -> {
                int i = ia.get();
                if (!field.isAnnotationPresent(OneToMany.class)) {
                    ubaciUStatement(st, field, value, i + 1);
                    ia.incrementAndGet();
                }

            });
            for(Entitet odo2 : odo2s) {
                odo2.forEachKey((field, value) -> {

                    int i = ia.get();
                    if (!field.isAnnotationPresent(OneToMany.class)) {
                        ubaciUStatement(st, field, value, i + 1);
                        ia.incrementAndGet();
                    }

                });
            }

            int rowsAffected = st.executeUpdate();
            if (rowsAffected == 0) {
                porukaMetode += "\nUpozorenje: update upit nije uticao ni na jednu torku";
            }

        }
        catch (SQLIntegrityConstraintViolationException e) {
            porukaMetode = porukaMetode + "\nNije uspesno promenjen slog u bazi. " + e;
            throw e;
        }
        catch (SQLException | IllegalAccessException e) {
            porukaMetode = porukaMetode + "\nNije uspesno promenjen slog u bazi. " + e;
            throw new Exception("Nije uspesno promenjen slog u bazi. " + e);
        } finally {
            if (oneoff) {
                try {
                    if (con != null && !con.isClosed()) {
                        con.close();
                        con = null;
                        oneoff = false;
                    }
                } catch (SQLException ex) {
                    porukaMetode = porukaMetode + "\nNeuspesno zatvaranje konekcije: " + ex;
                    throw new Exception("Neuspesno zatvaranje konekcije: " + ex);
                }
            }

        }
        porukaMetode = porukaMetode + "\nUspesno promenjen slog u bazi. ";
    }

    public boolean daLiPostojiSlog(Entitet odo, boolean napuniti) {
        return daLiPostojiSlog(odo, napuniti, null);

    }

    public boolean daLiPostojiSlog(Entitet odo, Entitet napuni) {
        return daLiPostojiSlog(odo, true, napuni);

    }

    public boolean daLiPostojiSlog(Entitet odo) {
        return daLiPostojiSlog(odo, true, null);

    }

    public boolean daLiPostojiSlog(Entitet odo, boolean napuniti, Entitet napuni) {
        EntityCache.EntityMetadata metadata = EntityCache.getMetadata(odo.getClass());
        List<String> atributi = odo.vratiListuImenaAtributa();

        StringBuilder upitSb = new StringBuilder("SELECT * FROM ");
        upitSb.append(quote).append(metadata.imeTabele).append(quote).append(" WHERE ");

        try {
            odo.forEachKey((field, value) -> {
                if (!field.isAnnotationPresent(OneToMany.class)) {
                    upitSb.append(quote).append(EntityManager.vratiImePolja(field)).append(quote).append("=").append("? AND ");
                }
            }
            );
        } catch (IllegalAccessException | SQLException e) {
            porukaMetode = porukaMetode + "\nNije uspesno pretrazena baza: " + e;
            for (var ee : e.getStackTrace()) {
                System.out.println(ee);
            }
            return false;
        }

        upitSb.delete(upitSb.length() - 5, upitSb.length());

        String upit = upitSb.toString();
        ResultSet RSslogovi;
        boolean oneoff = false;
        try {
            if (con == null || con.isClosed()) {
                con = ConnectionPool.getConnection();
                con.setAutoCommit(true);
                oneoff = true;

            }
        } catch (SQLException ex) {
            porukaMetode = porukaMetode + "\n Neuspesno dobijanje konekcije";
            return false;
        }
        try (Connection con = ConnectionPool.getConnection(); PreparedStatement st = con.prepareStatement(upit)) {

            int atributiLength = atributi.size();
            AtomicInteger ia = new AtomicInteger(0);

            odo.forEachKey((field, value) -> {

                int i = ia.get();
                if (!field.isAnnotationPresent(OneToMany.class)) {
                    ubaciUStatement(st, field, value, i + 1);
                }
                ia.incrementAndGet();
            });

            RSslogovi = st.executeQuery();
            if (!RSslogovi.next()) {
                porukaMetode = porukaMetode + "\nSlog ne postoji u bazi podataka.";
                return false;
            }
            if (napuniti) {
                if (napuni != null) {
                    napuni.Napuni(RSslogovi);
                } else {
                    odo.Napuni(RSslogovi);
                }
            }

            RSslogovi.close();

        } catch (SQLException | IllegalAccessException e) {
            porukaMetode = porukaMetode + "\nNije uspesno pretrazena baza: " + e;
            for (var ee : e.getStackTrace()) {
                System.out.println(ee);
            }
            return false;
        } finally {
            if (oneoff) {
                try {
                    con.close();
                    con = null;
                    oneoff = false;
                } catch (SQLException ex) {
                    porukaMetode = porukaMetode + "\nNeuspesno zatvaranje konekcije: " + ex;
                    return false;
                }
            }

        }
        porukaMetode = porukaMetode + "\nSlog postoji u bazi.";
        return true;

    }

}
