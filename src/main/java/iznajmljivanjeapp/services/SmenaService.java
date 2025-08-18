package iznajmljivanjeapp.services;

import framework.injector.OpstiServis;
import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.enumeracije.InsertBehaviour;
import iznajmljivanjeapp.domain.uslovniobjekti.SmenaUslov;
import iznajmljivanjeapp.exceptions.SistemskaOperacijaException;
import iznajmljivanjeapp.domain.Smena;
import iznajmljivanjeapp.repositories.Repository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SmenaService extends OpstiServis {

    private final Repository<Smena> smenaRepository;

    public SmenaService(Repository<Smena> smenaRepository) {
        this.smenaRepository = smenaRepository;
    }

    public void kreirajSmena(InsertBehaviour ib,Smena... smene) throws Exception {
        //ostale greske ce hendlovati metode nizeg nivoa
        for(Smena s : smene) {
            if(s == null || s.getDatum() == null) throw new Exception("Losa smena");

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            Date today = cal.getTime();

            if(s.getDatum().before(today)) {
                throw new SistemskaOperacijaException("Smene se mogu kreirati samo za nadolazeće dane.");
            }
        }
        smenaRepository.kreiraj(ib,smene);
    }

    public void obrisiConditional(SmenaUslov u) throws Exception {
        smenaRepository.obrisiConditional(u);
    }

    public void obrisiSmena(Smena... s) throws Exception {
        smenaRepository.obrisi(s);
    }

    public void promeniSmena(Smena s) throws Exception {
        smenaRepository.promeni(s);
    }
    public void promeniSmena(Smena s1, Smena[] s2) throws Exception {
        smenaRepository.promeni(s1,s2);
    }

    public List<Smena> vratiListuSviSmena(List<KriterijumDescriptor> descriptors) throws Exception {
        return smenaRepository.vratiListuSvi(descriptors);
    }

    public List<Smena> vratiListuSviSmena(KriterijumWrapper w) throws Exception {
        return smenaRepository.vratiListuSvi(w);
    }


}
