package iznajmljivanjeapp.services;

import framework.injector.OpstiServis;
import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import iznajmljivanjeapp.domain.TerminDezurstva;
import iznajmljivanjeapp.repositories.Repository;

import java.util.List;

public class TerminDezurstvaService extends OpstiServis {

    private final Repository<TerminDezurstva> terminDezurstvaRepository;

    public TerminDezurstvaService(Repository<TerminDezurstva> terminDezurstvaRepository) {
        this.terminDezurstvaRepository = terminDezurstvaRepository;
    }

    public void kreirajTerminDezurstva(TerminDezurstva terminDezurstva) throws Exception {
         terminDezurstvaRepository.kreiraj(terminDezurstva);
    }

    public void obrisiTerminDezurstva(TerminDezurstva terminDezurstva) throws Exception {
        terminDezurstvaRepository.obrisi(terminDezurstva);
    }

    public void promeniTerminDezurstva(TerminDezurstva terminDezurstva) throws Exception {
        terminDezurstvaRepository.promeni(terminDezurstva);
    }

    public void promeniTerminDezurstva(TerminDezurstva terminDezurstva1,TerminDezurstva terminDezurstva2) throws Exception {
        terminDezurstvaRepository.promeni(terminDezurstva1,new TerminDezurstva[]{terminDezurstva2});
    }
    public List<TerminDezurstva> vratiListuSviTerminDezurstva(List<KriterijumDescriptor> descriptors) throws Exception {
        return terminDezurstvaRepository.vratiListuSvi(descriptors);
    }

    public List<TerminDezurstva> vratiListuSviTerminDezurstva(KriterijumWrapper w) throws Exception {
        return terminDezurstvaRepository.vratiListuSvi(w);
    }
}
