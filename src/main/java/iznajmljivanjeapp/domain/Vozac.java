/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iznajmljivanjeapp.domain;

import framework.orm.anotacije.NePrikazuj;
import framework.orm.anotacije.vrednosnaogranicenja.Email;
import framework.orm.anotacije.vrednosnaogranicenja.NotNull;
import framework.orm.Entitet;
import framework.orm.anotacije.kljuc.PrimarniKljuc;
import framework.orm.anotacije.kljuc.ManyToOne;

/**
 *
 * @author Djurkovic
 */
public class Vozac extends Entitet {

    @Override
    public String toString() {
        return "Vozac{" + "id=" + id + ", ime=" + ime + ", prezime=" + prezime + ", email=" + email + ", dozvola=" + dozvola + '}';
    }

    @NePrikazuj
    @PrimarniKljuc
    private Integer id;
    
    @NotNull
    private String ime;
    
    @NotNull
    private String prezime;
    
    @Email
    @NotNull
    private String email;
    
    @NotNull
    @ManyToOne(joinColumn = "idDozvola", poljaZaPrikazivanje = {"kategorija"})
    private Dozvola dozvola;  

    public Vozac() {
    }

    public Vozac(String ime, String prezime, String email, Dozvola dozvola) {
        this.ime = ime;
        this.prezime = prezime;
        this.email = email;
        this.dozvola = dozvola;
    }

    public Vozac(int id) {
        this.id = id;
    }

    public Vozac(int id, String ime, String prezime, String email, Dozvola dozvola) {
        this.id = id;
        this.ime = ime;
        this.prezime = prezime;
        this.email = email;
        this.dozvola = dozvola;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Dozvola getDozvola() {
        return dozvola;
    }

    public void setDozvola(Dozvola dozvola) {
        this.dozvola = dozvola;
    }

}
