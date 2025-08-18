package poslovnalogika;
import static poslovnalogika.PoslovnaLogikaTestSuite.sendRequest;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import framework.model.KriterijumDescriptor;
import iznajmljivanjeapp.repositories.inmemoryrepositories.DozvolaInMemoryRepository;
import org.junit.Test;
import framework.model.network.NetworkRequest;
import framework.model.network.NetworkResponse;
import iznajmljivanjeapp.domain.Dozvola;

public class DozvolaTest {

    private static final int PORT = 9999;

    @Test
    public void testKreirajDozvola_nullPayload() throws Exception {
        NetworkRequest req = new NetworkRequest("dozvola/kreiraj", null);
        NetworkResponse resp = sendRequest(req);
        assertFalse(resp.success);
        assertEquals("Sistem ne može da zapamti dozvolu.", resp.responseMessage);
        assertNull(resp.payload);
    }

    @Test
    public void testKreirajDozvola_wrongType() throws Exception {
        // payload is not a Dozvola
        NetworkRequest req = new NetworkRequest("dozvola/kreiraj", "los objekat");
        NetworkResponse resp = sendRequest(req);
        assertFalse(resp.success);
        assertEquals("Sistem ne može da zapamti dozvolu.", resp.responseMessage);
    }

    @Test
    public void testKreirajDozvola_success() throws Exception {
        Dozvola d = new Dozvola('A');
        NetworkRequest req = new NetworkRequest("dozvola/kreiraj", d);
        NetworkResponse resp = sendRequest(req);
        assertTrue(resp.success);
        assertEquals("Sistem je zapamtio dozvolu.", resp.responseMessage);
        assertTrue(resp.payload instanceof Dozvola);
        Dozvola returned = (Dozvola) resp.payload;
        assertNotNull(returned.getId());   // kreiran objekat ima popunjen id
        assertEquals(Character.valueOf('A'), returned.getKategorija());
    }

    @Test
    public void testObrisiDozvola_nullPayload() throws Exception {
        NetworkRequest req = new NetworkRequest("dozvola/obrisi", null);
        NetworkResponse resp = sendRequest(req);
        assertFalse(resp.success);
        assertEquals("Sistem ne može da obriše dozvolu.", resp.responseMessage);
    }

    @Test
    public void testObrisiDozvola_success() throws Exception {

        Dozvola toDelete = new Dozvola('B');
        NetworkResponse createResp = sendRequest(new NetworkRequest("dozvola/kreiraj", toDelete));
        Dozvola created = (Dozvola) createResp.payload;


        NetworkResponse deleteResp = sendRequest(
                new NetworkRequest("dozvola/obrisi", new Dozvola(created.getId()))
        );
        assertTrue(deleteResp.success);
        assertEquals("Sistem je obrisao dozvolu.", deleteResp.responseMessage);
    }

    @Test
    public void testPromeniDozvola_single() throws Exception {

        Dozvola d = new Dozvola('D');
        Dozvola created = (Dozvola) sendRequest(new NetworkRequest("dozvola/kreiraj", d)).payload;


        created.setKategorija('A');
        NetworkResponse resp = sendRequest(new NetworkRequest("dozvola/promeni", created));
        assertTrue(resp.success);
        assertEquals("Sistem je promenio dozvolu.", resp.responseMessage);
    }

    @Test
    public void testVratiSveDozvola_emptyCriteria() throws Exception {
        NetworkResponse resp = sendRequest(new NetworkRequest("dozvola/vrati_sve", null));
        assertTrue(resp.success);
        assertEquals("Sistem je našao dozvole po zadatim kriterijumima.", resp.responseMessage);
        assertTrue(resp.payload instanceof List);
        List<?> list = (List<?>) resp.payload;

        assertTrue(list.size() >= 1);
    }
    @Test
    public void testVratiSveDozvola_withCriteria() throws Exception {

        DozvolaInMemoryRepository.clear();

        Dozvola d1 = new Dozvola('A');
        Dozvola d2 = new Dozvola('B');
        Dozvola d3 = new Dozvola('D');
        Dozvola d4 = new Dozvola('B');

        sendRequest(new NetworkRequest("dozvola/kreiraj", d1));
        sendRequest(new NetworkRequest("dozvola/kreiraj", d2));
        sendRequest(new NetworkRequest("dozvola/kreiraj", d3));
        sendRequest(new NetworkRequest("dozvola/kreiraj", d4));


        List<KriterijumDescriptor> criteria = new ArrayList<>();
        criteria.add(new KriterijumDescriptor(Dozvola.class, "kategorija", "=", 'B'));


        NetworkResponse response = sendRequest(new NetworkRequest("dozvola/vrati_sve", criteria));

        assertTrue(response.success);
        assertEquals("Sistem je našao dozvole po zadatim kriterijumima.", response.responseMessage);
        assertTrue(response.payload instanceof List);

        @SuppressWarnings("unchecked")
        List<Dozvola> dozvole = (List<Dozvola>) response.payload;


        assertEquals(2, dozvole.size());

        for (Dozvola d : dozvole) {
            assertEquals(Character.valueOf('B'), d.getKategorija());
        }
    }
}
