package iznajmljivanjeapp.repositories;

import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.model.MarkerRepository;
import framework.model.enumeracije.InsertBehaviour;
import framework.orm.Entitet;

import java.util.List;

public interface Repository<T extends Entitet> extends MarkerRepository {
    @SuppressWarnings("unchecked")
    void kreiraj(T... entitet) throws Exception;
    @SuppressWarnings("unchecked")
    void kreiraj(InsertBehaviour ib, T... entitet) throws Exception;
    void promeni(T entitet) throws Exception;
    void promeni(T entitet1, T[] entitet2) throws Exception;
    @SuppressWarnings("unchecked")
    void obrisi(T... entitet) throws Exception;
    void obrisiConditional(Object c) throws Exception;
    List<T> vratiListuSvi(List<KriterijumDescriptor> descriptors) throws Exception;
    List<T> vratiListuSvi(KriterijumWrapper w) throws Exception;
}
