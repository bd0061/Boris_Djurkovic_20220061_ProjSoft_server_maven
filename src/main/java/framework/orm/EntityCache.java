/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package framework.orm;

import framework.orm.anotacije.ImeTabele;
import framework.orm.anotacije.Transient;
import framework.orm.anotacije.kljuc.PrimarniKljuc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;
import framework.orm.anotacije.kljuc.ManyToOne;
import framework.orm.anotacije.kljuc.OneToMany;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import framework.orm.anotacije.kljuc.SlozenKljuc;

/**
 *
 * @author Djurkovic
 */
//za kesiranje nekih nepromenljivih podataka kod domenskih klasa poput polja primarnih kljuceva 
//kako ne bi bespotrebno morali vise puta za redom da se oslanjamo na sporu refleksiju
public final class EntityCache {

    private static final Set<Class<?>> WRAPPER_TYPES = Set.of(
            Boolean.class,
            Byte.class,
            Character.class,
            Double.class,
            Float.class,
            Integer.class,
            Long.class,
            Short.class,
            java.util.Date.class
    );

    private static boolean isWrapperType(Class<?> clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }
    //nas cache, za svaku klasu po jedan skup metapodataka
    private static final Map<Class<? extends Entitet>, EntityMetadata> cache = new HashMap<>();

    public static <T extends Entitet> EntityMetadata getMetadata(Class<T> clazz) {

        return cache.computeIfAbsent(clazz, EntityCache::extractMetadata);
    }

    private static <T extends Entitet> EntityMetadata extractMetadata(Class<T> clazz) {
        SimpleLogger.log(LogLevel.LOG_INFO, "Kesiram metapodatke za domensku klasu " + clazz.getName() + "...");
        String imeTabele;

//        SimpleLogger.log(LogLevel.LOG_INFO, "Kesiram join fetch string za " + clazz.getName() + "...");
//        QueryBuilder<T> qb = new QueryBuilder<>(clazz);
//        String joinFetchString = ""; //qb.generateJoinString();
//        SimpleLogger.log(LogLevel.LOG_INFO, "Join fetch string uspesno kesiran za " + clazz.getName() + ".");

        SimpleLogger.log(LogLevel.LOG_INFO, "Kesiram ime tabele za " + clazz.getName() + "...");
        if (clazz.isAnnotationPresent(ImeTabele.class)) {
            ImeTabele it = clazz.getAnnotation(ImeTabele.class);
            imeTabele = it.value();
        } else {
            imeTabele = clazz.getSimpleName().toLowerCase();
        }
        SimpleLogger.log(LogLevel.LOG_INFO, "Ime tabele uspesno kesirano za " + clazz.getName() + ".");

        SimpleLogger.log(LogLevel.LOG_INFO, "Kesiram polja za " + clazz.getName() + "...");
        List<Field> primarniKljucevi = new ArrayList<>();
        List<Field> oneToManys = new ArrayList<>();
        List<Field> manyToOnes = new ArrayList<>();
        List<Field> svaPolja = new ArrayList<>();
        List<Field> skalari = new ArrayList<>();

        // getDeclaredFields  je osrednje skupa operacija, nema razloga zasto da je ne kesiramo jednom za svaki entitet
        // a kada smo vec ovde, nema razloga zasto da ne skladistimo polja primarne kljuceve u odvojenu kolekciju i ne izvrsimo najosnovniju proveru spoljnih kljuceva
        for (Field field : clazz.getDeclaredFields()) {

            //ne zanimaju nas staticna polja i polja koja eksplicitno oznacavamo da nisu deo tabele
            if (Modifier.isStatic(field.getModifiers()) || field.isAnnotationPresent(Transient.class)) {
                continue;
            }

            boolean isManyToOne = field.isAnnotationPresent(ManyToOne.class);
            boolean isOneToMany = field.isAnnotationPresent(OneToMany.class);
            if (!isManyToOne && !isOneToMany) {
                skalari.add(field);
            }
            if(isManyToOne && isOneToMany) {
                throw new RuntimeException("Polje ne moze u isto vreme predstavljati i jedan-vise i vise-jedan mapiranje: " + field.getName() + " u " + clazz.getName());
            }
            if (isManyToOne) {
                if(field.getType().isAnnotationPresent(SlozenKljuc.class)) {
                    throw new RuntimeException("Slozeni spoljni kljucevi nisu podrzani: " + field.getName() + " u " + clazz.getName());
                }
                manyToOnes.add(field);
                Class<?> tip = field.getType();
                if (!Entitet.class.isAssignableFrom(tip)) {
                    SimpleLogger.log(LogLevel.LOG_FATAL, "Greska pri kesiranju metapodataka: za domenski objekat spoljnji kljuc ne referencira domenski objekat");
                    throw new RuntimeException("Spoljnji kljuc od " + clazz.getName() + " referencira klasu koja ne predstavlja domensku klasu: " + tip.getName());
                }
            }
            if (isOneToMany) {
                oneToManys.add(field);
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType pt) {
                    Type elementType = pt.getActualTypeArguments()[0];
                    if (elementType instanceof Class<?> actualListType) {
                        if (!Entitet.class.isAssignableFrom(actualListType)) {
                            SimpleLogger.log(LogLevel.LOG_FATAL, "Greska pri kesiranju metapodataka: za " + clazz.getName() + "jedan-vise mapiranje ne referencira domenski objekat: " + actualListType.getName());
                            throw new RuntimeException("Greska pri kesiranju metapodataka: za " + clazz.getName() + "jedan-vise mapiranje ne referencira domenski objekat: " + actualListType.getName());
                        }
                        OneToMany otm = field.getAnnotation(OneToMany.class);
                        try {
                            Field ff = actualListType.getDeclaredField(otm.mappedBy());
                            if(!ff.isAnnotationPresent(ManyToOne.class)) {
                                throw new RuntimeException("One to many mappedBy polje nije ManyToOne: " + field.getName() + " u " + clazz.getName() );
                            }
                        }
                        catch(NoSuchFieldException ex) {
                            throw new RuntimeException("(ACTUALTYPE=" + actualListType.getSimpleName() + ")One to many mapiranje nema odgovarajuce obrnuto many to one mapiranje: " + field.getName() + " u " + clazz.getName() + "   " + ex );  
                        }
                        

                    }
                    else
                    {
                        throw new RuntimeException("nmp");
                    }
                }
                else
                {
                    throw new RuntimeException("Dodeljen ManyToOne tip nije validan ManyToOne tip (nije parametrizovana lista) ili je raw tip: " + field.getName() + " u " + clazz.getName() );
                }
            }

            if (field.isAnnotationPresent(PrimarniKljuc.class)) {
                Class<?> t = field.getType();
                if (!isWrapperType(t) && (!Entitet.class.isAssignableFrom(t) || !field.isAnnotationPresent(ManyToOne.class))) {
                    SimpleLogger.log(LogLevel.LOG_FATAL, "Greska pri kesiranju metapodataka za: " + clazz.getName() + " primarni kljuc mora biti ili primitivan ili entitet koji je takodje i u funkciji spoljnog kljuca: " + t.getName());
                    throw new RuntimeException("Greska pri kesiranju metapodataka za: " + clazz.getName() + " primarni kljuc mora biti ili primitivan ili entitet koji je takodje i u funkciji spoljnog kljuca: " + t.getName());
                }
                primarniKljucevi.add(field);
            }
            svaPolja.add(field);
        }

        if (primarniKljucevi.isEmpty()) {
            SimpleLogger.log(LogLevel.LOG_FATAL, "Greska pri kesiranju metapodataka: za domensku klasu nije definisan primarni kljuc");
            throw new RuntimeException("Nedefinisan primarni kljuc za " + clazz.getName());
        }

        SimpleLogger.log(LogLevel.LOG_INFO, "Uspesno kesirani metapodaci o " + clazz.getSimpleName() + "\n");
        return new EntityMetadata(imeTabele, svaPolja, primarniKljucevi, manyToOnes, oneToManys, skalari);
    }

    public static class EntityMetadata {

        public final String imeTabele;
        public final List<Field> svaPolja;
        public final List<Field> primarniKljucevi;
        public final List<Field> manyToOnes;
        public final List<Field> oneToManys;
        public final List<Field> skalari;

        public EntityMetadata(String imeTabele, List<Field> svaPolja, List<Field> primaryKeys, List<Field> manyToOnes, List<Field> oneToManys, List<Field> skalari) {
            this.imeTabele = imeTabele;
            this.svaPolja = svaPolja;
            this.primarniKljucevi = primaryKeys;
            this.manyToOnes = manyToOnes;
            this.oneToManys = oneToManys;
            this.skalari = skalari;
        }
    }
}
