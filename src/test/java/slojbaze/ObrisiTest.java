/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package slojbaze;

import iznajmljivanjeapp.domain.TerminDezurstva;
import iznajmljivanjeapp.domain.Zaposleni;
import iznajmljivanjeapp.domain.Dozvola;
import iznajmljivanjeapp.domain.Vozilo;
import iznajmljivanjeapp.domain.StavkaIznajmljivanja;
import iznajmljivanjeapp.domain.Smena;
import iznajmljivanjeapp.domain.Iznajmljivanje;
import iznajmljivanjeapp.domain.Vozac;
import iznajmljivanjeapp.domain.enumeracije.TipTerminaEnum;
import iznajmljivanjeapp.domain.enumeracije.KategorijaEnum;
import iznajmljivanjeapp.bezbednost.PasswordHasher;
import framework.config.AppConfig;
import framework.orm.ConnectionPool;
import framework.orm.EntityManager;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 *
 * @author Djurkovic
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ObrisiTest {

    public static EntityManager em;
    public static AppConfig cfg = new AppConfig("projektovanjesoftvera_seminarski","projektovanjesoftvera_seminarski_test");

    public ObrisiTest() {
    }

    @BeforeClass
    public static void setUpClass() {

        //koristimo pravu bazu, integration test
        ConnectionPool.initialize(cfg, true);
        em = new EntityManager(cfg);

        //dodajemo jos neke entitete za potrebe brisanja
        System.out.println("Dodajem dodatne entiete za testiranje...");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 15);
        Date futureDate = calendar.getTime();

        byte[] salt = PasswordHasher.generateSalt();

        try {
            em.kreirajEntitet(new Zaposleni(666, "Marko", "Markovic", "markomarkovic@gmail.com", PasswordHasher.hash("eeee123", salt), Base64.getEncoder().encodeToString(salt)));
            em.kreirajEntitet(new StavkaIznajmljivanja(new Iznajmljivanje(1), 2, new Date(), futureDate, new Vozilo(1))); //nova stavka za demonstriranje brisanja kod DELETE /
            em.kreirajEntitet(new Smena(new Date(), new Zaposleni(666), new TerminDezurstva(2), false, 5, 0)); //novi zapter za demonstriranje brisanja kod DELETE /
            em.kreirajEntitet(new Vozilo(44, "Minibus", "Mercedes", 15000.0, 2012, "kako god", KategorijaEnum.SREDNJA,50)); //novo vozilo za demonstriranje brisanja vozila koja se ne vezuju ni za jednu stavku iznajmljivanja
            em.kreirajEntitet(new Dozvola(44, 'A')); // nova dozvola za koju se ne vezuje ni jedan vozac
            em.kreirajEntitet(new TerminDezurstva(44, "aaaa", TipTerminaEnum.NOC)); //nov td za koji se ne vezuje ni jedan zapter
            em.kreirajEntitet(new Vozac(44, "Petar", "Jovanovic", "petjov@yahoo.com", new Dozvola(1)));
            em.kreirajEntitet(new Iznajmljivanje(44, new Date(), 12345, new Zaposleni(1), new Vozac(1)));  //nov vozac za kojg se ne vezuje ni jedno iznajmljivanje
            em.kreirajEntitet(new Iznajmljivanje(447, new Date(), 12345, new Zaposleni(1), new Vozac(1)));
            em.kreirajEntitet(new StavkaIznajmljivanja(new Iznajmljivanje(447), 1, new Date(), futureDate, new Vozilo(1))); //nova stavka za demonstriranje brisanja kod DELETE /
        }
        catch(Exception e) {
            fail("Greska pri kreiranju pomocnih objekata");
        }
    }

    @AfterClass
    public static void tearDownClass() {
        System.out.println("Testiranje zavrseno, brisem preostale test objekte...");
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of kreirajNovi method, of class Obrisi.
     */
    @Test
    public void test01_Obrisi_strukturnoOgranicenjeDeleteZaposleni() {
        System.out.println("Provera da li strukturno ogranicenje \"ON DELETE RESTRICTED Iznajmljivanje ZapTer\" ispravno funkcionise pri brisanju zaposlenog");

        Zaposleni zaposleni = new Zaposleni(1);
        try {
            em.obrisiEntitet(zaposleni);
        } catch (Exception e) {
            System.out.println("Zaposleni nije obrisan jer se vezuje za postojece iznajmljivanje/zapter");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Zaposleni je obrisan iako se vezuje za postojece iznajmljivanje/zapter.");
    }

    @Test
    public void test02_Obrisi_strukturnoOgranicenjeDeleteVozilo() {
        System.out.println("Provera da li strukturno ogranicenje \"ON DELETE RESTRICTED StavkaIznajmljivanja\" ispravno funkcionise pri brisanju vozila");

        Vozilo vozilo = new Vozilo(1);
        try {
            em.obrisiEntitet(vozilo);
        } catch (Exception e) {
            System.out.println("Vozilo nije obrisano jer se vezuje za postojecu stavku iznajmljivanja.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Vozilo je obrisano iako se vezuje sa postojecom stavkom iznajmljivanja.");
    }

    @Test
    public void test03_Obrisi_strukturnoOgranicenjeDeleteDozvola() {
        System.out.println("Provera da li strukturno ogranicenje \"ON DELETE RESTRICTED Vozac\" ispravno funkcionise pri brisanju dozvole");

        Dozvola dozvola = new Dozvola(1);
        try {
            em.obrisiEntitet(dozvola);
        } catch (Exception e) {
            System.out.println("Dozvola nije obrisana jer se vezuje za postojeceg  vozaca.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Dozvola je obrisana iako se vezuje za postojeceg vozaca.");
    }

    @Test
    public void test04_Obrisi_strukturnoOgranicenjeTerminDezurstva() {
        System.out.println("Provera da li strukturno ogranicenje \"ON DELETE RESTRICTED ZapTer\" ispravno funkcionise pri brisanju termina dezurstva");

        TerminDezurstva terminDezurstva = new TerminDezurstva(1);
        try {
            em.obrisiEntitet(terminDezurstva);
        } catch (Exception e) {
            System.out.println("Termin dezurstva nije obrisan jer se vezuje za postojeci zapter");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Termin dezurstva je obrisan iako se vezuje za postojeci zapter.");
    }

    @Test
    public void test05_Obrisi_strukturnoOgranicenjeVozac() {
        System.out.println("Provera da li strukturno ogranicenje \"ON DELETE RESTRICTED Iznajmljivanje\" ispravno funkcionise pri brisanju vozaca");

        Vozac vozac = new Vozac(1);
        try {
            em.obrisiEntitet(vozac);
        } catch (Exception e) {
            System.out.println("Vozac nije obrisan jer se vezuje za postojeci iznajmljivanje");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Vozac je obrisan iako se vezuje za postojece iznajmljivanje.");
    }

    @Test
    public void test06_Obrisi_strukturnoOgranicenjeIznajmljivanje() {
        System.out.println("Provera da li strukturno ogranicenje \"ON DELETE CASCADES StavkaIznajmljivanja\" ispravno funkcionise pri brisanju iznajmljivanja");

        Iznajmljivanje iznajmljivanje = new Iznajmljivanje(447);
        try {
            em.obrisiEntitet(iznajmljivanje);
        } catch (Exception e) {
            fail("Iznajmljivanje je obrisano iako se vezuje za postojecu stavku iznajmljivanja.");
        }
        System.out.println("Iznajmljivanje je obrisano kao i stavke iznajmljivanja koje se za njega vezuju");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test07_Obrisi_strukturnoOgranicenjeStavkaIznajmljivanje() {
        System.out.println("Provera da li strukturno ogranicenje \"ON DELETE /\" ispravno funkcionise pri brisanju stavke iznajmljivanja");

        StavkaIznajmljivanja stavkaIznajmljivanja = new StavkaIznajmljivanja(new Iznajmljivanje(1), 2);
        try {
            em.obrisiEntitet(stavkaIznajmljivanja);
        } catch (Exception e) {
            System.out.println(e);
            fail("Stavka iznajmljivanja nije obrisana iako nema restricted strukturno ogranicenje ka iznajmljivanju");
        }
        System.out.println("Stavka iznajmljivanja je obrisana jer nema restricted strukturno ogranicenje ka iznajmljivanju");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test08_Obrisi_strukturnoOgranicenjeZapTer() {
        System.out.println("Provera da li strukturno ogranicenje \"ON DELETE /\" ispravno funkcionise pri brisanju zapter");

        Smena zapTer = new Smena(new Date(), new Zaposleni(666), new TerminDezurstva(2));
        try {
            em.obrisiEntitet(zapTer);
        } catch (Exception e) {
            fail("Zapter nije obrisan iako nema restricted strukturno ogranicenje ka iznajmljivanju");
        }
        System.out.println("Zapter je obrisan jer nema restricted strukturno ogranicenje ka iznajmljivanju");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test09_Obrisi_obrisiZaposleni() {
        System.out.println("Normalno brisanje zaposlenog - ne vezuje se ni za jedan zapter ili iznajmljivanje");
        Zaposleni zaposleni = new Zaposleni(666);
        try {
            em.obrisiEntitet(zaposleni);
        } catch (Exception e) {
            fail("Zaposleni nije obrisan iako se ne vezuje ni za jedan zapter ili iznajmljivanje");
        }
        System.out.println("Zaposleni je obrisan jer se ne vezuje ni za jedan zapter ili iznajmljivanje");
    }

    @Test
    public void test10_Obrisi_obrisiVozilo() {
        System.out.println("Normalno brisanje vozila - ne vezuje se ni za jednu stavku iznajmljivanja");
        Vozilo vozilo = new Vozilo(44);
        try {
            em.obrisiEntitet(vozilo);
        } catch (Exception e) {
            fail("Vozilo nije obrisano iako se ne vezuje ni za jednu stavku iznajmljivanja");
        }
        System.out.println("Vozilo je obrisano jer se ne vezuje ni za jednu stavku iznajmljivanja");
    }

    @Test
    public void test11_Obrisi_obrisiDozvola() {
        System.out.println("Normalno brisanje dozvole - ne vezuje se ni za jednog vozaca");
        Dozvola dozvola = new Dozvola(44);
        try {
            em.obrisiEntitet(dozvola);
        } catch (Exception e) {
            fail("Dozvola nije obrisana iako se ne vezuje ni za jednog vozaca");
        }
        System.out.println("Dozvola je obrisana jer se ne vezuje ni za jednog vozaca");
    }

    @Test
    public void test12_Obrisi_obrisiTerminDezurstva() {
        System.out.println("Normalno brisanje termina dezurstva - ne vezuje se ni za jedan zapter");
        TerminDezurstva terminDezurstva = new TerminDezurstva(44);
        try {
            em.obrisiEntitet(terminDezurstva);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        System.out.println("Termin dezurstva je obrisan jer se ne vezuje  ni za jedan zapter");
    }

    @Test
    public void test13_Obrisi_obrisiVozac() {
        System.out.println("Normalno brisanje vozaca - ne vezuje se ni za jedno iznajmljivanje ");
        Vozac vozac = new Vozac(44);
        try {
            em.obrisiEntitet(vozac);
        } catch (Exception e) {
            fail("Vozac nije obrisan iako se ne vezuje ni za jedno iznajmljivanje");
        }
        System.out.println("Vozac je obrisan jer se ne vezuje  ni za jedno iznajmljivanje");
    }

    @Test
    public void test14_Obrisi_obrisiIznajmljivanje() {
        System.out.println("Normalno brisanje iznajmljivanja - ne vezuje se ni za jednu stavku iznajmljivanja ");
        Iznajmljivanje iznajmljivanje = new Iznajmljivanje(44);
        try {
            em.obrisiEntitet(iznajmljivanje);
        } catch (Exception e) {
            fail("Iznajmljivanje nije obrisano iako se ne vezuje ni za jednu stavku iznajmljivanja");
        }
        System.out.println("Iznajmljivanje je obrisano jer se ne vezuje  ni za jednu stavku iznajmljivanja");
    }
}
