package iznajmljivanjeapp.repositories.dbrepositories;

import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.enumeracije.InsertBehaviour;
import framework.orm.EntityManager;
import iznajmljivanjeapp.domain.Zaposleni;
import iznajmljivanjeapp.repositories.Repository;

import java.util.List;

public class ZaposleniDbRepository implements Repository<Zaposleni> {

    private EntityManager em;
    @Override
    public void kreiraj(Zaposleni... entitet) throws Exception {
         em.kreirajEntitet(entitet);
    }

    @Override
    public void kreiraj(InsertBehaviour ib, Zaposleni... entitet) throws Exception {
        em.kreirajEntitet(ib,entitet);
    }


    @Override
    public void promeni(Zaposleni entitet) throws Exception {
        em.promeniEntitet(entitet);
    }

    @Override
    public void promeni(Zaposleni entitet1, Zaposleni[] entitet2) throws Exception {
        em.promeniEntitet(entitet1, entitet2);
    }

    @Override
    public void obrisi(Zaposleni... entitet) throws Exception {
        em.obrisiEntitet(entitet);
    }

    @Override
    public void obrisiConditional(Object c) throws Exception {

    }

    @Override
    public List<Zaposleni> vratiListuSvi(List<KriterijumDescriptor> descriptors) throws Exception {
        return em.vratiSve(Zaposleni.class, descriptors);
    }

    @Override
    public List<Zaposleni> vratiListuSvi(KriterijumWrapper w) throws Exception {
        return em.vratiSve(Zaposleni.class, w);
    }
}
