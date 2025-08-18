package iznajmljivanjeapp.repositories.inmemoryrepositories;

import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.enumeracije.InsertBehaviour;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;
import iznajmljivanjeapp.domain.Dozvola;
import iznajmljivanjeapp.repositories.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DozvolaInMemoryRepository implements Repository<Dozvola> {

    private static final Map<Integer, Dozvola> storage = new ConcurrentHashMap<>();
    private static int idSequence = 1;

    @Override
    public synchronized void kreiraj(Dozvola... entitet) throws Exception {
        for (Dozvola d : entitet) {
            if(d == null) {
                throw new Exception("null entitet");
            }
            if(!d.vrednosnaOgranicenja()) {
                throw new Exception("entitet ne zadovoljava vrednosna ogranicenja");
            }
            if (d.getId() == null) {
                d.setId(idSequence++);
            }
            storage.put(d.getId(), new Dozvola(d.getId(), d.getKategorija()));
            SimpleLogger.log(LogLevel.LOG_INFO, "Uspesno ubacena dozvola " + d);
        }
    }

    @Override
    public void kreiraj(InsertBehaviour ib, Dozvola... entitet) throws Exception {
        kreiraj(entitet);
    }


    //since changes can be partial we identify object being changed by id then set the non null attributes of entitet to tis attributes then check for constraints
    @Override
    public synchronized void promeni(Dozvola entitet) throws Exception {
        Integer id = entitet.getId();
        if (id == null || !storage.containsKey(id)) {
            throw new Exception("Dozvola not found for update");
        }
        Dozvola d = storage.get(id);
        if(entitet.getKategorija() != null)
            d.setKategorija(entitet.getKategorija());
        if(!d.vrednosnaOgranicenja()) {
            throw new Exception("Nezadovoljena vrednosna ogranicenja nakon promene");
        }
    }


    @Override
    public synchronized void promeni(Dozvola entitet1, Dozvola[] entitet2) throws Exception {
        throw new UnsupportedOperationException("nepotrebno za in memory zzz");
    }

    @Override
    public synchronized void obrisi(Dozvola... entitet) throws Exception {
        for (Dozvola d : entitet) {
            if (d.getId() == null || !storage.containsKey(d.getId())) {
                throw new Exception("Dozvola not found for delete");
            }
            storage.remove(d.getId());
        }
    }

    @Override
    public void obrisiConditional(Object c) throws Exception {

    }

    @Override
    public synchronized List<Dozvola> vratiListuSvi(List<KriterijumDescriptor> descriptors) {
        Predicate<Dozvola> pred = KriterijumDescriptor.buildPredicate(descriptors);
        return storage.values().stream()
                .filter(pred)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dozvola> vratiListuSvi(KriterijumWrapper w) throws Exception {
        Predicate<Dozvola> pred = KriterijumDescriptor.buildPredicate(w.kds);
        return storage.values().stream()
                .filter(pred)
                .collect(Collectors.toList());
    }

    public static synchronized void clear() {
        storage.clear();
        idSequence = 1;
    }
}
