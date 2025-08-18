package iznajmljivanjeapp.repositories.dbrepositories;

import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.enumeracije.InsertBehaviour;
import framework.orm.EntityManager;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;
import iznajmljivanjeapp.domain.Smena;
import iznajmljivanjeapp.domain.uslovniobjekti.SmenaUslov;
import iznajmljivanjeapp.repositories.Repository;

import java.text.SimpleDateFormat;
import java.util.List;

public class SmenaDbRepository implements Repository<Smena> {

    private EntityManager em;

    @Override
    public void kreiraj(Smena... entitet) throws Exception {
         em.kreirajEntitet(entitet);
    }

    @Override
    public void kreiraj(InsertBehaviour ib, Smena... entitet) throws Exception {
        em.kreirajEntitet(ib,entitet);
    }


    @Override
    public void promeni(Smena entitet) throws Exception {
        em.promeniEntitet(entitet);
    }

    @Override
    public void promeni(Smena entitet1, Smena[] entitet2) throws Exception {
        em.promeniEntitet(entitet1, entitet2);
    }

    @Override
    public void obrisi(Smena... entitet) throws Exception {
        em.obrisiEntitet(entitet);
    }

    @Override
    public void obrisiConditional(Object c) throws Exception{
        if(!(c instanceof SmenaUslov uslov)) {
            throw new Exception("los uslov");
        }
        var sdf = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder query = new StringBuilder("DELETE FROM ").append(EntityManager.vratiImeTabele(Smena.class)).append(" WHERE ");
        if(uslov.datumOd != null) {
            query.append(" datum >= ").append(sdf.format(uslov.datumOd)).append(" AND ");
        }
        if(uslov.datumDo != null) {
            query.append(" datum <= ").append(sdf.format(uslov.datumOd)).append(" AND ");;
        }
        if(uslov.vanredne != null) {
            query.append(" vanredan = ").append(uslov.vanredne).append(" AND ");;
        }
        if(uslov.zaposleni != null && !uslov.zaposleni.isEmpty()) {
            query.append(" idZaposleni IN (");
            for(var z : uslov.zaposleni) {
                query.append(z.getId()).append(",");
            }
            query.delete(query.length()-1, query.length());
            query.append(")");
        }
        else {
            query.delete(query.length() - 5, query.length());
        }
        SimpleLogger.log(LogLevel.LOG_INFO, query.toString());
        em.rawSQL(query.toString());
    }


    @Override
    public List<Smena> vratiListuSvi(List<KriterijumDescriptor> descriptors) throws Exception {
        return em.vratiSve(Smena.class, descriptors);
    }

    @Override
    public List<Smena> vratiListuSvi(KriterijumWrapper w) throws Exception {
        return em.vratiSve(Smena.class, w);
    }
}
