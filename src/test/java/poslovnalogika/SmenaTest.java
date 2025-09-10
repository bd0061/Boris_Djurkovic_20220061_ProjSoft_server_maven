package poslovnalogika;

import static poslovnalogika.PoslovnaLogikaTestSuite.sendRequest;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import framework.model.KriterijumDescriptor;
import framework.model.network.NetworkRequest;
import framework.model.network.NetworkResponse;

import iznajmljivanjeapp.domain.Smena;
import iznajmljivanjeapp.domain.Zaposleni;
import iznajmljivanjeapp.domain.TerminDezurstva;
import iznajmljivanjeapp.domain.enumeracije.TipTerminaEnum;
import iznajmljivanjeapp.repositories.inmemoryrepositories.*;
import org.junit.Before;
import org.junit.Test;

public class SmenaTest {


    @Before
    public void clearRepositories() {
        ZaposleniInMemoryRepository.clear();
        TerminDezurstvaInMemoryRepository.clear();
        SmenaInMemoryRepository.clear();
    }

    private List<Zaposleni> napraviTestZaposleni(int broj) throws Exception {
        assert broj > 0;
        List<Zaposleni> zs = new ArrayList<>();
        for(int i = 0; i < broj; i++) {
            Zaposleni z = new Zaposleni("Ime " + broj, "Prezime " + broj, "test" + broj + "@email.com", "mojakulsifra" + broj);
            NetworkResponse r = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("zaposleni/kreiraj",z));
            assertTrue(r.success);
            assertTrue(r.payload instanceof Zaposleni);
            z = (Zaposleni) r.payload;
            assertNotNull(z.getId());
            zs.add(z);
        }

        return zs;
    }

    private Zaposleni napraviTestZaposleni() throws Exception {

        return napraviTestZaposleni(1).get(0);
    }

    private TerminDezurstva napraviTestTermin() throws Exception {
        TerminDezurstva td = new TerminDezurstva("Test napomena", TipTerminaEnum.PREPODNE);
        NetworkResponse r = PoslovnaLogikaTestSuite.sendRequest(new NetworkRequest("termindezurstva/kreiraj",td));
        assertTrue(r.success);
        assertTrue(r.payload instanceof TerminDezurstva);
        td = (TerminDezurstva) r.payload;
        assertNotNull(td.getId());
        return td;
    }

    @Test
    public void testKreirajSmena_nullPayload() throws Exception {
        NetworkRequest req = new NetworkRequest("smena/kreiraj", null);
        NetworkResponse resp = sendRequest(req);
        assertFalse(resp.success);
        assertEquals("Sistem ne može da zapamti smenu.", resp.responseMessage);
        assertNull(resp.payload);
    }

    @Test
    public void testKreirajSmena_wrongType() throws Exception {
        NetworkRequest req = new NetworkRequest("smena/kreiraj", "los objekat");
        NetworkResponse resp = sendRequest(req);
        assertFalse(resp.success);
        assertEquals("Sistem ne može da zapamti smenu.", resp.responseMessage);
    }

    @Test
    public void testKreirajSmena_success() throws Exception {
        Zaposleni zaposleni = napraviTestZaposleni();
        TerminDezurstva termin = napraviTestTermin();
        Date datum = new Date();

        Smena s = new Smena(datum, zaposleni, termin, true, 6, 1000);
        NetworkRequest req = new NetworkRequest("smena/kreiraj", s);
        NetworkResponse resp = sendRequest(req);
        assertTrue(resp.success);
        assertEquals("Sistem je zapamtio smenu.", resp.responseMessage);
        assertTrue(resp.payload instanceof Smena[]);

        Smena returned = ((Smena[]) resp.payload)[0];
        assertNotNull(returned.getDatum());
        assertEquals(zaposleni.getId(), returned.getZaposleni().getId());
        assertEquals(termin.getId(), returned.getTerminDezurstva().getId());
        assertEquals(Integer.valueOf(6), returned.getBrojSati());
        assertTrue(returned.isVanredan());
        assertEquals(Integer.valueOf(1000), returned.getFiksniBonus());
    }

    @Test
    public void testKreirajSmena_fail_datumproslost() throws Exception {
        Zaposleni zaposleni = napraviTestZaposleni();
        TerminDezurstva termin = napraviTestTermin();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -10);
        Date datum = calendar.getTime();

        Smena s = new Smena(datum, zaposleni, termin, true, 6, 1000);
        NetworkRequest req = new NetworkRequest("smena/kreiraj", s);
        NetworkResponse resp = sendRequest(req);
        assertFalse(resp.success);
        assertEquals("Smene se mogu kreirati samo za nadolazeće dane.", resp.responseMessage);
    }


    private static Date addOne(Date d) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }
    @Test
    public void testKreirajViseSmena_success() throws Exception {
        List<Zaposleni> zaposleni = napraviTestZaposleni(10);
        TerminDezurstva termin = napraviTestTermin();
        Date datum = new Date();
        List<Smena> smene = new ArrayList<>();

        for(Zaposleni z : zaposleni) {
            Smena s = new Smena(datum, z, termin, false, 6, 0);
            smene.add(s);
            datum = addOne(datum);
        }

        NetworkRequest req = new NetworkRequest("smena/kreiraj", smene);
        NetworkResponse resp = sendRequest(req);
        assertTrue(resp.success);
        assertEquals("Sistem je zapamtio smene.", resp.responseMessage);
        assertTrue(resp.payload instanceof Smena[]);

        Smena[] returned = ((Smena[]) resp.payload);
        assertEquals(returned.length, smene.size());

        for(int i = 0; i < returned.length; i++) {
            assertNotNull(returned[i].getDatum());
            assertEquals(zaposleni.get(i).getId(), returned[i].getZaposleni().getId());
            assertEquals(termin.getId(), returned[i].getTerminDezurstva().getId());
            assertEquals(Integer.valueOf(6), returned[i].getBrojSati());
            assertFalse(returned[i].isVanredan());
            assertEquals(Integer.valueOf(0), returned[i].getFiksniBonus());
        }
    }

    @Test
    public void testObrisiViseSmena_success() throws Exception {
        List<Zaposleni> zaposleni = napraviTestZaposleni(10);
        TerminDezurstva termin = napraviTestTermin();
        Date datum = new Date();
        List<Smena> smene = new ArrayList<>();

        for(Zaposleni z : zaposleni) {
            Smena s = new Smena(datum, z, termin, false, 6, 0);
            smene.add(s);
            datum = addOne(datum);
        }

        NetworkRequest req = new NetworkRequest("smena/kreiraj", smene);
        NetworkResponse resp = sendRequest(req);
        assertTrue(resp.success);
        assertEquals("Sistem je zapamtio smene.", resp.responseMessage);
        assertTrue(resp.payload instanceof Smena[]);

        Smena[] returned = ((Smena[]) resp.payload);
        assertEquals(returned.length, smene.size());

        for(int i = 0; i < returned.length; i++) {
            assertNotNull(returned[i].getDatum());
            assertEquals(zaposleni.get(i).getId(), returned[i].getZaposleni().getId());
            assertEquals(termin.getId(), returned[i].getTerminDezurstva().getId());
            assertEquals(Integer.valueOf(6), returned[i].getBrojSati());
            assertFalse(returned[i].isVanredan());
            assertEquals(Integer.valueOf(0), returned[i].getFiksniBonus());
        }

        NetworkRequest req2 = new NetworkRequest("smena/obrisi", smene);
        NetworkResponse resp2 = sendRequest(req2);
        assertTrue(resp2.success);

        NetworkResponse resp3 = sendRequest(new NetworkRequest("smena/vrati_sve", null));
        assertTrue(resp3.success);
        assertEquals("Sistem je našao smene po zadatim kriterijumima.", resp3.responseMessage);

        assertTrue(resp3.payload instanceof List);
        List<?> l = (List<?>) resp3.payload;
        assertEquals(l.size(),0);
    }





    @Test
    public void testObrisiSmena_nullPayload() throws Exception {
        NetworkRequest req = new NetworkRequest("smena/obrisi", null);
        NetworkResponse resp = sendRequest(req);
        assertFalse(resp.success);
        assertEquals("Sistem ne može da obriše smenu.", resp.responseMessage);
    }

    @Test
    public void testObrisiSmena_success() throws Exception {
        // Prvo kreiraj smenu da bi postojala za brisanje
        Zaposleni zaposleni = napraviTestZaposleni();
        TerminDezurstva termin = napraviTestTermin();
        Date datum = new Date();

        Smena s = new Smena(datum, zaposleni, termin, false, 5, 0);
        NetworkResponse createResp = sendRequest(new NetworkRequest("smena/kreiraj", s));
        Smena created = ((Smena[]) createResp.payload)[0];

        NetworkResponse deleteResp = sendRequest(new NetworkRequest("smena/obrisi", created));
        assertTrue(deleteResp.success);
        assertEquals("Sistem je obrisao smenu.", deleteResp.responseMessage);
    }

    @Test
    public void testPromeniSmena_single() throws Exception {
        Zaposleni zaposleni = napraviTestZaposleni();
        TerminDezurstva termin = napraviTestTermin();
        Date datum = new Date();

        Smena s = new Smena(datum, zaposleni, termin, false, 5, 0);
        Smena created = ((Smena[]) sendRequest(new NetworkRequest("smena/kreiraj", s)).payload)[0];

        created.setBrojSati(7);

        NetworkResponse resp = sendRequest(new NetworkRequest("smena/promeni", created));
        assertTrue(resp.success);
        assertEquals("Sistem je promenio smenu.", resp.responseMessage);
    }

    @Test
    public void testVratiSveSmene_emptyCriteria() throws Exception {
        List<Zaposleni> zaposleni = napraviTestZaposleni(10);
        TerminDezurstva termin = napraviTestTermin();
        Date datum = new Date();
        List<Smena> smene = new ArrayList<>();

        for(Zaposleni z : zaposleni) {
            Smena s = new Smena(datum, z, termin, false, 6, 0);
            smene.add(s);
            datum = addOne(datum);
        }

        NetworkRequest req = new NetworkRequest("smena/kreiraj", smene);
        NetworkResponse resp = sendRequest(req);
        assertTrue(resp.success);
        assertEquals("Sistem je zapamtio smene.", resp.responseMessage);
        assertTrue(resp.payload instanceof Smena[]);

        Smena[] returned = ((Smena[]) resp.payload);
        assertEquals(returned.length, smene.size());

        for(int i = 0; i < returned.length; i++) {
            assertNotNull(returned[i].getDatum());
            assertEquals(zaposleni.get(i).getId(), returned[i].getZaposleni().getId());
            assertEquals(termin.getId(), returned[i].getTerminDezurstva().getId());
            assertEquals(Integer.valueOf(6), returned[i].getBrojSati());
            assertFalse(returned[i].isVanredan());
            assertEquals(Integer.valueOf(0), returned[i].getFiksniBonus());
        }



        NetworkResponse response = sendRequest(new NetworkRequest("smena/vrati_sve", null));
        assertTrue(response.success);
        assertEquals("Sistem je našao smene po zadatim kriterijumima.", response.responseMessage);

        assertTrue(response.payload instanceof List);
        List<?> l = (List<?>) response.payload;
        assertEquals(l.size(),10);
    }

    @Test
    public void testVratiSveSmene_withCriteria() throws Exception {
        List<Zaposleni> zaposleni = napraviTestZaposleni(10);
        TerminDezurstva termin = napraviTestTermin();
        Date datum = new Date();
        List<Smena> smene = new ArrayList<>();

        for(int i = 0; i < zaposleni.size(); i++) {
            boolean van = i % 2 == 0;
            Smena s = new Smena(datum, zaposleni.get(i), termin, van, 6, van ? 1000 : 0);
            smene.add(s);
            datum = addOne(datum);
        }

        NetworkRequest req = new NetworkRequest("smena/kreiraj", smene);
        NetworkResponse resp = sendRequest(req);
        assertTrue(resp.success);
        assertEquals("Sistem je zapamtio smene.", resp.responseMessage);
        assertTrue(resp.payload instanceof Smena[]);

        Smena[] returned = ((Smena[]) resp.payload);
        assertEquals(returned.length, smene.size());

        for(int i = 0; i < returned.length; i++) {
            assertNotNull(returned[i].getDatum());
            assertEquals(zaposleni.get(i).getId(), returned[i].getZaposleni().getId());
            assertEquals(termin.getId(), returned[i].getTerminDezurstva().getId());
            assertEquals(Integer.valueOf(6), returned[i].getBrojSati());
            if(i % 2 == 0) {
                assertTrue(returned[i].isVanredan());
                assertEquals(Integer.valueOf(1000), returned[i].getFiksniBonus());
            }
            else {
                assertFalse(returned[i].isVanredan());
                assertEquals(Integer.valueOf(0), returned[i].getFiksniBonus());
            }

        }




        List<KriterijumDescriptor> criteria = new ArrayList<>();
        criteria.add(new KriterijumDescriptor(Smena.class, "vanredan", "=", true));
        criteria.add(new KriterijumDescriptor(Zaposleni.class, "id", "<", 6)); // primer kompleksnijeg kriterijuma

        NetworkResponse response = sendRequest(new NetworkRequest("smena/vrati_sve", criteria));

        assertTrue(response.success);
        assertEquals("Sistem je našao smene po zadatim kriterijumima.", response.responseMessage);
        assertTrue(response.payload instanceof List);

        @SuppressWarnings("unchecked")
        List<Smena> smenee = (List<Smena>) response.payload;
        assertEquals(smenee.size(),3);
    }



    @Test
    public void testProemeniViseSmena_success() throws Exception {
        List<Zaposleni> zaposleni = napraviTestZaposleni(10);
        TerminDezurstva termin = napraviTestTermin();
        Date datum = new Date();
        List<Smena> smene = new ArrayList<>();

        for(Zaposleni z : zaposleni) {
            Smena s = new Smena(datum, z, termin, false, 6, 0);
            smene.add(s);
            datum = addOne(datum);
        }

        NetworkRequest req = new NetworkRequest("smena/kreiraj", smene);
        NetworkResponse resp = sendRequest(req);
        assertTrue(resp.success);
        assertEquals("Sistem je zapamtio smene.", resp.responseMessage);
        assertTrue(resp.payload instanceof Smena[]);

        Smena[] returned = ((Smena[]) resp.payload);
        assertEquals(returned.length, smene.size());

        for(int i = 0; i < returned.length; i++) {
            assertNotNull(returned[i].getDatum());
            assertEquals(zaposleni.get(i).getId(), returned[i].getZaposleni().getId());
            assertEquals(termin.getId(), returned[i].getTerminDezurstva().getId());
            assertEquals(Integer.valueOf(6), returned[i].getBrojSati());
            assertFalse(returned[i].isVanredan());
            assertEquals(Integer.valueOf(0), returned[i].getFiksniBonus());
        }

        List<Smena> ss = new ArrayList<>();
        Smena promena = new Smena();
        promena.setBrojSati(8);
        ss.add(promena);

        for(int i = 0; i < 4; i++) {
            ss.add(new Smena(smene.get(i).getDatum(),smene.get(i).getZaposleni(),smene.get(i).getTerminDezurstva()));
        }


        NetworkRequest req2 = new NetworkRequest("smena/promeni", ss);
        NetworkResponse resp2 = sendRequest(req2);
        assertTrue(resp2.success);

        NetworkResponse resp3 = sendRequest(new NetworkRequest("smena/vrati_sve", List.of(new KriterijumDescriptor(Smena.class,"brojSati","=",8))));
        assertTrue(resp3.success);
        assertEquals("Sistem je našao smene po zadatim kriterijumima.", resp3.responseMessage);

        assertTrue(resp3.payload instanceof List);
        List<?> l = (List<?>) resp3.payload;
        assertEquals(l.size(),4);
    }
}

