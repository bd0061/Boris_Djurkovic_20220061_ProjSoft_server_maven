/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iznajmljivanjeapp.domain;

import framework.orm.anotacije.vrednosnaogranicenja.In;
import framework.orm.anotacije.vrednosnaogranicenja.NotNull;
import framework.orm.Entitet;
import framework.orm.anotacije.kljuc.PrimarniKljuc;


/**
 *
 * @author Djurkovic
 */
public class Dozvola extends Entitet {
    
    @PrimarniKljuc
    private Integer id; 
    
    @NotNull
    @In({"A","B","D"})
    private Character kategorija;
    

    @Override
    public String toString() {
        return "Dozvola{" + "id=" + id + ", kategorija=" + kategorija + '}';
    }
    
    public Dozvola() {}
    
    public Dozvola(int id, char kategorija) {
        this.id = id;
        this.kategorija = kategorija;
    }

    public Dozvola(char kategorija) {
        this.kategorija = kategorija;
    }

    public Dozvola(int id) {
        this.id = id;
    }
    

    public Integer getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Character getKategorija() {
        return kategorija;
    }

    public void setKategorija(char kategorija) {
        this.kategorija = kategorija;
    }
}
