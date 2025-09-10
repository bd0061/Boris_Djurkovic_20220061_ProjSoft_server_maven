/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iznajmljivanjeapp.services;

import framework.injector.OpstiServis;
import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;
import iznajmljivanjeapp.bezbednost.PasswordHasher;
import iznajmljivanjeapp.exceptions.SistemskaOperacijaException;
import iznajmljivanjeapp.domain.Smena;
import iznajmljivanjeapp.domain.Zaposleni;
import iznajmljivanjeapp.repositories.Repository;
import javassist.Loader;

import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Djurkovic
 */

public class ZaposleniService extends OpstiServis {
    private final Repository<Zaposleni> zaposleniRepository;

    public ZaposleniService(Repository<Zaposleni> zaposleniRepository) {
        this.zaposleniRepository = zaposleniRepository;
    }


    public void kreirajZaposleni(Zaposleni zaposleni) throws Exception {
        //sve ostale greske ce hendlovati metode nizeg sloja
        if(zaposleni == null || zaposleni.getSifra() == null) throw new Exception("los zaposleni");
        byte[] salt = PasswordHasher.generateSalt();
        zaposleni.setSalt(Base64.getEncoder().encodeToString(salt));
        zaposleni.setSifra(PasswordHasher.hash(zaposleni.getSifra(),salt));

        zaposleniRepository.kreiraj(zaposleni);
        SimpleLogger.log(LogLevel.LOG_INFO, "Zaposleni(id = " + zaposleni.getId() + ") pw=" + zaposleni.getSifra() + " salt=" + zaposleni.getSalt());
    }

    public void obrisiZaposleni(Zaposleni zaposleni) throws Exception {
        zaposleniRepository.obrisi(zaposleni);
    }

    public void promeniZaposleni(Zaposleni zaposleni) throws Exception {
        if(zaposleni == null) throw new Exception("los zaposleni");
        if(zaposleni.getSifra() != null) {
            byte[] salt = PasswordHasher.generateSalt();
            zaposleni.setSalt(Base64.getEncoder().encodeToString(salt));
            zaposleni.setSifra(PasswordHasher.hash(zaposleni.getSifra(),salt));
        }


        zaposleniRepository.promeni(zaposleni);
    }

    public void promeniZaposleni(Zaposleni z1, Zaposleni z2) throws Exception {
        zaposleniRepository.promeni(z1, new Zaposleni[]{z2});
    }

    public List<Zaposleni> vratiListuSviZaposleni(List<KriterijumDescriptor> descriptors) throws Exception {
        return zaposleniRepository.vratiListuSvi(descriptors);
    }

    public List<Zaposleni> vratiListuSviZaposleni(KriterijumWrapper w) throws Exception {
        return zaposleniRepository.vratiListuSvi(w);
    }


    private static boolean isSameDay(Date d1, Date d2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(d1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(d2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    public Zaposleni prijavaZaposleni(Zaposleni zaposleni) throws Exception {
        if(zaposleni.getEmail() == null || zaposleni.getSifra() == null) {
            throw new Exception("Nisu prosledjeni sifra ili email zaposlenog");
        }
        List<Zaposleni> zl = zaposleniRepository.vratiListuSvi(
                new KriterijumWrapper(List.of(new KriterijumDescriptor(Zaposleni.class,"email","=",zaposleni.getEmail())), KriterijumWrapper.DepthLevel.FULL));
        if(zl.isEmpty()) {
            throw new SistemskaOperacijaException("Unet email ili šifra nisu ispravni. Molimo pokušajte ponovo.");
        }
        Zaposleni z = zl.get(0);
        if(!PasswordHasher.hashEquals(z.getSifra(),z.getSalt(),zaposleni.getSifra())) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Losa sifra: Ocekivano: " + z.getSifra() + " , dobijeno: " + PasswordHasher.hash(zaposleni.getSifra(),Base64.getDecoder().decode(z.getSalt())));
            throw new SistemskaOperacijaException("Unet email ili šifra nisu ispravni. Molimo pokušajte ponovo.");
        }

        if(!z.isAdmin()) {
            Date danas = new Date();
            if(z.getSmene() == null || z.getSmene().isEmpty() || (z.getSmene().size() == 1 && z.getSmene().get(0).getDatum() == null)) {
                throw new SistemskaOperacijaException("Zaposleni nema zabeleženu smenu danas. Ako mislite da je ovo greška, obratite se administratoru.");
            }
            for(Smena s : z.getSmene()) {
                if(isSameDay(danas,s.getDatum())) {
                    return z;
                }
            }

            throw new SistemskaOperacijaException("Zaposleni nema zabeleženu smenu danas. Ako mislite da je ovo greška, obratite se administratoru.");
        }
        return z;
    }
}
