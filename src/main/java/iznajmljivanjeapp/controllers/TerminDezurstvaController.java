package iznajmljivanjeapp.controllers;

import framework.injector.anotacije.Controller;
import framework.injector.anotacije.RequestHandler;
import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.network.NetworkRequest;
import framework.model.network.NetworkResponse;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;
import iznajmljivanjeapp.domain.TerminDezurstva;
import iznajmljivanjeapp.services.TerminDezurstvaService;

import java.util.ArrayList;
import java.util.List;

import static framework.model.network.NetworkResponse.Neuspeh;
import static framework.model.network.NetworkResponse.Uspeh;

@Controller(mapping = "termindezurstva")
public class TerminDezurstvaController {

    private final TerminDezurstvaService terminDezurstvaService;
    public TerminDezurstvaController(TerminDezurstvaService terminDezurstvaService) {
        this.terminDezurstvaService = terminDezurstvaService;
    }
    @RequestHandler(requestType = "/kreiraj")
    public NetworkResponse kreirajTerminDezurstva(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajTerminDezurstva, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da zapamti termin dežurstva.");
        }
        if (!(zahtev.payload instanceof TerminDezurstva td)) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajTerminDezurstva, prosledjen domenski objekat nije termin dežurstva");
            return Neuspeh("Sistem ne može da zapamti termin dežurstva.");
        }

        try {
            terminDezurstvaService.kreirajTerminDezurstva(td);
            return Uspeh("Sistem je zapamtio termin dežurstva.",td);
        }
        catch(Exception e) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajTerminDezurstva: " + e);
            return Neuspeh("Sistem ne može da zapamti termin dežurstva.");
        }
    }

    @RequestHandler(requestType = "/obrisi")
    public NetworkResponse obrisiTerminDezurstva(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiTerminDezurstva, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da obriše termin dežurstva.");
        }
        if (!(zahtev.payload instanceof TerminDezurstva td)) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiTerminDezurstva, prosledjen domenski objekat nije termin dežurstva");
            return Neuspeh("Sistem ne može da obriše termin dežurstva.");
        }

        try {
            terminDezurstvaService.obrisiTerminDezurstva(td);
            return Uspeh("Sistem je obrisao termin dežurstva.");
        }
        catch(Exception e) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiTerminDezurstva: " + e);
            return Neuspeh("Sistem ne može da obriše termin dežurstva.");
        }
    }

    @RequestHandler(requestType = "/promeni")
    public NetworkResponse promeniTerminDezurstva(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniTerminDezurstva, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da promeni termin dežurstva.");
        }
        try {
            if (zahtev.payload instanceof TerminDezurstva td) {
                terminDezurstvaService.promeniTerminDezurstva(td);
            } else if (zahtev.payload instanceof List<?> l) {
                if (l.size() == 1 && l.get(0) instanceof TerminDezurstva td) {
                    terminDezurstvaService.promeniTerminDezurstva(td);

                } else if (l.size() == 2 && l.get(0) instanceof TerminDezurstva e1 && l.get(1) instanceof TerminDezurstva e2) {
                    terminDezurstvaService.promeniTerminDezurstva(e1, e2);

                } else {
                    SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniTerminDezurstva, prosledjen objekat nije validan za sistemsku operaciju promene");
                    return Neuspeh("Sistem ne može da promeni termin dežurstva.");
                }
            } else {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniTerminDezurstva, prosledjen domenski objekat nije termin dežurstva");
                return Neuspeh("Sistem ne može da promeni termin dežurstva.");
            }
        }
        catch(Exception ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniTerminDezurstva: " + ex);
            return Neuspeh("Sistem ne može da promeni termin dežurstva.");
        }
        return Uspeh("Sistem je promenio termin dežurstva.");
    }

    @RequestHandler(requestType = "/vrati_sve")
    public NetworkResponse vratiListuSviTerminDezurstva(NetworkRequest zahtev)  {
        List<TerminDezurstva> terminiDezurstva;

        if (!KriterijumDescriptor.validniDeskriptori(zahtev.payload)) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji vratiListuSviTerminDezurstva, Kriterijumi nisu ispravno formirani.");
            return Neuspeh("Sistem ne može da nađe termine dežurstva po zadatim kriterijumima.");
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
            terminiDezurstva = terminDezurstvaService.vratiListuSviTerminDezurstva(w);
        } catch (Exception ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji vratiListuSviTerminDezurstva: " + ex);
            return Neuspeh("Sistem ne može da nađe termine dežurstva po zadatim kriterijumima.");
        }

        return Uspeh("Sistem je našao termine dežurstva po zadatim kriterijumima.", terminiDezurstva);
    }



}
