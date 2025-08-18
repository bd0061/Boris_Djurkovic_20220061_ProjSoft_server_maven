/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iznajmljivanjeapp.domain;

import framework.orm.anotacije.kljuc.OneToMany;
import iznajmljivanjeapp.domain.enumeracije.TipTerminaEnum;
import framework.orm.Entitet;
import framework.orm.anotacije.kljuc.PrimarniKljuc;
import framework.orm.anotacije.vrednosnaogranicenja.NotNull;

import java.util.List;

/**
 *
 * @author Djurkovic
 */
public class TerminDezurstva extends Entitet {

    @PrimarniKljuc
    private Integer id; // pk

    private String napomena;

    @OneToMany(mappedBy = "terminDezurstva")
    private List<Smena> smene;

    @NotNull
    private TipTerminaEnum tipTermina;

    public TerminDezurstva() {
    }

    @Override
    public String toString() {
        if(tipTermina == null) return "";
        return switch(tipTermina) {
            case TipTerminaEnum.PREPODNE -> "Prepodne";
            case TipTerminaEnum.POPODNE -> "Popodne";
            case TipTerminaEnum.NOC -> "Noć";
        };
    }



    public TerminDezurstva(int id, String napomena, TipTerminaEnum tipTermina) {
        this.id = id;
        this.napomena = napomena;
        this.tipTermina = tipTermina;
    }

    public TerminDezurstva(String napomena, TipTerminaEnum tipTermina) {
        this.napomena = napomena;
        this.tipTermina = tipTermina;
    }

    public TerminDezurstva(TipTerminaEnum tipTermina) {
        this.tipTermina = tipTermina;
    }

    public TerminDezurstva(int id) {
        this.id = id;
    }

    public List<Smena> getSmene() {
        return smene;
    }

    public void setSmene(List<Smena> smene) {
        this.smene = smene;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNapomena() {
        return napomena;
    }

    public void setNapomena(String napomena) {
        this.napomena = napomena;
    }

    public TipTerminaEnum getTipTermina() {
        return tipTermina;
    }

    public void setTipTermina(TipTerminaEnum tipTermina) {
        this.tipTermina = tipTermina;
    }
}
