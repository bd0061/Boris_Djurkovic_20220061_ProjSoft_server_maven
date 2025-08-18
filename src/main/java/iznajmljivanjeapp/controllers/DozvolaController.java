/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iznajmljivanjeapp.controllers;

import framework.injector.anotacije.Controller;
import framework.injector.anotacije.RequestHandler;
import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.network.NetworkRequest;
import framework.model.network.NetworkResponse;
import static framework.model.network.NetworkResponse.Neuspeh;
import static framework.model.network.NetworkResponse.Uspeh;

import java.util.ArrayList;
import java.util.List;

import iznajmljivanjeapp.domain.Dozvola;
import iznajmljivanjeapp.services.DozvolaService;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;

/**
 *
 * @author Djurkovic
 */
@Controller(mapping = "dozvola")
public class DozvolaController  {

    private final DozvolaService dozvolaService;

    public DozvolaController(DozvolaService dozvolaService) {
        this.dozvolaService = dozvolaService;
    }

    @RequestHandler(requestType = "/kreiraj")
    public NetworkResponse kreirajDozvola(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajDozvola, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da zapamti dozvolu.");
        }
        if (!(zahtev.payload instanceof Dozvola dozvola)) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajDozvola, prosledjen domenski objekat nije dozvola");
            return Neuspeh("Sistem ne može da zapamti dozvolu.");
        }

        try {
            dozvolaService.kreirajDozvola(dozvola);
            return Uspeh("Sistem je zapamtio dozvolu.",dozvola);
        }
        catch(Exception e) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajDozvola: " + e);
            return Neuspeh("Sistem ne može da zapamti dozvolu.");
        }
    }

    @RequestHandler(requestType = "/obrisi")
    public NetworkResponse obrisiDozvola(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiDozvola, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da obriše dozvolu.");
        }
        if (!(zahtev.payload instanceof Dozvola dozvola)) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiDozvola, prosledjen domenski objekat nije dozvola");
            return Neuspeh("Sistem ne može da obriše dozvolu.");
        }
        try {
            dozvolaService.obrisiDozvola(dozvola);
            return Uspeh("Sistem je obrisao dozvolu.");
        }
        catch(Exception e) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiDozvola: " + e);
            return Neuspeh("Sistem ne može da obriše dozvolu.");
        }
    }

    @RequestHandler(requestType = "/promeni")
    public NetworkResponse promeniDozvola(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniDozvola, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da promeni dozvolu.");
        }
        try {
            if (zahtev.payload instanceof Dozvola d) {
                dozvolaService.promeniDozvola(d);
            } else if (zahtev.payload instanceof List<?> l) {
                if (l.size() == 1 && l.get(0) instanceof Dozvola e) {
                    dozvolaService.promeniDozvola(e);

                } else if (l.size() == 2 && l.get(0) instanceof Dozvola e1 && l.get(1) instanceof Dozvola e2) {
                    dozvolaService.promeniDozvola(e1, e2);

                } else {
                    SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniDozvola, prosledjen objekat nije validan za sistemsku operaciju promene");
                    return Neuspeh("Sistem ne može da promeni dozvolu.");
                }
            } else {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniDozvola, prosledjen domenski objekat nije dozvola");
                return Neuspeh("Sistem ne može da promeni dozvolu.");
            }
        }
        catch(Exception ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniDozvola: " + ex);
            return Neuspeh("Sistem ne može da promeni dozvolu.");
        }
        return Uspeh("Sistem je promenio dozvolu.");
    }


    @RequestHandler(requestType = "/vrati_sve")
    public NetworkResponse vratiListuSviDozvola(NetworkRequest zahtev)  {
        List<Dozvola> dozvole;

        if (!KriterijumDescriptor.validniDeskriptori(zahtev.payload)) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji vratiListuSviDozvola, Kriterijumi nisu ispravno formirani.");
            return Neuspeh("Sistem ne može da nađe dozvole po zadatim kriterijumima.");
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
            dozvole = dozvolaService.vratiListuSviDozvola(w);
        } catch (Exception ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji vratiListuSviDozvola: " + ex);
            return Neuspeh("Sistem ne može da nađe dozvole po zadatim kriterijumima.");
        }

        return Uspeh("Sistem je našao dozvole po zadatim kriterijumima.", dozvole);

    }
}
