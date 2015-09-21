package stamboom.domain;

import java.util.*;
import stamboom.util.StringUtilities;
import java.text.SimpleDateFormat;
import javafx.collections.*;

public class Gezin implements java.io.Serializable {

    // *********datavelden*************************************
    private final int nr;
    private final Persoon ouder1;
    private final Persoon ouder2;
    private final List<Persoon> kinderen;
    
    private transient ObservableList<Persoon> observableKinderen;
    
    /**
     * kan onbekend zijn (dan is het een ongehuwd gezin):
     */
    private Calendar huwelijksdatum;
    /**
     * kan onbekend zijn; als huwelijksdatum onbekend dan scheidingsdatum
     * onbekend; start en scheiding beide bekend dan is scheiding later dan
     * start van huwelijk
     */
    private Calendar scheidingsdatum;

    // *********constructoren***********************************
    /**
     * er wordt een (kinderloos) gezin met ouder1 en ouder2 als ouders
     * geregistreerd; de huwelijks-(en scheidings)datum zijn onbekend (null);
     * het gezin krijgt gezinsNr als nummer;
     *
     * @param ouder1 mag niet null zijn
     * @param ouder2 ongelijk aan ouder1
     */
    Gezin(int gezinsNr, Persoon ouder1, Persoon ouder2) {
        if (ouder1 == null) {
            throw new RuntimeException("Eerste ouder mag niet null zijn");
        }
        if (ouder1 == ouder2) {
            throw new RuntimeException("ouders hetzelfde");
        }
        this.nr = gezinsNr;
        this.ouder1 = ouder1;
        this.ouder2 = ouder2;
        this.kinderen = new ArrayList<>();
        this.huwelijksdatum = null;
        this.scheidingsdatum = null;
        
        this.observableKinderen = FXCollections.observableList(kinderen);
    }

    // ********methoden*****************************************
    /**
     * @return alle kinderen uit dit gezin
     */
    public List<Persoon> getKinderen() {
        return (List<Persoon>) Collections.unmodifiableList(kinderen);
    }
    
    public ObservableList<Persoon> getObservableKinderen()
    {
        return this.observableKinderen;
    }

    /**
     *
     * @return het aantal kinderen in dit gezin
     */
    public int aantalKinderen() {
        return kinderen.size();
    }

    /**
     *
     * @return het nummer van dit gezin
     */
    public int getNr() {
        return nr;
    }

    /**
     * @return de eerste ouder van dit gezin
     */
    public Persoon getOuder1() {
        return ouder1;
    }

    /**
     * @return de tweede ouder van dit gezin (kan null zijn)
     */
    public Persoon getOuder2() {
        return ouder2;
    }

    /**
     *
     * @return het nr, de naam van de eerste ouder, gevolgd door de naam van de
     * eventuele tweede ouder en als dit gezin getrouwd is, wordt ook de
     * huwelijksdatum erin opgenomen
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(this.nr).append(" ");
        s.append(ouder1.getNaam());
        if (ouder2 != null) {
            s.append(" met ");
            s.append(ouder2.getNaam());
        }
        if (heeftGetrouwdeOudersOp(Calendar.getInstance())) {
            s.append(" ").append(StringUtilities.datumString(huwelijksdatum));
        }
        return s.toString();
    }

    /**
     * @return de datum van het huwelijk (kan null zijn)
     */
    public Calendar getHuwelijksdatum() {
        return huwelijksdatum;
    }

    /**
     * @return de datum van scheiding (kan null zijn)
     */
    public Calendar getScheidingsdatum() {
        return scheidingsdatum;
    }

    /**
     * als de ouders gehuwd zijn en nog niet gescheiden, en de als parameter
     * gegeven datum na de huwelijksdatum ligt, wordt dit de scheidingsdatum.
     * Anders gebeurt er niets.
     *
     * @param datum
     * @return true als scheiding geaccepteerd, anders false
     */
    boolean setScheiding(Calendar datum) {
        
            String s = this.ouder1.getVoornamen().toLowerCase();
            String s2 = this.ouder1.getVoornamen().toLowerCase();
            if (s.contains("jacco") || s2.contains("jacco"))
            {
                
            }
        
        
        if (this.scheidingsdatum == null && huwelijksdatum != null
                && datum.after(huwelijksdatum)) {
            this.scheidingsdatum = datum;
            return true;
        } else {
            return false;
        }
    }

    /**
     * registreert het huwelijk, mits dit gezin nog geen huwelijk is en beide
     * ouders op deze datum mogen trouwen (pas op: ook de toekomst kan hierbij
     * een rol spelen omdat toekomstige gezinnen eerder zijn geregisteerd)
     *
     * @param datum de huwelijksdatum
     * @return false als huwelijk niet mocht worden voltrokken, anders true
     */
    boolean setHuwelijk(Calendar datum) {
        //todo opgave 1
        if(huwelijksdatum == null || scheidingsdatum != null)
        {
            if (scheidingsdatum != null)
            {
                if (datum.after(scheidingsdatum))
                {
                    huwelijksdatum = datum;
                    scheidingsdatum = null;
                    return true;
                }
                return false;
            }
            huwelijksdatum = datum;
            scheidingsdatum = null;
            return true;
        }
        return false;
    }

    /**
     * @return het nummer van de relatie, gevolgd door de namen van de ouder(s),
     * de eventueel bekende huwelijksdatum, gevolgd door (als er kinderen zijn)
     * de constante tekst '; kinderen:' gevolgd door de voornamen van de
     * kinderen uit deze relatie (per kind voorafgegaan door ' -')
     */
    public String beschrijving() {
        //todo opgave 1  
        String b = "";
        
        b += nr + " ";
        
        if(ouder1 != null)
        {
            b += ouder1.getNaam() + " met ";
        }
        
        if(ouder2 != null)
        {
            b += ouder2.getNaam() + " ";
        }
        
        if(huwelijksdatum != null)
        {
            //SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //String date_s = dateformatter.format(huwelijksdatum);
            
            String date_s = StringUtilities.datumString(huwelijksdatum);
            b += date_s;
        }
        
        
        if(kinderen.size() > 0)
        {        
            b += "; kinderen: ";
            
            for(Persoon k : kinderen)
            {
                b += "-" + k.getVoornamen() + " ";
            }
            
        }
        return b.trim();
    }

    void breidUitMet(Persoon kind) {
        if (!kinderen.contains(kind)) {
            kinderen.add(kind);
        }
    }

    /**
     *
     * @param datum
     * @return true als dit gezin op datum getrouwd en nog niet gescheiden is,
     * anders false
     */
    public boolean heeftGetrouwdeOudersOp(Calendar datum) {
        return isHuwelijkOp(datum)
                && (scheidingsdatum == null || scheidingsdatum.after(datum));
    }

    /**
     *
     * @param datum
     * @return true als dit gezin op datum een huwelijk is, anders false
     */
    public boolean isHuwelijkOp(Calendar datum) {
        //todo opgave 1
        if(huwelijksdatum != null)
        {
            if(datum.after(huwelijksdatum) == true || datum == huwelijksdatum)
            {
                if(scheidingsdatum != null)
                {
                    if(datum.before(scheidingsdatum) == true)
                    {
                        return true;
                    }
                }
                else
                {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     *
     * @return true als de ouders van dit gezin niet getrouwd zijn, anders false
     */
    public boolean isOngehuwd() {
        if (huwelijksdatum == null || scheidingsdatum != null)
        {
            return true;
        }
        return false;
    }

    /**
     *
     * @param datum
     * @return true als dit een gescheiden huwelijk is op datum, anders false
     */
    public boolean heeftGescheidenOudersOp(Calendar datum) {
        //todo opgave 1
        
        //if (scheidingsdatum != null)
        //{
        //    if (datum == scheidingsdatum)
        //    {
        //        return true;
        //    }
        // }
        
        //return false;
        
        
        if(scheidingsdatum != null)
        {
            if(datum.after(scheidingsdatum) == true || datum == scheidingsdatum)
            {
                return true;
            }
        }
        
        return false;
    }
}
