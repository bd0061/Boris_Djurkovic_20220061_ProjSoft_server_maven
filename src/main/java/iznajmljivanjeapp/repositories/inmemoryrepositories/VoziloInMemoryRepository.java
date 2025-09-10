package iznajmljivanjeapp.repositories.inmemoryrepositories;


import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.enumeracije.InsertBehaviour;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;
import iznajmljivanjeapp.domain.Vozilo;
import iznajmljivanjeapp.repositories.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class VoziloInMemoryRepository implements Repository<Vozilo> {

    private static final Map<Integer, Vozilo> storage = new ConcurrentHashMap<>();
    private static int idSequence = 1;

    @Override
    public synchronized void kreiraj(Vozilo... entitet) throws Exception {
        for (Vozilo v : entitet) {
            if (v == null) {
                throw new Exception("null entitet");
            }
            if (!v.vrednosnaOgranicenja()) {
                throw new Exception("entitet ne zadovoljava vrednosna ogranicenja");
            }
            if (v.getId() == null) {
                v.setId(idSequence++);
            }
            storage.put(v.getId(), new Vozilo(
                    v.getId(),
                    v.getKlasa(),
                    v.getProizvodjac(),
                    v.getKupovnaCena(),
                    v.getGodiste(),
                    v.getImeModela(),
                    v.getKategorija(),
                    v.getCenaPoDanu()
            ));
            SimpleLogger.log(LogLevel.LOG_INFO, "Uspesno ubaceno vozilo " + v);
        }
    }

    @Override
    public void kreiraj(InsertBehaviour ib, Vozilo... entitet) throws Exception {
        kreiraj(entitet);
    }


    @Override
    public synchronized void promeni(Vozilo entitet) throws Exception {
        Integer id = entitet.getId();
        if (id == 0 || !storage.containsKey(id)) {
            throw new Exception("Vozilo not found for update");
        }
        Vozilo v = storage.get(id);
        if (entitet.getKlasa() != null) v.setKlasa(entitet.getKlasa());
        if (entitet.getProizvodjac() != null) v.setProizvodjac(entitet.getProizvodjac());
        if (entitet.getKupovnaCena() != null) v.setKupovnaCena(entitet.getKupovnaCena());
        if (entitet.getGodiste() != null) v.setGodiste(entitet.getGodiste());
        if (entitet.getImeModela() != null) v.setImeModela(entitet.getImeModela());
        if (entitet.getKategorija() != null) v.setKategorija(entitet.getKategorija());

        if (!v.vrednosnaOgranicenja()) {
            throw new Exception("Nezadovoljena vrednosna ogranicenja nakon promene");
        }
    }

    @Override
    public synchronized void promeni(Vozilo entitet1, Vozilo[] entitet2) throws Exception {
        throw new UnsupportedOperationException("nepotrebno za in memory");
    }

    @Override
    public synchronized void obrisi(Vozilo... entitet) throws Exception {
        for (Vozilo v : entitet) {
            if (v.getId() == 0 || !storage.containsKey(v.getId())) {
                throw new Exception("Vozilo not found for delete");
            }
            storage.remove(v.getId());
        }
    }

    @Override
    public void obrisiConditional(Object c)  throws Exception {

    }

    @Override
    public synchronized List<Vozilo> vratiListuSvi(List<KriterijumDescriptor> descriptors) {
        Predicate<Vozilo> pred = KriterijumDescriptor.buildPredicate(descriptors);
        return storage.values().stream()
                .filter(pred)
                .collect(Collectors.toList());
    }

    @Override
    public List<Vozilo> vratiListuSvi(KriterijumWrapper w) throws Exception {
        Predicate<Vozilo> pred = KriterijumDescriptor.buildPredicate(w.kds);
        return storage.values().stream()
                .filter(pred)
                .collect(Collectors.toList());

    }

    public static synchronized void clear() {
        storage.clear();
        idSequence = 1;
    }
}

