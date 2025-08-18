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

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import iznajmljivanjeapp.domain.Vozac;
import iznajmljivanjeapp.services.VozacService;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;

/**
 *
 * @author Djurkovic
 */
@Controller(mapping = "vozac")
public class VozacController  {

    private final VozacService vozacService;

    public VozacController(VozacService vozacService) {
        this.vozacService = vozacService;
    }


    @RequestHandler(requestType = "/kreiraj")
    public NetworkResponse kreirajVozac(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajVozac, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da zapamti vozača.");
        }
        if (!(zahtev.payload instanceof Vozac vozac)) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajVozac, prosledjen domenski objekat nije vozac");
            return Neuspeh("Sistem ne može da zapamti vozača.");
        }

        try {
            vozacService.kreirajVozac(vozac);
            return Uspeh("Sistem je zapamtio vozača.",vozac);
        }
        catch(SQLIntegrityConstraintViolationException ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajVozac: " + ex);
            return Neuspeh("Sistem ne može da zapamti vozača: vozač sa unetim mejlom već postoji.");
        }
        catch(Exception e) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajVozac: " + e);
            return Neuspeh("Sistem ne može da zapamti vozača.");
        }
    }

    @RequestHandler(requestType = "/obrisi")
    public NetworkResponse obrisiVozac(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiVozac, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da obriše vozača.");
        }
        if (!(zahtev.payload instanceof Vozac vozac)) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiVozac, prosledjen domenski objekat nije vozac");
            return Neuspeh("Sistem ne može da obriše vozača.");
        }

        try {
            vozacService.obrisiVozac(vozac);
            return Uspeh("Sistem je obrisao vozača.");
        }
        catch(SQLIntegrityConstraintViolationException ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiVozac: " + ex);
            return Neuspeh("Sistem ne može da obriše vozača: vozač se vezuje za barem jedno iznajmljivanje.");
        }
        catch(Exception e) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiVozac: " + e);
            return Neuspeh("Sistem ne može da obriše vozača.");
        }
    }

    @RequestHandler(requestType = "/promeni")
    public NetworkResponse promeniVozac(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniVozac, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da promeni vozača.");
        }
        try {
            if (zahtev.payload instanceof Vozac v) {
                vozacService.promeniVozac(v);
            } else if (zahtev.payload instanceof List<?> l) {
                if (l.size() == 1 && l.get(0) instanceof Vozac v) {
                    vozacService.promeniVozac(v);

                } else if (l.size() == 2 && l.get(0) instanceof Vozac e1 && l.get(1) instanceof Vozac e2) {
                    vozacService.promeniVozac(e1, e2);

                } else {
                    SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniVozac, prosledjen objekat nije validan za sistemsku operaciju promene");
                    return Neuspeh("Sistem ne može da promeni vozača.");
                }
            } else {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniVozac, prosledjen domenski objekat nije vozac");
                return Neuspeh("Sistem ne može da promeni vozača.");
            }
        }
        catch(SQLIntegrityConstraintViolationException ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajVozac: " + ex);
            return Neuspeh("Sistem ne može da promeni vozača: vozač sa unetim mejlom već postoji.");
        }
        catch(Exception ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniVozac: " + ex);
            return Neuspeh("Sistem ne može da promeni vozača.");
        }
        return Uspeh("Sistem je promenio vozača.");
    }

    @RequestHandler(requestType = "/vrati_sve")
    public NetworkResponse vratiListuSviVozac(NetworkRequest zahtev) {
        List<Vozac> vozaci;

        if (!KriterijumDescriptor.validniDeskriptori(zahtev.payload)) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji vratiListuSviVozac, Kriterijumi nisu ispravno formirani.");
            return Neuspeh("Sistem ne može da nađe vozače po zadatim kriterijumima.");
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
            vozaci = vozacService.vratiListuSviVozac(w);
        } catch (Exception ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji vratiListuSviVozac: " + ex);
            return Neuspeh("Sistem ne može da nađe vozače po zadatim kriterijumima.");
        }

        return Uspeh("Sistem je našao vozače po zadatim kriterijumima.", vozaci);
    }

}
