package iznajmljivanjeapp.repositories.dbrepositories;

import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.enumeracije.InsertBehaviour;
import framework.orm.EntityManager;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;
import iznajmljivanjeapp.domain.Vozilo;
import iznajmljivanjeapp.repositories.Repository;

import java.util.List;

public class VoziloDbRepository implements Repository<Vozilo> {

    private EntityManager em;
    @Override
    public void kreiraj(Vozilo... entitet) throws Exception {
         em.kreirajEntitet(entitet);
    }

    @Override
    public void kreiraj(InsertBehaviour ib, Vozilo... entitet) throws Exception {
        em.kreirajEntitet(ib,entitet);
    }


    @Override
    public void promeni(Vozilo entitet) throws Exception {
        em.promeniEntitet(entitet);
    }

    @Override
    public void promeni(Vozilo entitet1, Vozilo[] entitet2) throws Exception {
        em.promeniEntitet(entitet1, entitet2);
    }

    @Override
    public void obrisi(Vozilo... entitet) throws Exception {
        em.obrisiEntitet(entitet);
    }

    @Override
    public void obrisiConditional(Object c) throws Exception {

    }

    @Override
    public List<Vozilo> vratiListuSvi(List<KriterijumDescriptor> descriptors) throws Exception {
        return em.vratiSve(Vozilo.class, descriptors);
    }

    @Override
    public List<Vozilo> vratiListuSvi(KriterijumWrapper w) throws Exception {
        return em.vratiSve(Vozilo.class, w);
    }
}
