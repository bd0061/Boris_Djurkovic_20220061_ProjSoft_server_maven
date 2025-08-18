package iznajmljivanjeapp.repositories.dbrepositories;

import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.enumeracije.InsertBehaviour;
import framework.orm.EntityManager;
import iznajmljivanjeapp.domain.Vozac;
import iznajmljivanjeapp.repositories.Repository;

import java.util.List;

public class VozacDbRepository implements Repository<Vozac> {

    private EntityManager em;

    @Override
    public void kreiraj(Vozac... entitet) throws Exception {
        em.kreirajEntitet(entitet);
    }

    @Override
    public void kreiraj(InsertBehaviour ib, Vozac... entitet) throws Exception {
        em.kreirajEntitet(ib,entitet);
    }


    @Override
    public void promeni(Vozac entitet) throws Exception {
        em.promeniEntitet(entitet);
    }

    @Override
    public void promeni(Vozac entitet1, Vozac[] entitet2) throws Exception {
        em.promeniEntitet(entitet1, entitet2);
    }

    @Override
    public void obrisi(Vozac... entitet) throws Exception {
        em.obrisiEntitet(entitet);
    }

    @Override
    public void obrisiConditional(Object c) throws Exception {

    }

    @Override
    public List<Vozac> vratiListuSvi(List<KriterijumDescriptor> descriptors) throws Exception {
        return em.vratiSve(Vozac.class, descriptors);
    }

    @Override
    public List<Vozac> vratiListuSvi(KriterijumWrapper w) throws Exception {
        return em.vratiSve(Vozac.class, w);
    }
}
