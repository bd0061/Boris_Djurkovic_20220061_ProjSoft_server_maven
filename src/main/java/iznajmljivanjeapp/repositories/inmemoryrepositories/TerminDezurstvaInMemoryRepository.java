package iznajmljivanjeapp.repositories.inmemoryrepositories;

import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.enumeracije.InsertBehaviour;
import iznajmljivanjeapp.domain.TerminDezurstva;
import iznajmljivanjeapp.repositories.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TerminDezurstvaInMemoryRepository implements Repository<TerminDezurstva> {

    private static final Map<Integer, TerminDezurstva> storage = new ConcurrentHashMap<>();
    private static int idSequence = 1;

    @Override
    public synchronized void kreiraj(TerminDezurstva... entiteti) throws Exception {
        for (TerminDezurstva td : entiteti) {
            if (td == null) throw new Exception("null entitet");
            if (!td.vrednosnaOgranicenja()) throw new Exception("Neispravan termin dezurstva");
            if (td.getId() == null) td.setId(idSequence++);
            storage.put(td.getId(), deepCopy(td));
        }
    }

    @Override
    public void kreiraj(InsertBehaviour ib, TerminDezurstva... entitet) throws Exception {
        kreiraj(entitet);
    }


    @Override
    public synchronized void promeni(TerminDezurstva entitet) throws Exception {
        Integer id = entitet.getId();
        if (id == null || !storage.containsKey(id)) {
            throw new Exception("Termin dezurstva nije pronadjen");
        }
        TerminDezurstva original = storage.get(id);
        if (entitet.getNapomena() != null) original.setNapomena(entitet.getNapomena());
        if (entitet.getTipTermina() != null) original.setTipTermina(entitet.getTipTermina());
        if (!original.vrednosnaOgranicenja()) {
            throw new Exception("Nezadovoljena vrednosna ogranicenja nakon promene");
        }
    }

    @Override
    public synchronized void promeni(TerminDezurstva entitet1, TerminDezurstva[] entitet2) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void obrisi(TerminDezurstva... entitet) throws Exception {
        for (TerminDezurstva td : entitet) {
            if (td.getId() == null || !storage.containsKey(td.getId())) {
                throw new Exception("Termin dezurstva nije pronadjen");
            }
            storage.remove(td.getId());
        }
    }

    @Override
    public void obrisiConditional(Object c) throws Exception {

    }

    @Override
    public synchronized List<TerminDezurstva> vratiListuSvi(List<KriterijumDescriptor> descriptors) throws Exception {
        Predicate<TerminDezurstva> pred = KriterijumDescriptor.buildPredicate(descriptors);
        return storage.values().stream()
                .filter(pred)
                .map(TerminDezurstvaInMemoryRepository::deepCopy)
                .collect(Collectors.toList());
    }

    @Override
    public List<TerminDezurstva> vratiListuSvi(KriterijumWrapper w) throws Exception {
        Predicate<TerminDezurstva> pred = KriterijumDescriptor.buildPredicate(w.kds);
        return storage.values().stream()
                .filter(pred)
                .map(TerminDezurstvaInMemoryRepository::deepCopy)
                .collect(Collectors.toList());

    }

    private static TerminDezurstva deepCopy(TerminDezurstva td) {
        TerminDezurstva copy = new TerminDezurstva(td.getId(), td.getNapomena(),td.getTipTermina());
        // Deep copy smene list if present
        if (td.getSmene() != null) {
            copy.setSmene(new ArrayList<>(td.getSmene()));
        }
        return copy;
    }
    public static synchronized void clear() {
        storage.clear();
        idSequence = 1;
    }
}
