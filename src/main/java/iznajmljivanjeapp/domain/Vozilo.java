/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iznajmljivanjeapp.domain;

import framework.orm.anotacije.NePrikazuj;
import framework.orm.anotacije.vrednosnaogranicenja.GreaterThan;
import framework.orm.anotacije.vrednosnaogranicenja.In;
import framework.orm.anotacije.vrednosnaogranicenja.NotNull;
import framework.orm.anotacije.vrednosnaogranicenja.Between;
import framework.orm.anotacije.kljuc.OneToMany;
import iznajmljivanjeapp.domain.enumeracije.KategorijaEnum;
import framework.orm.Entitet;
import framework.orm.anotacije.kljuc.PrimarniKljuc;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Djurkovic
 */
public class Vozilo extends Entitet {

    @Override
    public String toString() {
        return proizvodjac + " " + imeModela + " (id = " + id + ")";
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.id);
        hash = 89 * hash + Objects.hashCode(this.klasa);
        hash = 89 * hash + Objects.hashCode(this.proizvodjac);
        hash = 89 * hash + Objects.hashCode(this.kupovnaCena);
        hash = 89 * hash + Objects.hashCode(this.godiste);
        hash = 89 * hash + Objects.hashCode(this.imeModela);
        hash = 89 * hash + Objects.hashCode(this.cenaPoDanu);
        hash = 89 * hash + Objects.hashCode(this.kategorija);
        hash = 89 * hash + Objects.hashCode(this.stavke);
        return hash;
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
        final Vozilo other = (Vozilo) obj;
        if (!Objects.equals(this.klasa, other.klasa)) {
            return false;
        }
        if (!Objects.equals(this.proizvodjac, other.proizvodjac)) {
            return false;
        }
        if (!Objects.equals(this.imeModela, other.imeModela)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.kupovnaCena, other.kupovnaCena)) {
            return false;
        }
        if (!Objects.equals(this.godiste, other.godiste)) {
            return false;
        }
        if (!Objects.equals(this.cenaPoDanu, other.cenaPoDanu)) {
            return false;
        }
        if (this.kategorija != other.kategorija) {
            return false;
        }
        return Objects.equals(this.stavke, other.stavke);
    }
    
    


    @NePrikazuj
    @PrimarniKljuc
    private Integer id; //primarni kljuc
    
    @NotNull
    @In({"Automobil", "Minibus", "Motor"})
    private String klasa;
    
    @NotNull
    @In({"Mercedes", "Fiat", "Volkswagen", "Dacia", "Audi", "Skoda"})
    private String proizvodjac;
    
    @NotNull
    @GreaterThan(0)
    private Double kupovnaCena;
    
    //moramo da hard codujemo trenutnu godinu jer anotacije moraju da sadrze samo compile time izraze :)
    @NotNull
    @Between(donjaGranica = 2004, gornjaGranica = 2025, equal = true)
    private Integer godiste;
    
    @NotNull
    private String imeModela;
    
    @NotNull
    @GreaterThan(0)
    private Double cenaPoDanu;
    
    @NotNull
    private KategorijaEnum kategorija;


    @OneToMany(mappedBy = "vozilo")
    private List<StavkaIznajmljivanja> stavke;

    public List<StavkaIznajmljivanja> getStavke() {
        return stavke;
    }

    public void setStavke(List<StavkaIznajmljivanja> stavke) {
        this.stavke = stavke;
    }
    
    //konstruktori
    public Vozilo() {
    }

    public Vozilo(int id) {
        this.id = id;
    }

    public Vozilo(String klasa, String proizvodjac, double kupovnaCena, int godiste, String imeModela, KategorijaEnum kategorija, double cenaPoDanu) {
        this.klasa = klasa;
        this.proizvodjac = proizvodjac;
        this.kupovnaCena = kupovnaCena;
        this.godiste = godiste;
        this.imeModela = imeModela;
        this.kategorija = kategorija;
        this.cenaPoDanu = cenaPoDanu;
    }

    public Vozilo(int id, String klasa, String proizvodjac, double kupovnaCena, int godiste, String imeModela, KategorijaEnum kategorija,double cenaPoDanu) {
        this.id = id;
        this.klasa = klasa;
        this.proizvodjac = proizvodjac;
        this.kupovnaCena = kupovnaCena;
        this.godiste = godiste;
        this.imeModela = imeModela;
        this.kategorija = kategorija;
        this.cenaPoDanu = cenaPoDanu;
    }

    public Double getCenaPoDanu() {
        return cenaPoDanu;
    }

    public void setCenaPoDanu(Double cenaPoDanu) {
        this.cenaPoDanu = cenaPoDanu;
    }
    
    //geteri i seteri
    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKlasa() {
        return klasa;
    }

    public void setKlasa(String klasa) {
        this.klasa = klasa;
    }

    public String getProizvodjac() {
        return proizvodjac;
    }

    public void setProizvodjac(String proizvodjac) {
        this.proizvodjac = proizvodjac;
    }

    public Double getKupovnaCena() {
        return kupovnaCena;
    }

    public void setKupovnaCena(double kupovnaCena) {
        this.kupovnaCena = kupovnaCena;
    }

    public Integer getGodiste() {
        return godiste;
    }

    public void setGodiste(int godiste) {
        this.godiste = godiste;
    }

    public String getImeModela() {
        return imeModela;
    }

    public void setImeModela(String imeModela) {
        this.imeModela = imeModela;
    }

    public KategorijaEnum getKategorija() {
        return kategorija;
    }

    public void setKategorija(KategorijaEnum kategorija) {
        this.kategorija = kategorija;
    }

    @Override
    public boolean vrednosnaOgranicenja() {
        if(!super.vrednosnaOgranicenja()) return false;

        if (klasa.equals("Motor") && proizvodjac.equals("Dacia")) {
            return false;
        }

        return true;
    }
}
