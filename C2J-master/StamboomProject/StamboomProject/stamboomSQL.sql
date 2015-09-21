DROP SCHEMA IF EXISTS stamboom;
CREATE SCHEMA stamboom;

DROP TABLE IF EXISTS personen;
DROP TABLE IF EXISTS gezinnen;

CREATE TABLE personen (
	persoonsNummer int,
    achternaam varchar(100),
    voornamen varchar(100),
    tussenvoegsel varchar(100),
    geboortedatum varchar(100),
    geboorteplaats varchar(100),
    geslacht varchar(10),
    ouders int
);

CREATE TABLE gezinnen (
	gezinsNummer int,
    ouder1 int,
    ouder2 int,
    huwelijksdatum varchar(100),
    scheidingsdatum varchar(100)
);

ALTER TABLE personen ADD PRIMARY KEY (persoonsNummer);
ALTER TABLE gezinnen ADD PRIMARY KEY (gezinsNummer);

ALTER TABLE personen ADD CONSTRAINT c1 FOREIGN KEY (ouders) REFERENCES gezinnen(gezinsNummer) ON DELETE CASCADE;
ALTER TABLE gezinnen ADD CONSTRAINT c2 FOREIGN KEY (ouder1) REFERENCES personen(persoonsNummer) ON DELETE CASCADE;
ALTER TABLE gezinnen ADD CONSTRAINT c3 FOREIGN KEY (ouder2) REFERENCES personen(persoonsNummer) ON DELETE CASCADE;

