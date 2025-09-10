#!/usr/bin/env python3
"""
populate_db.py

Populates the 'projektovanjesoftvera_seminarski' MySQL database with
convincing random data according to your business rules.

Usage:
    pip install mysql-connector-python
    python populate_db.py
"""

import mysql.connector
import random
import string
import base64
import os
from datetime import date, timedelta
import hashlib

# ——— KONFIGURACIJA ————————————————————————————————
DB_KONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': '',
    'port': 3306,
    'database': 'projektovanjesoftvera_seminarski_fix'
}

# Povećavamo sve za najmanje red veličine
BROJ_VOZILA               = 2000
BROJ_ZAPOSLENIH           = 200
BROJ_VOZAČA               = 200
BROJ_IZNAJMLJIVANJA       = 2000
MAKS_STAVKI_PO_IZNAJMLJIVANJU = 5

# ——— POMOĆNE FUNKCIJE —————————————————————————————
def slucajni_email(ime: str, prezime: str) -> str:
    """Generiše realističan e‑mail na osnovu imena."""
    domen = random.choice(['gmail.com', 'yahoo.com', 'outlook.com', 'hotbox.rs'])
    user = f"{ime.lower()}.{prezime.lower()}{random.randint(1,999)}"
    return user + '@' + domen

def hesiraj_lozinku(lozinka: str, salt: bytes) -> str:
    """PBKDF2‐HMAC‐SHA256 (65 536 iteracija, 256‑bitni ključ), vraća Base64."""
    dk = hashlib.pbkdf2_hmac('sha256', lozinka.encode('utf-8'), salt, 65536, dklen=32)
    return base64.b64encode(dk).decode('utf-8')

# Nizovi za realistična imena
IMENA_ZAPOSLENIH = ['Marko','Jovana','Nikola','Ana','Milos','Marija','Stefan','Ivana','Petar','Sara']
PREZIMENA_ZAPOSLENIH = ['Jankovic','Petrovic','Nikolic','Markovic','Tanaskovic','Stojanovic','Kovacevic','Lukic']

IMENA_VOZAČA = ['Aleksandar','Milica','Vladimir','Tamara','Filip','Tanja','Bojan','Kristina']
PREZIMENA_VOZAČA = ['Popovic','Jovanovic','Matic','Mladenovic','Ristic','Knezevic','Djordjevic']

PROIZVOĐAČI = ["Mercedes", "Fiat", "Volkswagen", "Dacia", "Audi", "Škoda"]
KLASE      = ["Automobil", "Minibus", "Motor"]
KATEGORIJE = [0,1,2]  # 0=budget,1=midrange,2=luxury

MODEL_PO_PROIZVODJAČU = {
    "Mercedes": ["A-Klasa","C-Klasa","E-Klasa","GLE","GLA"],
    "Fiat":     ["500","Panda","Tipo","Doblo"],
    "Volkswagen": ["Golf","Passat","Tiguan","Polo"],
    "Dacia":    ["Sandero","Duster","Logan"],
    "Audi":     ["A3","A4","Q5","TT"],
    "Škoda":    ["Octavia","Fabia","Superb","Kodiaq"]
}

def main():
    cnx    = mysql.connector.connect(**DB_KONFIG)
    cursor = cnx.cursor()

    # 1) dozvola: samo A, B, D
    cursor.executemany(
        "INSERT INTO dozvola (kategorija) VALUES (%s)",
        [('A',), ('B',), ('D',)]
    )

    # 2) termindezurstva: tipTermina 0,1,2; napomena prazna
    cursor.executemany(
        "INSERT INTO termindezurstva (tipTermina, napomena) VALUES (%s, %s)",
        [(0, ''), (1, ''), (2, '')]
    )

    # 3) vozilo: slučajan, realističan model, no Dacia+Motor
    lista_vozila = {}
    for _ in range(BROJ_VOZILA):
        while True:
            proiz = random.choice(PROIZVOĐAČI)
            kl   = random.choice(KLASE)
            if not (proiz == "Dacia" and kl == "Motor"):
                break
        model = random.choice(MODEL_PO_PROIZVODJAČU[proiz])
        cena  = round(random.uniform(7_000, 150_000), 2)
        godište = random.randint(2008, 2025)
        kat   = random.choice(KATEGORIJE)
        cenaPoDanu = round(random.uniform(15, 250),2)
        cursor.execute(
            """
            INSERT INTO vozilo
              (klasa, proizvodjac, kupovnaCena, godiste, imeModela, kategorija, cenaPoDanu)
            VALUES (%s,%s,%s,%s,%s,%s,%s)
            """,
            (kl, proiz, cena, godište, model, kat, cenaPoDanu)
        )
        vid = cursor.lastrowid
        lista_vozila[vid] = (kl,cenaPoDanu)
        #lista_vozila.append((vid, kl))

    # 4) zaposleni: realistična imena, jedinstveni email, lozinka
    lista_zaposlenih = []
    for i in range(BROJ_ZAPOSLENIH):
        ime     = "Admin" if i == 0 else random.choice(IMENA_ZAPOSLENIH)
        prezime = "Admin" if i == 0 else random.choice(PREZIMENA_ZAPOSLENIH)
        email   = "admin@gmail.com" if i == 0 else slucajni_email(ime, prezime)
        salt    = os.urandom(16)
        lozinka = "admin123" if i==0 else "password123"
        hash_   = hesiraj_lozinku(lozinka, salt)
        admin   = 1 if i==0 else 0

        cursor.execute(
            """
            INSERT INTO zaposleni
              (ime, prezime, email, sifra, salt, admin)
            VALUES (%s,%s,%s,%s,%s,%s)
            """,
            (ime, prezime, email, hash_, base64.b64encode(salt).decode('utf-8'), admin)
        )
        lista_zaposlenih.append(cursor.lastrowid)

    # 5) vozači: realistična imena, jedinstveni email, random dozvola
    cursor.execute("SELECT id, kategorija FROM dozvola")
    sve_dozvole = cursor.fetchall()  # [(id, 'A'), ...]

    lista_vozača = []
    for _ in range(BROJ_VOZAČA):
        ime     = random.choice(IMENA_VOZAČA)
        prezime = random.choice(PREZIMENA_VOZAČA)
        email   = slucajni_email(ime, prezime)
        doz_id, kat = random.choice(sve_dozvole)

        cursor.execute(
            "INSERT INTO vozac (ime, prezime, email, idDozvola) VALUES (%s,%s,%s,%s)",
            (ime, prezime, email, doz_id)
        )
        lista_vozača.append((cursor.lastrowid, kat))

    # 6) mapiranje po dozvoli
    vozilo_po_dozvoli = {'A': [], 'B': [], 'D': []}
    for vid, tuple in lista_vozila.items():
        if tuple[0]=="Motor":      vozilo_po_dozvoli['A'].append(vid)
        elif tuple[0]=="Automobil":vozilo_po_dozvoli['B'].append(vid)
        elif tuple[0]=="Minibus":  vozilo_po_dozvoli['D'].append(vid)

    # 7) evidencija zauzeća vozila
    raspored_vozila = {vid: [] for vid in lista_vozila.keys()}

    # 8) kreiranje iznajmljivanja + više stavki
    for _ in range(BROJ_IZNAJMLJIVANJA):
        zaposleni_id = random.choice(lista_zaposlenih)
        vozač_id, vozač_kat = random.choice(lista_vozača)

        # datum sklapanja unutar poslednjih 18 meseci
        start_sklapanja = date.today() - timedelta(days=random.randint(0, 550))

        # broj stavki 1…M
        br_stavki = random.randint(1, MAKS_STAVKI_PO_IZNAJMLJIVANJU)
        ukupno_iznos = 0.0
        stavke = []
        v_id = -1
        for rb in range(1, br_stavki + 1):
            dozvoljena_lista = vozilo_po_dozvoli[vozač_kat]
            # izaberemo ne–preklapajuće vozilo
            while True:
                v_id = random.choice(dozvoljena_lista)
                # slučajan period 3–60 dana
                poc = start_sklapanja + timedelta(days=random.randint(0, 30))
                trajanje = random.randint(3, 60)
                kraj = poc + timedelta(days=trajanje - 1)
                # provera preklapanja
                if not any(not (kraj < s or poc > e) for (s,e) in raspored_vozila[v_id]):
                    raspored_vozila[v_id].append((poc, kraj))
                    break


            ukupno_iznos += lista_vozila[v_id][1] * ((kraj-poc).days + 1)
            stavke.append((rb, poc, kraj,v_id))

        # ubacimo iznajmljivanje sa akumuliranim iznosom
        cursor.execute(
            """
            INSERT INTO iznajmljivanje
              (datumSklapanja, ukupanIznos, idZaposleni, idVozac)
            VALUES (%s,%s,%s,%s)
            """,
            (start_sklapanja, round(ukupno_iznos,2), zaposleni_id, vozač_id)
        )
        iz_id = cursor.lastrowid

        # ubacimo sve stavke
        for rb, poc, kraj, v_id in stavke:
            cursor.execute(
                """
                INSERT INTO stavkaiznajmljivanja
                  (idIznajmljivanje, rb, datumPocetka, datumZavrsetka, idVozilo)
                VALUES (%s,%s,%s,%s,%s)
                """,
                (iz_id, rb, poc, kraj, v_id)
            )

    # 9) završetak
    cnx.commit()
    cursor.close()
    cnx.close()
    print("Baza uspešno popunjena!")

if __name__ == "__main__":
    main()
