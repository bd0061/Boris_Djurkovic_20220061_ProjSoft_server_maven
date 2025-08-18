package iznajmljivanjeapp.repositories.inmemoryrepositories;

import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.enumeracije.InsertBehaviour;
import iznajmljivanjeapp.domain.Smena;
import iznajmljivanjeapp.domain.kljucevi.SmenaKljuc;
import iznajmljivanjeapp.repositories.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SmenaInMemoryRepository implements Repository<Smena> {

    private static final Map<SmenaKljuc, Smena> storage = new ConcurrentHashMap<>();

    @Override
    public synchronized void kreiraj(Smena... entiteti) throws Exception {
        for (Smena s : entiteti) {
            if (s == null || s.getZaposleni() == null || s.getTerminDezurstva() == null) throw new Exception("null entitet");
            if(s.getDatum() == null || s.getTerminDezurstva().getId() == null || s.getZaposleni().getId() == null) throw new Exception("Losa smena");
            if (!s.vrednosnaOgranicenja()) throw new Exception("Neispravna smena");
            SmenaKljuc key = new SmenaKljuc(
                    s.getDatum(),
                    s.getZaposleni().getId(),
                    s.getTerminDezurstva().getId()
            );
            if (storage.containsKey(key)) {
                throw new Exception("Smena sa tim ključem već postoji: " + key);
            }
            storage.put(key, deepCopy(s));
        }
    }

    @Override
    public void kreiraj(InsertBehaviour ib, Smena... entitet) throws Exception {
        kreiraj(entitet);
    }


    @Override
    public synchronized void promeni(Smena s) throws Exception {
        SmenaKljuc key = new SmenaKljuc(
                s.getDatum(),
                s.getZaposleni().getId(),
                s.getTerminDezurstva().getId()
        );
        if (!storage.containsKey(key)) {
            throw new Exception("Smena nije pronađena");
        }
        Smena orig = storage.get(key);
        if(s.isVanredan() != null) orig.setVanredan(s.isVanredan());
        if(s.getBrojSati() != null) orig.setBrojSati(s.getBrojSati());
        if(s.getFiksniBonus() != null) orig.setFiksniBonus(s.getFiksniBonus());

        if(!orig.vrednosnaOgranicenja()) {
            throw new Exception("Nezadovoljena vrednosna ogranicenja nakon promene");
        }
    }

    @Override
    public synchronized void promeni(Smena entitet1, Smena[] entitet2) throws Exception {
        for(Smena s : entitet2) {
            SmenaKljuc key = new SmenaKljuc(
                    s.getDatum(),
                    s.getZaposleni().getId(),
                    s.getTerminDezurstva().getId()
            );
            if (!storage.containsKey(key)) {
                throw new Exception("Smena nije pronađena");
            }
            Smena orig = storage.get(key);
            if(entitet1.isVanredan() != null) orig.setVanredan(entitet1.isVanredan());
            if(entitet1.getBrojSati() != null) orig.setBrojSati(entitet1.getBrojSati());
            if(entitet1.getFiksniBonus() != null) orig.setFiksniBonus(entitet1.getFiksniBonus());

            if(!orig.vrednosnaOgranicenja()) {
                throw new Exception("Nezadovoljena vrednosna ogranicenja nakon promene");
            }
        }
    }

    @Override
    public synchronized void obrisi(Smena... smene) throws Exception {
        for (Smena s : smene) {
            SmenaKljuc key = new SmenaKljuc(
                    s.getDatum(),
                    s.getZaposleni().getId(),
                    s.getTerminDezurstva().getId()
            );
            if(!storage.containsKey(key)) {
                throw new Exception("Nepostojeci entitet");
            }
            storage.remove(key);
        }

    }

    @Override
    public void obrisiConditional(Object c) throws Exception {

    }

    @Override
    public synchronized List<Smena> vratiListuSvi(List<KriterijumDescriptor> descriptors) throws Exception {
        Predicate<Smena> pred = KriterijumDescriptor.buildPredicate(descriptors);
        return storage.values().stream().filter(pred).map(SmenaInMemoryRepository::deepCopy).collect(Collectors.toList());
    }

    @Override
    public List<Smena> vratiListuSvi(KriterijumWrapper w) throws Exception {
        Predicate<Smena> pred = KriterijumDescriptor.buildPredicate(w.kds);
        return storage.values().stream().filter(pred).map(SmenaInMemoryRepository::deepCopy).collect(Collectors.toList());

    }

    public static synchronized void clear() {
        storage.clear();
    }

    private static Smena deepCopy(Smena s) {
        return new Smena(
                s.getDatum(),
                s.getZaposleni(),
                s.getTerminDezurstva(),
                s.isVanredan(),
                s.getBrojSati(),
                s.getFiksniBonus()
        );
    }
}
