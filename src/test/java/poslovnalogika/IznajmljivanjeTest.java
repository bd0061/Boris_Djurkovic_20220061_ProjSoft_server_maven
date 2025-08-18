package poslovnalogika;

import iznajmljivanjeapp.domain.*;
import iznajmljivanjeapp.domain.enumeracije.KategorijaEnum;
import iznajmljivanjeapp.repositories.inmemoryrepositories.*;
import org.junit.*;
import framework.model.network.*;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

public class IznajmljivanjeTest {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Before
    public void clearRepositories() {
        VozacInMemoryRepository.clear();
        DozvolaInMemoryRepository.clear();
        VoziloInMemoryRepository.clear();
        IznajmljivanjeInMemoryRepository.clear();
        StavkaIznajmljivanjaInMemoryRepository.clear();
    }

    @Test
    public void testKreirajIznajmljivanjeSuccess() throws Exception {
        Dozvola dozvola = new Dozvola('B');
        NetworkResponse r1 = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("dozvola/kreiraj", dozvola));
        assertTrue(r1.success);

        Vozac vozac = new Vozac("Petar", "Petrovic", "petar@gmail.com", dozvola);
        NetworkResponse r2 = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("vozac/kreiraj", vozac));
        assertTrue(r2.success);

        Vozilo vozilo1 = new Vozilo("Automobil", "Skoda", 20000, 2022, "Octavia", KategorijaEnum.SREDNJA);
        NetworkResponse r3 = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("vozilo/kreiraj", vozilo1));
        assertTrue(r3.success);
        assertNotNull(r3.payload);
        vozilo1 = (Vozilo) r3.payload;
        assertNotNull(vozilo1.getId());


        Vozilo vozilo2 = new Vozilo("Automobil", "Fiat", 3000, 2009, "Multipla", KategorijaEnum.SREDNJA);
        NetworkResponse r4 = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("vozilo/kreiraj", vozilo2));
        assertNotNull(r4.payload);
        vozilo2 = (Vozilo) r4.payload;
        assertNotNull(vozilo2.getId());

        String password = "tajna123";

        Zaposleni zaposleni = new Zaposleni("Mika", "Mikic", "mika3@gmail.com", password,true);
        NetworkRequest r = new NetworkRequest("zaposleni/kreiraj",zaposleni);
        NetworkResponse r5 = PoslovnaLogikaTestSuite.sendRequest(r);
        assertTrue(r5.success);

        StavkaIznajmljivanja stavka1 = new StavkaIznajmljivanja(
                new Iznajmljivanje(1),
                1,
                sdf.parse("2025-07-10"),
                sdf.parse("2025-07-15"),
                3000,
                vozilo1
        );

        StavkaIznajmljivanja stavka2 = new StavkaIznajmljivanja(
                new Iznajmljivanje(1),
                2,
                sdf.parse("2025-08-10"),
                sdf.parse("2025-08-15"),
                2666,
                vozilo2
        );

        Iznajmljivanje iznajmljivanje = new Iznajmljivanje(new Date(), 5666,zaposleni,vozac);
        iznajmljivanje.setStavke(List.of(stavka1,stavka2));

        NetworkRequest req = new NetworkRequest("iznajmljivanje/kreiraj", iznajmljivanje);
        NetworkResponse res = PoslovnaLogikaTestSuite.sendRequest(req);

        assertTrue(res.success);
        assertNotNull(res.payload);
    }

    @Test
    public void testFailsWhenNoStavke() throws Exception {
        Iznajmljivanje iznajmljivanje = new Iznajmljivanje();
        iznajmljivanje.setStavke(new ArrayList<>());
        iznajmljivanje.setUkupanIznos(1000);
        NetworkResponse res = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("iznajmljivanje/kreiraj", iznajmljivanje));
        assertFalse(res.success);
    }

    @Test
    public void testFailsWhenSumDoesNotMatch() throws Exception {
        Dozvola dozvola = new Dozvola('B');
        NetworkResponse r1 = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("dozvola/kreiraj", dozvola));
        assertTrue(r1.success);

        Vozac vozac = new Vozac("Petar", "Petrovic", "petar@gmail.com", dozvola);
        NetworkResponse r2 = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("vozac/kreiraj", vozac));
        assertTrue(r2.success);

        Vozilo vozilo = new Vozilo("Automobil", "Skoda", 20000, 2022, "Octavia", KategorijaEnum.SREDNJA);
        NetworkResponse r3 = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("vozilo/kreiraj", vozilo));
        assertTrue(r3.success);

        String password = "tajna123";

        Zaposleni zaposleni = new Zaposleni("Mika", "Mikic", "mika3@gmail.com", password,true);
        NetworkRequest r = new NetworkRequest("zaposleni/kreiraj",zaposleni);
        NetworkResponse r4 = PoslovnaLogikaTestSuite.sendRequest(r);
        assertTrue(r4.success);

        StavkaIznajmljivanja stavka = new StavkaIznajmljivanja(
                new Iznajmljivanje(1),
                1,
                sdf.parse("2025-07-10"),
                sdf.parse("2025-07-15"),
                3000,
                vozilo
        );

        Iznajmljivanje iznajmljivanje = new Iznajmljivanje(new Date(), 123,zaposleni,vozac);
        iznajmljivanje.setStavke(List.of(stavka));

        NetworkRequest req = new NetworkRequest("iznajmljivanje/kreiraj", iznajmljivanje);
        NetworkResponse res = PoslovnaLogikaTestSuite.sendRequest(req);
        assertFalse(res.success);
    }

    @Test
    public void testFailsOnMissingFields() throws Exception {
        StavkaIznajmljivanja stavka = new StavkaIznajmljivanja(); // no fields set
        Iznajmljivanje iznajmljivanje = new Iznajmljivanje();
        iznajmljivanje.setStavke(List.of(stavka));
        iznajmljivanje.setUkupanIznos(0);

        NetworkResponse res = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("iznajmljivanje/kreiraj", iznajmljivanje));
        assertFalse(res.success);
    }

    @Test
    public void testFailsOnMismatchedDozvola() throws Exception {
        Dozvola dozvola = new Dozvola('A');
        NetworkResponse r1 = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("dozvola/kreiraj", dozvola));
        assertTrue(r1.success);

        Vozac vozac = new Vozac("Petar", "Petrovic", "petar@gmail.com", dozvola);
        NetworkResponse r2 = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("vozac/kreiraj", vozac));
        assertTrue(r2.success);

        Vozilo vozilo = new Vozilo("Automobil", "Skoda", 20000, 2022, "Octavia", KategorijaEnum.SREDNJA);
        NetworkResponse r3 = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("vozilo/kreiraj", vozilo));
        assertTrue(r3.success);


        String password = "tajna123";

        Zaposleni zaposleni = new Zaposleni("Mika", "Mikic", "mika3@gmail.com", password,true);
        NetworkRequest r = new NetworkRequest("zaposleni/kreiraj",zaposleni);
        NetworkResponse r4 = PoslovnaLogikaTestSuite.sendRequest(r);
        assertTrue(r4.success);

        StavkaIznajmljivanja stavka = new StavkaIznajmljivanja(
                new Iznajmljivanje(1),
                1,
                sdf.parse("2025-07-10"),
                sdf.parse("2025-07-15"),
                3000,
                vozilo
        );

        Iznajmljivanje iznajmljivanje = new Iznajmljivanje(new Date(), 3000,zaposleni,vozac);
        iznajmljivanje.setStavke(List.of(stavka));

        NetworkRequest req = new NetworkRequest("iznajmljivanje/kreiraj", iznajmljivanje);
        NetworkResponse res = PoslovnaLogikaTestSuite.sendRequest(req);

        assertFalse(res.success);
    }

    @Test
    public void testFailsOnOverlappingRentalForSameVozilo() throws Exception {
        Dozvola dozvola = new Dozvola('B');
        NetworkResponse r1 = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("dozvola/kreiraj", dozvola));
        assertTrue(r1.success);

        Vozac vozac = new Vozac("Petar", "Petrovic", "petar@gmail.com", dozvola);
        NetworkResponse r2 = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("vozac/kreiraj", vozac));
        assertTrue(r2.success);

        Vozilo vozilo = new Vozilo("Automobil", "Skoda", 20000, 2022, "Octavia", KategorijaEnum.SREDNJA);
        NetworkResponse r3 = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("vozilo/kreiraj", vozilo));
        assertTrue(r3.success);
        assertNotNull(r3.payload);
        vozilo = (Vozilo) r3.payload;
        assertNotNull(vozilo.getId());

        vozilo.setStavke(List.of(new StavkaIznajmljivanja(
                new Iznajmljivanje(666),
                1,
                sdf.parse("2025-07-3"),
                sdf.parse("2025-07-10"),
                3000,
                vozilo
        )));


        String password = "tajna123";

        Zaposleni zaposleni = new Zaposleni("Mika", "Mikic", "mika3@gmail.com", password,true);
        NetworkRequest r = new NetworkRequest("zaposleni/kreiraj",zaposleni);
        NetworkResponse r4 = PoslovnaLogikaTestSuite.sendRequest(r);
        assertTrue(r4.success);

        StavkaIznajmljivanja stavka = new StavkaIznajmljivanja(
                new Iznajmljivanje(1),
                1,
                sdf.parse("2025-07-10"),
                sdf.parse("2025-07-15"),
                3000,
                vozilo
        );

        Iznajmljivanje iznajmljivanje = new Iznajmljivanje(new Date(), 3000,zaposleni,vozac);
        iznajmljivanje.setStavke(List.of(stavka));

        NetworkRequest req = new NetworkRequest("iznajmljivanje/kreiraj", iznajmljivanje);
        NetworkResponse res = PoslovnaLogikaTestSuite.sendRequest(req);

        assertFalse(res.success);
    }
    @Test
    public void testSuccessIstoVoziloUViseStavki() throws Exception {
        Dozvola dozvola = new Dozvola('B');
        NetworkResponse r1 = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("dozvola/kreiraj", dozvola));
        assertTrue(r1.success);

        Vozac vozac = new Vozac("Petar", "Petrovic", "petar@gmail.com", dozvola);
        NetworkResponse r2 = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("vozac/kreiraj", vozac));
        assertTrue(r2.success);

        Vozilo vozilo1 = new Vozilo("Automobil", "Skoda", 20000, 2022, "Octavia", KategorijaEnum.SREDNJA);
        NetworkResponse r3 = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("vozilo/kreiraj", vozilo1));
        assertTrue(r3.success);
        assertNotNull(r3.payload);
        vozilo1 = (Vozilo) r3.payload;
        assertNotNull(vozilo1.getId());



        String password = "tajna123";

        Zaposleni zaposleni = new Zaposleni("Mika", "Mikic", "mika3@gmail.com", password,true);
        NetworkRequest r = new NetworkRequest("zaposleni/kreiraj",zaposleni);
        NetworkResponse r5 = PoslovnaLogikaTestSuite.sendRequest(r);
        assertTrue(r5.success);

        StavkaIznajmljivanja stavka1 = new StavkaIznajmljivanja(
                new Iznajmljivanje(1),
                1,
                sdf.parse("2025-07-10"),
                sdf.parse("2025-07-15"),
                3000,
                vozilo1
        );

        StavkaIznajmljivanja stavka2 = new StavkaIznajmljivanja(
                new Iznajmljivanje(1),
                2,
                sdf.parse("2025-08-08"),
                sdf.parse("2025-08-20"),
                2666,
                vozilo1
        );

        Iznajmljivanje iznajmljivanje = new Iznajmljivanje(new Date(), 5666,zaposleni,vozac);
        iznajmljivanje.setStavke(List.of(stavka1,stavka2));

        NetworkRequest req = new NetworkRequest("iznajmljivanje/kreiraj", iznajmljivanje);
        NetworkResponse res = PoslovnaLogikaTestSuite.sendRequest(req);

        assertTrue(res.success);
    }

    @Test
    public void testFailsOverlappingDatumiUNovimStavkama() throws Exception {
        Dozvola dozvola = new Dozvola('B');
        NetworkResponse r1 = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("dozvola/kreiraj", dozvola));
        assertTrue(r1.success);

        Vozac vozac = new Vozac("Petar", "Petrovic", "petar@gmail.com", dozvola);
        NetworkResponse r2 = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("vozac/kreiraj", vozac));
        assertTrue(r2.success);

        Vozilo vozilo1 = new Vozilo("Automobil", "Skoda", 20000, 2022, "Octavia", KategorijaEnum.SREDNJA);
        NetworkResponse r3 = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("vozilo/kreiraj", vozilo1));
        assertTrue(r3.success);
        assertNotNull(r3.payload);
        vozilo1 = (Vozilo) r3.payload;
        assertNotNull(vozilo1.getId());



        String password = "tajna123";

        Zaposleni zaposleni = new Zaposleni("Mika", "Mikic", "mika3@gmail.com", password,true);
        NetworkRequest r = new NetworkRequest("zaposleni/kreiraj",zaposleni);
        NetworkResponse r5 = PoslovnaLogikaTestSuite.sendRequest(r);
        assertTrue(r5.success);

        StavkaIznajmljivanja stavka1 = new StavkaIznajmljivanja(
                new Iznajmljivanje(1),
                1,
                sdf.parse("2025-07-10"),
                sdf.parse("2025-07-15"),
                3000,
                vozilo1
        );

        StavkaIznajmljivanja stavka2 = new StavkaIznajmljivanja(
                new Iznajmljivanje(1),
                2,
                sdf.parse("2025-07-08"),
                sdf.parse("2025-07-20"),
                2666,
                vozilo1
        );

        Iznajmljivanje iznajmljivanje = new Iznajmljivanje(new Date(), 5666,zaposleni,vozac);
        iznajmljivanje.setStavke(List.of(stavka1,stavka2));

        NetworkRequest req = new NetworkRequest("iznajmljivanje/kreiraj", iznajmljivanje);
        NetworkResponse res = PoslovnaLogikaTestSuite.sendRequest(req);

        assertFalse(res.success);
    }
}
