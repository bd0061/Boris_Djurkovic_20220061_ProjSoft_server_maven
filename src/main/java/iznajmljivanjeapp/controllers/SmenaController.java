package iznajmljivanjeapp.controllers;

import framework.injector.anotacije.Controller;
import framework.injector.anotacije.RequestHandler;
import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.enumeracije.InsertBehaviour;
import framework.model.network.NetworkRequest;
import framework.model.network.NetworkResponse;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;
import iznajmljivanjeapp.domain.insertwrappers.SmenaInsertWrapper;
import iznajmljivanjeapp.domain.uslovniobjekti.SmenaUslov;
import iznajmljivanjeapp.exceptions.SistemskaOperacijaException;
import iznajmljivanjeapp.domain.Smena;
import iznajmljivanjeapp.services.SmenaService;

import java.util.ArrayList;
import java.util.List;

import static framework.model.network.NetworkResponse.Neuspeh;
import static framework.model.network.NetworkResponse.Uspeh;

@Controller(mapping = "smena")
public class SmenaController {

    private final SmenaService smenaService;

    public SmenaController(SmenaService smenaService) {
        this.smenaService = smenaService;
    }

    @RequestHandler(requestType = "/obrisi_conditional")
    public NetworkResponse obrisiSmenaBulk(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiSmenaBulk, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da obriše smene.");
        }
        if(!(zahtev.payload instanceof SmenaUslov su)) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiSmenaBulk, los objekat");
            return Neuspeh("Sistem ne može da obrise smene.");
        }
        try {
            smenaService.obrisiConditional(su);
        }
        catch(Exception e) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiSmenaBulk: " + e);
            return Neuspeh("Sistem ne može da obriše smene.");
        }

        return Uspeh("Uspešno brisanje smena");
    }


    @RequestHandler(requestType = "/kreiraj")
    public NetworkResponse kreirajSmena(NetworkRequest zahtev) {
        InsertBehaviour ib;
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajSmena, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da zapamti smenu.");
        }
        boolean vise = false;
        Smena[] smene;
        if (zahtev.payload instanceof Smena s) {
            smene = new Smena[] {s};
            ib = InsertBehaviour.NORMAL;
        }
        else if(zahtev.payload instanceof List<?> l) {
            if(l.isEmpty()) {
                //moze da se desi pri normalnom toku logike, na primer ako korisnik ubaci smenu samo za jedan dan pri cemu ignorise vikende a bas taj dan je vikend
                //samo necemo nista uraditi i vraticemo uspeh
                return Uspeh("Sistem je zapamtio " + (vise ? "smene." : "smenu."), new ArrayList<Smena>());
            }
            for(Object o : l) {
                if(!(o instanceof Smena)) {
                    SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajSmena, neki od prosledjenih objekata nisu domenski objekti.");
                    return Neuspeh("Sistem ne može da zapamti smene.");
                }
            }
            @SuppressWarnings("unchecked")
            List<Smena> lista = (List<Smena>) l;

            smene = lista.toArray(new Smena[0]);
            vise = true;
            ib = InsertBehaviour.NORMAL;
        }
        else if(zahtev.payload instanceof SmenaInsertWrapper siw) {
            smene = siw.smene.toArray(new Smena[0]);
            ib = siw.ib;
        }
        else{
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajSmena, prosledjen domenski objekat nije smena");
            return Neuspeh("Sistem ne može da zapamti smenu.");
        }

        try {
            smenaService.kreirajSmena(ib,smene);
            return Uspeh("Sistem je zapamtio " + (vise ? "smene." : "smenu."), smene);
        }
        catch(SistemskaOperacijaException e) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajSmena: " + e);
            return Neuspeh(e.getMessage());
        }
        catch(Exception e) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajSmena: " + e);
            return Neuspeh("Sistem ne može da zapamti " + (vise ? "smene." : "smenu."));
        }


    }
    @RequestHandler(requestType = "/obrisi")
    public NetworkResponse obrisiSmena(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiSmena, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da obriše smenu.");
        }
        boolean vise = false;
        Smena[] smene;
        if (zahtev.payload instanceof Smena s) {
            smene = new Smena[] {s};
        }
        else if(zahtev.payload instanceof List<?> l) {
            if(l.isEmpty()) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiSmena, nije prosledjen domenski objekat");
                return Neuspeh("Sistem ne može da obriše smenu.");
            }
            for(Object o : l) {
                if(!(o instanceof Smena)) {
                    SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiSmena, neki od prosledjenih objekata nisu domenski objekti.");
                    return Neuspeh("Sistem ne može da obriše smene.");
                }
            }
            @SuppressWarnings("unchecked")
            List<Smena> lista = (List<Smena>) l;

            smene = lista.toArray(new Smena[0]);
            vise = true;
        }
        else {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiSmena, prosledjen domenski objekat nije smena");
            return Neuspeh("Sistem ne može da zapamti smenu.");
        }

        try {
            smenaService.obrisiSmena(smene);
            return Uspeh("Sistem je obrisao " + (vise ? "smene." : "smenu."));
        }
        catch(Exception e) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiSmena: " + e);
            return Neuspeh("Sistem ne može da obriše " + (vise ? "smene." : "smenu."));
        }
    }

    @RequestHandler(requestType = "/promeni")
    public NetworkResponse promeniSmena(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniSmena, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da promeni smenu.");
        }
        boolean vise = false;
        try {
            if (zahtev.payload instanceof Smena s) {
                smenaService.promeniSmena(s);
            }
            else if (zahtev.payload instanceof List<?> l) {
                if (l.size() == 1 && l.get(0) instanceof Smena s) {
                    smenaService.promeniSmena(s);
                } else if (l.size() >= 2) {
                    for(Object o : l) {
                        if(!(o instanceof Smena)) {
                            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniSmena, nije prosledjen domenski objekat");
                            return Neuspeh("Sistem ne može da promeni smene.");
                        }
                    }
                    Smena promene = (Smena) l.getFirst();
                    Smena[] kljucevi = l.subList(1,l.size()).toArray(new Smena[0]);
                    smenaService.promeniSmena(promene, kljucevi);
                    vise = true;
                } else {
                    SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniSmena, prosledjen objekat nije validan za sistemsku operaciju promene");
                    return Neuspeh("Sistem ne može da promeni smenu.");
                }
            }
            else {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniSmena, prosledjen domenski objekat nije smena");
                return Neuspeh("Sistem ne može da promeni smenu.");
            }
        }
        catch(Exception ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniSmena: " + ex);
            return Neuspeh("Sistem ne može da promeni smenu.");
        }
        return Uspeh("Sistem je promenio " + (vise ? "smene." : "smenu."));
    }

    @RequestHandler(requestType = "/vrati_sve")
    public NetworkResponse vratiListuSveSmene(NetworkRequest zahtev) {
        List<Smena> smene;

        if (!KriterijumDescriptor.validniDeskriptori(zahtev.payload)) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji vratiListuSveSmene, Kriterijumi nisu ispravno formirani.");
            return Neuspeh("Sistem ne može da nađe smene po zadatim kriterijumima.");
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
            smene = smenaService.vratiListuSviSmena(w);
        } catch (Exception ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji vratiListuSveSmene: " + ex);
            return Neuspeh("Sistem ne može da nađe smene po zadatim kriterijumima.");
        }

        return Uspeh("Sistem je našao smene po zadatim kriterijumima.", smene);
    }
}
