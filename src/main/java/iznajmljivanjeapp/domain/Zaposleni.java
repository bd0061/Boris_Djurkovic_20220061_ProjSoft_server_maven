/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iznajmljivanjeapp.domain;

import framework.orm.Entitet;
import framework.orm.anotacije.NePrikazuj;
import framework.orm.anotacije.kljuc.PrimarniKljuc;
import framework.orm.anotacije.kljuc.OneToMany;
import framework.orm.anotacije.vrednosnaogranicenja.Email;
import framework.orm.anotacije.vrednosnaogranicenja.NotNull;
import java.util.List;

/**
 *
 * @author Djurkovic
 */
public class Zaposleni extends Entitet {

    public Zaposleni(String email, String sifra) {
        this.email = email;
        this.sifra = sifra;
    }

    @NePrikazuj
    @PrimarniKljuc
    private Integer id;
    
    @NotNull
    private String ime;
    
    @NotNull
    private String prezime;
    
    @NotNull
    @Email
    private String email;

    @NePrikazuj
    @NotNull
    private String sifra; //hash

    @NePrikazuj
    @NotNull
    private String salt;

    @NotNull
    private Boolean admin;

    @OneToMany(mappedBy ="zaposleni")
    private List<Smena> smene;

    @Override
    public String toString() {
        return "Zaposleni{" + "id=" + id + ", ime=" + ime + ", prezime=" + prezime + ", email=" + email + ", salt=" + salt + '}';
    }
    


    public Zaposleni() {}
    public Zaposleni(int id) {
        this.id = id;
    }

    
    public List<Smena> getSmene() {
        return this.smene;
    }
    
    public void setSmene(List<Smena> smene) {
        this.smene = smene;
    }
    
    public Zaposleni(int id, String ime, String prezime, String email, String sifra, String salt) {
        this.id = id;
        this.ime = ime;
        this.prezime = prezime;
        this.email = email;
        this.sifra = sifra;
        this.salt = salt;
        this.admin = false;
    }

    public Zaposleni(Integer id, String ime, String prezime, String email, String sifra) {
        this.id = id;
        this.ime = ime;
        this.prezime = prezime;
        this.email = email;
        this.sifra = sifra;
        this.admin = false;
    }

    public Zaposleni(int id, String ime, String prezime, String email, String sifra, String salt, boolean admin) {
        this.id = id;
        this.ime = ime;
        this.prezime = prezime;
        this.email = email;
        this.sifra = sifra;
        this.salt = salt;
        this.admin = admin;
    }

    public Zaposleni(String ime, String prezime, String email, String sifra, String salt) {
        this.ime = ime;
        this.prezime = prezime;
        this.email = email;
        this.sifra = sifra;
        this.salt = salt;
        this.admin = false;
    }

    public Zaposleni(String ime, String prezime, String email, String sifra) {
        this.ime = ime;
        this.prezime = prezime;
        this.email = email;
        this.sifra = sifra;
        this.admin = false;
    }

    public Zaposleni(String ime, String prezime, String email, String sifra, String salt, boolean admin) {
        this.ime = ime;
        this.prezime = prezime;
        this.email = email;
        this.sifra = sifra;
        this.salt = salt;
        this.admin = admin;
    }

    public Zaposleni(String ime, String prezime, String email, String sifra, boolean admin) {
        this.ime = ime;
        this.prezime = prezime;
        this.email = email;
        this.sifra = sifra;
        this.admin = admin;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int idZaposleni) {
        this.id = idZaposleni;
    }

    public Boolean isAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
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

    public String getSifra() {
        return sifra;
    }

    public void setSifra(String sifra) {
        this.sifra = sifra;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

}
