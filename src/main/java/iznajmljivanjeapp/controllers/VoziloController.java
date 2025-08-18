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
import iznajmljivanjeapp.domain.Vozilo;
import iznajmljivanjeapp.services.VoziloService;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static framework.model.network.NetworkResponse.Neuspeh;
import static framework.model.network.NetworkResponse.Uspeh;

/**
 *
 * @author Djurkovic
 */
@Controller(mapping = "vozilo")
public class VoziloController  {

    private final VoziloService voziloService;

    public VoziloController(VoziloService voziloService) {
        this.voziloService = voziloService;
    }

    @RequestHandler(requestType = "/kreiraj")
    public NetworkResponse kreirajVozilo(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajVozilo, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da zapamti vozilo.");
        }
        if (!(zahtev.payload instanceof Vozilo vozilo)) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajVozilo, prosledjen domenski objekat nije vozilo");
            return Neuspeh("Sistem ne može da zapamti vozilo.");
        }
        try {
            voziloService.kreirajVozilo(vozilo);
            return Uspeh("Sistem je zapamtio vozilo.",vozilo);
        }
        catch(Exception e) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajVozilo: " + e);
            return Neuspeh("Sistem ne može da zapamti vozilo.");
        }
    }

    @RequestHandler(requestType = "/obrisi")
    public NetworkResponse obrisiVozilo(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiVozilo, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da obriše vozilo.");
        }
        if (!(zahtev.payload instanceof Vozilo vozilo)) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiVozilo, prosledjen domenski objekat nije vozilo");
            return Neuspeh("Sistem ne može da obriše vozilo.");
        }
        try {
            voziloService.obrisiVozilo(vozilo);
            return Uspeh("Sistem je obrisao vozilo.");
        }
        catch(SQLIntegrityConstraintViolationException ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiVozilo: " + ex);
            return Neuspeh("Sistem ne može da obriše vozilo: vozilo se vezuje za barem jednu stavku iznajmljivanja.");
        }
        catch(Exception e) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiVozilo: " + e);
            return Neuspeh("Sistem ne može da obriše vozilo.");
        }
    }

    @RequestHandler(requestType = "/promeni")
    public NetworkResponse promeniVozilo(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniVozilo, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da promeni vozilo.");
        }
        try {
            if (zahtev.payload instanceof Vozilo v) {
                voziloService.promeniVozilo(v);
            }
            else if (zahtev.payload instanceof List<?> l) {
                if (l.size() == 1 && l.get(0) instanceof Vozilo v) {
                    voziloService.promeniVozilo(v);
                } else if (l.size() == 2 && l.get(0) instanceof Vozilo e1 && l.get(1) instanceof Vozilo e2) {
                    voziloService.promeniVozilo(e1, e2);
                } else {
                    SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniVozilo, prosledjen objekat nije validan za sistemsku operaciju promene");
                    return Neuspeh("Sistem ne može da promeni vozilo.");
                }
            }
            else {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniVozilo, prosledjen domenski objekat nije vozilo");
                return Neuspeh("Sistem ne može da promeni vozilo.");
            }
        }
        catch(Exception ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniVozilo: " + ex);
            return Neuspeh("Sistem ne može da promeni vozilo.");
        }
        return Uspeh("Sistem je promenio vozilo.");
    }

    @RequestHandler(requestType = "/vrati_sve")
    public NetworkResponse vratiListuSviVozilo(NetworkRequest zahtev) {
        List<Vozilo> vozila;

        if (!KriterijumDescriptor.validniDeskriptori(zahtev.payload)) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji vratiListuSviVozilo, Kriterijumi nisu ispravno formirani.");
            return Neuspeh("Sistem ne može da nađe vozila po zadatim kriterijumima.");
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
            vozila = voziloService.vratiListuSviVozilo(w);
        } catch (Exception ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji vratiListuSviVozilo: " + ex);
            return Neuspeh("Sistem ne može da nađe vozila po zadatim kriterijumima.");
        }

        return Uspeh("Sistem je našao vozila po zadatim kriterijumima.", vozila);
    }

}
