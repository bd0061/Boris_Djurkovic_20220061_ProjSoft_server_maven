package iznajmljivanjeapp.repositories.dbrepositories;

import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.enumeracije.InsertBehaviour;
import framework.orm.EntityManager;
import iznajmljivanjeapp.domain.Dozvola;
import iznajmljivanjeapp.repositories.Repository;

import java.util.List;

public class DozvolaDbRepository implements Repository<Dozvola> {

    private EntityManager em;

    @Override
    public void kreiraj(Dozvola... entitet) throws Exception {
        em.kreirajEntitet(entitet);
    }

    @Override
    public void kreiraj(InsertBehaviour ib, Dozvola... entitet) throws Exception {
        em.kreirajEntitet(ib,entitet);
    }


    @Override
    public void promeni(Dozvola entitet) throws Exception {
        em.promeniEntitet(entitet);
    }

    @Override
    public void promeni(Dozvola entitet1, Dozvola[] entitet2) throws Exception {
        em.promeniEntitet(entitet1, entitet2);
    }

    @Override
    public void obrisi(Dozvola... entitet) throws Exception {
        em.obrisiEntitet(entitet);
    }

    @Override
    public void obrisiConditional(Object c) throws Exception {

    }

    @Override
    public List<Dozvola> vratiListuSvi(List<KriterijumDescriptor> descriptors) throws Exception {
        return em.vratiSve(Dozvola.class,descriptors);
    }

    @Override
    public List<Dozvola> vratiListuSvi(KriterijumWrapper w) throws Exception {
        return em.vratiSve(Dozvola.class,w);
    }
}
