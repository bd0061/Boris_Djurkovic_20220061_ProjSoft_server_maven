package iznajmljivanjeapp.repositories.inmemoryrepositories;


import framework.model.KriterijumWrapper;
import framework.model.enumeracije.InsertBehaviour;
import iznajmljivanjeapp.domain.Zaposleni;
import framework.model.KriterijumDescriptor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ZaposleniInMemoryRepository implements iznajmljivanjeapp.repositories.Repository<Zaposleni> {

    private static final Map<Integer, Zaposleni> storage = new ConcurrentHashMap<>();
    private static int idSequence = 1;

    @Override
    public synchronized void kreiraj(Zaposleni... entiteti) throws Exception {
        for (Zaposleni z : entiteti) {
            if (z == null) throw new Exception("null entitet");
            if (!z.vrednosnaOgranicenja()) throw new Exception("Neispravan zaposleni");
            if (z.getId() == null) z.setId(idSequence++);
            storage.put(z.getId(), deepCopy(z));
        }
    }

    @Override
    public void kreiraj(InsertBehaviour ib, Zaposleni... entitet) throws Exception {
        kreiraj(entitet);
    }


    @Override
    public synchronized void promeni(Zaposleni entitet) throws Exception {
        Integer id = entitet.getId();
        if (id == null || !storage.containsKey(id)) {
            throw new Exception("Zaposleni nije pronadjen");
        }
        Zaposleni original = storage.get(id);
        if (entitet.getIme() != null) original.setIme(entitet.getIme());
        if (entitet.getPrezime() != null) original.setPrezime(entitet.getPrezime());
        if (entitet.getEmail() != null) original.setEmail(entitet.getEmail());
        if (entitet.getSifra() != null) original.setSifra(entitet.getSifra());
        if (entitet.getSalt() != null) original.setSalt(entitet.getSalt());
        if (!original.vrednosnaOgranicenja()) {
            throw new Exception("Nezadovoljena vrednosna ogranicenja nakon promene");
        }
    }

    @Override
    public synchronized void promeni(Zaposleni entitet1, Zaposleni[] entitet2) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void obrisi(Zaposleni... entiteti) throws Exception {
        for (Zaposleni z : entiteti) {
            if (z.getId() == null || !storage.containsKey(z.getId())) {
                throw new Exception("Zaposleni nije pronadjen");
            }
            storage.remove(z.getId());
        }
    }

    @Override
    public void obrisiConditional(Object c) throws Exception {

    }

    @Override
    public synchronized List<Zaposleni> vratiListuSvi(List<KriterijumDescriptor> kriterijumi) {
        Predicate<Zaposleni> pred = KriterijumDescriptor.buildPredicate(kriterijumi);
        return storage.values().stream()
                .filter(pred)
                .map(ZaposleniInMemoryRepository::deepCopy)
                .collect(Collectors.toList());
    }

    @Override
    public List<Zaposleni> vratiListuSvi(KriterijumWrapper w) throws Exception {
        Predicate<Zaposleni> pred = KriterijumDescriptor.buildPredicate(w.kds);
        return storage.values().stream()
                .filter(pred)
                .map(ZaposleniInMemoryRepository::deepCopy)
                .collect(Collectors.toList());

    }

    public static synchronized void clear() {
        storage.clear();
        idSequence = 1;
    }

    private static Zaposleni deepCopy(Zaposleni z) {
        Zaposleni copy = new Zaposleni(z.getId(), z.getIme(), z.getPrezime(), z.getEmail(), z.getSifra(), z.getSalt(),z.isAdmin());
        // Deep copy smene list if present
        if (z.getSmene() != null) {
            copy.setSmene(new ArrayList<>(z.getSmene()));
        }
        return copy;
    }

    /**
     * Helper method to add smene to a stored Zaposleni.
     * Must be called after kreiraj() and setting the Zaposleni id.
     */
    public static synchronized void addSmene(Zaposleni zaposleni) throws Exception {
        if (zaposleni.getId() == null || !storage.containsKey(zaposleni.getId())) {
            throw new Exception("Zaposleni za dodavanje smena nije pronadjen");
        }
        storage.get(zaposleni.getId()).setSmene(zaposleni.getSmene());
    }
}

