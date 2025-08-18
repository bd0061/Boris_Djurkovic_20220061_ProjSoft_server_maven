package iznajmljivanjeapp.repositories.dbrepositories;

import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.enumeracije.InsertBehaviour;
import framework.orm.EntityManager;
import iznajmljivanjeapp.domain.Iznajmljivanje;
import iznajmljivanjeapp.repositories.Repository;

import java.util.List;

public class IznajmljivanjeDbRepository implements Repository<Iznajmljivanje> {
    private EntityManager em;
    @Override
    public void kreiraj(Iznajmljivanje... entitet) throws Exception {
         em.kreirajEntitet(entitet);
    }

    @Override
    public void kreiraj(InsertBehaviour ib, Iznajmljivanje... entitet) throws Exception {
        em.kreirajEntitet(ib,entitet);
    }


    @Override
    public void promeni(Iznajmljivanje entitet) throws Exception {
        em.promeniEntitet(entitet);
    }

    @Override
    public void promeni(Iznajmljivanje entitet1, Iznajmljivanje[] entitet2) throws Exception {
        em.promeniEntitet(entitet1, entitet2);
    }

    @Override
    public void obrisi(Iznajmljivanje... entitet) throws Exception {
        em.obrisiEntitet(entitet);
    }

    @Override
    public void obrisiConditional(Object c) throws Exception{

    }

    @Override
    public List<Iznajmljivanje> vratiListuSvi(List<KriterijumDescriptor> descriptors) throws Exception {
        return em.vratiSve(Iznajmljivanje.class,descriptors);
    }

    @Override
    public List<Iznajmljivanje> vratiListuSvi(KriterijumWrapper w) throws Exception {
        return em.vratiSve(Iznajmljivanje.class,w);
    }
}
