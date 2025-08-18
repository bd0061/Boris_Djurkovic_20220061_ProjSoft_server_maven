package iznajmljivanjeapp.repositories.inmemoryrepositories;

import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.enumeracije.InsertBehaviour;
import iznajmljivanjeapp.domain.Iznajmljivanje;
import iznajmljivanjeapp.repositories.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class IznajmljivanjeInMemoryRepository implements Repository<Iznajmljivanje> {

    private static final Map<Integer, Iznajmljivanje> storage = new ConcurrentHashMap<>();
    private static int idSequence = 1;

    @Override
    public synchronized void kreiraj(Iznajmljivanje... entiteti) throws Exception {
        for (Iznajmljivanje iz : entiteti) {
            if (iz == null) throw new Exception("null entitet");
            if (!iz.vrednosnaOgranicenja()) throw new Exception("Neispravno iznajmljivanje");
            if (iz.getId() == null) iz.setId(idSequence++);
            storage.put(iz.getId(), deepCopy(iz));
        }
    }

    @Override
    public void kreiraj(InsertBehaviour ib, Iznajmljivanje... entitet) throws Exception {
        kreiraj(entitet);
    }


    @Override
    public synchronized void promeni(Iznajmljivanje iz) throws Exception {
        Integer id = iz.getId();
        if (id == null || !storage.containsKey(id)) {
            throw new Exception("Iznajmljivanje nije pronađeno");
        }
        Iznajmljivanje orig = storage.get(id);
        if (iz.getDatumSklapanja() != null) orig.setDatumSklapanja(iz.getDatumSklapanja());
        if (iz.getUkupanIznos() != null)     orig.setUkupanIznos(iz.getUkupanIznos());
        if (iz.getZaposleni() != null)    orig.setZaposleni(iz.getZaposleni());
        if (iz.getVozac() != null)        orig.setVozac(iz.getVozac());

        if (!orig.vrednosnaOgranicenja()) {
            throw new Exception("Nezadovoljena vrednosna ograničenja nakon promene");
        }
    }

    @Override
    public void promeni(Iznajmljivanje a, Iznajmljivanje[] b) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void obrisi(Iznajmljivanje... entiteti) throws Exception {
        for (Iznajmljivanje iz : entiteti) {
            if (iz.getId() == null || !storage.containsKey(iz.getId())) {
                throw new Exception("Iznajmljivanje nije pronađeno");
            }
            storage.remove(iz.getId());
        }
    }

    @Override
    public void obrisiConditional(Object c) throws Exception {

    }

    @Override
    public synchronized List<Iznajmljivanje> vratiListuSvi(List<KriterijumDescriptor> kriterijumi) {
        Predicate<Iznajmljivanje> pred = KriterijumDescriptor.buildPredicate(kriterijumi);
        return storage.values().stream()
                .filter(pred)
                .map(IznajmljivanjeInMemoryRepository::deepCopy)
                .collect(Collectors.toList());
    }

    @Override
    public List<Iznajmljivanje> vratiListuSvi(KriterijumWrapper w) throws Exception {
        throw new UnsupportedOperationException();

    }

    public static synchronized void clear() {
        storage.clear();
        idSequence = 1;
    }

    private static Iznajmljivanje deepCopy(Iznajmljivanje iz) {
        Iznajmljivanje copy = new Iznajmljivanje(
                iz.getId(),
                iz.getDatumSklapanja(),
                iz.getUkupanIznos(),
                iz.getZaposleni(),
                iz.getVozac()
        );
        // napomena: lista stavki ostaje prazna ovde; unošenje stavki radi Stavka repo
        return copy;
    }
}
