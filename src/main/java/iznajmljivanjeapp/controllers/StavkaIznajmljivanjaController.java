package iznajmljivanjeapp.controllers;

import framework.injector.anotacije.Controller;
import framework.injector.anotacije.RequestHandler;
import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.network.NetworkRequest;
import framework.model.network.NetworkResponse;
import iznajmljivanjeapp.domain.StavkaIznajmljivanja;
import iznajmljivanjeapp.services.StavkaIznajmljivanjaService;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;

import java.util.ArrayList;
import java.util.List;

import static framework.model.network.NetworkResponse.Neuspeh;
import static framework.model.network.NetworkResponse.Uspeh;

@Controller(mapping = "stavkaiznajmljivanja")
public class StavkaIznajmljivanjaController {
    private final StavkaIznajmljivanjaService stavkaIznajmljivanjaService;

    public StavkaIznajmljivanjaController(StavkaIznajmljivanjaService stavkaIznajmljivanjaService) {
        this.stavkaIznajmljivanjaService = stavkaIznajmljivanjaService;
    }
    @RequestHandler(requestType = "/kreiraj")
    public NetworkResponse kreirajStavkaIznajmljivanja(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajStavkaIznajmljivanja, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da zapamti stavku iznajmljivanja.");
        }
        if (!(zahtev.payload instanceof StavkaIznajmljivanja s)) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajStavkaIznajmljivanja, prosledjen domenski objekat nije stavka iznajmljivanja");
            return Neuspeh("Sistem ne može da zapamti stavku iznajmljivanja.");
        }

        try {
             stavkaIznajmljivanjaService.kreirajStavkaIznajmljivanja(s);
            return Uspeh("Sistem je zapamtio stavku iznajmljivanja.", s);
        }
        catch(Exception e) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajStavkaIznajmljivanja: " + e);
            return Neuspeh("Sistem ne može da zapamti stavku iznajmljivanja.");
        }
    }
    @RequestHandler(requestType = "/obrisi")
    public NetworkResponse obrisiStavkaIznajmljivanja(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiStavkaIznajmljivanja, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da obriše stavku iznajmljivanja.");
        }
        if (!(zahtev.payload instanceof StavkaIznajmljivanja s)) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiStavkaIznajmljivanja, prosledjen domenski objekat nije stavka iznajmljivanja");
            return Neuspeh("Sistem ne može da obriše stavku iznajmljivanja.");
        }

        try {
            stavkaIznajmljivanjaService.obrisiStavkaIznajmljivanja(s);
            return Uspeh("Sistem je obrisao stavku iznajmljivanja.");
        }
        catch(Exception e) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiStavkaIznajmljivanja: " + e);
            return Neuspeh("Sistem ne može da obriše stavku iznajmljivanja.");
        }
    }

    @RequestHandler(requestType = "/promeni")
    public NetworkResponse promeniStavkaIznajmljivanja(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniStavkaIznajmljivanja, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da promeni stavku iznajmljivanja.");
        }
        try {
            if (zahtev.payload instanceof StavkaIznajmljivanja s) {
                stavkaIznajmljivanjaService.promeniStavkaIznajmljivanja(s);
            } else if (zahtev.payload instanceof List<?> l) {
                if (l.size() == 1 && l.get(0) instanceof StavkaIznajmljivanja s) {
                    stavkaIznajmljivanjaService.promeniStavkaIznajmljivanja(s);

                } else if (l.size() == 2 && l.get(0) instanceof StavkaIznajmljivanja e1 && l.get(1) instanceof StavkaIznajmljivanja e2) {
                    stavkaIznajmljivanjaService.promeniStavkaIznajmljivanja(e1, e2);

                } else {
                    SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniStavkaIznajmljivanja, prosledjen objekat nije validan za sistemsku operaciju promene");
                    return Neuspeh("Sistem ne može da promeni stavku iznajmljivanja.");
                }
            } else {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniStavkaIznajmljivanja, prosledjen domenski objekat nije stavka iznajmljivanja");
                return Neuspeh("Sistem ne može da promeni stavku iznajmljivanja.");
            }
        }
        catch(Exception ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniStavkaIznajmljivanja: " + ex);
            return Neuspeh("Sistem ne može da promeni stavku iznajmljivanja.");
        }
        return Uspeh("Sistem je promenio stavku iznajmljivanja.");
    }

    @RequestHandler(requestType = "/vrati_sve")
    public NetworkResponse vratiListuSviStavkaIznajmljivanja(NetworkRequest zahtev) {
        List<StavkaIznajmljivanja> stavke;

        if (!KriterijumDescriptor.validniDeskriptori(zahtev.payload)) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji vratiListuSviStavkaIznajmljivanja, Kriterijumi nisu ispravno formirani.");
            return Neuspeh("Sistem ne može da nađe stavke po zadatim kriterijumima.");
        }

        try {
            KriterijumWrapper w;
            if(zahtev.payload == null) {
                w = new KriterijumWrapper(new ArrayList<>(),KriterijumWrapper.DepthLevel.FULL);
            }
            if(zahtev.payload instanceof KriterijumWrapper ww) {
                w = ww;
            }
            else {
                w = new KriterijumWrapper(((List<KriterijumDescriptor>)zahtev.payload),KriterijumWrapper.DepthLevel.FULL);
            }
            stavke = stavkaIznajmljivanjaService.vratiListuSviStavkaIznajmljivanja(w);
        } catch (Exception ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji vratiListuSviStavkaIznajmljivanja: " + ex);
            return Neuspeh("Sistem ne može da nađe stavke po zadatim kriterijumima.");
        }

        return Uspeh("Sistem je našao stavke po zadatim kriterijumima.", stavke);
    }
}
