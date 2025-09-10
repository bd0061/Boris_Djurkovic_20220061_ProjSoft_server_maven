package poslovnalogika;

import framework.model.KriterijumDescriptor;
import framework.model.network.NetworkRequest;
import framework.model.network.NetworkResponse;
import iznajmljivanjeapp.domain.Vozilo;
import iznajmljivanjeapp.domain.enumeracije.KategorijaEnum;
import iznajmljivanjeapp.repositories.inmemoryrepositories.VoziloInMemoryRepository;
import org.junit.*;

import java.util.List;

import static org.junit.Assert.*;

public class VoziloTest {

    @Before
    public void setUp() {
        VoziloInMemoryRepository.clear();
    }

    @Test
    public void testKreirajVoziloSuccess() throws Exception {
        Vozilo v = new Vozilo("Automobil", "Audi", 30000, 2021, "A6", KategorijaEnum.LUKSUZ,50);
        NetworkRequest req = new NetworkRequest("vozilo/kreiraj", v);

        NetworkResponse res = PoslovnaLogikaTestSuite.sendRequest(req);

        assertTrue(res.success);
        assertNotNull(res.payload);
        Vozilo created = (Vozilo) res.payload;
        assertEquals("Audi", created.getProizvodjac());
        assertNotNull(created.getId());
    }

    @Test
    public void testKreirajVoziloFailsDueToValidation() throws Exception {
        Vozilo v = new Vozilo("Motor", "Dacia", 12000, 2020, "X125", KategorijaEnum.BUDZET,50); // violates custom rule
        NetworkRequest req = new NetworkRequest("vozilo/kreiraj", v);

        NetworkResponse res = PoslovnaLogikaTestSuite.sendRequest(req);

        assertFalse(res.success);
    }

    @Test
    public void testObrisiVoziloSuccess() throws Exception {
        Vozilo v = new Vozilo("Automobil", "Fiat", 15000, 2020, "Punto", KategorijaEnum.BUDZET,50);
        NetworkRequest createReq = new NetworkRequest("vozilo/kreiraj", v);
        Vozilo created = (Vozilo) PoslovnaLogikaTestSuite.sendRequest(createReq).payload;

        NetworkRequest deleteReq = new NetworkRequest("vozilo/obrisi", new Vozilo(created.getId()));
        NetworkResponse deleteRes = PoslovnaLogikaTestSuite.sendRequest(deleteReq);

        assertTrue(deleteRes.success);
    }

    @Test
    public void testObrisiVoziloFailsWhenNotFound() throws Exception {
        Vozilo notExisting = new Vozilo(999);
        NetworkRequest req = new NetworkRequest("vozilo/obrisi", notExisting);

        NetworkResponse res = PoslovnaLogikaTestSuite.sendRequest(req);

        assertFalse(res.success);
    }

    @Test
    public void testPromeniVozilo() throws Exception {
        Vozilo v = new Vozilo("Minibus", "Mercedes", 50000, 2023, "Sprinter", KategorijaEnum.SREDNJA,50);
        NetworkRequest createReq = new NetworkRequest("vozilo/kreiraj", v);
        Vozilo created = (Vozilo) PoslovnaLogikaTestSuite.sendRequest(createReq).payload;

        Vozilo updated = new Vozilo(created.getId());
        updated.setKupovnaCena(45000.0);

        NetworkRequest updateReq = new NetworkRequest("vozilo/promeni", updated);
        NetworkResponse updateRes = PoslovnaLogikaTestSuite.sendRequest(updateReq);

        assertTrue(updateRes.success);
    }

    @Test
    public void testVratiSvaVozilaBezKriterijuma() throws Exception {
        Vozilo v1 = new Vozilo("Automobil", "Audi", 30000, 2021, "A6", KategorijaEnum.LUKSUZ,50);
        Vozilo v2 = new Vozilo("Automobil", "Fiat", 10000, 2020, "Punto", KategorijaEnum.BUDZET,50);

        PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("vozilo/kreiraj", v1));
        PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("vozilo/kreiraj", v2));

        NetworkRequest req = new NetworkRequest("vozilo/vrati_sve", null);
        NetworkResponse res = PoslovnaLogikaTestSuite.sendRequest(req);

        assertTrue(res.success);
        List<?> list = (List<?>) res.payload;
        assertEquals(2, list.size());
    }

    @Test
    public void testVratiVozilaSaKriterijumima() throws Exception {
        Vozilo v1 = new Vozilo("Automobil", "Fiat", 10000, 2020, "Punto", KategorijaEnum.BUDZET,50);
        Vozilo v2 = new Vozilo("Automobil", "Audi", 20000, 2022, "A4", KategorijaEnum.LUKSUZ,50);

        PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("vozilo/kreiraj", v1));
        PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("vozilo/kreiraj", v2));

        KriterijumDescriptor byProizvodjac = new KriterijumDescriptor(
                Vozilo.class,
                "proizvodjac",
                "=",
                "Fiat"
        );
        List<KriterijumDescriptor> kriterijumi = List.of(byProizvodjac);

        NetworkRequest req = new NetworkRequest("vozilo/vrati_sve", kriterijumi);
        NetworkResponse res = PoslovnaLogikaTestSuite.sendRequest(req);

        assertTrue(res.success);
        List<?> list = (List<?>) res.payload;
        assertEquals(1, list.size());

        Vozilo found = (Vozilo) list.get(0);
        assertEquals("Fiat", found.getProizvodjac());
    }
}

