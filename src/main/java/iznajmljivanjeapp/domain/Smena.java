/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iznajmljivanjeapp.domain;

import framework.orm.anotacije.vrednosnaogranicenja.NotNull;
import framework.orm.anotacije.vrednosnaogranicenja.Between;
import framework.orm.anotacije.ImeTabele;
import framework.orm.anotacije.kljuc.ManyToOne;
import framework.orm.Entitet;
import framework.orm.anotacije.kljuc.PrimarniKljuc;
import framework.orm.anotacije.kljuc.SlozenKljuc;
import iznajmljivanjeapp.domain.kljucevi.SmenaKljuc;

import java.util.Date;

/**
 *
 * @author Djurkovic
 */
@ImeTabele("zapter")
@SlozenKljuc(wrapper = SmenaKljuc.class)
public class Smena extends Entitet {

       
    @PrimarniKljuc
    private Date datum; 
    
    @PrimarniKljuc
    @ManyToOne(joinColumn = "idZaposleni")
    @NotNull
    private Zaposleni zaposleni;  
    
    @PrimarniKljuc
    @ManyToOne(joinColumn = "idTerminDezurstva")
    @NotNull
    private TerminDezurstva terminDezurstva; 
    
    @NotNull
    private Boolean vanredan;
    
    @NotNull
    @Between(donjaGranica = 4, gornjaGranica = 8, equal = true)
    private Integer brojSati;
    
    @NotNull
    private Integer fiksniBonus;

    public Smena() {
    }

    public Smena(boolean vanredan, int brojSati, int fiksniBonus) {
        this.vanredan = vanredan;
        this.brojSati = brojSati;
        this.fiksniBonus = fiksniBonus;
    }

    public Smena(Date datum, Zaposleni zaposleni, TerminDezurstva terminDezurstva) {
        this.datum = datum;
        this.zaposleni = zaposleni;
        this.terminDezurstva = terminDezurstva;
    }

    public Smena(Date datum, Zaposleni zaposleni, TerminDezurstva terminDezurstva, boolean vanredan, int brojSati, int fiksniBonus) {
        this.datum = datum;
        this.zaposleni = zaposleni;
        this.terminDezurstva = terminDezurstva;
        this.vanredan = vanredan;
        this.brojSati = brojSati;
        this.fiksniBonus = fiksniBonus;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public Zaposleni getZaposleni() {
        return zaposleni;
    }

    public void setZaposleni(Zaposleni zaposleni) {
        this.zaposleni = zaposleni;
    }

    public TerminDezurstva getTerminDezurstva() {
        return terminDezurstva;
    }

    public void setTerminDezurstva(TerminDezurstva terminDezurstva) {
        this.terminDezurstva = terminDezurstva;
    }

    public Boolean isVanredan() {
        return vanredan;
    }

    public void setVanredan(boolean vanredan) {
        this.vanredan = vanredan;
    }

    public Integer getBrojSati() {
        return brojSati;
    }

    public void setBrojSati(int brojSati) {
        this.brojSati = brojSati;
    }

    public Integer getFiksniBonus() {
        return fiksniBonus;
    }

    public void setFiksniBonus(int fiksniBonus) {
        this.fiksniBonus = fiksniBonus;
    }

    @Override
    public boolean vrednosnaOgranicenja() {
        if(!super.vrednosnaOgranicenja()) return false;
        if (!vanredan && fiksniBonus != 0) {
            return false;
        }
        if (vanredan && (fiksniBonus < 150 * brojSati || fiksniBonus > 250 * brojSati)) {
            return false;
        }
        
        return true;
    }

}
