package iznajmljivanjeapp.repositories.inmemoryrepositories;

import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.enumeracije.InsertBehaviour;
import iznajmljivanjeapp.domain.StavkaIznajmljivanja;
import iznajmljivanjeapp.domain.kljucevi.StavkaIznajmljivanjaKljuc;
import iznajmljivanjeapp.repositories.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StavkaIznajmljivanjaInMemoryRepository implements Repository<StavkaIznajmljivanja> {

    private static final Map<StavkaIznajmljivanjaKljuc, StavkaIznajmljivanja> storage = new ConcurrentHashMap<>();

    @Override
    public synchronized void kreiraj(StavkaIznajmljivanja... entiteti) throws Exception {
        for (StavkaIznajmljivanja si : entiteti) {
            if (si == null || si.getIznajmljivanje() == null) throw new Exception("null entitet");
            if (!si.vrednosnaOgranicenja()) throw new Exception("Neispravna stavka iznajmljivanja");

            StavkaIznajmljivanjaKljuc key = new StavkaIznajmljivanjaKljuc(
                    si.getIznajmljivanje().getId(),
                    si.getRb()
            );
            if (storage.containsKey(key)) {
                throw new Exception("Stavka sa tim ključem već postoji: " + key);
            }
            storage.put(key, deepCopy(si));
        }
    }

    @Override
    public void kreiraj(InsertBehaviour ib, StavkaIznajmljivanja... entitet) throws Exception {
        kreiraj(entitet);
    }


    @Override
    public synchronized void promeni(StavkaIznajmljivanja si) throws Exception {
        StavkaIznajmljivanjaKljuc key = new StavkaIznajmljivanjaKljuc(
                si.getIznajmljivanje().getId(),
                si.getRb()
        );
        if (!storage.containsKey(key)) {
            throw new Exception("Stavka nije pronađena");
        }
        StavkaIznajmljivanja orig = storage.get(key);
        if (si.getDatumPocetka() != null) orig.setDatumPocetka(si.getDatumPocetka());
        if (si.getDatumZavrsetka() != null) orig.setDatumZavrsetka(si.getDatumZavrsetka());
        if (si.getVozilo() != null)        orig.setVozilo(si.getVozilo());

        if (!orig.vrednosnaOgranicenja()) {
            throw new Exception("Nezadovoljena vremenska ograničenja nakon promene");
        }
    }

    @Override
    public void promeni(StavkaIznajmljivanja a, StavkaIznajmljivanja[] b) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void obrisi(StavkaIznajmljivanja... entiteti) throws Exception {
        for (StavkaIznajmljivanja si : entiteti) {
            StavkaIznajmljivanjaKljuc key = new StavkaIznajmljivanjaKljuc(
                    si.getIznajmljivanje().getId(),
                    si.getRb()
            );
            if (!storage.containsKey(key)) {
                throw new Exception("Stavka nije pronađena");
            }
            storage.remove(key);
        }
    }

    @Override
    public void obrisiConditional(Object c) throws Exception {

    }

    @Override
    public synchronized List<StavkaIznajmljivanja> vratiListuSvi(List<KriterijumDescriptor> kriterijumi) {
        Predicate<StavkaIznajmljivanja> pred = KriterijumDescriptor.buildPredicate(kriterijumi);
        return storage.values().stream()
                .filter(pred)
                .map(StavkaIznajmljivanjaInMemoryRepository::deepCopy)
                .collect(Collectors.toList());
    }

    @Override
    public List<StavkaIznajmljivanja> vratiListuSvi(KriterijumWrapper w) throws Exception {
        Predicate<StavkaIznajmljivanja> pred = KriterijumDescriptor.buildPredicate(w.kds);
        return storage.values().stream()
                .filter(pred)
                .map(StavkaIznajmljivanjaInMemoryRepository::deepCopy)
                .collect(Collectors.toList());

    }

    public static synchronized void clear() {
        storage.clear();
    }

    private static StavkaIznajmljivanja deepCopy(StavkaIznajmljivanja si) {
        StavkaIznajmljivanja copy = new StavkaIznajmljivanja(
                si.getIznajmljivanje(),
                si.getRb(),
                si.getDatumPocetka(),
                si.getDatumZavrsetka(),
                si.getVozilo()
        );
        return copy;
    }
}
