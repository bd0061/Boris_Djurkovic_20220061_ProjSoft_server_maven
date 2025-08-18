/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package framework.orm;

import framework.config.AppConfig;
import framework.orm.anotacije.vrednosnaogranicenja.GreaterThan;
import framework.orm.anotacije.vrednosnaogranicenja.In;
import framework.orm.anotacije.vrednosnaogranicenja.Email;
import framework.orm.anotacije.vrednosnaogranicenja.NotNull;
import framework.orm.anotacije.vrednosnaogranicenja.Between;
import framework.orm.anotacije.vrednosnaogranicenja.LessThan;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;
import framework.orm.anotacije.kljuc.ManyToOne;
import framework.orm.anotacije.kljuc.OneToMany;
import framework.orm.util.EnumConverter;

/**
 *
 * @author Djurkovic
 */
public abstract class Entitet implements Serializable {

    //funkcionalna apstrakcija za petlju gde proveravamo sva polja klase
    public void forEachValidField(MojConsumer action) throws IllegalAccessException, SQLException {
        //refleksijom izvlacimo sva polja klase (i kesiramo za kasnije pozive)
        EntityCache.EntityMetadata metadata = EntityCache.getMetadata(getClass());
        List<Field> fs = metadata.svaPolja;
        for (Field field : fs) {

            field.setAccessible(true);

            Object value = field.get(this);

            if (value == null) {
                continue;
            }
            action.accept(field, value);
        }
    }

    public void forEachKey(MojConsumer action) throws IllegalAccessException, SQLException {

        //refleksijom izvlacimo sva polja klase (i kesiramo za kasnije pozive)
        EntityCache.EntityMetadata metadata = EntityCache.getMetadata(getClass());
        List<Field> fs = metadata.primarniKljucevi;

        for (Field field : fs) {
            field.setAccessible(true);

            Object value = field.get(this);

            if (value == null) {
                continue;
            }

            action.accept(field, value);
        }
    }


    public List<String> vratiListuImenaAtributa() {
        try {
            List<String> res = new ArrayList<>();
            forEachValidField((field, _) -> {
                
                //OneToMany lista polje nije clan same tabele
                if(!field.isAnnotationPresent(OneToMany.class))
                    res.add(EntityManager.vratiImePolja(field));

            });
            return res;
        } catch (IllegalAccessException | SQLException e) {
            return null;
        }
    }

    public void NapuniHelper(Field f, ResultSet rs, int index) throws SQLException, IllegalAccessException {
        Class<?> type = f.getType();
        if (type.equals(Integer.class)) {
            f.set(this, rs.getInt(index));
        } else if (type.equals(Double.class)) {
            f.set(this, rs.getDouble(index));

        } else if (type.equals(Float.class)) {
            f.set(this, rs.getFloat(index));

        } else if (type.equals(Short.class)) {
            f.set(this, rs.getShort(index));

        } else if (type.equals(String.class)) {
            f.set(this, rs.getString(index));

        } else if (type.equals(Character.class)) {
            f.set(this, rs.getString(index).charAt(0));
        } else if (type.equals(Boolean.class)) {
            f.set(this, rs.getBoolean(index));

        } else if (type.equals(Long.class)) {
            f.set(this, rs.getLong(index));

        } else if (type.equals(Byte.class)) {
            f.set(this, rs.getByte(index));
        }
        else if(Enum.class.isAssignableFrom(type)) {
            f.set(this,EnumConverter.fromOrdinal((Class<Enum>)type,rs.getInt(index)));
        }
        else if (type.equals(java.util.Date.class)) {
            //forsiraj da vracen tip ne bude java.sql.Date vec java.util.Date
            Date raw = rs.getTimestamp(index);
            f.set(this, raw != null ? new Date(raw.getTime()) : null);
        } else {
            throw new SQLException("FIXMEFIXMEFIXMEFIXMEFIXME");
        }
    }

    public void NapuniHelper(Field f, Field z, Class<?> type, Object target, ResultSet rs) throws SQLException, IllegalAccessException {
        if (type.equals(Integer.class)) {
            f.set(target, rs.getInt(EntityManager.vratiImePolja(z)));
        } else if (type.equals(Double.class)) {
            f.set(target, rs.getDouble(EntityManager.vratiImePolja(z)));

        } else if (type.equals(Float.class)) {
            f.set(target, rs.getFloat(EntityManager.vratiImePolja(z)));

        } else if (type.equals(Short.class)) {
            f.set(target, rs.getShort(EntityManager.vratiImePolja(z)));

        } else if (type.equals(String.class)) {
            f.set(target, rs.getString(EntityManager.vratiImePolja(z)));

        } else if (type.equals(Character.class)) {
            f.set(target, rs.getString(EntityManager.vratiImePolja(z)).charAt(0));
        } else if (type.equals(Boolean.class)) {
            f.set(target, rs.getBoolean(EntityManager.vratiImePolja(z)));

        } else if (type.equals(Long.class)) {
            f.set(target, rs.getLong(EntityManager.vratiImePolja(z)));

        } else if (type.equals(Byte.class)) {
            f.set(target, rs.getByte(EntityManager.vratiImePolja(z)));
        }
        else if(Enum.class.isAssignableFrom(type)) {
            f.set(target,EnumConverter.fromOrdinal((Class<Enum>)type,rs.getInt(EntityManager.vratiImePolja(z))));
        }
        else if (type.equals(java.util.Date.class)) {
            //forsiraj da vracen tip ne bude java.sql.Date vec java.util.Date
            Date raw = rs.getTimestamp(EntityManager.vratiImePolja(z));
            f.set(target, raw != null ? new Date(raw.getTime()) : null);
        } else {
            throw new SQLException("FIXMEFIXMEFIXMEFIXMEFIXME");
        }
    }

    //na osnovu rezultata sql upita uz pomoc ove metode cemo napuniti klasu sa rezultatom
    public boolean Napuni(ResultSet rs) {
        EntityCache.EntityMetadata metadata = EntityCache.getMetadata(getClass());
        try {
            for (Field f : metadata.svaPolja) {
                f.setAccessible(true);
                Class<?> type = f.getType();
                if(f.isAnnotationPresent(OneToMany.class)) {
                    continue;
                }
                if (f.isAnnotationPresent(ManyToOne.class)) {
                    EntityCache.EntityMetadata fkCache = EntityCache.getMetadata((Class<? extends Entitet>)type);
                    Object fkInstance = type.getDeclaredConstructor().newInstance();
                    Field pk = fkCache.primarniKljucevi.get(0);
                    pk.setAccessible(true);
                    NapuniHelper(pk,f, pk.getType(), fkInstance, rs);
                    f.set(this, fkInstance);
                } else {
                    NapuniHelper(f,f, type, this, rs);
                }
            }
        } catch (SQLException | IllegalAccessException | NoSuchMethodException | InstantiationException | IllegalArgumentException | InvocationTargetException e) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri punjenju " + EntityManager.vratiImeTabele(getClass()) + ": " + e);
            return false;
        }
        return true;
    }

    private enum InAnotacijaEnum {
        DOUBLE, FLOAT, INT, LONG, SHORT, BYTE, STRING, CHAR;
    }

    private static InAnotacijaEnum vratiTipInAnotacija(Class<?> type) {
        InAnotacijaEnum a;

        return switch (type.getSimpleName()) {
            case "Double" ->
                InAnotacijaEnum.DOUBLE;
            case "Float" ->
                InAnotacijaEnum.FLOAT;
            case "Integer" ->
                InAnotacijaEnum.INT;
            case "Long" ->
                InAnotacijaEnum.LONG;
            case "Short" ->
                InAnotacijaEnum.SHORT;
            case "Byte" ->
                InAnotacijaEnum.BYTE;
            case "String" ->
                InAnotacijaEnum.STRING;
            case "Character" ->
                InAnotacijaEnum.CHAR;
            default ->
                null;

        };
    }

    private static Object vratiVrednostClanaInAnotacije(String s, InAnotacijaEnum a) {
        if (s == null) {
            return null;
        }
        try {
            return switch (a) {
                case InAnotacijaEnum.DOUBLE ->
                    Double.parseDouble(s);
                case InAnotacijaEnum.FLOAT ->
                    Float.parseFloat(s);
                case InAnotacijaEnum.INT ->
                    Integer.parseInt(s);
                case InAnotacijaEnum.LONG ->
                    Long.parseLong(s);
                case InAnotacijaEnum.SHORT ->
                    Short.parseShort(s);
                case InAnotacijaEnum.BYTE ->
                    Byte.parseByte(s);
                case InAnotacijaEnum.STRING ->
                    s;
                case InAnotacijaEnum.CHAR ->
                    s.length() != 1 ? null : s.charAt(0);
            };
        } catch (NumberFormatException e) {
            return null;
        }

    }

    //uz pomoc anotacija mozemo da u opstem slucaju resimo veliki broj jednostavnih vrednosnih ogranicenja
    //za slozena ogranicenja korisnik moze jednostavno da overriduje ovu metodu u svom entitetu, da pozove super metod za osnovna ogranicenja,
    //i da zatim doda svoju kompleksnu logiku
    public boolean vrednosnaOgranicenja() {
        Class<? extends Entitet> clazz = getClass();
        EntityCache.EntityMetadata metadata = EntityCache.getMetadata(clazz);
        List<Field> fs = metadata.svaPolja;
        try {
            for (Field f : fs) {
                f.setAccessible(true);
                Class<?> type = f.getType();
                Object value = f.get(this);

                //NotNull
                if (f.isAnnotationPresent(NotNull.class) && value == null) {
                    return false;
                }

                //ako je vrednost null i to nije zabranjeno necemo bacati vreme da gledamo ostala ogranicenja
                if (!f.isAnnotationPresent(NotNull.class) && value == null) {

                    continue;
                }

                //In
                //prvi error - Anotacija za nepodrzan tip
                //drugi error - Vrednost u anotaciji nije odgovarajuca za dat tip
                if (f.isAnnotationPresent(In.class)) {
                    In anotacija = f.getAnnotation(In.class);
                    String[] arr = anotacija.value();
                    boolean found = false;
                    InAnotacijaEnum a = Entitet.vratiTipInAnotacija(type);
                    if (a == null) {
                        SimpleLogger.log(LogLevel.LOG_ERROR, "Nepodrzan tip za anotaciju In");
                        return false;
                    }

                    for (int i = 0; i < arr.length; i++) {
                        Object vrednostClanaAnotacije = Entitet.vratiVrednostClanaInAnotacije(arr[i], a);
                        if (vrednostClanaAnotacije == null) {
                            SimpleLogger.log(LogLevel.LOG_ERROR, "Vrednost clana u In anotaciji: " + arr[i] + " se ne poklapa sa tipom polja: " + type.getName());
                            return false;
                        }
                        if (vrednostClanaAnotacije.equals(value)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        return false;
                    }
                }

                //GreaterThan
                if (f.isAnnotationPresent(GreaterThan.class)) {
                    GreaterThan anotacija = f.getAnnotation(GreaterThan.class);

                    if (Number.class.isAssignableFrom(type)) {
                        Number n = (Number) value;
                        double v = n.doubleValue();
                        if (anotacija.equal()
                                ? v < anotacija.value()
                                : v <= anotacija.value()) {
                            return false;
                        }
                    } else if (String.class.equals(type)) {
                        String s = (String) value;
                        if (anotacija.equal()
                                ? s.length() < anotacija.value()
                                : s.length() <= anotacija.value()) {
                            return false;
                        }
                    } else {
                        SimpleLogger.log(LogLevel.LOG_ERROR, "Polje " + EntityManager.vratiImePolja(f) + " ima anotaciju GreaterThan koja nije kompatibilna za njegov tip " + type.getSimpleName() + ", sprecavam..");
                        return false;
                    }
                }

                //LessThan
                if (f.isAnnotationPresent(LessThan.class)) {
                    LessThan anotacija = f.getAnnotation(LessThan.class);

                    if (Number.class.isAssignableFrom(type)) {
                        Number n = (Number) value;
                        double v = n.doubleValue();
                        if (anotacija.equal()
                                ? v > anotacija.value()
                                : v >= anotacija.value()) {
                            return false;
                        }
                    } else if (String.class.equals(type)) {
                        String s = (String) value;
                        if (anotacija.equal()
                                ? s.length() > anotacija.value()
                                : s.length() >= anotacija.value()) {
                            return false;
                        }
                    } else {
                        SimpleLogger.log(LogLevel.LOG_ERROR, "Polje " + EntityManager.vratiImePolja(f) + " ima anotaciju GreaterThan koja nije kompatibilna za njegov tip: " + type.getSimpleName() + ", sprecavam..");
                        return false;
                    }
                }

                //Between
                if (f.isAnnotationPresent(Between.class)) {
                    Between anotacija = f.getAnnotation(Between.class);

                    if (Number.class.isAssignableFrom(type)) {
                        Number n = (Number) value;
                        double v = n.doubleValue();
                        if (anotacija.equal()
                                ? (v < anotacija.donjaGranica() || v > anotacija.gornjaGranica())
                                : (v <= anotacija.donjaGranica() || v >= anotacija.gornjaGranica())) {
                            return false;
                        }
                    } else if (String.class.equals(type)) {
                        String s = (String) value;
                        int len = s.length();
                        if (anotacija.equal()
                                ? (len < anotacija.donjaGranica() || len > anotacija.gornjaGranica())
                                : (len <= anotacija.donjaGranica() || len >= anotacija.gornjaGranica())) {
                            return false;
                        }
                    } else {
                        SimpleLogger.log(LogLevel.LOG_ERROR, "Polje " + EntityManager.vratiImePolja(f) + " ima anotaciju GreaterThan koja nije kompatibilna za njegov tip: " + type.getSimpleName() + ", sprecavam..");
                        return false;
                    }
                }

                //Email
                if (f.isAnnotationPresent(Email.class)) {
                    if (!String.class.equals(type)) {
                        SimpleLogger.log(LogLevel.LOG_ERROR, "Polje " + EntityManager.vratiImePolja(f) + " ima anotaciju Email koja nije kompatibilna za njegov tip: " + type.getSimpleName() + ", sprecavam..");
                        return false;
                    }
                    Pattern p = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"); //regex za mejl format
                    String email = (String) value;
                    Matcher m = p.matcher(email);
                    if (!m.matches()) {
                        return false;
                    }
                }

            }
            return true;
        } catch (IllegalAccessException e) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Doslo je do greske pri proveri vrednosnih ogranicenja: " + e.getMessage());
            return false;
        }

    }

    //kod parcijalnih promena moramo da povucemo ceo objekat iz baze za polja koja nedostaju i da proverimo da li je doslo do krsenja vrednosnih ogranicenja
    //u slucaju potpune promene mozemo da ih proverimo u celosti u aplikaciji
    //zato je bitno razdvojiti parcijalne i potpune promene jer je pristup bazi za svaku promenu jako skup (minimalno kasnjenje je roundtrip do baze)
    public boolean promenaVrednosnaOgranicenja(Entitet link,AppConfig config) {
        /*
        this - objekat ciji kljuc identifikuje objekat za promenu, a ostala polja nove vrednosti
        link - sadrzi kljuc za identifikaicju, moramo da ga ukljucimo za slucaj da zelimo da promenimo vrednosti samog kljuca u this objektu
         */
        try {
            boolean partial = false;
            Class<? extends Entitet> clazz = getClass();
            EntityCache.EntityMetadata metadata = EntityCache.getMetadata(clazz);

            for (Field f : metadata.svaPolja) {
                f.setAccessible(true);
                Object val = f.get(this);
                if (val == null) {
                    partial = true;
                    break;
                }
            }
            if (partial) {
                Entitet instance = (Entitet) clazz.getDeclaredConstructor().newInstance();
                if (!new EntityManager(config).daLiPostojiSlog(link, instance)) {
                    //ovo ce svakako primetiti konkretna sistemska operacija promene tako da cemo samo da prosledimo kontrolu njoj radi korektnog ispisa
                    return true;
                }

                //podesavamo vrednosti tako da instanca ima vrednosti koje bi nas objekat imao nakon promene
                for (Field f : metadata.svaPolja) {
                    f.setAccessible(true);
                    Object val = f.get(this);
                    if (val != null) {
                        f.set(instance, val);
                    } else {
                        partial = true;
                    }
                }
                return instance.vrednosnaOgranicenja();
            }
            return this.vrednosnaOgranicenja();

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Error promenaVrednosnaOgranicenja: " + ex.getMessage());
            return false;
        }
    }

    //vrednosna ogranicenja za kljuceve, koriste se pri sistemskim operacijama poput pretrazivanja
    public boolean ogranicenjaKljuc() {
        EntityCache.EntityMetadata metadata = EntityCache.getMetadata(getClass());
        boolean result = true;
        for (Field f : metadata.primarniKljucevi) {
            try {
                f.setAccessible(true);
                result = result && (f.get(this) != null);
            } catch (IllegalAccessException ex) {
                return false;
            }
        }

        return result;
    }
}
