/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package framework.model;

import framework.DbEngine;
import framework.config.AppConfig;
import framework.orm.Entitet;
import framework.orm.EntityCache;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import framework.orm.EntityManager;
import framework.orm.QueryBuilder;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;

/**
 *
 * @author Djurkovic
 */
public record KriterijumDescriptor(boolean negate, Class<? extends Entitet> target, String imePolja, String op, Object vrednost) implements Serializable {

    public KriterijumDescriptor(Class<? extends Entitet> target, String imePolja, String op, Object vrednost) {
        this(false, target, imePolja, op, vrednost);
    }

    public static boolean validniDeskriptori(Object o) {
        if(o == null) return true;
        List<?> kds;
        if(o instanceof List<?> l)
            kds = l;
        else if(o instanceof KriterijumWrapper w) {
            kds = w.kds;
        }
        else {
            return false;
        }
        for(var x : kds) {
            if(!(x instanceof KriterijumDescriptor))
                return false;
        }
        return true;
    }

    private static boolean postavi(int index, PreparedStatement ps, Object val) throws SQLException {
        if (val instanceof String s) {
            ps.setString(index, s);
        } else if (val instanceof Integer ii) {
            ps.setInt(index, ii);
        } else if (val instanceof Long l) {
            ps.setLong(index, l);
        } else if (val instanceof Short s) {
            ps.setShort(index, s);
        } else if (val instanceof Byte b) {
            ps.setByte(index, b);
        } else if (val instanceof Float f) {
            ps.setFloat(index, f);
        } else if (val instanceof Double d) {
            ps.setDouble(index, d);
        } else if (val instanceof Boolean b) {
            ps.setBoolean(index, b);
        } else if (val instanceof Character c) {
            ps.setString(index, String.valueOf(c));
        } else if (val instanceof java.util.Date date) {
            ps.setDate(index, new java.sql.Date(date.getTime()));
        }
        else if(val instanceof Enum e) {
            ps.setInt(index,e.ordinal());
        }
        else {
            return false;
        }
        return true;
    }

    public static PreparedStatement procesirajKriterijume(Connection con, String joinString, KriterijumWrapper w, AppConfig cfg, Class<? extends Entitet> clazz) throws SQLException {
        List<KriterijumDescriptor> deskriptori = w.kds;
        if (deskriptori.isEmpty()) {
            SimpleLogger.log(LogLevel.LOG_WARN, "Prosledjena lista kriterijuma je prazna");
            return con.prepareStatement(joinString);
        }
        StringBuilder where = new StringBuilder(" WHERE ");
        Set<String> dozvoljeneOperacije = Set.of("!=", "=", ">", "<", "LIKE", "IN");
        Set<String> iskljucivoNumerickeOperacije = Set.of(">", "<");
        char quote = cfg.dbEngine == DbEngine.MYSQL ? '`' : '"';
        for (var kd : deskriptori) {
            if(!Entitet.class.isAssignableFrom(kd.target())) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Prosledjen kriterijum ima losu klasu kao metu, abortiram");
                return null;
            }
            if (!dozvoljeneOperacije.contains(kd.op())) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Prosledjen kriterijum ima losu operaciju, abortiram");
                return null;
            }
            var md = EntityCache.getMetadata(kd.target());
            try {
                Field f = kd.target().getDeclaredField(kd.imePolja());
                if (!kd.op().equals("IN")) {

                    if (!f.getType().equals(kd.vrednost().getClass())) {
                        SimpleLogger.log(LogLevel.LOG_ERROR, "Tip vrednosti prosledjenog kriterijuma se ne poklapa sa pravom vrednoscu polja, abortiram ( ocekivano: " + f.getType().getSimpleName() + ", primljeno: " + kd.vrednost().getClass().getSimpleName() + ")");
                        return null;
                    }
                    if (!Number.class.isAssignableFrom(f.getType()) && !java.util.Date.class.isAssignableFrom(f.getType()) && iskljucivoNumerickeOperacije.contains(kd.op())) {
                        SimpleLogger.log(LogLevel.LOG_ERROR, "Vrsta operacije iskljuciva za brojeve (" + kd.op() + ") se koristi na tipu koji nije numericki (" + f.getName() + "), abortiram...");
                        return null;
                    }
                    if (!f.getType().equals(String.class) && kd.op().equals("LIKE")) {
                        SimpleLogger.log(LogLevel.LOG_ERROR, "Vrsta operacije iskljuciva za stringove (" + kd.op() + ") se koristi na tipu koji nije string (" + f.getName() + "), abortiram...");
                        return null;
                    }
                } else {
                    if (kd.vrednost() instanceof List<?> l) {
                        if (l.isEmpty() || !(l.get(0).getClass().equals(f.getType()))) {
                            SimpleLogger.log(LogLevel.LOG_ERROR, "Lista prazna/Tip vrednosti clana liste prosledjenog kriterijuma se ne poklapa sa pravom vrednoscu polja, abortiram");
                            return null;
                        }
                    } else {
                        SimpleLogger.log(LogLevel.LOG_ERROR, "Vrednost prosledjenog kriterijuma za operaciju IN nije lista, abortiram");
                        return null;
                    }
                }

            } catch (NoSuchFieldException ex) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Ime polja prosledjenog kriterijuma ne postoji u klasi koju targetira, abortiram");
                return null;
            }
            if (kd.negate()) {
                where.append("NOT ( ");
            }
            where.append(quote).append(md.imeTabele).append(quote).append(".").append(kd.imePolja()).append(" ").append(kd.op()).append(" ");

            if (!kd.op().equals("IN")) {
                where.append("?");
            } else {
                where.append("(");
                List<?> l = (List<?>) kd.vrednost();
                for (int i = 0; i < l.size(); i++) {
                    where.append("?,");
                }
                where.delete(where.length() - 1, where.length());
                where.append(")");
            }
            if (kd.negate()) {
                where.append(" )");
            }
            where.append(" AND ");
        }
        where.delete(where.length() - 5, where.length());
        System.out.println("Konacan WHERE string:\n" + where.toString());


        String finalString = joinString + where.toString();
        PreparedStatement ps = con.prepareStatement(finalString);
        int i = 1;
        for (var kd : deskriptori) {
            if (!kd.op().equals("IN")) {
                if (!postavi(i, ps, kd.vrednost())) {
                    SimpleLogger.log(LogLevel.LOG_ERROR, "Vrednost prosledjenog kriterijuma nije podrzana, abortiram");
                    return null;
                }
                i++;
            } else {
                List<?> l = (List<?>) kd.vrednost();
                for (var o : l) {
                    if (!postavi(i, ps, o)) {
                        SimpleLogger.log(LogLevel.LOG_ERROR, "Vrednost u listi prosledjenog kriterijuma nije podrzana, abortiram");
                        return null;
                    }
                    i++;
                }
            }
        }

        return ps;
    }


    //na osnovu nasih scuffed kriterijum objekata pravimo predikat
    public static <T extends Entitet> Predicate<T> buildPredicate(List<KriterijumDescriptor> kriterijumi) {
        if (kriterijumi == null || kriterijumi.isEmpty()) {
            return d -> true;
        }

        return d -> {
            try {
                for (KriterijumDescriptor kd : kriterijumi) {
                    Object targetObject = d;
                    Field matchingField = null;

                    // ukoliko se ne poklapaju klase d i kd.target() kriterijum gledamo kod entiteta ka kome d ima spoljni kljuc
                    for (Field f : d.getClass().getDeclaredFields()) {
                        if (kd.target().isAssignableFrom(f.getType())) {
                            f.setAccessible(true);
                            targetObject = f.get(d); // pomeramo se ka tom entitetu
                            matchingField = kd.target().getDeclaredField(kd.imePolja());
                            matchingField.setAccessible(true);
                            break;
                        }
                    }

                    // u suprotnom gledamo originaln entitet
                    if (matchingField == null) {
                        matchingField = d.getClass().getDeclaredField(kd.imePolja());
                        matchingField.setAccessible(true);
                    }

                    Object fieldValue = matchingField.get(targetObject);

                    boolean matches = switch (kd.op()) {
                        case "=" -> Objects.equals(fieldValue, kd.vrednost());
                        case "!=" -> !Objects.equals(fieldValue, kd.vrednost());
                        case ">" -> compare(fieldValue, kd.vrednost()) > 0;
                        case "<" -> compare(fieldValue, kd.vrednost()) < 0;
                        case "LIKE" -> (fieldValue instanceof String s) && s.contains(kd.vrednost().toString());
                        case "IN" -> {
                            if (kd.vrednost() instanceof List<?> list) {
                                yield list.contains(fieldValue);
                            } else {
                                yield false;
                            }
                        }
                        default -> false;
                    };

                    if (kd.negate()) {
                        matches = !matches;
                    }

                    if (!matches) {
                        return false;
                    }
                }
            } catch (Exception e) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Doslo je do greske pri procesiranju deskriptora: " + e);
                return false;
            }

            return true;
        };
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private static int compare(Object o1, Object o2) {
        if (o1 == null || o2 == null) return 0;
        if (o1 instanceof Comparable c1 && o2.getClass().isAssignableFrom(o1.getClass())) {
            return c1.compareTo(o2);
        }
        return 0;
    }


}
