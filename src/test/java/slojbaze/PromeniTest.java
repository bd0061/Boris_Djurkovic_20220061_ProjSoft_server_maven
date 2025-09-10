/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package slojbaze;

import iznajmljivanjeapp.domain.TerminDezurstva;
import iznajmljivanjeapp.domain.Zaposleni;
import iznajmljivanjeapp.domain.Dozvola;
import iznajmljivanjeapp.domain.StavkaIznajmljivanja;
import iznajmljivanjeapp.domain.Vozilo;
import iznajmljivanjeapp.domain.Smena;
import iznajmljivanjeapp.domain.Iznajmljivanje;
import iznajmljivanjeapp.domain.Vozac;
import iznajmljivanjeapp.domain.enumeracije.TipTerminaEnum;
import iznajmljivanjeapp.domain.enumeracije.KategorijaEnum;
import iznajmljivanjeapp.bezbednost.PasswordHasher;
import framework.config.AppConfig;
import framework.orm.ConnectionPool;
import framework.orm.EntityManager;
import java.time.Year;
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
public class PromeniTest {

    public static EntityManager em;
    public static AppConfig cfg = new AppConfig("projektovanjesoftvera_seminarski","projektovanjesoftvera_seminarski_test");



    public static final Vozac[] vozaci = new Vozac[] {new Vozac("Petar","Bojovic","petboj@gmail.com",new Dozvola(888)),
            new Vozac("Momir","Segan","ms@gmail.com",new Dozvola(888)),
            new Vozac("Jovan","Andjelokovic","joan@gmail.com",new Dozvola(888)),
            new Vozac("Stevan","Mokranjac","stevmok@gmail.com",new Dozvola(888))};

    public PromeniTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        //koristimo pravu test bazu, integration test
        ConnectionPool.initialize(cfg, true);
        em = new EntityManager(cfg);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of promeni method, of class Promeni.
     */
    @Test
    public void test01_PromeniZaposleniCascades() {
        System.out.println("Provera da li spoljni kljucevi iznajmljivanja i zapter \"kaskadiraju\" svoje vrednosti nakon promene vrednosti kljuca zaposlenog");
        try {
            em.promeniEntitet(new Zaposleni(888), new Zaposleni(1));
        } catch (Exception e) {
            fail("Greska pri pokusaju promene " + e);
        }
        Iznajmljivanje i = new Iznajmljivanje();
        i.setZaposleni(new Zaposleni(888));

        Smena z = new Smena();
        z.setZaposleni(new Zaposleni(888));

        boolean result = em.daLiPostojiSlog(i) && em.daLiPostojiSlog(z);
        assertEquals("Spoljni kljucevi idZaposleni od zapter i iznajmljivanje nisu promenili vrednosti iako imaju CASCADE", result, true);
        System.out.println("Spoljni kljucevi idZaposleni od zapter i iznajmljivanje su promenili vrednosti jer imaju CASCADE");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test02_PromeniVoziloCascades() {
        System.out.println("Provera da li spoljni kljucevi stavke iznajmljivanja \"kaskadiraju\" svoje vrednosti nakon promene vrednosti kljuca vozila");
        try {
            em.promeniEntitet(new Vozilo(888), new Vozilo(1));
        } catch (Exception e) {
            fail("Greska pri pokusaju promene");
        }
        StavkaIznajmljivanja s = new StavkaIznajmljivanja();
        s.setVozilo(new Vozilo(8));

        boolean result = em.daLiPostojiSlog(s);
        assertEquals("Spoljni kljuc idVozilo od stavke iznajmljivanja nije promenio vrednosti iako ima CASCADE", result, true);
        System.out.println("Spoljni kljuc idVozilo od stavke iznajmljivanja je promenio vrednosti jer ima CASCADE");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test03_PromeniDozvolaCascades() {
        System.out.println("Provera da li spoljni kljucevi vozaca \"kaskadiraju\" svoje vrednosti nakon promene vrednosti kljuca dozvole");
        try {
            em.promeniEntitet(new Dozvola(888), new Dozvola(1));
        } catch (Exception e) {
            fail("Greska pri pokusaju promene");
        }
        Vozac v = new Vozac();
        v.setDozvola(new Dozvola(888));

        boolean result = em.daLiPostojiSlog(v);
        assertEquals("Spoljni kljuc idDozvola od vozaca nije promenio vrednosti iako ima CASCADE", result, true);
        System.out.println("Spoljni kljuc idDozvola od vozaca je promenio vrednosti jer ima CASCADE");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test04_PromeniTermindezurstvaCascades() {
        System.out.println("Provera da li spoljni kljucevi zapter \"kaskadiraju\" svoje vrednosti nakon promene vrednosti kljuca termina dezurstva");
        try {
            em.promeniEntitet(new TerminDezurstva(888), new TerminDezurstva(1));
        } catch (Exception e) {
            fail("Greska pri pokusaju promene");
        }
        Smena z = new Smena();
        z.setTerminDezurstva(new TerminDezurstva(888));

        boolean result = em.daLiPostojiSlog(z);
        assertEquals("Spoljni kljuc idTerminDezustva od zapter nije promenio vrednosti iako ima CASCADE", result, true);
        System.out.println("Spoljni kljuc idTerminDezustva od zapter je promenio vrednosti jer ima CASCADE");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test05_PromeniVozacCascades() {
        System.out.println("Provera da li spoljni kljuce idVozac od iznajmljivanja \"kaskadira\" nakon promene vrednosti kljuca vozaca");
        try {
            em.promeniEntitet(new Vozac(888), new Vozac(1));
        } catch (Exception e) {
            fail("Greska pri pokusaju promene");
        }
        Iznajmljivanje i = new Iznajmljivanje();
        i.setVozac(new Vozac(888));

        boolean result = em.daLiPostojiSlog(i);
        assertEquals("Spoljni kljuc idVozac od iznajmljivanja nije promenio vrednosti iako ima CASCADE", result, true);
        System.out.println("Spoljni kljuc idVozac od iznajmljivanja je promenio vrednosti jer ima CASCADE");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test06_PromeniVozacRestricted() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene spoljnog kljuca vozaca idDozvola na nepostojecu dozvolu");
        Vozac v = new Vozac();
        v.setId(888);
        v.setDozvola(new Dozvola(555));
        try {
            em.promeniEntitet(v);
        } catch (Exception e) {
            System.out.println("Spoljni kljuc idDozvola od vozaca nije promenjen jer specifira nevalidnu dozvolu");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Spoljni kljuc idDozvola od vozaca je promenjen iako specifira nevalidnu dozvolu");
    }

    @Test
    public void test07_PromeniIznajmljivanjeCascades() {
        System.out.println("Provera da li spoljni kljuce idIznajmljivanje od stavke iznajmljivanja \"kaskadira\" nakon promene vrednosti kljuca iznajmljivanja");
        try {
            em.promeniEntitet(new Iznajmljivanje(888), new Iznajmljivanje(1));
        } catch (Exception e) {
            fail("Greska pri pokusaju promene");
        }
        StavkaIznajmljivanja si = new StavkaIznajmljivanja();
        si.setIznajmljivanje(new Iznajmljivanje(888));

        boolean result = em.daLiPostojiSlog(si);
        assertEquals("Spoljni kljuc idIznajmljivanje od stavke iznajmljivanja nije promenio vrednosti iako ima CASCADE", result, true);
        System.out.println("Spoljni kljuc idIznajmljivanje od stavke iznajmljivanja je promenio vrednosti jer ima CASCADE");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test08_PromeniIznajmljivanjeRestrictedVozac() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene spoljnog kljuca iznajmljivanja idVozac na nepostojeceg vozaca");
        Iznajmljivanje i = new Iznajmljivanje();
        i.setId(888);
        i.setVozac(new Vozac(1337));
        try {
            em.promeniEntitet(i);
        } catch (Exception e) {
            System.out.println("Spoljni kljuc idVozac od iznajmljivanja nije promenjen jer specifira nevalidnog vozaca");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Spoljni kljuc idVozac od iznajmljivanja je promenjen iako specifira nevalidnog vozaca");
    }

    @Test
    public void test09_PromeniIznajmljivanjeRestrictedZaposleni() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene spoljnog kljuca iznajmljivanja idZaposleni na nepostojeceg zaposlenog");
        Iznajmljivanje i = new Iznajmljivanje();
        i.setId(888);
        i.setZaposleni(new Zaposleni(1337));
        try {
            em.promeniEntitet(i);
        } catch (Exception e) {
            System.out.println("Spoljni kljuc idZaposleni od iznajmljivanja nije promenjen jer specifira nevalidnog zaposlenog");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Spoljni kljuc idZaposleni od iznajmljivanja je promenjen iako specifira nevalidnog zaposlenog");
    }

    @Test
    public void test10_PromeniStavkaIznajmljivanjaRestrictedVozilo() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene spoljnog kljuca stavke iznajmljivanja idVozilo na nepostojece vozilo");
        StavkaIznajmljivanja si = new StavkaIznajmljivanja();
        si.setIznajmljivanje(new Iznajmljivanje(888));
        si.setRb(1);
        si.setVozilo(new Vozilo(5252));
        try {
            em.promeniEntitet(si);
        } catch (Exception e) {
            System.out.println("Spoljni kljuc idVozilo od stavke iznajmljivanja nije promenjen jer specifira nevalidno vozilo");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Spoljni kljuc idVozilo od stavke iznajmljivanja je promenjen iako specifira nevalidno vozilo");
    }

    @Test
    public void test11_PromeniStavkaIznajmljivanjaRestrictedIznajmljivanje() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene spoljnog kljuca stavke iznajmljivanja idIznajmljivanje na nepostojece iznajmljivanje");
        StavkaIznajmljivanja si = new StavkaIznajmljivanja();
        si.setIznajmljivanje(new Iznajmljivanje(5252));

        //za where klazulu posto je idiznajmljivanja deo primarnog kljuca
        StavkaIznajmljivanja si2 = new StavkaIznajmljivanja();
        si2.setIznajmljivanje(new Iznajmljivanje(888));
        si2.setRb(1);

        try {
            em.promeniEntitet(si, si2);
        } catch (Exception e) {
            System.out.println("Spoljni kljuc idIznajmljivanje od stavke iznajmljivanja nije promenjen jer specifira nevalidno iznajmljivanje");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Spoljni kljuc idIznajmljivanje od stavke iznajmljivanja je promenjen iako specifira nevalidno iznajmljivanje");
    }

    @Test
    public void test12_PromeniZapTerRestrictedZaposleni() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene spoljnog kljuca od zapter idZaposleni na nepostojeceh zaposlenog");

        Smena zt1 = new Smena();
        zt1.setZaposleni(new Zaposleni(381));

        //za where klazulu posto je idZaposleni deo primarnog kljuca
        Smena zt2 = new Smena(new Date(), new Zaposleni(888), new TerminDezurstva(2));

        try {
            em.promeniEntitet(zt1, zt2);
        } catch (Exception e) {
            System.out.println("Spoljni kljuc idZaposleni od zapter nije promenjen jer specifira nevalidnog zaposlenog");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Spoljni kljuc idZaposleni od zapter je promenjen iako specifira nevalidnog zaposlenog");
    }

    @Test
    public void test13_PromeniZapTerRestrictedTerminDezurstva() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene spoljnog kljuca od zapter idTerminDezurstva na nepostojeci termin dezurstva");

        Smena zt1 = new Smena();
        zt1.setTerminDezurstva(new TerminDezurstva(734));

        //za where klazulu posto je idTerminDezurstva deo primarnog kljuca
        Smena zt2 = new Smena(new Date(), new Zaposleni(888), new TerminDezurstva(2));

        try {
            em.promeniEntitet(zt1, zt2);
        } catch (Exception e) {
            System.out.println("Spoljni kljuc idTerminDezurstva od zapter nije promenjen jer specifira nevalidnan termin dezurstva");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Spoljni kljuc idTerminDezurstva od zapter je promenjen iako specifira nevalidan termin dezurstva");
    }

    @Test
    public void test14_PromeniDozvolaVrednosnaOgranicenja() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene dozvole koji krsi vrednosna ogranicenja - losa kategorija");
        Dozvola d = new Dozvola(888);
        d.setKategorija('X');
        try {
            em.promeniEntitet(d);
        } catch (Exception e) {
            System.out.println("Kategorija dozvole nije promenjena jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Kategorija dozvole je promenjena iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test15_PromeniIznajmljivanjeVrednosnaOgranicenja() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene iznajmljivanja koji krsi vrednosna ogranicenja - los iznos");
        Iznajmljivanje i = new Iznajmljivanje(888);
        i.setUkupanIznos(-666);
        try {
            em.promeniEntitet(i);
        } catch (Exception e) {
            System.out.println("Ukupan iznos iznajmljivanja nije promenjen jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ukupan iznos iznajmljivanja je promenjen iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test16_PromeniVozacVrednosnaOgranicenja() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene vozaca koji krsi vrednosna ogranicenja - los email");
        Vozac v = new Vozac(888);
        v.setEmail("efgsbhsef");
        try {
            em.promeniEntitet(v);
        } catch (Exception e) {
            System.out.println("Email vozaca nije promenjen jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Email vozaca je promenjen iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test17_PromeniZaposleniVrednosnaOgranicenja() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene zaposlenog koji krsi vrednosna ogranicenja - los email");
        Zaposleni z = new Zaposleni(888);
        z.setEmail("gtgnkngjk");
        try {
            em.promeniEntitet(z);
        } catch (Exception e) {
            System.out.println("Email zaposlenog nije promenjen jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Email zaposlenog je promenjen iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test18_PromeniStavkaIznajmljivanjaVrednosnaOgranicenjaDatumPocetka() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene stavke iznajmljivanja koji krsi vrednosna ogranicenja - los datum pocetka");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -75);
        Date d = calendar.getTime();
        StavkaIznajmljivanja s = new StavkaIznajmljivanja(new Iznajmljivanje(888), 1);
        s.setDatumPocetka(d);
        try {
            em.promeniEntitet(s);
        } catch (Exception e) {
            System.out.println("Stavka iznajmljivanja nuje promenjena jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Stavka iznajmljivanja je promenjena iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test19_PromeniStavkaIznajmljivanjaDatumPocetka() {
        for (int i = 0; i < 20; i++)
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        System.out.println("Provera da li ce biti prekinut pokusaj promene stavke iznajmljivanja koji ne krsi vrednosna ogranicenja - korektan datum pocetka");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        Date d = calendar.getTime();
        StavkaIznajmljivanja s = new StavkaIznajmljivanja(new Iznajmljivanje(888), 1);
        s.setDatumPocetka(d);
        try {
            em.promeniEntitet(s);
        } catch (Exception e) {
            fail("Stavka iznajmljivanja nije promenjena iako zadovoljava vrednosna ogranicenja");
        }
        System.out.println("Stavka iznajmljivanja je promenjena jer zadovoljava vrednosna ogranicenja");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test20_PromeniStavkaIznajmljivanjaVrednosnaOgranicenjaDatumPocetka() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene stavke iznajmljivanja koji krsi vrednosna ogranicenja - los datum zavrsetka");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 75);
        Date d = calendar.getTime();
        StavkaIznajmljivanja s = new StavkaIznajmljivanja(new Iznajmljivanje(888), 1);
        s.setDatumZavrsetka(d);
        try {
            em.promeniEntitet(s);
        } catch (Exception e) {
            System.out.println("Stavka iznajmljivanja nuje promenjena jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Stavka iznajmljivanja je promenjena iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test21_PromeniStavkaIznajmljivanjaDatumPocetka() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene stavke iznajmljivanja koji ne krsi vrednosna ogranicenja - korektan datum zavrsetka");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 20);
        Date d = calendar.getTime();
        StavkaIznajmljivanja s = new StavkaIznajmljivanja(new Iznajmljivanje(888), 1);
        s.setDatumZavrsetka(d);
        try {
            em.promeniEntitet(s);
        } catch (Exception e) {
            fail("Stavka iznajmljivanja nije promenjena iako zadovoljava vrednosna ogranicenja");
        }
        System.out.println("Stavka iznajmljivanja je promenjena jer zadovoljava vrednosna ogranicenja");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test22_PromeniVoziloVrednosnaOgranicenjaProizvodjac() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene vozila koji krsi vrednosna ogranicenja - los proizvodjac");
        Vozilo v = new Vozilo(888);
        v.setProizvodjac("Jugo");
        try {
            em.promeniEntitet(v);
        } catch (Exception e) {
            System.out.println("Vozilo nije promenjeno jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Vozilo je promenjeno iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test23_PromeniVoziloVrednosnaOgranicenjaKlasa() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene vozila koji krsi vrednosna ogranicenja - losa klasa");
        Vozilo v = new Vozilo(888);
        v.setKlasa("UFO");
        try {
            em.promeniEntitet(v);
        } catch (Exception e) {
            System.out.println("Vozilo nije promenjeno jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Vozilo je promenjeno iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test24_PromeniVoziloVrednosnaOgranicenjaKupovnaCena() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene vozila koji krsi vrednosna ogranicenja - losa kupovna cena");
        Vozilo v = new Vozilo(888);
        v.setKupovnaCena(-666);
        try {
            em.promeniEntitet(v);
        } catch (Exception e) {
            System.out.println("Vozilo nije promenjeno jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Vozilo je promenjeno iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test25_PromeniVoziloVrednosnaOgranicenjaGodiste() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene vozila koji krsi vrednosna ogranicenja - prestaro godiste");
        Vozilo v = new Vozilo(888);
        v.setGodiste(2000);
        try {
            em.promeniEntitet(v);
        } catch (Exception e) {
            System.out.println("Vozilo nije promenjeno jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Vozilo je promenjeno iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test26_PromeniVoziloVrednosnaOgranicenjaGodiste() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene vozila koji krsi vrednosna ogranicenja - godiste u buducnosti");
        Vozilo v = new Vozilo(888);
        v.setGodiste(Year.now().getValue() + 1);
        try {
            em.promeniEntitet(v);
        } catch (Exception e) {
            System.out.println("Vozilo nije promenjeno jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Vozilo je promenjeno iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test27_PromeniVoziloVrednosnaOgranicenjaMotorDacia() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene vozila koji krsi vrednosna ogranicenja - promena proizvodjaca na daciu kod motora");
        Vozilo v = new Vozilo(2);
        v.setProizvodjac("Dacia");
        try {
            em.promeniEntitet(v);
        } catch (Exception e) {
            System.out.println("Vozilo nije promenjeno jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Vozilo je promenjeno iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test28_PromeniVoziloVrednosnaOgranicenjaMotorDacia() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene vozila koji krsi vrednosna ogranicenja - promena klase na motor kod dacia");
        Vozilo x = new Vozilo(888);
        x.setProizvodjac("Dacia");
        try {
            em.promeniEntitet(x);
        } catch (Exception e) {
            fail("greska pri podesavanju objekta za testiranje");
        }
        Vozilo v = new Vozilo(888);
        v.setKlasa("Motor");
        try {
            em.promeniEntitet(v);
        } catch (Exception e) {
            System.out.println("Vozilo nije promenjeno jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Vozilo je promenjeno iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test29_PromeniZapTerVrednosnaOgranicenjaVanredan() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene zapter koji krsi vrednosna ogranicenja - stavljanje da nije vanredan neko ko ima fiksni bonus");

        Smena zt = new Smena(new Date(), new Zaposleni(888), new TerminDezurstva(2));
        zt.setVanredan(false);
        try {
            em.promeniEntitet(zt);
        } catch (Exception e) {
            System.out.println("Zapter nije promenjen jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Zapter je promenjen iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test30_PromeniZapTerVrednosnaOgranicenjaVanredanFiksniBonus() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene zapter koji krsi vrednosna ogranicenja - stavljanje da nije vanredan neko ko ima fiksni bonus (eksplicitno menjamo i fiksniBonus)");

        Smena zt = new Smena(new Date(), new Zaposleni(888), new TerminDezurstva(2));
        zt.setVanredan(false);
        zt.setFiksniBonus(1500);
        try {
            em.promeniEntitet(zt);
        } catch (Exception e) {
            System.out.println("Zapter nije promenjen jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Zapter je promenjen iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test31_PromeniZapTerVrednosnaOgranicenjaVanredanFiksniBonusBrojSati() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene zapter koji krsi vrednosna ogranicenja - stavljanje da nije vanredan neko ko ima fiksni bonus (eksplicitno menjamo i fiksniBonus i brojSati)");

        Smena zt = new Smena(new Date(), new Zaposleni(888), new TerminDezurstva(2));
        zt.setVanredan(false);
        zt.setFiksniBonus(1500);
        zt.setBrojSati(7);
        try {
            em.promeniEntitet(zt);
        } catch (Exception e) {
            System.out.println("Zapter nije promenjen jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Zapter je promenjen iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test32_PromeniZapTerVrednosnaOgranicenjaVanredanBrojSati() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene zapter koji krsi vrednosna ogranicenja - stavljanje da nije vanredan neko ko ima fiksni bonus (eksplicitno menjamo i brojSati)");

        Smena zt = new Smena(new Date(), new Zaposleni(888), new TerminDezurstva(2));
        zt.setVanredan(false);
        zt.setBrojSati(7);
        try {
            em.promeniEntitet(zt);
        } catch (Exception e) {
            System.out.println("Zapter nije promenjen jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Zapter je promenjen iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test33_PromeniZapTerVrednosnaOgranicenjaBrojSatiPremali() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene zapter koji krsi vrednosna ogranicenja - premali broj sati");

        Smena zt = new Smena(new Date(), new Zaposleni(888), new TerminDezurstva(2));
        zt.setBrojSati(1);
        try {
            em.promeniEntitet(zt);
        } catch (Exception e) {
            System.out.println("Zapter nije promenjen jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Zapter je promenjen iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test34_PromeniZapTerVrednosnaOgranicenjaBrojSati() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene zapter koji krsi vrednosna ogranicenja - prevelik broj sati");

        Smena zt = new Smena(new Date(), new Zaposleni(888), new TerminDezurstva(2));
        zt.setBrojSati(12);
        try {
            em.promeniEntitet(zt);
        } catch (Exception e) {
            System.out.println("Zapter nije promenjen jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Zapter je promenjen iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test35_PromeniZapTerVrednosnaOgranicenjaFiksniBonus() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene zapter koji krsi vrednosna ogranicenja - prevelik fiksni bonus");

        Smena zt = new Smena(new Date(), new Zaposleni(888), new TerminDezurstva(2));
        zt.setFiksniBonus(50000);
        try {
            em.promeniEntitet(zt);
        } catch (Exception e) {
            System.out.println("Zapter nije promenjen jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Zapter je promenjen iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test36_PromeniZapTerVrednosnaOgranicenjaFiksniBonusPremali() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene zapter koji krsi vrednosna ogranicenja - premali fiksni bonus");

        Smena zt = new Smena(new Date(), new Zaposleni(888), new TerminDezurstva(2));
        zt.setFiksniBonus(50);
        try {
            em.promeniEntitet(zt);
        } catch (Exception e) {
            System.out.println("Zapter nije promenjen jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Zapter je promenjen iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test37_PromeniZapTerVrednosnaOgranicenjaFiksniBonusKrsi() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene zapter koji krsi vrednosna ogranicenja - normalan fiksni bonus nekome ko nije vanredan");

        Smena zt = new Smena(new Date(), new Zaposleni(888), new TerminDezurstva(888));
        zt.setFiksniBonus(1200);
        try {
            em.promeniEntitet(zt);
        } catch (Exception e) {
            System.out.println("Zapter nije promenjen jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Zapter je promenjen iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test38_PromeniZapTerVrednosnaOgranicenjaFiksniBonusKorektno() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene zapter koji ne krsi vrednosna ogranicenja - korektna promena bonusa");

        Smena zt = new Smena(new Date(), new Zaposleni(888), new TerminDezurstva(2));
        zt.setFiksniBonus(1350);
        try {
            em.promeniEntitet(zt);
        } catch (Exception e) {
            fail("Zapter nije promenjen iako zadovoljava vrednosna ogranicenja");
        }
        System.out.println("Zapter je promenjen jer zadovoljava vrednosna ogranicenja");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test39_PromeniZapTerVrednosnaOgranicenjaFiksniBonusVanredanPrevelikBonus() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene zapter koji krsi vrednosna ogranicenja - pokusaj menjanja u vanredno i dodavanje nekorektnog bonusa");

        Smena zt = new Smena(new Date(), new Zaposleni(888), new TerminDezurstva(888));
        zt.setVanredan(true);
        zt.setFiksniBonus(135000);
        try {
            em.promeniEntitet(zt);
        } catch (Exception e) {
            System.out.println("Zapter nije promenjen jer ne zadovoljava vrednosna ogranicenja");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Zapter je promenjen iako ne zadovoljava vrednosna ogranicenja");
    }

    @Test
    public void test40_PromeniZapTerVrednosnaOgranicenjaFiksniBonusVanredanKorektno() {
        System.out.println("Provera da li ce biti prekinut pokusaj promene zapter koji ne krsi vrednosna ogranicenja - korektno menjanje u vanredno i dodavanje bonusa");

        Smena zt = new Smena(new Date(), new Zaposleni(888), new TerminDezurstva(888));
        zt.setVanredan(true);
        zt.setFiksniBonus(1350);
        try {
            em.promeniEntitet(zt);
        } catch (Exception e) {
            fail("Zapter nije promenjen iako zadovoljava vrednosna ogranicenja");
        }
        System.out.println("Zapter je promenjen jer zadovoljava vrednosna ogranicenja");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test41_PromeniDozvolaNormalno() {
        System.out.println("Provera da li ce dozvola biti normalno promenjena");
        Dozvola d = new Dozvola(888, 'A');
        try {
            em.promeniEntitet(d);
        } catch (Exception e) {
            fail("Dozvola nije promenjena iako zadovoljava vrednosna ogranicenja");
        }
        System.out.println("Dozvola je korektno promenjena i zadovoljava vrednosna ogranicenja");
        System.out.println("---------------------------------------------------------------------------------");

    }

    @Test
    public void test42_PromeniIznajmljivanjeNormalno() {
        System.out.println("Provera da li ce iznajmljivanje biti normalno promenjeno");
        Iznajmljivanje i = new Iznajmljivanje(888, new Date(), 1337.0, new Zaposleni(888), new Vozac(888));
        try {
            em.promeniEntitet(i);
        } catch (Exception e) {
            fail("Iznajmljivanje nije promenjeno iako zadovoljava vrednosna ogranicenja");
        }
        System.out.println("Iznajmljivanje je korektno promenjeno i zadovoljava vrednosna ogranicenja");
        System.out.println("---------------------------------------------------------------------------------");

    }

    @Test
    public void test43_PromeniStavkaIznajmljivanjeNormalno() {
        System.out.println("Provera da li ce stavka iznajmljivanja biti normalno promenjena");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 25);
        Date d = calendar.getTime();

        StavkaIznajmljivanja i = new StavkaIznajmljivanja(new Iznajmljivanje(888), 1, new Date(), d, new Vozilo(888));
        try {
            em.promeniEntitet(i);
        } catch (Exception e) {
            fail("Stavka iznajmljivanja nije promenjena iako zadovoljava vrednosna ogranicenja");
        }
        System.out.println("Stavka iznajmljivanja je korektno promenjena i zadovoljava vrednosna ogranicenja");
        System.out.println("---------------------------------------------------------------------------------");

    }

    @Test
    public void test44_PromeniTerminDezurstvaNormalno() {
        System.out.println("Provera da li ce termin dezurstva biti normalno promenjen");

        TerminDezurstva td = new TerminDezurstva(2, "moja nova napomena", TipTerminaEnum.NOC);
        try {
            em.promeniEntitet(td);
        } catch (Exception e) {
            fail("Termin Dezurstva nije promenjen iako zadovoljava vrednosna ogranicenja");
        }
        System.out.println("Termin Dezurstva je korektno promenjen i zadovoljava vrednosna ogranicenja");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test45_PromeniVozacNormalno() {
        System.out.println("Provera da li ce vozac biti normalno promenjen");

        Vozac v = new Vozac(888, "Srdjan", "Srecmevic", "novimail@gmail.com", new Dozvola(888));
        try {
            em.promeniEntitet(v);
        } catch (Exception e) {
            fail("Vozac nije promenjen iako zadovoljava vrednosna ogranicenja");
        }
        System.out.println("Vozac je korektno promenjen i zadovoljava vrednosna ogranicenja");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test46_PromeniZaposleniNormalno() {
        System.out.println("Provera da li ce zaposleni biti normalno promenjen");

        byte[] salt = PasswordHasher.generateSalt();
        Zaposleni zaposleni = new Zaposleni(888, "Sinisa", "Vlajic", "sinisav@fon.bg.ac.rs", PasswordHasher.hash("novasifra", salt), Base64.getEncoder().encodeToString(salt));
        try {
            em.promeniEntitet(zaposleni);
        } catch (Exception e) {
            fail("Zaposleni nije promenjen iako zadovoljava vrednosna ogranicenja");
        }
        System.out.println("Zaposleni je korektno promenjen i zadovoljava vrednosna ogranicenja");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test47_PromeniVoziloNormalno() {
        System.out.println("Provera da li ce vozilo biti normalno promenjeno");

        Vozilo v = new Vozilo(2, "Minibus", "Fiat", 3150, 2008, "Multipla", KategorijaEnum.BUDZET,50);
        try {
            em.promeniEntitet(v);
        } catch (Exception e) {
            fail("Vozilo nije promenjeno iako zadovoljava vrednosna ogranicenja");
        }
        System.out.println("Vozilo je korektno promenjeno i zadovoljava vrednosna ogranicenja");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test48() {
        System.out.println("multi bacivanje test");
        try {
            em.kreirajEntitet(vozaci);
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("lol " + e);
        }
        System.out.println("ok");
    }

    @Test
    public void test49() {
        System.out.println("multi brisanje test");
        try {
            em.obrisiEntitet(vozaci);
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("lol " + e);
        }
        System.out.println("ok");
    }

}
