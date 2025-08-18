package iznajmljivanjeapp.services;

import framework.injector.OpstiServis;
import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import iznajmljivanjeapp.domain.Dozvola;
import iznajmljivanjeapp.repositories.Repository;

import java.util.List;


public class DozvolaService extends OpstiServis {

    private final Repository<Dozvola> dozvolaRepository;

    public DozvolaService(Repository<Dozvola> dozvolaRepository) {
        this.dozvolaRepository = dozvolaRepository;
    }

    public void kreirajDozvola(Dozvola dozvola) throws Exception {
         dozvolaRepository.kreiraj(dozvola);
    }
    public void obrisiDozvola(Dozvola dozvola) throws Exception {
        dozvolaRepository.obrisi(dozvola);
    }
    public void promeniDozvola(Dozvola dozvola) throws Exception {
        dozvolaRepository.promeni(dozvola);
    }

    public void promeniDozvola(Dozvola dozvola1, Dozvola dozvola2) throws Exception {
        dozvolaRepository.promeni(dozvola1,new Dozvola[]{dozvola2});
    }

    public List<Dozvola> vratiListuSviDozvola(KriterijumWrapper w) throws Exception {
        return dozvolaRepository.vratiListuSvi(w);
    }

}
