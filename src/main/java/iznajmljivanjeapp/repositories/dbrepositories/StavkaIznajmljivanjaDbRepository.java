package iznajmljivanjeapp.repositories.dbrepositories;

import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.enumeracije.InsertBehaviour;
import framework.orm.EntityManager;
import iznajmljivanjeapp.domain.StavkaIznajmljivanja;
import iznajmljivanjeapp.repositories.Repository;

import java.util.List;

public class StavkaIznajmljivanjaDbRepository implements Repository<StavkaIznajmljivanja> {

    private EntityManager em;

    @Override
    public void kreiraj(StavkaIznajmljivanja... entitet) throws Exception {
         em.kreirajEntitet(entitet);
    }

    @Override
    public void kreiraj(InsertBehaviour ib, StavkaIznajmljivanja... entitet) throws Exception {
        em.kreirajEntitet(ib,entitet);
    }


    @Override
    public void promeni(StavkaIznajmljivanja entitet) throws Exception {
        em.promeniEntitet(entitet);
    }

    @Override
    public void promeni(StavkaIznajmljivanja entitet1, StavkaIznajmljivanja[] entitet2) throws Exception {
        em.promeniEntitet(entitet1, entitet2);
    }

    @Override
    public void obrisi(StavkaIznajmljivanja... entitet) throws Exception {
        em.obrisiEntitet(entitet);
    }

    @Override
    public void obrisiConditional(Object c) throws Exception {

    }

    @Override
    public List<StavkaIznajmljivanja> vratiListuSvi(List<KriterijumDescriptor> descriptors) throws Exception {
        return em.vratiSve(StavkaIznajmljivanja.class, descriptors);
    }

    @Override
    public List<StavkaIznajmljivanja> vratiListuSvi(KriterijumWrapper w) throws Exception {
        return em.vratiSve(StavkaIznajmljivanja.class, w);
    }
}
