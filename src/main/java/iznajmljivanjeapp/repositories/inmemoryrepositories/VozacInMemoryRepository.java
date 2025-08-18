package iznajmljivanjeapp.repositories.inmemoryrepositories;

import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.enumeracije.InsertBehaviour;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;
import iznajmljivanjeapp.domain.Dozvola;
import iznajmljivanjeapp.domain.Vozac;
import iznajmljivanjeapp.repositories.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class VozacInMemoryRepository implements Repository<Vozac> {
    private static final Map<Integer, Vozac> storage = new ConcurrentHashMap<>();
    private static int idSequence = 1;

    @Override
    public synchronized void kreiraj(Vozac... entitet) throws Exception {
        for (Vozac v : entitet) {
            if(v == null) {
                throw new Exception("null entitet");
            }
            if(!v.vrednosnaOgranicenja()) {
                throw new Exception("entitet ne zadovoljava vrednosna ogranicenja");
            }
            if (v.getId() == null) {
                v.setId(idSequence++);
            }
            storage.put(v.getId(), deepCopy(v));
            SimpleLogger.log(LogLevel.LOG_INFO, "Uspesno ubacen vozac " + v);
        }
    }

    @Override
    public void kreiraj(InsertBehaviour ib, Vozac... entitet) throws Exception {
        kreiraj(entitet);
    }


    @Override
    public synchronized void promeni(Vozac entitet) throws Exception {
        Integer id = entitet.getId();
        if (id == null || !storage.containsKey(id)) {
            throw new Exception("Vozac nije pronadjen");
        }
        Vozac vozac = storage.get(id);
        if(entitet.getIme() != null) vozac.setIme(entitet.getIme());
        if(entitet.getPrezime() != null) vozac.setPrezime(entitet.getPrezime());
        if(entitet.getEmail() != null) vozac.setEmail(entitet.getEmail());
        if(entitet.getDozvola() != null) vozac.setDozvola(new Dozvola(entitet.getDozvola().getId(),entitet.getDozvola().getKategorija()));
        if (!vozac.vrednosnaOgranicenja()) {
            throw new Exception("Nezadovoljena vrednosna ogranicenja nakon promene");
        }
    }

    @Override
    public synchronized void promeni(Vozac entitet1, Vozac[] entitet2) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void obrisi(Vozac... entitet) throws Exception {
        for (Vozac v : entitet) {
            if (v.getId() == null || !storage.containsKey(v.getId())) {
                throw new Exception("Vozac nije pronadjen");
            }
            storage.remove(v.getId());
        }
    }

    @Override
    public void obrisiConditional(Object c) throws Exception {

    }

    @Override
    public synchronized List<Vozac> vratiListuSvi(List<KriterijumDescriptor> descriptors) throws Exception {
        Predicate<Vozac> pred = KriterijumDescriptor.buildPredicate(descriptors);
        return storage.values().stream()
                .filter(pred)
                .map(VozacInMemoryRepository::deepCopy)
                .collect(Collectors.toList());
    }

    @Override
    public List<Vozac> vratiListuSvi(KriterijumWrapper w) throws Exception {
        Predicate<Vozac> pred = KriterijumDescriptor.buildPredicate(w.kds);
        return storage.values().stream()
                .filter(pred)
                .map(VozacInMemoryRepository::deepCopy)
                .collect(Collectors.toList());

    }


    public static synchronized void clear() {
        storage.clear();
        idSequence = 1;
    }
    private static Vozac deepCopy(Vozac v) {
        return new Vozac(v.getId(),v.getIme(),v.getPrezime(),v.getEmail(),v.getDozvola());
    }


}
