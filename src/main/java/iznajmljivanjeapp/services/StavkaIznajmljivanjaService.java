package iznajmljivanjeapp.services;

import framework.injector.OpstiServis;
import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import iznajmljivanjeapp.domain.StavkaIznajmljivanja;
import iznajmljivanjeapp.repositories.Repository;

import java.util.List;

public class StavkaIznajmljivanjaService extends OpstiServis {
    private final Repository<StavkaIznajmljivanja> stavkaIznajmljivanjaRepository;

    public StavkaIznajmljivanjaService(Repository<StavkaIznajmljivanja> stavkaIznajmljivanjaRepository) {
        this.stavkaIznajmljivanjaRepository = stavkaIznajmljivanjaRepository;
    }

    public void kreirajStavkaIznajmljivanja(StavkaIznajmljivanja s) throws Exception {
         stavkaIznajmljivanjaRepository.kreiraj(s);
    }

    public void obrisiStavkaIznajmljivanja(StavkaIznajmljivanja s) throws Exception {
        stavkaIznajmljivanjaRepository.obrisi(s);
    }

    public void promeniStavkaIznajmljivanja(StavkaIznajmljivanja s) throws Exception {
        stavkaIznajmljivanjaRepository.promeni(s);
    }
    public void promeniStavkaIznajmljivanja(StavkaIznajmljivanja s1, StavkaIznajmljivanja s2) throws Exception {
        stavkaIznajmljivanjaRepository.promeni(s1,new StavkaIznajmljivanja[]{s2});
    }

    public List<StavkaIznajmljivanja> vratiListuSviStavkaIznajmljivanja(List<KriterijumDescriptor> descriptors) throws Exception {
        return stavkaIznajmljivanjaRepository.vratiListuSvi(descriptors);
    }

    public List<StavkaIznajmljivanja> vratiListuSviStavkaIznajmljivanja(KriterijumWrapper w) throws Exception {
        return stavkaIznajmljivanjaRepository.vratiListuSvi(w);
    }

}
