package stamboom.domain;

import java.util.*;
import javafx.collections.*;

public class Administratie implements java.io.Serializable {

    //************************datavelden*************************************
    private int nextGezinsNr;
    private int nextPersNr;
    private final List<Persoon> personen;
    private final List<Gezin> gezinnen;
    private final List<String> geslachten;
    
    private transient ObservableList<Persoon> observablePersonen;
    private transient ObservableList<Gezin> observableGezinnen;
    private transient ObservableList<String> observableGeslachten;

    //***********************constructoren***********************************
    /**
     * er wordt een administratie gecreeerd met 0 personen en dus 0 gezinnen
     * personen en gezinnen die in de toekomst zullen worden gecreeerd, worden
     * elk opvolgend genummerd vanaf 1
     */
    public Administratie() {
        //todo opgave 1
        personen = new ArrayList<>();
        gezinnen = new ArrayList<>();     
        geslachten = new ArrayList<>();
        
        for (Geslacht g : Geslacht.values())
        {
            geslachten.add(g.toString());
        }
        
        observablePersonen = FXCollections.observableList(personen);
        observableGezinnen = FXCollections.observableList(gezinnen);
        observableGeslachten = FXCollections.observableList(geslachten);
        
        nextGezinsNr = 1;
        nextPersNr = 1;
    }
    
    public ObservableList<Persoon> getObservablePersonen()
    {
        return this.observablePersonen;
    }
    
    public ObservableList<Gezin> getObservableGezinnen()
    {
        return this.observableGezinnen;
    }
    
    public ObservableList<String> getObservableGeslachten()
    {
        return this.observableGeslachten;
    }
    
    public void UpdateObservableLists()
    {
        observablePersonen = FXCollections.observableList(personen);
        observableGezinnen = FXCollections.observableList(gezinnen);
        observableGeslachten = FXCollections.observableList(geslachten);
    }

    //**********************methoden****************************************
    /**
     * er wordt een persoon met een gegeven geslacht, met als voornamen vnamen,
     * achternaam anaam, tussenvoegsel tvoegsel, geboortedatum gebdat,
     * geboorteplaats gebplaats en een gegeven ouderlijk gezin gecreeerd; de persoon
     * krijgt een uniek nummer toegewezen de persoon is voortaan ook bij het
     * ouderlijk gezin bekend. Voor de voornamen, achternaam en gebplaats geldt
     * dat de eerste letter naar een hoofdletter en de resterende letters naar
     * een kleine letter zijn geconverteerd; het tussenvoegsel is zo nodig in
     * zijn geheel geconverteerd naar kleine letters; overbodige spaties zijn 
     * verwijderd
     *
     * @param geslacht
     * @param vnamen vnamen.length>0; alle strings zijn niet leeg
     * @param anaam niet leeg
     * @param tvoegsel
     * @param gebdat
     * @param gebplaats niet leeg
     * @param ouderlijkGezin mag de waarde null (=onbekend) hebben
     *
     * @return als de persoon al bekend was (op basis van combinatie van getNaam(),
     * geboorteplaats en geboortedatum), wordt er null geretourneerd, anders de 
     * nieuwe persoon
     */
    public Persoon addPersoon(Geslacht geslacht, String[] vnamen, String anaam,
            String tvoegsel, Calendar gebdat,
            String gebplaats, Gezin ouderlijkGezin) {

        if (vnamen.length == 0) {
            throw new IllegalArgumentException("ten minst 1 voornaam");
        }
        for (String voornaam : vnamen) {
            if (voornaam.trim().isEmpty()) {
                throw new IllegalArgumentException("lege voornaam is niet toegestaan");
            }
        }

        if (anaam.trim().isEmpty()) {
            throw new IllegalArgumentException("lege achternaam is niet toegestaan");
        }

        if (gebplaats.trim().isEmpty()) {
            throw new IllegalArgumentException("lege geboorteplaats is niet toegestaan");
        }

        //todo opgave 1
        for (String voornaam : vnamen)
        {
            voornaam = voornaam.trim();
            
            if (voornaam.length() > 1)
            {
                voornaam = voornaam.substring(0, 1).toUpperCase() + voornaam.substring(1).toLowerCase();
            }
            else
            {
                voornaam = voornaam.substring(0, 1).toUpperCase();
            }
        }
        
        anaam = anaam.trim();
        gebplaats = gebplaats.trim();
            
        if (anaam.length() > 1)
        {
            anaam = anaam.substring(0, 1).toUpperCase() + anaam.substring(1).toLowerCase();
        }
        else
        {
            anaam = anaam.substring(0, 1).toUpperCase();
        }
        
        if (gebplaats.length() > 1)
        {
            gebplaats = gebplaats.substring(0, 1).toUpperCase() + gebplaats.substring(1).toLowerCase();
        }
        else
        {
           gebplaats = gebplaats.substring(0, 1).toUpperCase();
        }
        
        if(tvoegsel != null)
            tvoegsel = tvoegsel.toLowerCase();
        
        Persoon persoon = new Persoon(nextPersNr, vnamen, anaam, tvoegsel, gebdat, gebplaats, geslacht, ouderlijkGezin);
        
        boolean persoonBestaatAl = false;
        if (personen.size() > 0)
        {
            for (Persoon p : personen)
            {
                if (p.getGebDat().equals(persoon.getGebDat()) && p.getGebPlaats().equals(persoon.getGebPlaats()) && p.getNaam().equals(persoon.getNaam()))
                {
                    persoonBestaatAl = true;
                }
            }
        }
        
            
        if (persoonBestaatAl == false)
        {
            personen.add(persoon);
            nextPersNr++;
            return persoon;
        }

        return null;
    }

    /**
     * er wordt, zo mogelijk (zie return) een (kinderloos) ongehuwd gezin met
     * ouder1 en ouder2 als ouders gecreeerd; de huwelijks- en scheidingsdatum
     * zijn onbekend (null); het gezin krijgt een uniek nummer toegewezen; dit
     * gezin wordt ook bij de afzonderlijke ouders geregistreerd;
     *
     * @param ouder1
     * @param ouder2 mag null zijn
     *
     * @return null als ouder1 = ouder2 of als de volgende voorwaarden worden
     * overtreden: 1) een van de ouders is op dit moment getrouwd 2) het koppel
     * uit een ongehuwd gezin kan niet tegelijkertijd als koppel bij een ander
     * ongehuwd gezin betrokken zijn anders het gewenste gezin
     */
    public Gezin addOngehuwdGezin(Persoon ouder1, Persoon ouder2) {
        if (ouder1 == ouder2) {
            return null;
        }

        Calendar nu = Calendar.getInstance();
        if (ouder1.isGetrouwdOp(nu) || (ouder2 != null
                && ouder2.isGetrouwdOp(nu))
                || ongehuwdGezinBestaat(ouder1, ouder2)) {
            return null;
        }

        Gezin gezin = new Gezin(nextGezinsNr, ouder1, ouder2);
        nextGezinsNr++;
        gezinnen.add(gezin);

        ouder1.wordtOuderIn(gezin);
        if (ouder2 != null) {
            ouder2.wordtOuderIn(gezin);
        }

        return gezin;
    }

    /**
     * Als het ouderlijk gezin van persoon nog onbekend is dan wordt persoon een
     * kind van ouderlijkGezin en tevens wordt persoon als kind in dat gezin
     * geregistreerd; <br>
     * Als de ouders bij aanroep al bekend zijn, verandert er
     * niets
     *
     * @param persoon
     * @param ouderlijkGezin
     */
    public void setOuders(Persoon persoon, Gezin ouderlijkGezin) {
        persoon.setOuders(ouderlijkGezin);
    }

    /**
     * als de ouders van dit gezin gehuwd zijn en nog niet gescheiden en datum
     * na de huwelijksdatum ligt, wordt dit de scheidingsdatum. Anders gebeurt
     * er niets.
     *
     * @param gezin
     * @param datum
     * @return true als scheiding geaccepteerd, anders false
     */
    public boolean setScheiding(Gezin gezin, Calendar datum) {
        return gezin.setScheiding(datum);
    }

    /**
     * registreert het huwelijk, mits gezin nog geen huwelijk is en beide ouders
     * op deze datum mogen trouwen (pas op: ook de toekomst kan hierbij een rol
     * spelen omdat toekomstige gezinnen eerder zijn geregisteerd)
     *
     * @param gezin
     * @param datum de huwelijksdatum
     * @return false als huwelijk niet mocht worden voltrokken, anders true
     */
    public boolean setHuwelijk(Gezin gezin, Calendar datum) {
        return gezin.setHuwelijk(datum);
    }

    /**
     *
     * @param ouder1
     * @param ouder2
     * @return true als dit koppel (ouder1,ouder2) al een ongehuwd gezin vormt
     */
    boolean ongehuwdGezinBestaat(Persoon ouder1, Persoon ouder2) {
        return ouder1.heeftOngehuwdGezinMet(ouder2) != null;
    }

    /**
     * als er al een ongehuwd gezin voor dit koppel bestaat, wordt het huwelijk
     * voltrokken, anders wordt er zo mogelijk (zie return) een (kinderloos)
     * gehuwd gezin met ouder1 en ouder2 als ouders gecreeerd; de
     * scheidingsdatum is onbekend (null); het gezin krijgt een uniek nummer
     * toegewezen; dit gezin wordt ook bij de afzonderlijke ouders
     * geregistreerd;
     *
     * @param ouder1
     * @param ouder2
     * @param huwdatum
     * @return null als ouder1 = ouder2 of als een van de ouders getrouwd is
     * anders het gehuwde gezin
     */
    public Gezin addHuwelijk(Persoon ouder1, Persoon ouder2, Calendar huwdatum) {
        //todo opgave 1     
        boolean isgetrouwd = false;
        
        if (ouder1 != ouder2)
        {        
            for (Gezin g : ouder1.getAlsOuderBetrokkenIn())
            {
                if (g.isHuwelijkOp(huwdatum) || huwdatum.before(g.getHuwelijksdatum()) == true)
                {
                    isgetrouwd = true;
                }
            }
            
            for (Gezin g : ouder2.getAlsOuderBetrokkenIn())
            {
                if (g.isHuwelijkOp(huwdatum))
                {
                    isgetrouwd = true;
                }
            }
            
            if (isgetrouwd == true)
            {
                return null;
            }
            
            for (Gezin g : ouder1.getAlsOuderBetrokkenIn())
            { 
                for (Gezin g2 : ouder2.getAlsOuderBetrokkenIn())
                {                 
                    if (g == g2)
                    {
                        g.setHuwelijk(huwdatum);
                        return g;
                    }
                }
            }
            
            Gezin g = new Gezin(nextGezinsNr, ouder1, ouder2);
            g.setHuwelijk(huwdatum);
            ouder1.wordtOuderIn(g);
            ouder2.wordtOuderIn(g);
            gezinnen.add(g);
            nextGezinsNr++;
            return g;
        }
        
        
        return null;
    }

    /**
     *
     * @return het aantal geregistreerde personen
     */
    public int aantalGeregistreerdePersonen() {
        return personen.size();
    }

    /**
     *
     * @return het aantal geregistreerde gezinnen
     */
    public int aantalGeregistreerdeGezinnen() {
        return gezinnen.size();
    }

    /**
     *
     * @param nr
     * @return de persoon met nummer nr, als die niet bekend is wordt er null
     * geretourneerd
     */
    public Persoon getPersoon(int nr) {
        //todo opgave 1
        //aanname: er worden geen personen verwijderd
        
        for (Persoon p : personen)
        {
            if (p.getNr() == nr)
            {
                return p;
            }
        }
        
        return null;
    }

    /**
     * @param achternaam
     * @return alle personen met een achternaam gelijk aan de meegegeven
     * achternaam (ongeacht hoofd- en kleine letters)
     */
    public ArrayList<Persoon> getPersonenMetAchternaam(String achternaam) {
        //todo opgave 1
        ArrayList<Persoon> DezelfdeAchternaam = new ArrayList();
        
        for (Persoon p : personen)
        {
            if (p.getAchternaam().toLowerCase().equals(achternaam.toLowerCase()))
            {
                DezelfdeAchternaam.add(p);
            }
        }
        
        return DezelfdeAchternaam;
       
    }

    /**
     *
     * @return de geregistreerde personen
     */
    public List<Persoon> getPersonen() {
        // todo opgave 1
        
        List<Persoon> newlist = new ArrayList<>();
        
        for (Persoon p : personen)
        {
            newlist.add(p);
        }
        
        return newlist;
    }

    /**
     *
     * @param vnamen
     * @param anaam
     * @param tvoegsel
     * @param gebdat
     * @param gebplaats
     * @return de persoon met dezelfde initialen, tussenvoegsel, achternaam,
     * geboortedatum en -plaats mits bekend (ongeacht hoofd- en kleine letters),
     * anders null
     */
    public Persoon getPersoon(String[] vnamen, String anaam, String tvoegsel,
            Calendar gebdat, String gebplaats) {
        //todo opgave 1
        Persoon persoon = new Persoon(-1, vnamen, anaam, tvoegsel, gebdat, gebplaats, Geslacht.MAN, null);
        
        
        
        for (Persoon p : personen)
        {         
            if (p.getNaam().equals(persoon.getNaam()))
            {
                if (p.getAchternaam().toLowerCase().equals(anaam.toLowerCase()))
                {
                    if (p.getTussenvoegsel().toLowerCase().equals(tvoegsel.toLowerCase()))
                    {
                        if (p.getGebDat().compareTo(persoon.getGebDat()) == 0)
                        {
                            if (p.getGebPlaats().toLowerCase().equals(gebplaats.toLowerCase()))
                            {
                                return p;
                            }
                        }
                    }
                }
            }
        }
        
        return null;
    }

    /**
     *
     * @return de geregistreerde gezinnen
     */
    public List<Gezin> getGezinnen() {
        return gezinnen;
    }

    /**
     *
     * @param gezinsNr
     * @return het gezin met nummer nr. Als dat niet bekend is wordt er null
     * geretourneerd
     */
    public Gezin getGezin(int gezinsNr) {
        // aanname: er worden geen gezinnen verwijderd
        if (1 <= gezinsNr && 1 <= gezinnen.size()) {
            return gezinnen.get(gezinsNr - 1);
        }
        return null;
    }
}
