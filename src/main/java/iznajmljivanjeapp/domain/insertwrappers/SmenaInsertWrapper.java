package iznajmljivanjeapp.domain.insertwrappers;

import framework.model.enumeracije.InsertBehaviour;
import iznajmljivanjeapp.domain.Smena;

import java.io.Serializable;
import java.util.List;

public class SmenaInsertWrapper implements Serializable {
    public InsertBehaviour ib;
    public List<Smena> smene;
}
