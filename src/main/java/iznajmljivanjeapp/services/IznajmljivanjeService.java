package iznajmljivanjeapp.services;

import framework.injector.OpstiServis;
import framework.model.FieldDescriptor;
import framework.model.KriterijumDescriptor;
import framework.model.KriterijumWrapper;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;
import iznajmljivanjeapp.exceptions.SistemskaOperacijaException;
import iznajmljivanjeapp.domain.*;
import iznajmljivanjeapp.repositories.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class IznajmljivanjeService extends OpstiServis {

    private final Repository<Iznajmljivanje> iznajmljivanjeRepository;
    private final Repository<StavkaIznajmljivanja> stavkaIznajmljivanjaRepository;

    private static long daysBetween(Date start, Date end) {
        long diffMillis = end.getTime() - start.getTime();
        return TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS) + 1;
    }

    public IznajmljivanjeService(Repository<Iznajmljivanje> iznajmljivanjeRepository, Repository<StavkaIznajmljivanja> stavkaIznajmljivanjaRepository) {
        this.iznajmljivanjeRepository = iznajmljivanjeRepository;
        this.stavkaIznajmljivanjaRepository = stavkaIznajmljivanjaRepository;
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    public void kreirajIznajmljivanje(Iznajmljivanje iznajmljivanje) throws Exception {
        final Map<String, Character> klasaDozvolaMapa = Map.of("Automobil", 'B', "Motor", 'A', "Minibus", 'D');

        if (iznajmljivanje.getStavke() == null || iznajmljivanje.getStavke().isEmpty()) {
            throw new SistemskaOperacijaException("Iznajmljivanje mora da ima barem jednu stavku.");
        }
        boolean test;
        try {
            test = iznajmljivanje.getUkupanIznos() != iznajmljivanje.getStavke().stream().mapToDouble(
                    (s) -> s.getVozilo().getCenaPoDanu() * daysBetween(s.getDatumPocetka(),s.getDatumZavrsetka())).sum();
        } catch (Exception e) {
            throw new Exception("Iznajmljivanje ili njegove stavke nisu potpuni");
        }

        if (test) {
            throw new SistemskaOperacijaException("Iznos iznajmljivanja i njegovih stavki se ne poklapa");
        }

        boolean uspeh = true;
        List<StavkaIznajmljivanja> noveStavke = new ArrayList<>();
        try {
            pocniTransakciju();
            iznajmljivanjeRepository.kreiraj(iznajmljivanje);
            for (var si : iznajmljivanje.getStavke()) {
                if (si.getDatumPocetka() == null || si.getDatumZavrsetka() == null || si.getVozilo() == null || si.getVozilo().getKlasa() == null
                        || iznajmljivanje.getVozac() == null || iznajmljivanje.getVozac().getDozvola() == null
                        || iznajmljivanje.getVozac().getDozvola().getKategorija() == null) {
                    uspeh = false;
                    throw new Exception("Neispravan domenski objekat.");
                }
                if (!(klasaDozvolaMapa.get(si.getVozilo().getKlasa()).equals(iznajmljivanje.getVozac().getDozvola().getKategorija()))) {
                    uspeh = false;
                    throw new SistemskaOperacijaException("Vozač mora imati odgovarajuću dozvolu za vozila u stavci. ( potrebna dozvola za  " + si.getVozilo().getKlasa() + ": " + klasaDozvolaMapa.get(si.getVozilo().getKlasa()) + ", vozaceva dozvola: " + iznajmljivanje.getVozac().getDozvola().getKategorija() + ")");
                }
                Vozilo v = si.getVozilo();

                //moramo da proverimo da li dato vozilo vec ima iznajmljivanja u periodu u kom zelimo da ga iznajmimo u novoj stavci
                //takodje moramo ovoj listi da dodamo stavke koje sada kreiramo za slucaj da u jednom iznajmljivanju neko pokusa da preklopi datume za isto vozilo
                List<StavkaIznajmljivanja> postojeceStavkeZaVozilo = v.getStavke();
                if (postojeceStavkeZaVozilo == null || (postojeceStavkeZaVozilo.size() == 1 && postojeceStavkeZaVozilo.get(0).getDatumPocetka() == null)) {
                    postojeceStavkeZaVozilo = new ArrayList<>();
                }
                for (var ns : noveStavke) {
                    if (ns.getVozilo() != null && ns.getVozilo().getId().equals(v.getId())) {
                        postojeceStavkeZaVozilo.add(ns);
                    }
                }
                for (var st : postojeceStavkeZaVozilo) {
                    //za dva segmenta [a,b] i [c,d] postoji podsegment koji dele ako i samo ako a <= d && c <= b
                    if (!st.getDatumPocetka().after(si.getDatumZavrsetka()) && !si.getDatumPocetka().after(st.getDatumZavrsetka())) {
                        SimpleLogger.log(LogLevel.LOG_ERROR, "Stavka broj " + si.getRb() + " sadrzi vozilo koje je vec iznajmljeno u datom periodu: Pokusaj iznajmljivanja " + v.getProizvodjac() + " " + v.getImeModela() + "(id=" + v.getId() + ") od " + si.getDatumPocetka() + " do " + si.getDatumZavrsetka() + " a ono se vec koristi u tom periodu: od " + st.getDatumPocetka() + " do " + st.getDatumZavrsetka());
                        uspeh = false;
                        throw new SistemskaOperacijaException("Stavka broj " + si.getRb() + " sadrži vozilo koje je već iznajmljeno u datom periodu:\nPokušaj iznajmljivanja " + v.getProizvodjac() + " " + v.getImeModela() + "(id=" + v.getId() + ") od " + formatDate(si.getDatumPocetka()) + " do " + formatDate(si.getDatumZavrsetka()) + " a ono se već koristi u tom periodu: od " + formatDate(st.getDatumPocetka()) + " do " + formatDate(st.getDatumZavrsetka()));
                    }
                }
                si.setIznajmljivanje(new Iznajmljivanje(iznajmljivanje.getId()));
                noveStavke.add(si);
            }
            stavkaIznajmljivanjaRepository.kreiraj(iznajmljivanje.getStavke().toArray(new StavkaIznajmljivanja[0]));
            SimpleLogger.log(LogLevel.LOG_INFO, "Uspesno kreirano iznajmljivane zajedno sa svim svojim stavkama");
        } catch (Exception e) {
            //koristimo try catch blok u servisu samo zbog finally statementa za korektno postupanje sa transakcijama
            //ponovo throwujemo kako bi ulogu upravljanja izuzecima dali kontroleru
            uspeh = false;
            throw e;
        } finally {
            if (uspeh) {
                commitTransakcija();
            } else {
                rollbackTransakcija();
            }
        }
    }

    public void obrisiIznajmljivanje(Iznajmljivanje iznajmljivanje) throws Exception {
        iznajmljivanjeRepository.obrisi(iznajmljivanje);
    }

    public void promeniIznajmljivanje(Iznajmljivanje iznajmljivanje) throws Exception {
        try {
            pocniTransakciju();
            List<Iznajmljivanje> is = iznajmljivanjeRepository.vratiListuSvi(new KriterijumWrapper(
                    List.of(new KriterijumDescriptor(Iznajmljivanje.class, "id", "=", iznajmljivanje.getId())),
                    KriterijumWrapper.DepthLevel.FULL,
                    List.of(new FieldDescriptor(Vozilo.class.getName(), "stavke"), new FieldDescriptor(Zaposleni.class.getName(), "smene")),
                    List.of(Smena.class.getName(), TerminDezurstva.class.getName(), Dozvola.class.getName())));
            if (is == null || is.isEmpty()) {
                throw new Exception("Greska pri trazenju iznajmljivanja");
            }

            var i = is.get(0);
            stavkaIznajmljivanjaRepository.obrisi(i.getStavke().toArray(new StavkaIznajmljivanja[0]));
            iznajmljivanjeRepository.promeni(iznajmljivanje);
            stavkaIznajmljivanjaRepository.kreiraj(iznajmljivanje.getStavke().toArray(new StavkaIznajmljivanja[0]));
            commitTransakcija();
        } catch (Exception e) {
            rollbackTransakcija();
            throw e;
        }
    }

    public void promeniIznajmljivanje(Iznajmljivanje i1, Iznajmljivanje i2) throws Exception {
        iznajmljivanjeRepository.promeni(i1, new Iznajmljivanje[]{i2});
    }

    public List<Iznajmljivanje> vratiListuSviIznajmljivanje(KriterijumWrapper w) throws Exception {
        return iznajmljivanjeRepository.vratiListuSvi(w);
    }

}
