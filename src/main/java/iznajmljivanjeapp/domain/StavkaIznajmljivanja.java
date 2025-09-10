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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import framework.orm.anotacije.kljuc.ManyToOne;
import framework.orm.anotacije.kljuc.SlozenKljuc;
import iznajmljivanjeapp.domain.kljucevi.StavkaIznajmljivanjaKljuc;

@SlozenKljuc(wrapper = StavkaIznajmljivanjaKljuc.class)
public class StavkaIznajmljivanja extends Entitet {

    @NePrikazuj
    @PrimarniKljuc
    @ManyToOne(joinColumn = "idIznajmljivanje")
    private Iznajmljivanje iznajmljivanje;

    @NePrikazuj
    @PrimarniKljuc
    private Integer rb;

    @NotNull
    private Date datumPocetka;

    @NotNull
    private Date datumZavrsetka;
    
    @ManyToOne(joinColumn = "idVozilo", poljaZaPrikazivanje = {"proizvodjac","imeModela:Model","cenaPoDanu:Cena po danu"})
    @NotNull
    private Vozilo vozilo;  // sk ka Vozilo

    public static final int MIN_DANA = 3;
    public static final int MAX_DANA = 60;

    @Override
    public String toString() {
        return "StavkaIznajmljivanja{" + "iznajmljivanje=" + (iznajmljivanje != null ? iznajmljivanje.getId() : null) + ", rb=" + rb
                + ", datumPocetka=" + (datumPocetka != null ? new SimpleDateFormat("yyyy-MM-dd").format(datumPocetka) : null)
                + ", datumZavrsetka=" + (datumZavrsetka != null ? new SimpleDateFormat("yyyy-MM-dd").format(datumZavrsetka) : null)
                + ", vozilo=" + vozilo + '}';
    }

    public StavkaIznajmljivanja() {
    }

    public StavkaIznajmljivanja(Iznajmljivanje iznajmljivanje, int rb, Date datumPocetka, Date datumZavrsetka, Vozilo vozilo) {
        this.iznajmljivanje = iznajmljivanje;
        this.rb = rb;
        this.datumPocetka = datumPocetka;
        this.datumZavrsetka = datumZavrsetka;
        this.vozilo = vozilo;
    }

    public StavkaIznajmljivanja(Iznajmljivanje iznajmljivanje, int rb) {
        this.iznajmljivanje = iznajmljivanje;
        this.rb = rb;
    }

    public StavkaIznajmljivanja(Iznajmljivanje iznajmljivanje, Date datumPocetka, Date datumZavrsetka, Vozilo vozilo) {
        this.iznajmljivanje = iznajmljivanje;
        this.datumPocetka = datumPocetka;
        this.datumZavrsetka = datumZavrsetka;
        this.vozilo = vozilo;
    }

    public StavkaIznajmljivanja(Date datumPocetka, Date datumZavrsetka, Vozilo vozilo) {
        this.datumPocetka = datumPocetka;
        this.datumZavrsetka = datumZavrsetka;
        this.vozilo = vozilo;
    }

    public Iznajmljivanje getIznajmljivanje() {
        return iznajmljivanje;
    }

    public void setIznajmljivanje(Iznajmljivanje iznajmljivanje) {
        this.iznajmljivanje = iznajmljivanje;
    }

    public Integer getRb() {
        return rb;
    }

    public void setRb(int rb) {
        this.rb = rb;
    }

    public Date getDatumPocetka() {
        return datumPocetka;
    }

    public void setDatumPocetka(Date datumPocetka) {
        this.datumPocetka = datumPocetka;
    }

    public Date getDatumZavrsetka() {
        return datumZavrsetka;
    }

    public void setDatumZavrsetka(Date datumZavrsetka) {
        this.datumZavrsetka = datumZavrsetka;
    }

    public Vozilo getVozilo() {
        return vozilo;
    }

    public void setVozilo(Vozilo vozilo) {
        this.vozilo = vozilo;
    }

    @Override
    public boolean vrednosnaOgranicenja() {

//        if (!super.vrednosnaOgranicenja()) {
//            return false;
//        }
//
//        LocalDate localDate1 = datumPocetka.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
//        LocalDate localDate2 = datumZavrsetka.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
//
//        long brojDana = ChronoUnit.DAYS.between(localDate1, localDate2);
//
//        return brojDana >= MIN_DANA && brojDana <= MAX_DANA;


        long millisDiff = Math.abs(datumZavrsetka.getTime() - datumPocetka.getTime());
        long brojDana = TimeUnit.MILLISECONDS.toDays(millisDiff) + 1;
        return brojDana >= MIN_DANA && brojDana <= MAX_DANA;
    }
}
