/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iznajmljivanjeapp.domain.kljucevi;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Djurkovic
 */
public class SmenaKljuc {
    private Date datum;
    private Integer idZaposleni;
    private Integer idTerminDezurstva;


    public SmenaKljuc() {}


    public SmenaKljuc(Date datum, Integer idTerminDezurstva, Integer idZaposleni) {
        this.datum = datum;
        this.idTerminDezurstva = idTerminDezurstva;
        this.idZaposleni = idZaposleni;
    }


    @Override
    public int hashCode() {
        return Objects.hash(datum, idZaposleni, idTerminDezurstva);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SmenaKljuc other = (SmenaKljuc) obj;
        if (!Objects.equals(this.datum, other.datum)) {
            return false;
        }
        if (!Objects.equals(this.idZaposleni, other.idZaposleni)) {
            return false;
        }
        return Objects.equals(this.idTerminDezurstva, other.idTerminDezurstva);
    }
    
    
    
}
