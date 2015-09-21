/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import stamboom.domain.Administratie;
import stamboom.domain.Geslacht;
import stamboom.domain.Gezin;
import stamboom.domain.Persoon;
import stamboom.util.StringUtilities;

public class DatabaseMediator implements IStorageMediator {

    private Properties props;
    private Connection conn;

    public DatabaseMediator(Properties props)
    {
        this.conn = null;
        this.props = props;
        this.configure(this.props);
    }
    
    
    @Override
    public Administratie load() throws IOException {
        //todo opgave 4
        Administratie loadAdmin = new Administratie();
        
        
        
        try
        {
            this.initConnection();
            //Get Personen
            Statement getPersonen = conn.createStatement();
            ResultSet rsPersonen = getPersonen.executeQuery("SELECT * FROM personen ORDER BY persoonsNummer");
        
            List<Persoon> personenMetOuders = new ArrayList();
            List<Integer> oudersNummers = new ArrayList();
            
            //Read personen
            while(rsPersonen.next())
            {         
                String achternaam = rsPersonen.getString("");
                
                List<String> voornamenList = new ArrayList();
                for (String voornaam : rsPersonen.getString("voornamen").trim().split(" "))
                {
                    if (voornaam.trim().length()> 0)
                    {
                        voornamenList.add(voornaam);
                    }
                }
                
                String[] voornamen = voornamenList.toArray(new String[voornamenList.size()]);
                
                String tussenvoegsel = rsPersonen.getString("tussenvoegsel");
                Calendar geboortedatum = StringUtilities.datum(rsPersonen.getString("geboortedatum"));
                String geboorteplaats = rsPersonen.getString("geboorteplaats");
                Geslacht geslacht = Geslacht.valueOf(rsPersonen.getString("geslacht"));
                
                Persoon p = loadAdmin.addPersoon(geslacht, voornamen, achternaam, tussenvoegsel, geboortedatum, geboorteplaats, null);
                
                //Add ouderlijkGezinNr to list, so it can be added to persoon later
                int ouderlijkGezinNr = rsPersonen.getInt("ouder");
                if (p != null)
                {
                    personenMetOuders.add(p);
                    oudersNummers.add(ouderlijkGezinNr);
                }
            }
            
            //Get Gezinnen
            Statement getGezinnen = conn.createStatement();
            ResultSet rsGezinnen = getGezinnen.executeQuery("SELECT * FROM gezinnen");
                   
            //Read personen
            while(rsGezinnen.next())
            {
                Persoon ouder1 = loadAdmin.getPersoon(rsGezinnen.getInt("ouder1"));
                
                Persoon ouder2 = null;
                
                if (rsGezinnen.getString("ouder2") != null)
                {
                    ouder2 = loadAdmin.getPersoon(rsGezinnen.getInt("ouder2"));
                }
                
                Calendar huwelijksdatum  = null;
                Calendar scheidingsdatum = null;
                
                String huwelijksdatumString = rsGezinnen.getString("huwelijksdatum");
                String scheidingsdatumString = rsGezinnen.getString("scheidingsdatum");
                
                if (huwelijksdatumString != null)
                {
                    huwelijksdatum = StringUtilities.datum(huwelijksdatumString);
                }
                
                if (scheidingsdatumString != null)
                {
                    scheidingsdatum = StringUtilities.datum(scheidingsdatumString);
                }
                
                Gezin g = null;
                
                g = loadAdmin.addOngehuwdGezin(ouder1, ouder2);
                
                if (g != null && huwelijksdatum != null)
                {
                    loadAdmin.setHuwelijk(g, huwelijksdatum);
                    
                    if (scheidingsdatum != null)
                    {
                        loadAdmin.setScheiding(g, scheidingsdatum);
                    }
                }
            }
            
            //Add ouderlijkGezin to Personen
            for (Persoon p : personenMetOuders)
            {
                if (p != null)
                {
                    Gezin ouderlijkGezin = loadAdmin.getGezin(oudersNummers.indexOf(p));
                    if (ouderlijkGezin != null)
                    {
                        loadAdmin.setOuders(p, ouderlijkGezin);
                    }
                }
            }
        }
        catch (SQLException ex)
        {
            System.out.println("Exception: " + ex.getMessage());
            throw new IOException();
        }
       
        return loadAdmin;
    }

    @Override
    public void save(Administratie admin) throws IOException {
        //todo opgave 4
        if (this.isCorrectlyConfigured() == false)
        {
            throw new IOException("Incorectly configured");
        }
        
        try
        {
            //Initialize
            this.initConnection();
            
            //Delete everything
            Statement deletePersonen = conn.createStatement();
            Statement deleteGezinnen = conn.createStatement();
            
            deletePersonen.execute("DELETE * FROM personen");
            deleteGezinnen.execute("DELETE * FROM gezinnen");
            
            //Insert
            Statement insertPersoon = conn.createStatement();
            String baseInsertPersoonQuery = "INSERT INTO personen (persoonsNummer, achternaam, voornamen, tussenvoegsel, geboortedatum, geboorteplaats, geslacht, ouders) VALUES (";
            String insertPersoonQuery = "";
            
            for(Persoon p : admin.getPersonen())
            {
                insertPersoonQuery = baseInsertPersoonQuery;
                insertPersoonQuery += p.getNr() + ", ";
                insertPersoonQuery += p.getAchternaam() + ", ";
                insertPersoonQuery += p.getVoornamen() + ", ";
                insertPersoonQuery += p.getTussenvoegsel() + ", ";
                insertPersoonQuery += p.getGebDat() + ", ";
                insertPersoonQuery += p.getGebPlaats() + ", ";
                insertPersoonQuery += p.getGeslacht() + ", ";
                
                if (p.getOuderlijkGezin() == null)
                {
                    insertPersoonQuery += "null";
                }
                else
                {
                    insertPersoonQuery += p.getOuderlijkGezin().getNr();
                }
                
                insertPersoonQuery += ")";
                
                insertPersoon.execute(insertPersoonQuery);
            }
            
            //Insert
            Statement insertGezin = conn.createStatement();
            String baseInsertGezinQuery = "INSERT INTO gezinnen (gezinsNummer, ouder1, ouder2, huwelijksdatum, scheidingsdatum) VALUES (";
            String insertGezinQuery = "";
            
            for(Gezin g : admin.getGezinnen())
            {
                insertGezinQuery = baseInsertGezinQuery;
                insertGezinQuery += g.getNr() + ", ";
                insertGezinQuery += g.getOuder1().getNr() + ", ";
                
                if (g.getOuder2() == null)
                {
                    insertGezinQuery += "null" + ", ";
                }
                else
                {
                    insertGezinQuery += g.getOuder2().getNr() + ", ";
                }
                
                if (g.getHuwelijksdatum() == null)
                {
                    insertGezinQuery += "null" + ", ";
                }
                else
                {
                    insertGezinQuery += g.getHuwelijksdatum() + ", ";
                }
                
                if (g.getHuwelijksdatum() == null)
                {
                    insertGezinQuery += "null";
                }
                else
                {
                    insertGezinQuery += g.getScheidingsdatum();
                }
                
                
                insertGezinQuery += ")";
                
                insertGezin.execute(insertGezinQuery);
            }
            
        }
        catch(SQLException ex)
        {
            System.out.println("Exception: " + ex.getMessage());
            throw new IOException();
        }
    }

    @Override
    public final boolean configure(Properties props) {
        this.props = props;

        try {
            initConnection();
            return isCorrectlyConfigured();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            this.props = null;
            return false;
        } finally {
            closeConnection();
        }
    }

    @Override
    public Properties config() {
        return props;
    }

    @Override
    public boolean isCorrectlyConfigured() {
        if (props == null) {
            return false;
        }
        if (!props.containsKey("driver")) {
            return false;
        }
        if (!props.containsKey("url")) {
            return false;
        }
        if (!props.containsKey("username")) {
            return false;
        }
        if (!props.containsKey("password")) {
            return false;
        }
        return true;
    }

    private void initConnection() throws SQLException {
        //opgave 4
        String url = "jdbc:mysql://localhost:3306/?user=root";
        String username = "root";
        String password = "Lomm1994";
        
        try
        {
            /*
            url = this.props.getProperty(url);
            username = this.props.getProperty(username);
            password = this.props.getProperty(password);
                    */
            
            if (!url.equals("") && !username.equals("") && !password.equals(""))
            {
                this.conn = DriverManager.getConnection(url, username, password);
            }
        }
        catch(Exception ex)
        {
            System.out.println("Exception: " + ex.getMessage());
        }
        
        
    }

    private void closeConnection() {
        try {
            conn.close();
            conn = null;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
