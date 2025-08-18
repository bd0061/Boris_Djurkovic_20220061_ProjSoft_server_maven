/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iznajmljivanjeapp.domain.kljucevi;

import java.util.Objects;

/**
 *
 * @author Djurkovic
 */
public class StavkaIznajmljivanjaKljuc {
    private Integer  idIznajmljivanje;
    private Integer rb;


    public StavkaIznajmljivanjaKljuc() {

    }

    public StavkaIznajmljivanjaKljuc(Integer idIznajmljivanje, Integer rb) {
        this.idIznajmljivanje = idIznajmljivanje;
        this.rb = rb;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idIznajmljivanje,rb);
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
        final StavkaIznajmljivanjaKljuc other = (StavkaIznajmljivanjaKljuc) obj;
        if (!Objects.equals(this.idIznajmljivanje, other.idIznajmljivanje)) {
            return false;
        }
        return Objects.equals(this.rb, other.rb);
    }
    
}
