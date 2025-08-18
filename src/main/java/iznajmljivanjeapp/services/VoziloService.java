package iznajmljivanjeapp.services;

import framework.injector.OpstiServis;
import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import iznajmljivanjeapp.domain.Vozilo;
import iznajmljivanjeapp.repositories.Repository;

import java.util.List;


public class VoziloService extends OpstiServis {
    private final Repository<Vozilo> voziloRepository;

    public VoziloService(Repository<Vozilo> voziloRepository) {
        this.voziloRepository = voziloRepository;
    }

    public void kreirajVozilo(Vozilo vozilo) throws Exception {
         voziloRepository.kreiraj(vozilo);
    }
    public void obrisiVozilo(Vozilo vozilo) throws Exception {
        voziloRepository.obrisi(vozilo);
    }
    public void promeniVozilo(Vozilo vozilo) throws Exception {
        voziloRepository.promeni(vozilo);
    }

    public void promeniVozilo(Vozilo vozilo1, Vozilo vozilo2) throws Exception {
        voziloRepository.promeni(vozilo1,new Vozilo[]{vozilo2});
    }

    public List<Vozilo> vratiListuSviVozilo(List<KriterijumDescriptor> descriptors) throws Exception {
        return voziloRepository.vratiListuSvi(descriptors);
    }

    public List<Vozilo> vratiListuSviVozilo(KriterijumWrapper w) throws Exception {
        return voziloRepository.vratiListuSvi(w);
    }
}
