package iznajmljivanjeapp.domain.uslovniobjekti;

import iznajmljivanjeapp.domain.Zaposleni;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SmenaUslov implements Serializable {
    public Date datumOd;
    public Date datumDo;
    public Boolean vanredne;
    public List<Zaposleni> zaposleni;
}
