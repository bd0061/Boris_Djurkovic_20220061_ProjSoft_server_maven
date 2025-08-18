package iznajmljivanjeapp.repositories.dbrepositories;

import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.enumeracije.InsertBehaviour;
import framework.orm.EntityManager;
import iznajmljivanjeapp.domain.TerminDezurstva;
import iznajmljivanjeapp.repositories.Repository;

import java.util.List;

public class TerminDezurstvaDbRepository implements Repository<TerminDezurstva> {
    private EntityManager em;

    @Override
    public void kreiraj(TerminDezurstva... entitet) throws Exception {
         em.kreirajEntitet(entitet);
    }

    @Override
    public void kreiraj(InsertBehaviour ib, TerminDezurstva... entitet) throws Exception {
        em.kreirajEntitet(ib,entitet);
    }


    @Override
    public void promeni(TerminDezurstva entitet) throws Exception {
        em.promeniEntitet(entitet);
    }

    @Override
    public void promeni(TerminDezurstva entitet1, TerminDezurstva[] entitet2) throws Exception {
        em.promeniEntitet(entitet1, entitet2);
    }

    @Override
    public void obrisi(TerminDezurstva... entitet) throws Exception {
        em.obrisiEntitet(entitet);
    }

    @Override
    public void obrisiConditional(Object c) throws Exception {

    }

    @Override
    public List<TerminDezurstva> vratiListuSvi(List<KriterijumDescriptor> descriptors) throws Exception {
        return em.vratiSve(TerminDezurstva.class, descriptors);
    }

    @Override
    public List<TerminDezurstva> vratiListuSvi(KriterijumWrapper w) throws Exception {
        return em.vratiSve(TerminDezurstva.class, w);
    }
}
