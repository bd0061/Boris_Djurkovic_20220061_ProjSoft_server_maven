package iznajmljivanjeapp.services;

import framework.injector.OpstiServis;
import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import iznajmljivanjeapp.domain.Vozac;
import iznajmljivanjeapp.repositories.Repository;

import java.util.List;


public class VozacService extends OpstiServis {
    private final Repository<Vozac> vozacRepository;

    public VozacService(Repository<Vozac> vozacRepository) {
        this.vozacRepository = vozacRepository;
    }

    public void kreirajVozac(Vozac v) throws Exception {
         vozacRepository.kreiraj(v);
    }
    public void obrisiVozac(Vozac v) throws Exception {
        vozacRepository.obrisi(v);
    }
    public void promeniVozac(Vozac v) throws Exception {
        vozacRepository.promeni(v);
    }
    public void promeniVozac(Vozac v1, Vozac v2) throws Exception {
        vozacRepository.promeni(v1,new Vozac[]{v2});
    }
    public List<Vozac> vratiListuSviVozac(List<KriterijumDescriptor> descriptors) throws Exception {
        return vozacRepository.vratiListuSvi(descriptors);
    }

    public List<Vozac> vratiListuSviVozac(KriterijumWrapper w) throws Exception {
        return vozacRepository.vratiListuSvi(w);
    }
}
