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

import iznajmljivanjeapp.exceptions.SistemskaOperacijaException;
import iznajmljivanjeapp.domain.Zaposleni;
import iznajmljivanjeapp.services.ZaposleniService;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Djurkovic
 */
@Controller(mapping = "zaposleni")
public class ZaposleniController  {


    private final ZaposleniService zaposleniService;

    public ZaposleniController(ZaposleniService zaposleniService) {
        this.zaposleniService = zaposleniService;
    }

    @RequestHandler(requestType = "/kreiraj")
    public NetworkResponse kreirajZaposleni(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajZaposleni, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da zapamti zaposlenog.");
        }
        if (zahtev.payload instanceof Zaposleni zaposleni) {
            try {
                zaposleniService.kreirajZaposleni(zaposleni);
                return Uspeh("Sistem je zapamtio zaposlenog.",zaposleni);
            }
            catch(SQLIntegrityConstraintViolationException ex) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajZaposleni: " + ex);
                return Neuspeh("Sistem ne može da zapamti zaposlenog: zaposleni sa unetim mejlom već postoji.");
            }
            catch(Exception e) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajZaposleni: " + e);
                return Neuspeh("Sistem ne može da zapamti zaposlenog.");
            }

        }
        SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji kreirajZaposleni, prosledjen domenski objekat nije zaposleni");
        return Neuspeh("Sistem ne može da zapamti zaposlenog.");
    }

    @RequestHandler(requestType = "/obrisi")
    public NetworkResponse obrisiZaposleni(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiZaposleni, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da obriše zaposlenog.");
        }
        if (zahtev.payload instanceof Zaposleni zaposleni) {
            try {
                zaposleniService.obrisiZaposleni(zaposleni);
                return Uspeh("Sistem je obrisao zaposlenog.");
            }
            catch(SQLIntegrityConstraintViolationException ex) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiZaposleni: " + ex);
                return Neuspeh("Sistem ne može da obriše zaposlenog: zaposleni se vezuje za barem jedno iznajmljivanje.");
            }
            catch(Exception e) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiZaposleni: " + e);
                return Neuspeh("Sistem ne može da obriše zaposlenog.");
            }

        }
        SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji obrisiZaposleni, prosledjen domenski objekat nije zaposleni");
        return Neuspeh("Sistem ne može da obriše zaposlenog.");
    }

    @RequestHandler(requestType = "/promeni")
    public NetworkResponse promeniZaposleni(NetworkRequest zahtev) {
        if (zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniZaposleni, nije prosledjen domenski objekat");
            return Neuspeh("Sistem ne može da promeni zaposlenog.");
        }
        if(zahtev.payload instanceof Zaposleni z) {
            try {
                zaposleniService.promeniZaposleni(z);
            }
            catch(SQLIntegrityConstraintViolationException ex) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniZaposleni: " + ex);
                return Neuspeh("Sistem ne može da promeni zaposlenog: zaposleni sa unetim mejlom već postoji.");
            }
            catch(Exception ex) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniZaposleni: " + ex);
                return Neuspeh("Sistem ne može da promeni zaposlenog.");
            }
        }
        else if (zahtev.payload instanceof List<?> l) {
            if (l.size() == 1 && l.get(0) instanceof Zaposleni z) {
                try {
                    zaposleniService.promeniZaposleni(z);
                }
                catch(SQLIntegrityConstraintViolationException ex) {
                    SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniZaposleni: " + ex);
                    return Neuspeh("Sistem ne može da promeni zaposlenog: zaposleni sa unetim mejlom već postoji.");
                }
                catch(Exception ex) {
                    SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniZaposleni: " + ex);
                    return Neuspeh("Sistem ne može da promeni zaposlenog.");
                }
            } else if (l.size() == 2 && l.get(0) instanceof Zaposleni e1 && l.get(1) instanceof Zaposleni e2) {
                try {
                    zaposleniService.promeniZaposleni(e1,e2);
                }
                catch(SQLIntegrityConstraintViolationException ex) {
                    SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniZaposleni: " + ex);
                    return Neuspeh("Sistem ne može da promeni zaposlenog: zaposleni sa unetim mejlom već postoji.");
                }
                catch(Exception ex) {
                    SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniZaposleni: " + ex);
                    return Neuspeh("Sistem ne može da promeni zaposlenog.");
                }
            } else {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniZaposleni, prosledjen objekat nije validan za sistemsku operaciju promene");
                return Neuspeh("Sistem ne može da promeni zaposlenog.");
            }
        }
        else {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji promeniZaposleni, prosledjen domenski objekat nije zaposleni");
            return Neuspeh("Sistem ne može da promeni zaposlenog.");
        }
        return Uspeh("Sistem je promenio zaposlenog.");
    }

    @RequestHandler(requestType = "/vrati_sve")
    public NetworkResponse vratiListuSviZaposleni(NetworkRequest zahtev) {
        List<Zaposleni> zaposleni;

        if (!KriterijumDescriptor.validniDeskriptori(zahtev.payload)) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji vratiListuSviZaposleni, Kriterijumi nisu ispravno formirani.");
            return Neuspeh("Sistem ne može da nađe zaposlene po zadatim kriterijumima.");
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
            zaposleni = zaposleniService.vratiListuSviZaposleni(w);
        } catch (Exception ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji vratiListuSviZaposleni: " + ex);
            return Neuspeh("Sistem ne može da nađe zaposlene po zadatim kriterijumima.");
        }

        return Uspeh("Sistem je našao zaposlene po zadatim kriterijumima.", zaposleni);
    }

    
    @RequestHandler(requestType = "/prijava")
    public NetworkResponse prijaviZaposleni(NetworkRequest zahtev) {
        if(zahtev.payload == null) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji prijaviZaposleni: Domenski objekat nije prisutan");
            return Neuspeh("Sistem nije uspeo da obradi prijavu.");
        }
        if(zahtev.payload instanceof Zaposleni z) {
            try {
                Zaposleni vraceniZaposleni = zaposleniService.prijavaZaposleni(z);
                SimpleLogger.log(LogLevel.LOG_INFO, "Uspesna prijava na sistem");
                return Uspeh("Uspešna prijava na sistem.",vraceniZaposleni);
            }
            catch(SistemskaOperacijaException e) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji prijaviZaposleni: " + e.getMessage());
                return Neuspeh(e.getMessage());
            }
            catch(Exception e) {
                SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji prijaviZaposleni: " + e);
                return Neuspeh("Sistem nije uspeo da obradi prijavu.");
            }
        }
        SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri sistemskoj operaciji prijaviZaposleni: Domenski objekat nije tipa zaposleni");
        return Neuspeh("Sistem nije uspeo da obradi prijavu.");
    }
}
