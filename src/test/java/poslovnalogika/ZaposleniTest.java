package poslovnalogika;

import static org.junit.Assert.*;

import iznajmljivanjeapp.domain.enumeracije.TipTerminaEnum;
import org.junit.*;
import iznajmljivanjeapp.domain.*;
import iznajmljivanjeapp.repositories.inmemoryrepositories.ZaposleniInMemoryRepository;
import framework.model.network.NetworkRequest;
import framework.model.network.NetworkResponse;

import java.util.Date;
import java.util.List;

public class ZaposleniTest {

    @Before
    public void reset() {
        ZaposleniInMemoryRepository.clear();
    }

    @Test
    public void prijavaUspesna() throws Exception {
        // Arrange

        String password = "tajna123";

        Zaposleni z = new Zaposleni("Pera", "Peric", "pera@gmail.com", password);
        z.setSmene(List.of(new Smena(new Date(), z, new TerminDezurstva(1, "Napomena", TipTerminaEnum.PREPODNE))));
        NetworkRequest req = new NetworkRequest("zaposleni/kreiraj",z);
        NetworkResponse res = PoslovnaLogikaTestSuite.sendRequest(req);
        assertTrue(res.success);

        NetworkRequest request = new NetworkRequest("zaposleni/prijava", new Zaposleni( "pera@gmail.com", password));
        NetworkResponse response = PoslovnaLogikaTestSuite.sendRequest(request);

        // Assert
        assertTrue(response.success);
        assertTrue(response.responseMessage.contains("Uspešna prijava"));
        assertTrue(response.payload instanceof Zaposleni);
        Zaposleni loggedIn = (Zaposleni) response.payload;
        assertEquals("pera@gmail.com", loggedIn.getEmail());
    }

    @Test
    public void prijavaNeuspesnaNepostojeciEmail() throws Exception {
        NetworkRequest request = new NetworkRequest("zaposleni/prijava", new Zaposleni( "nepostojeci@gmail.com", "tajna123"));
        NetworkResponse response = PoslovnaLogikaTestSuite.sendRequest(request);

        assertFalse(response.success);
    }

    @Test
    public void prijavaNeuspesnaPogresnaSifra() throws Exception {

        String password = "ispravnaSifra";

        Zaposleni z = new Zaposleni("Pera", "Peric", "pera2@gmail.com", password );
        z.setSmene(List.of(new Smena(new Date(), z, new TerminDezurstva(1, "Napomena", TipTerminaEnum.PREPODNE))));
        NetworkRequest req = new NetworkRequest("zaposleni/kreiraj",z);
        NetworkResponse res = PoslovnaLogikaTestSuite.sendRequest(req);
        assertTrue(res.success);

        NetworkRequest request = new NetworkRequest("zaposleni/prijava", new Zaposleni( "pera2@gmail.com", "pogresnaSifra"));
        NetworkResponse response = PoslovnaLogikaTestSuite.sendRequest(request);

        assertFalse(response.success);
    }

    @Test
    public void prijavaNeuspesnaBezSmene() throws Exception {

        String password = "tajna123";

        Zaposleni z = new Zaposleni("Mika", "Mikic", "mika@gmail.com", password);
        NetworkRequest req = new NetworkRequest("zaposleni/kreiraj",z);
        NetworkResponse res = PoslovnaLogikaTestSuite.sendRequest(req);
        assertTrue(res.success);

        NetworkRequest request = new NetworkRequest("zaposleni/prijava", new Zaposleni("mika@gmail.com", password));
        NetworkResponse response = PoslovnaLogikaTestSuite.sendRequest(request);

        assertFalse(response.success);
        assertTrue(response.responseMessage.toLowerCase().contains("nema zabeleženu smenu"));
    }

    @Test
    public void prijavaUspesnaBezSmeneAdmin() throws Exception {
        String password = "tajna123";

        Zaposleni z = new Zaposleni("Mika", "Mikic", "mika3@gmail.com", password,true);
        NetworkRequest req = new NetworkRequest("zaposleni/kreiraj",z);
        NetworkResponse res = PoslovnaLogikaTestSuite.sendRequest(req);
        assertTrue(res.success);

        NetworkRequest request = new NetworkRequest("zaposleni/prijava", new Zaposleni("mika3@gmail.com", password));
        NetworkResponse response = PoslovnaLogikaTestSuite.sendRequest(request);

        assertTrue(response.success);
        assertTrue(response.responseMessage.contains("Uspešna prijava"));
        assertTrue(response.payload instanceof Zaposleni);
        Zaposleni loggedIn = (Zaposleni) response.payload;
        assertEquals("mika3@gmail.com", loggedIn.getEmail());

    }



}

