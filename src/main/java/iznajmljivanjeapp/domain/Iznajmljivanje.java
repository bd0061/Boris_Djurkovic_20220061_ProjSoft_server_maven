/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iznajmljivanjeapp.domain;

/**
 *
 * @author Djurkovic
 */
import framework.orm.anotacije.NePrikazuj;
import framework.orm.anotacije.vrednosnaogranicenja.GreaterThan;
import framework.orm.anotacije.vrednosnaogranicenja.NotNull;
import framework.orm.Entitet;
import framework.orm.anotacije.kljuc.PrimarniKljuc;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import framework.orm.anotacije.kljuc.ManyToOne;
import framework.orm.anotacije.kljuc.OneToMany;

public class Iznajmljivanje extends Entitet {

    @NePrikazuj
    @PrimarniKljuc
    private Integer id;
    
    @NotNull
    private Date datumSklapanja;
    
    @NotNull
    @GreaterThan(0)
    private Double ukupanIznos;
    

    @ManyToOne(joinColumn = "idZaposleni", poljaZaPrikazivanje = {"email:Email Zaposlenog"})
    @NotNull
    private Zaposleni zaposleni;  
    
    @ManyToOne(joinColumn = "idVozac", poljaZaPrikazivanje = {"email:Email Vozača"})
    @NotNull
    private Vozac vozac; 
    
    @OneToMany(mappedBy = "iznajmljivanje")
    private List<StavkaIznajmljivanja> stavke;

    @Override
    public String toString() {
        return "Iznajmljivanje{" + "id=" + id + ", datumSklapanja=" + (datumSklapanja != null ? new SimpleDateFormat("yyyy-MM-dd").format(datumSklapanja) : null) + ", ukupanIznos=" + ukupanIznos + ", zaposleni=" + zaposleni + ", vozac=" + vozac + ", stavke=" + stavke + '}';
    }

    

    public Iznajmljivanje() {
    }

    public Iznajmljivanje(Integer id, Date datumSklapanja, double ukupanIznos, Zaposleni zaposleni, Vozac vozac) {
        this.id = id;
        this.datumSklapanja = datumSklapanja;
        this.ukupanIznos = ukupanIznos;
        this.zaposleni = zaposleni;
        this.vozac = vozac;
    }

    public Iznajmljivanje(Date datumSklapanja, double ukupanIznos, Zaposleni zaposleni, Vozac vozac) {
        this.datumSklapanja = datumSklapanja;
        this.ukupanIznos = ukupanIznos;
        this.zaposleni = zaposleni;
        this.vozac = vozac;
    }

    public Iznajmljivanje(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDatumSklapanja() {
        return datumSklapanja;
    }

    public void setDatumSklapanja(Date datumSklapanja) {
        this.datumSklapanja = datumSklapanja;
    }

    public Double getUkupanIznos() {
        return ukupanIznos;
    }

    public void setUkupanIznos(double ukupanIznos) {
        this.ukupanIznos = ukupanIznos;
    }

    public Zaposleni getZaposleni() {
        return zaposleni;
    }

    public void setZaposleni(Zaposleni zaposleni) {
        this.zaposleni = zaposleni;
    }

    public Vozac getVozac() {
        return vozac;
    }

    public void setVozac(Vozac vozac) {
        this.vozac = vozac;
    }
    
    public List<StavkaIznajmljivanja> getStavke() {
        return this.stavke;
    }
    public void setStavke(List<StavkaIznajmljivanja> stavke){
        this.stavke = stavke;
    }
}
