package iznajmljivanjeapp.controllers;

import framework.injector.anotacije.Controller;
import framework.injector.anotacije.RequestHandler;
import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.network.NetworkRequest;
import framework.model.network.NetworkResponse;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;
import iznajmljivanjeapp.exceptions.SistemskaOperacijaException;
import iznajmljivanjeapp.domain.Iznajmljivanje;
import iznajmljivanjeapp.services.IznajmljivanjeService;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static framework.model.network.NetworkResponse.Neuspeh;
import static framework.model.network.NetworkResponse.Uspeh;

@Controller(mapping = "iznajmljivanje")
public class IznajmljivanjeController {

    private final IznajmljivanjeService iznajmljivanjeService;

    public IznajmljivanjeController(IznajmljivanjeService iznajmljivanjeService) {
        this.iznajmljivanjeService = iznajmljivanjeService;
    }

    @RequestHandler(requestType = "/kreiraj")
    public NetworkResponse kreirajIznajmljivanje(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajIznajmljivanje, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da zapamti iznajmljivanje.");
        }
        if (zahtev.payload instanceof Iznajmljivanje iznajmljivanje) {
            try {
                iznajmljivanjeService.kreirajIznajmljivanje(iznajmljivanje);
                return Uspeh("Sistem je zapamtio iznajmljivanje.",iznajmljivanje);
            }
            catch(SistemskaOperacijaException e) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajIznajmljivanje: " + e);
                return Neuspeh(e.getMessage());
            }
            catch(Exception e) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajIznajmljivanje: " + e);
                return Neuspeh("Sistem ne može da zapamti iznajmljivanje.");
            }

        }
        SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajIznajmljivanje, prosledjen domenski objekat nije iznajmljivanje");
        return Neuspeh("Sistem ne može da zapamti iznajmljivanje.");
    }

    @RequestHandler(requestType = "/obrisi")
    public NetworkResponse obrisiIznajmljivanje(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiIznajmljivanje, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da obriše iznajmljivanje.");
        }
        if (zahtev.payload instanceof Iznajmljivanje iznajmljivanje) {
            try {
                iznajmljivanjeService.obrisiIznajmljivanje(iznajmljivanje);
                return Uspeh("Sistem je obrisao iznajmljivanje.");
            }
            catch(Exception e) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiIznajmljivanje: " + e);
                return Neuspeh("Sistem ne može da obriše iznajmljivanje.");
            }

        }
        SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiIznajmljivanje, prosledjen domenski objekat nije iznajmljivanje");
        return Neuspeh("Sistem ne može da obriše iznajmljivanje.");
    }

    @RequestHandler(requestType = "/promeni")
    public NetworkResponse promeniIznajmljivanje(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniIznajmljivanje, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da promeni iznajmljivanje.");
        }
        if(zahtev.payload instanceof Iznajmljivanje i) {
            try {
                iznajmljivanjeService.promeniIznajmljivanje(i);
            }
            catch(Exception ex) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniIznajmljivanje: " + ex);
                return Neuspeh("Sistem ne može da promeni iznajmljivanje.");
            }
        }
        else if (zahtev.payload instanceof List<?> l) {
            if (l.size() == 1 && l.get(0) instanceof Iznajmljivanje i) {
                try {
                    iznajmljivanjeService.promeniIznajmljivanje(i);
                }
                catch(Exception ex) {
                    SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniIznajmljivanje: " + ex);
                    return Neuspeh("Sistem ne može da promeni iznajmljivanje.");
                }
            } else if (l.size() == 2 && l.get(0) instanceof Iznajmljivanje i1 && l.get(1) instanceof Iznajmljivanje i2) {
                try {
                    iznajmljivanjeService.promeniIznajmljivanje(i1, i2);
                }
                catch(Exception ex) {
                    SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniIznajmljivanje: " + ex);
                    return Neuspeh("Sistem ne može da promeni iznajmljivanje.");
                }
            } else {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniIznajmljivanje, prosledjen objekat nije validan za sistemsku operaciju promene");
                return Neuspeh("Sistem ne može da promeni iznajmljivanje.");
            }
        }
        else {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniIznajmljivanje, prosledjen domenski objekat nije iznajmljivanje");
            return Neuspeh("Sistem ne može da promeni iznajmljivanje.");
        }
        return Uspeh("Sistem je promenio iznajmljivanje.");
    }


    @RequestHandler(requestType = "/vrati_sve")
    public NetworkResponse vratiListuSviIznajmljivanja(NetworkRequest zahtev) {
        List<Iznajmljivanje> iznajmljivanja;

        if (!KriterijumDescriptor.validniDeskriptori(zahtev.payload)) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji vratiListuSviIznajmljivanja, Kriterijumi nisu ispravno formirani.");
            return Neuspeh("Sistem ne može da nađe iznajmljivanja po zadatim kriterijumima.");
        }

        try {
            KriterijumWrapper w;
            if(zahtev.payload == null) {
                w = new KriterijumWrapper(new ArrayList<>(), KriterijumWrapper.DepthLevel.FULL);
            }
            if(zahtev.payload instanceof KriterijumWrapper ww) {
                w = ww;
            }
            else {
                w = new KriterijumWrapper(((List<KriterijumDescriptor>)zahtev.payload),KriterijumWrapper.DepthLevel.FULL);
            }
            iznajmljivanja = iznajmljivanjeService.vratiListuSviIznajmljivanje(w);
        } catch (Exception ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji vratiListuSviIznajmljivanja: " + ex);
            return Neuspeh("Sistem ne može da nađe iznajmljivanja po zadatim kriterijumima.");
        }

        return Uspeh("Sistem je našao iznajmljivanja po zadatim kriterijumima.", iznajmljivanja);
    }






}
