/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package framework.orm;

import com.mysql.cj.log.Log;
import framework.DbEngine;
import framework.config.AppConfig;
import framework.model.FieldDescriptor;
import framework.model.KriterijumWrapper;
import framework.orm.anotacije.ImeKolone;
import framework.orm.anotacije.kljuc.ManyToOne;
import framework.orm.anotacije.kljuc.OneToMany;
import framework.orm.anotacije.vrednosnaogranicenja.NotNull;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;

import static framework.orm.EntityManager.vratiImePolja;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Djurkovic
 */
public final class QueryBuilder<T extends Entitet> {

    private final Class<T> target;
    private Map<Class<?>, Map<Object, Object>> identityCache = new HashMap<>();
    private Set<Object> inProgress = new HashSet<>();
    private AppConfig config;
    private char quote;
    public QueryBuilder(Class<T> target, AppConfig config) {
        this.target = target;
        this.config = config;
        this.quote = config.dbEngine == DbEngine.MYSQL ? '`' : '"';
    }


    
    public T buildObjectGraph(ResultSet rs, KriterijumWrapper w) throws Exception {
        return buildObjectGraph(target, rs, w);
    }

    private T buildObjectGraph(Class<T> clazz, ResultSet rs) throws Exception {
        return buildObjectGraph(clazz,rs, new KriterijumWrapper(List.of(),KriterijumWrapper.DepthLevel.FULL,List.of()));
    }



    // najvaznija funkcija koja zapravo predstavlja most izmedju domena relacija i objekata.
    // RADI NA NIVOU JEDNOG REDA: callee ovu funkciju poziva u petlji za svaki red resultseta
    // problem: graf objekata postaje jako brzo ogroman i jako kompleksan najvise zbog one to many mapiranja
    // moguce je i da sam kod mozda radi puno redundantnog posla, ali cini mi se da je logika uredu
    // veliki graf objekata predstavlja ogroman problem jer serijalizer koji treba da posalje objekte kroz mrezu ce
    // naici na stek overflow ne zbog beskonacne rekurzije vec zato sto ima previse objekata
    // svi serijalizeri(ili barem kryo i stdlib) funkcionisu rekurzivno, tako da dzabe gigabajti heapa kad je max razumna velicina steka ~8mb

    // obzervacija: moj builder je takodje rekurzivan i pravi kompletan objektni graf. zasto onda on ne dozivi stek overflow? da li kryo radi redundatne operacije?

    // kao resenje moze se specifirati "dubina" fetcha, pogledati kriterijumwrapper klasu za vise informacija, kao i bleklista polja koja ne zelimo da posecujemo
    // za neke specijalne situacije:
    // na primer, za neki slucaj koriscenja treba nam full fetch iznajmljivanja da bi dobili njegove stavke, ali nam ne treba full fetch stavki iznajmljivanja koje
    // ima vozilo na koje se odnosi neka stavka, pa cemo bleklistovati polje List<StavkaIznajmljivanja> od vozila

//    private T buildObjectGraph(Class<T> clazz, ResultSet rs, KriterijumWrapper.DepthLevel depthLevel) throws Exception {
//        return buildObjectGraph(clazz,rs,depthLevel);
//    }


    private T buildObjectGraph(Class<T> clazz, ResultSet rs, KriterijumWrapper w) throws Exception {

        Set<Field> blackListPolja = new HashSet<>();
        if(w.blacklist != null) {
            for(var fd : w.blacklist) {
                blackListPolja.add(fd.get());
            }
        }

        Set<Class<?>> blackListKlase = new HashSet<>();

        try {
            if(w.blacklistClass != null) {
                for(var b : w.blacklistClass) {
                    blackListKlase.add(Class.forName(b));
                }
            }
        }
        catch(Exception e) {
            SimpleLogger.log(LogLevel.LOG_WARN, "Greska pri formiranju blekliste, ignorisem: " + e);
        }


        var m = EntityCache.getMetadata(clazz);
        var rsMeta = rs.getMetaData();

        Set<String> columnNames = new HashSet<String>();
        int noCol = rsMeta.getColumnCount();
        for (int i = 1; i <= noCol; i++) {
            columnNames.add(rsMeta.getColumnLabel(i));
        }
        Object id = EntityManager.extractId(clazz, rs, columnNames);

        Map<Object, Object> classCache = identityCache.computeIfAbsent(clazz, _ -> new HashMap<>());
        Object instance = classCache.get(id);
        if (instance == null) {
            Constructor<?> ctor = clazz.getDeclaredConstructor(); //svi enttieti moraju da imaju prazan konstruktor
            ctor.setAccessible(true);
            instance = ctor.newInstance();
            classCache.put(id, instance);

        }
        // ako se u grafu ponovo referencira neki objekat cije obradjivanje nije zavrseno (dakle, ciklus), doslo bi do beskonacne rekurzije
        // koja garantuje stek overflow. zato pratimo koji se objekti trenutno popunjavaju.
        // ova pojava ce se uvek desiti kod bidirekcionog mapiranja
        // (iznajmljivanje ima listu stavki, program krece da popunjava jednu stavku, ta jedna stavka ima referencu na iznajmljivanje koje se popunjava)

        if (inProgress.contains(instance)) {
            return (T) instance;
        }
        inProgress.add(instance);
        //popuni skalarne vrednosti polja root objekta
        for (Field f : m.skalari) {
            String imeKolone = m.imeTabele + "_" + EntityManager.vratiImePolja(f);
            if (!columnNames.contains(imeKolone)) {
                throw new Exception("Neocekivano ime kolone: " + imeKolone);
            }
            Object val = EntityManager.vratiVrednostPolja(f.getType(), rs, imeKolone);
            f.setAccessible(true);
            f.set(instance, val);
        }

        //FORWARD PASS - idemo kroz sve many to one reference (spoljni kljucevi), popunjavamo ih rekurzivno, i stavljamo nazadnu referencu na nas root objekat
        if(w.depthLevel != KriterijumWrapper.DepthLevel.NONE) {
            for (Field mof : m.manyToOnes) {
                if(blackListKlase.contains(mof.getType())) {
                    continue;
                }
                Class<?> tip = mof.getType();

                // val - instanca objekta ka kome root objekat ima spoljnji kljuc, popunjavamo je sa svim njenim asocijacijama - rekurzija :)
                Object val = buildObjectGraph((Class<T>) tip, rs,w);

                // postavljamo je kao clan naseg root objekta
                mof.setAccessible(true);
                mof.set(instance, val);
                var m2 = EntityCache.getMetadata((Class<T>)tip);

                // uzimamo klasu objekta ka kome root objekat ima spoljnji kljuc, ona ce sadrzati OneToMany List<rootObjekat> kao nazadnu referencu
                // naci cemo je i popuniti sa trenutnom instancom kako bismo upotpunili bidirekciono mapiranje (ako postoji, nije obavezno)
                for (Field omf : m2.oneToManys) {
                    ParameterizedType pt = (ParameterizedType) omf.getGenericType(); // omf = List<rootCLass>
                    Class<?> childType = (Class<?>) pt.getActualTypeArguments()[0]; // childType = rootClass

                    //naci cemo odgovarajuce List<rootObjekat> polje tako sto ce childType biti jednak rootClass, a mappedBy anotacija ce biti jednaka
                    //imenu polja reference u root klasi
                    //sada koristimo getName a ne vratiImePolja jer nas intersuje referenca u aplikaciji a ne ime spoljnog kljuca u bazi
                    if (!clazz.equals(childType) || !omf.getAnnotation(OneToMany.class).mappedBy().equals(mof.getName())) {
                        continue;
                    }
                    //nakon ovog checka nalazimo se na polju koje predstavlja Listu objekata tipa root, to jest referenca unazad
                    //ako mapiranje nije bidirekciono do ovog dela koda se nece uopste doci sto ne predstavlja problem
                    omf.setAccessible(true);
                    List<Object> children = (List<Object>) omf.get(val); //uzimamo referencu unazad

                    if (children == null) { //prva nazad referenca, inicijalizujemo listu
                        children = new ArrayList<>();
                        omf.set(val, children);
                    }

                    if (!children.contains(instance)) { //dodajemo nazad referencu ako vec nije prisutna
                        children.add(instance);
                    }

                }
            }
        }
        //BACKWARD PASS - idemo kroz sve one to many nazadne reference, popunjavamo jedno dete, opet naravno rekurzivno
        //(ostala deca ce biti popunjena ako ih ima u drugim redovima, setimo se da ova funkcija radi sa jednim redom!),
        // proveravamo da li dete ime naprednu referencu ka root objektu, ako ima, ubacujemo ga u nasu listu nazadnih referennci

        // primetimo da nacin na koji su ovi prolazi implementirani znaci
        // da ukoliko nas ne zanimaju nazadne reference da mozemo da imamo unidirekciono many to one mapiranje
        // medjutim, za unidirekciono one to many bila bi potrebna dodatna logika da bi orm znao kako da interpretira spoljni kljuc u child klasi 
        // i popuni graf objekata i u vreme pisanje komentara nije podrzano


        //ukoliko nas ne interesuju "stavke" koje se vezuju za objekat, na primer kada prikazujemo listu svih iznajmljivanja,
        // nema razloga da fetchujemo jako veliki broj stavki sa njihovim kompleksnim vezama, onda skipujemo ovaj korak
        if(w.depthLevel == KriterijumWrapper.DepthLevel.FULL) {
            for (Field omf : m.oneToManys) {
                if(blackListPolja.contains(omf)) {
                    continue;
                }
                ParameterizedType pt = (ParameterizedType) omf.getGenericType();
                Class<?> childType = (Class<?>) pt.getActualTypeArguments()[0];
                Object child = buildObjectGraph((Class<T>) childType, rs,w);

                var m2 = EntityCache.getMetadata((Class<T>)childType);
                boolean referencesUs = false;

                //bez manytoone na drugoj strani(dakle,unidirekciono mapiranje), ne znamo sa kojim vrednostima da popunimo dete nase child liste!
                //moguci fix: trazimo po svim poljima i pauziramo kad naidjemo na polje ciji je tip isti kao tip liste u nasem root objektu
                //ali sta ako "druga strana" ima vise polja tog tipa? kako ovaj slucaj resavaju pravi ORM-ovi? neka vrsta bidirekcionog mapiranja je neophodna
                for (Field mof : m2.manyToOnes) {
                    if (mof.getType().equals(clazz) && mof.getName().equals(omf.getAnnotation(OneToMany.class).mappedBy())) {
                        referencesUs = true;
                        break;
                    }
                }
                if (!referencesUs) {
                    throw new Exception("OneToMany mapiranje mora biti bidirekciono: " + clazz.getName() + " - " + omf.getName());
                }
                omf.setAccessible(true);
                List<Object> list = (List<Object>) omf.get(instance);
                if (list == null) {
                    list = new ArrayList<>();
                    omf.set(instance, list);
                }
                if (!list.contains(child)) {
                    list.add(child);
                }

            }
        }
        inProgress.remove(instance);
        return (T) instance;
    }

    public String generateJoinString(KriterijumWrapper w) {
        Set<Class<T>> poseceneTabele = new HashSet<>();
        StringBuilder sp = new StringBuilder();
        StringBuilder fp = new StringBuilder();
        generateJoinString(target, sp, fp, poseceneTabele, w);
        sp.delete(sp.length() - 2, sp.length());
        SimpleLogger.log(LogLevel.LOG_INFO, "Konacan join string:\n" + sp.toString() + "\n" + fp.toString());


        return sp.toString() + "\n" + fp.toString();
    }


    private void generateJoinString(Class<T> root, StringBuilder select, StringBuilder from, Set<Class<T>> poseceneTabele, KriterijumWrapper w) {
        poseceneTabele.add(root);
        EntityCache.EntityMetadata metadata = EntityCache.getMetadata(root);
        List<Field> polja = metadata.svaPolja;
        List<Class<?>> nextLvl = new ArrayList<>();
        String rtbl = metadata.imeTabele;

        Set<Class<?>> blackList = new HashSet<>();

        try {
            if(w.blacklistClass != null) {
                for(var b : w.blacklistClass) {
                    blackList.add(Class.forName(b));
                }
            }
        }
        catch(Exception e) {
            SimpleLogger.log(LogLevel.LOG_WARN, "Greska pri formiranju blekliste, ignorisem: " + e);
        }




        if (from.isEmpty()) {
            from.append(" FROM ").append(quote).append(metadata.imeTabele).append(quote).append(" \n");
        }
        if (select.isEmpty()) {
            select.append("SELECT ");
            for (Field f : metadata.svaPolja) {
                String ime;
                if (f.isAnnotationPresent(OneToMany.class)) {
                    continue;
                }
                if (f.isAnnotationPresent(ImeKolone.class)) {
                    ime = f.getAnnotation(ImeKolone.class).value();
                } else if (f.isAnnotationPresent(ManyToOne.class)) {
                    if(blackList.contains(f.getType())) {
                        continue;
                    }
                    ime = f.getAnnotation(ManyToOne.class).joinColumn();
                } else {
                    ime = f.getName();
                }
                select.append(quote).append(metadata.imeTabele).append(quote).append(".").append(ime).append(" AS ").append(quote).append(metadata.imeTabele).append("_").append(ime).append(quote).append(",");

            }
            select.append("\n");
        }
        for (Field f : polja) {

            if (w.depthLevel != KriterijumWrapper.DepthLevel.NONE && f.isAnnotationPresent(ManyToOne.class)) {
                if(blackList.contains(f.getType())) {
                    continue;
                }
                if (poseceneTabele.contains(f.getType())) {
                    continue;
                }
                poseceneTabele.add((Class<T>)f.getType());
                nextLvl.add(f.getType());
                ManyToOne ano = f.getAnnotation(ManyToOne.class);
                var m = EntityCache.getMetadata((Class<T>)f.getType());
                String tbl = m.imeTabele;
                from.append("LEFT JOIN ").append(quote).append(tbl).append(quote).append(" ON ")
                        .append(quote).append(tbl).append(quote).append(".").append(quote).append(EntityManager.vratiImePolja(m.primarniKljucevi.getFirst())).append(quote)
                        .append("=").append(quote).append(rtbl).append(quote).append(".").append(quote).append(ano.joinColumn()).append(quote).append(" \n");
            } else if ( w.depthLevel == KriterijumWrapper.DepthLevel.FULL && f.isAnnotationPresent(OneToMany.class)) {
                Type genericType = f.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genericType;
                    Type[] typeArgs = pt.getActualTypeArguments();
                    if (typeArgs.length == 1 && typeArgs[0] instanceof Class) {
                        Class<?> elementType = (Class<?>) typeArgs[0];
                        if (blackList.contains(elementType)) {
                            continue;
                        }
                    }
                }
                Class<T> y = (Class<T>) (((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0]);
                if (poseceneTabele.contains(y)) {
                    continue;
                }
                poseceneTabele.add(y);
                nextLvl.add(y);
                OneToMany ano = f.getAnnotation(OneToMany.class);
                var m = EntityCache.getMetadata((Class<T>)y);
                String tbl = m.imeTabele;
                try {
                    Field f2 = y.getDeclaredField(ano.mappedBy());
                    if (!f2.isAnnotationPresent(ManyToOne.class)) {
                        throw new Exception("Strane mapiranja se ne poklapaju");
                    }
                    String fk = f2.getAnnotation(ManyToOne.class).joinColumn();
                    from.append("LEFT JOIN ").append(quote).append(tbl).append(quote).append(" ON ")
                            .append(quote).append(tbl).append(quote).append(".").append(quote).append(fk).append(quote)
                            .append("=").append(quote).append(rtbl).append(quote).append(".").append(quote).append(EntityManager.vratiImePolja(metadata.primarniKljucevi.getFirst())).append(quote).append(" \n");

                } catch (Exception ex) {
                    throw new RuntimeException("Lose definisiano mapiranje za OneToMany referencu " + y.getName() + " klase " + root.getName() + ": " + ex.getLocalizedMessage());
                }
            }
        }
        for (var c : nextLvl) {
            var m = EntityCache.getMetadata((Class<T>)c);
            for (Field f : m.svaPolja) {
                if (f.isAnnotationPresent(OneToMany.class) || blackList.contains(f.getType())) {
                    continue;
                }
                String ime = vratiImePolja(f);
                select.append(quote).append(m.imeTabele).append(quote).append(".").append(ime).append(" AS ").append(quote).append(m.imeTabele).append("_").append(ime).append(quote).append(",");
            }
            select.append("\n");
            generateJoinString((Class<T>)c, select, from, poseceneTabele,w);
        }

    }
}
