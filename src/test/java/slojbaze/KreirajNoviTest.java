/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package slojbaze;

import iznajmljivanjeapp.domain.TerminDezurstva;
import iznajmljivanjeapp.domain.Zaposleni;
import iznajmljivanjeapp.domain.StavkaIznajmljivanja;
import iznajmljivanjeapp.domain.Vozilo;
import iznajmljivanjeapp.domain.Dozvola;
import iznajmljivanjeapp.domain.Smena;
import iznajmljivanjeapp.domain.Iznajmljivanje;
import iznajmljivanjeapp.domain.Vozac;
import iznajmljivanjeapp.domain.enumeracije.TipTerminaEnum;
import iznajmljivanjeapp.domain.enumeracije.KategorijaEnum;
import iznajmljivanjeapp.bezbednost.PasswordHasher;
import framework.config.AppConfig;
import java.sql.Statement;
import framework.orm.ConnectionPool;
import framework.orm.EntityManager;
import java.sql.Connection;
import java.sql.SQLException;
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
public class KreirajNoviTest {

    public static EntityManager em;
    public static AppConfig cfg = new AppConfig("projektovanjesoftvera_seminarski","projektovanjesoftvera_seminarski_test");
    
    private static void ocistiTestBazu(Connection con) {
        try (Statement stmt = con.createStatement()) {

            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            stmt.execute("TRUNCATE TABLE dozvola");
            stmt.execute("TRUNCATE TABLE iznajmljivanje");
            stmt.execute("TRUNCATE TABLE stavkaiznajmljivanja");
            stmt.execute("TRUNCATE TABLE termindezurstva");
            stmt.execute("TRUNCATE TABLE vozac");
            stmt.execute("TRUNCATE TABLE vozilo");
            stmt.execute("TRUNCATE TABLE zaposleni");
            stmt.execute("TRUNCATE TABLE zapter");

            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

        } catch (SQLException e) {

            fail("Greska pri ciscenju podataka iz test baze: " + e.getMessage());

        }
    }

    public KreirajNoviTest() {
    }

    @BeforeClass
    public static void setUpClass() {

        //koristimo pravu bazu, integration test
        ConnectionPool.initialize(cfg,true);
        em = new EntityManager(cfg);

        System.out.println("Otvaram konekciju sa test bazom...");
        
        try(Connection con = ConnectionPool.getConnection()) {
            
            System.out.println("Uspesno je uspostavljena konekcija sa test bazom podataka.");

            System.out.println("Brisem sve podatke iz test baze...");
            
            ocistiTestBazu(con);
            
            System.out.println("Uspesno je resetovana test baza podataka.");
            
        } catch (SQLException ex) {
            fail("Greska pri otvaranju konekcije sa test bazom: " + ex.getMessage());
        }

        
        System.out.println("---------------------------------------------------------------------------------");

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

    @Test
    public void test01_KreirajNoviDozvola() {
        System.out.println("Ubacivanje ispravne dozvole");

        Dozvola dozvola = new Dozvola('B');
        try {
            em.kreirajEntitet(dozvola);
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Ubacivanje ispravne dozvole nije uspelo: " + e);
        }
        System.out.println("Ubacivanje ispravne dozvole uspesno");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test02_KreirajNoviDozvolaNepostojecaKategorija() {
        System.out.println("Ubacivanje neispravne dozvole - nepostojeca kategorija");
        Dozvola dozvola = new Dozvola('Z');
        try {
            em.kreirajEntitet(dozvola);
        }
        catch(Exception e) {
            System.out.println("Ubacivanje neispravne dozvole sa nepostojecom kategorijom nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravne dozvole sa nepostojecom kategorijom je uspelo.");
    }

    @Test
    public void test03_KreirajNoviDozvolaNullKategorija() {
        System.out.println("Ubacivanje neispravne dozvole - null kategorija");
        Dozvola dozvola = new Dozvola();
        try {
            em.kreirajEntitet(dozvola);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravne dozvole sa null kategorijom nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravne dozvole sa null kategorijom je uspelo.");
    }

    @Test
    public void test04_KreirajNoviVozac() {
        System.out.println("Ubacivanje ispravnog vozaca");
        Vozac vozac = new Vozac("Marko", "Markovic", "markomarkovic@gmail.com", new Dozvola(1));
        try {
            em.kreirajEntitet(vozac);
        } catch (Exception e) {
            fail("Ubacivanje ispravnog vozaca nije uspelo.");
        }
        System.out.println("Ubacivanje ispravnog vozaca je uspelo.");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test05_KreirajNoviVozacLosSpoljnjikljuc() {
        System.out.println("Ubacivanje neispravnog vozaca - nevalidan spoljnji kljuc");
        Vozac vozac = new Vozac("Jovan", "Jovanovic", "jovanjovanovic@gmail.com", new Dozvola(-555));
        try {
            em.kreirajEntitet(vozac);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog vozaca nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog vozaca je uspelo.");
    }

    @Test
    public void test06_KreirajNoviVozacNullEmail() {
        System.out.println("Ubacivanje neispravnog vozaca - null email");
        Vozac vozac = new Vozac("Jovan", "Jovanovic", null, new Dozvola(1));
        try {
            em.kreirajEntitet(vozac);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog vozaca nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog vozaca je uspelo.");
    }

    @Test
    public void test07_KreirajNoviVozacLosEmail() {
        System.out.println("Ubacivanje neispravnog vozaca - neispravan email");
        Vozac vozac = new Vozac("Jovan", "Jovanovic", "fjfjsekjesf", new Dozvola(1));
        try {
            em.kreirajEntitet(vozac);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog vozaca nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog vozaca je uspelo.");
    }

    @Test
    public void test08_KreirajNoviVozacNullIme() {
        System.out.println("Ubacivanje neispravnog vozaca - null ime");
        Vozac vozac = new Vozac(null, "Jovanovic", "jovanjovanovic@gmail.com", new Dozvola(1));
        try {
            em.kreirajEntitet(vozac);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog vozaca nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog vozaca je uspelo.");
    }

    @Test
    public void test09_KreirajNoviVozacNullPrezime() {
        System.out.println("Ubacivanje neispravnog vozaca - null prezime");
        Vozac vozac = new Vozac("Jovan", null, "jovanjovanovic@gmail.com", new Dozvola(1));
        try {
            em.kreirajEntitet(vozac);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog vozaca nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog vozaca je uspelo.");
    }

    @Test
    public void test10_KreirajNoviZaposleni() {
        System.out.println("Ubacivanje ispravnog zaposlenog");
        byte[] salt = PasswordHasher.generateSalt();
        Zaposleni zaposleni = new Zaposleni("Ivan", "Ivanovic", "ivanivanovic@gmail.com", PasswordHasher.hash("sifra123", salt), Base64.getEncoder().encodeToString(salt));
        try {
            em.kreirajEntitet(zaposleni);
        } catch (Exception e) {
            fail("Ubacivanje ispravnog zaposlenog nije uspelo.");
        }
        System.out.println("Ubacivanje ispravnog zaposlenog je uspelo.");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test11_KreirajNoviZaposleniNullSifra() {
        System.out.println("Ubacivanje neispravnog zaposlenog - null sifra");
        byte[] salt = PasswordHasher.generateSalt();
        Zaposleni zaposleni = new Zaposleni("Ivan", "Ivanovic", "ivanivanovic@gmail.com", null, Base64.getEncoder().encodeToString(salt));
        try {
            em.kreirajEntitet(zaposleni);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog zaposlenog nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog zaposlenog je uspelo.");
    }

    @Test
    public void test12_KreirajNoviZaposleniNullSalt() {
        System.out.println("Ubacivanje neispravnog zaposlenog - null salt");
        byte[] salt = PasswordHasher.generateSalt();
        Zaposleni zaposleni = new Zaposleni("Ivan", "Ivanovic", "ivanivanovic@gmail.com", PasswordHasher.hash("sifra123", salt), null);
        try {
            em.kreirajEntitet(zaposleni);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog zaposlenog nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog zaposlenog je uspelo.");
    }

    @Test
    public void test13_KreirajNoviZaposleniNullIme() {
        System.out.println("Ubacivanje neispravnog zaposlenog - null ime");
        byte[] salt = PasswordHasher.generateSalt();
        Zaposleni zaposleni = new Zaposleni((Integer) null, "Ivanovic", "ivanivanovic@gmail.com", PasswordHasher.hash("sifra123", salt), Base64.getEncoder().encodeToString(salt));
        try {
            em.kreirajEntitet(zaposleni);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog zaposlenog nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog zaposlenog je uspelo.");
    }

    @Test
    public void test14_KreirajNoviZaposleniNullPrezime() {
        System.out.println("Ubacivanje neispravnog zaposlenog - null prezime");
        byte[] salt = PasswordHasher.generateSalt();
        Zaposleni zaposleni = new Zaposleni("Ivan", null, "ivanivanovic@gmail.com", PasswordHasher.hash("sifra123", salt), Base64.getEncoder().encodeToString(salt));
        try {
            em.kreirajEntitet(zaposleni);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog zaposlenog nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog zaposlenog je uspelo.");
    }

    @Test
    public void test15_KreirajNoviZaposleniNullEmail() {
        System.out.println("Ubacivanje neispravnog zaposlenog - null preziime");
        byte[] salt = PasswordHasher.generateSalt();
        Zaposleni zaposleni = new Zaposleni("Ivan", "Ivanovic", null, PasswordHasher.hash("sifra123", salt), Base64.getEncoder().encodeToString(salt));
        try {
            em.kreirajEntitet(zaposleni);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog zaposlenog nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog zaposlenog je uspelo.");
    }

    @Test
    public void test16_KreirajNoviZaposleniLosEmail() {
        System.out.println("Ubacivanje neispravnog zaposlenog - neispravan email");
        byte[] salt = PasswordHasher.generateSalt();
        Zaposleni zaposleni = new Zaposleni("Ivan", "Ivanovic", "efnsjefj", PasswordHasher.hash("sifra123", salt), Base64.getEncoder().encodeToString(salt));
        try {
            em.kreirajEntitet(zaposleni);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog zaposlenog nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog zaposlenog je uspelo.");
    }

    @Test
    public void test17_KreirajNoviIznajmljivanje() {
        System.out.println("Ubacivanje ispravnog iznajmljivanja");
        Iznajmljivanje iznajmljivanje = new Iznajmljivanje(new Date(), 9999.0, new Zaposleni(1), new Vozac(1));
        try {
            em.kreirajEntitet(iznajmljivanje);
        } catch (Exception e) {
            fail("Ubacivanje ispravnog iznajmljivanja nije uspelo.");
        }
        System.out.println("Ubacivanje ispravnog iznajmljivanja je uspelo.");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test18_KreirajNoviIznajmljivanjeLosSpoljnjikljucZaposleni() {
        System.out.println("Ubacivanje neispravnog iznajmljivanja - nevalidan spoljnji kljuc ka zaposlenom");
        Iznajmljivanje iznajmljivanje = new Iznajmljivanje(new Date(), 9999.0, new Zaposleni(1), new Vozac(-51));
        try {
            em.kreirajEntitet(iznajmljivanje);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog iznajmljivanja nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog iznajmljivanja je uspelo.");
    }

    @Test
    public void test19_KreirajNoviIznajmljivanjeLosSpoljnjikljucVozac() {
        System.out.println("Ubacivanje neispravnog iznajmljivanja - nevalidan spoljnji kljuc ka vozacu");
        Iznajmljivanje iznajmljivanje = new Iznajmljivanje(new Date(), 9999.0, new Zaposleni(-51), new Vozac(1));
        try {
            em.kreirajEntitet(iznajmljivanje);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog iznajmljivanja nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog iznajmljivanja je uspelo.");
    }

    @Test
    public void test20_KreirajNoviIznajmljivanjeNullDatum() {
        System.out.println("Ubacivanje neispravnog iznajmljivanja - null datum");
        Iznajmljivanje iznajmljivanje = new Iznajmljivanje(null, 9999.0, new Zaposleni(1), new Vozac(1));
        try {
            em.kreirajEntitet(iznajmljivanje);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog iznajmljivanja nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog iznajmljivanja je uspelo.");
    }

    @Test
    public void test21_KreirajNoviVozilo() {
        System.out.println("Ubacivanje ispravnog vozila");
        Vozilo vozilo = new Vozilo("Automobil", "Mercedes", 15000.0, 2016, "S Klasa", KategorijaEnum.LUKSUZ);
        try {
            em.kreirajEntitet(vozilo);
        } catch (Exception e) {
            fail("Ubacivanje ispravnog vozila nije uspelo.");
        }
        System.out.println("Ubacivanje ispravnog vozila je uspelo.");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test22_KreirajNoviVoziloNullKlasa() {
        System.out.println("Ubacivanje neispravnog vozila - null klasa");
        Vozilo vozilo = new Vozilo(null, "Mercedes", 15000.0, 2016, "S Klasa", KategorijaEnum.LUKSUZ);
        try {
            em.kreirajEntitet(vozilo);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog vozila nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog vozila je uspelo.");
    }

    @Test
    public void test23_KreirajNoviVoziloNullProizvodjac() {
        System.out.println("Ubacivanje neispravnog vozila - null proizvodjac");
        Vozilo vozilo = new Vozilo("Automobil", null, 15000.0, 2016, "S Klasa", KategorijaEnum.LUKSUZ);
        try {
            em.kreirajEntitet(vozilo);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog vozila nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog vozila je uspelo.");
    }

    @Test
    public void test24_KreirajNoviVoziloNeispravnaCena() {
        System.out.println("Ubacivanje neispravnog vozila - neispravna kupovna cena");
        Vozilo vozilo = new Vozilo("Automobil", "Mercedes", -15000.0, 2016, "S Klasa", KategorijaEnum.LUKSUZ);
        try {
            em.kreirajEntitet(vozilo);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog vozila nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog vozila je uspelo.");
    }

    @Test
    public void test25_KreirajNoviVoziloNeispravnoGodiste() {
        System.out.println("Ubacivanje neispravnog vozila - neispravno godiste(stariji od 2006)");
        Vozilo vozilo = new Vozilo("Automobil", "Mercedes", 15000.0, 2003, "S Klasa", KategorijaEnum.LUKSUZ);
        try {
            em.kreirajEntitet(vozilo);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog vozila nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog vozila je uspelo.");
    }

    @Test
    public void test26_KreirajNoviVoziloNullImeModela() {
        boolean flag = false;
        System.out.println("Ubacivanje neispravnog vozila - null ime modela");
        Vozilo vozilo = new Vozilo("Motor", "Audi", 15000.0, 2016, null, KategorijaEnum.LUKSUZ);
        try {
            em.kreirajEntitet(vozilo);
        }
        catch(Exception e) {
            System.out.println("Ubacivanje neispravnog vozila nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            flag = true;
        }
        if(!flag)
            fail("Ubacivanje neispravnog vozila je uspelo.");

        vozilo.setImeModela("stagod");
        try {
            em.kreirajEntitet(vozilo);
        }
        catch(Exception e) {
            fail("fail");
        }
    }

    @Test
    public void test27_KreirajNoviVoziloNullEnum() {
        System.out.println("Ubacivanje neispravnog vozila - null kategorija");
        Vozilo vozilo = new Vozilo("Automobil", "Mercedes", 15000.0, 2016, "S Klasa", null);
        try {
            em.kreirajEntitet(vozilo);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog vozila nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog vozila je uspelo.");
    }

    @Test
    public void test28_KreirajNoviVoziloNeispravnaKlasa() {
        System.out.println("Ubacivanje neispravnog vozila - neispravna klasa");
        Vozilo vozilo = new Vozilo("Svemirski brod", "Mercedes", 15000.0, 2016, "S Klasa", KategorijaEnum.BUDZET);
        try {
            em.kreirajEntitet(vozilo);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog vozila nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog vozila je uspelo.");
    }

    @Test
    public void test29_KreirajNoviVoziloNeispravanProizvodjac() {
        System.out.println("Ubacivanje neispravnog vozila - neispravan proizvodjac");
        Vozilo vozilo = new Vozilo("Automobil", "Jugo", 1555000.0, 2025, "S Klasa", KategorijaEnum.LUKSUZ);
        try {
            em.kreirajEntitet(vozilo);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog vozila nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog vozila je uspelo.");
    }

    @Test
    public void test30_KreirajNoviVoziloDaciaMotor() {
        System.out.println("Ubacivanje neispravnog vozila - motor dacia");
        Vozilo vozilo = new Vozilo("Motor", "Dacia", 25.0, 2025, "S Klasa", KategorijaEnum.LUKSUZ);
        try {
            em.kreirajEntitet(vozilo);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog vozila nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog vozila je uspelo.");
    }

    @Test
    public void test31_KreirajNoviStavkaIznajmljivanja() {
        System.out.println("Ubacivanje ispravne stavke iznajmljivanja");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 15);
        Date futureDate = calendar.getTime();

        StavkaIznajmljivanja stavkaIznajmljivanja = new StavkaIznajmljivanja(new Iznajmljivanje(1), 1, new Date(), futureDate, 999.0, new Vozilo(1));
        try {
            em.kreirajEntitet(stavkaIznajmljivanja);
        } catch (Exception e) {
            fail("Ubacivanje ispravne stavke iznajmljivanja nije uspelo.");
        }
        System.out.println("Ubacivanje ispravne stavke iznajmljivanja je uspelo.");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test32_KreirajNoviStavkaIznajmljivanjaLosSpoljnjiKljuc() {
        System.out.println("Ubacivanje neispravne stavke iznajmljivanja - nevalidan spoljnji kljuc");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 15);
        Date futureDate = calendar.getTime();

        StavkaIznajmljivanja stavkaIznajmljivanja = new StavkaIznajmljivanja(new Iznajmljivanje(-1), 1, new Date(), futureDate, 999.0, new Vozilo(1));
        try {
            em.kreirajEntitet(stavkaIznajmljivanja);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravne stavke iznajmljivanja nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravne stavke iznajmljivanja je uspelo.");
    }

    @Test
    public void test33_KreirajNoviStavkaIznajmljivanjaPremaloDana() {
        System.out.println("Ubacivanje neispravne stavke iznajmljivanja - premalo dana");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        Date futureDate = calendar.getTime();

        StavkaIznajmljivanja stavkaIznajmljivanja = new StavkaIznajmljivanja(new Iznajmljivanje(1), 1, new Date(), futureDate, 999.0, new Vozilo(1));
        try {
            em.kreirajEntitet(stavkaIznajmljivanja);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravne stavke iznajmljivanja nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravne stavke iznajmljivanja je uspelo.");
    }

    @Test
    public void test34_KreirajNoviStavkaIznajmljivanjaPreviseDana() {
        System.out.println("Ubacivanje neispravne stavke iznajmljivanja - previse dana");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 62);
        Date futureDate = calendar.getTime();

        StavkaIznajmljivanja stavkaIznajmljivanja = new StavkaIznajmljivanja(new Iznajmljivanje(1), 1, new Date(), futureDate, 999.0, new Vozilo(1));
        try {
            em.kreirajEntitet(stavkaIznajmljivanja);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravne stavke iznajmljivanja nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravne stavke iznajmljivanja je uspelo.");
    }

    @Test
    public void test35_KreirajNoviStavkaIznajmljivanjaLosIznos() {
        System.out.println("Ubacivanje neispravne stavke iznajmljivanja - los iznos");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 15);
        Date futureDate = calendar.getTime();

        StavkaIznajmljivanja stavkaIznajmljivanja = new StavkaIznajmljivanja(new Iznajmljivanje(1), 1, new Date(), futureDate, -999.0, new Vozilo(1));
        try {
            em.kreirajEntitet(stavkaIznajmljivanja);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravne stavke iznajmljivanja nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravne stavke iznajmljivanja je uspelo.");
    }

    @Test
    public void test36_KreirajNoviStavkaIznajmljivanjaNullDatumPocetak() {
        System.out.println("Ubacivanje neispravne stavke iznajmljivanja - null datum pocetka");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 15);
        Date futureDate = calendar.getTime();

        StavkaIznajmljivanja stavkaIznajmljivanja = new StavkaIznajmljivanja(new Iznajmljivanje(1), 1, null, futureDate, -999.0, new Vozilo(1));
        try {
            em.kreirajEntitet(stavkaIznajmljivanja);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravne stavke iznajmljivanja nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravne stavke iznajmljivanja je uspelo.");
    }

    @Test
    public void test37_KreirajNoviStavkaIznajmljivanjaNullDatumZavrsetak() {
        System.out.println("Ubacivanje neispravne stavke iznajmljivanja - null datum zavrsetka");

        StavkaIznajmljivanja stavkaIznajmljivanja = new StavkaIznajmljivanja(new Iznajmljivanje(1), 1, new Date(), null, -999.0, new Vozilo(1));
        try {
            em.kreirajEntitet(stavkaIznajmljivanja);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravne stavke iznajmljivanja nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravne stavke iznajmljivanja je uspelo.");
    }

    @Test
    public void test38_KreirajNoviTerminDezurstva() {

        System.out.println("Ubacivanje ispravnog termina dezurstva");
        TerminDezurstva terminDezurstva = new TerminDezurstva("moja napomena", TipTerminaEnum.PREPODNE);
        try {
            em.kreirajEntitet(terminDezurstva);
        } catch (Exception e) {
            fail("Ubacivanje ispravnog termina dezurstva nije uspelo.");
        }
        System.out.println("Ubacivanje ispravnog termina dezurstva je uspelo.");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test39_KreirajNoviTerminDezurstvaBezNapomene() {

        System.out.println("Ubacivanje ispravnog termina dezurstva - bez napomene");
        TerminDezurstva terminDezurstva = new TerminDezurstva(TipTerminaEnum.POPODNE);
        try {
            em.kreirajEntitet(terminDezurstva);
        } catch (Exception e) {
            fail("Ubacivanje ispravnog termina dezurstva nije uspelo.");
        }
        System.out.println("Ubacivanje ispravnog termina dezurstva je uspelo.");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test40_KreirajNoviZapTer() {

        System.out.println("Ubacivanje ispravnog zapter");
        Smena zapTer = new Smena(new Date(), new Zaposleni(1), new TerminDezurstva(1), false, 6, 0);
        try {
            em.kreirajEntitet(zapTer);
        } catch (Exception e) {
            fail("Ubacivanje ispravnog termina dezurstva nije uspelo.");
        }
        System.out.println("Ubacivanje ispravnog termina dezurstva je uspelo.");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test41_KreirajNoviZapTerLosSpoljnjiKljucZaposleni() {

        System.out.println("Ubacivanje neispravnog zapter - nevalidan spoljnji kljuc ka zaposlenom");
        Smena zapTer = new Smena(new Date(), new Zaposleni(-1), new TerminDezurstva(1), false, 6, 0);
        try {
            em.kreirajEntitet(zapTer);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog zapter nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog zapter je uspelo.");
    }

    @Test
    public void test42_KreirajNoviZapTerLosSpoljnjiKljucTerminDezurstva() {

        System.out.println("Ubacivanje neispravnog zapter - nevalidan spoljnji kljuc ka terminu dezurstva");
        Smena zapTer = new Smena(new Date(), new Zaposleni(1), new TerminDezurstva(-1), false, 6, 0);
        try {
            em.kreirajEntitet(zapTer);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog zapter nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog zapter je uspelo.");
    }

    @Test
    public void test43_KreirajNoviZapTerVanredniBezBonusa() {

        System.out.println("Ubacivanje neispravnog zapter - vanredan bez bonusa");
        Smena zapTer = new Smena(new Date(), new Zaposleni(1), new TerminDezurstva(1), true, 6, 0);
        try {
            em.kreirajEntitet(zapTer);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog zapter nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog zapter je uspelo.");
    }

    @Test
    public void test44_KreirajNoviZapTerNevanredniSaBonusom() {

        System.out.println("Ubacivanje neispravnog zapter - nevanredan sa bonusom");
        Smena zapTer = new Smena(new Date(), new Zaposleni(1), new TerminDezurstva(1), false, 6, 1000);
        try {
            em.kreirajEntitet(zapTer);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog zapter nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog zapter je uspelo.");
    }

    @Test
    public void test45_KreirajNoviZapTerMaliSati() {

        System.out.println("Ubacivanje neispravnog zapter - premali broj sati");
        Smena zapTer = new Smena(new Date(), new Zaposleni(1), new TerminDezurstva(1), false, 2, 0);
        try {
            em.kreirajEntitet(zapTer);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog zapter nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog zapter je uspelo.");
    }

    @Test
    public void test46_KreirajNoviZapTerVelikiSati() {

        System.out.println("Ubacivanje neispravnog zapter - preveliki broj sati");
        Smena zapTer = new Smena(new Date(), new Zaposleni(1), new TerminDezurstva(1), false, 12, 0);
        try {
            em.kreirajEntitet(zapTer);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog zapter nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog zapter je uspelo.");
    }

    @Test
    public void test47_KreirajNoviZapTerNullDatum() {

        System.out.println("Ubacivanje neispravnog zapter - null datum");
        Smena zapTer = new Smena(null, new Zaposleni(1), new TerminDezurstva(1), false, 6, 0);
        try {
            em.kreirajEntitet(zapTer);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog zapter nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog zapter je uspelo.");
    }

    @Test
    public void test48_KreirajNoviZapTerVanredniBonus() {

        System.out.println("Ubacivanje ispravnog zapter - vanredni sa korektnim bonusom");
        Smena zapTer = new Smena(new Date(), new Zaposleni(1), new TerminDezurstva(2), true, 6, 1200);
        try {
            em.kreirajEntitet(zapTer);
        } catch (Exception e) {
            fail("Ubacivanje ispravnog termina dezurstva nije uspelo.");
        }
        System.out.println("Ubacivanje ispravnog termina dezurstva je uspelo.");
        System.out.println("---------------------------------------------------------------------------------");
    }

    @Test
    public void test49_KreirajNoviZapTerVanredniLosBonus() {

        System.out.println("Ubacivanje neispravnog zapter - vanredni sa premalim bonusom");
        Smena zapTer = new Smena(new Date(), new Zaposleni(1), new TerminDezurstva(1), true, 6, 899);
        try {
            em.kreirajEntitet(zapTer);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog termina dezurstva nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog termina dezurstva je uspelo.");
    }

    @Test
    public void test50_KreirajNoviZapTerVanredniLosBonus() {

        System.out.println("Ubacivanje neispravnog zapter - vanredni sa prevelikim bonusom");
        Smena zapTer = new Smena(new Date(), new Zaposleni(1), new TerminDezurstva(1), true, 6, 1501);
        try {
            em.kreirajEntitet(zapTer);
        } catch (Exception e) {
            System.out.println("Ubacivanje neispravnog termina dezurstva nije uspelo.");
            System.out.println("---------------------------------------------------------------------------------");
            return;
        }
        fail("Ubacivanje neispravnog termina dezurstva je uspelo.");
    }

}
