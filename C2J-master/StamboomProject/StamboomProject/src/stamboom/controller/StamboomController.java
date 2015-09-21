/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import stamboom.domain.Administratie;
import stamboom.storage.IStorageMediator;
import java.util.Properties;
import stamboom.storage.DatabaseMediator;

public class StamboomController {

    private Administratie admin;
    private IStorageMediator storageMediator;

    /**
     * creatie van stamboomcontroller met lege administratie en onbekend
     * opslagmedium
     */
    public StamboomController() {
        admin = new Administratie();
        storageMediator = null;
    }

    public Administratie getAdministratie() {
        return admin;
    }

    /**
     * administratie wordt leeggemaakt (geen personen en geen gezinnen)
     */
    public void clearAdministratie() {
        admin = new Administratie();
    }

    /**
     * administratie wordt in geserialiseerd bestand opgeslagen
     *
     * @param bestand
     * @throws IOException
     */

    public void serialize(File bestand) throws IOException {
        //todo opgave 2
        try
        {
            java.io.FileOutputStream fileOutput = new java.io.FileOutputStream(bestand);
            
            java.io.ObjectOutputStream objectOutput = new java.io.ObjectOutputStream(fileOutput);
            objectOutput.writeObject(this.admin);
            objectOutput.close();
            
            fileOutput.close();
            
            /*
            System.out.println(bestand.getAbsolutePath());
            Properties props = new Properties();
            props.setProperty("file", bestand.getAbsolutePath());
            storageMediator.configure(props);
            storageMediator.save(admin);
            */
        }
        catch (Exception ex)
        {
            System.out.print("Exception: " + ex.getMessage());
        }
    }

    /**
     * administratie wordt vanuit geserialiseerd bestand gevuld
     *
     * @param bestand
     * @throws IOException
     */
    public void deserialize(File bestand) throws IOException {
        //todo opgave 2       
        try
        {
            java.io.FileInputStream fileInput = new java.io.FileInputStream(bestand);
            
            java.io.ObjectInputStream objectInput = new java.io.ObjectInputStream(fileInput);
            this.admin = (Administratie)objectInput.readObject();
            objectInput.close();
            
            fileInput.close();
            
            /*
            Properties props = new Properties();
            props.setProperty("file", bestand.getPath());
            storageMediator.configure(props);
            this.admin = storageMediator.load();
            */
        }
        catch (Exception ex)
        {
            System.out.print("Exception: " + ex.getMessage());
        }
    }
    
    // opgave 4
    private void initDatabaseMedium() throws IOException {
        if (!(storageMediator instanceof DatabaseMediator)) {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream("database.properties")) {
                props.load(in);
            }
            storageMediator = new DatabaseMediator(props);
        }
    }
    
    /**
     * administratie wordt vanuit standaarddatabase opgehaald
     *
     * @throws IOException
     */
    public void loadFromDatabase() throws IOException {
        //todo opgave 4
        this.initDatabaseMedium();
        this.admin = this.storageMediator.load();
    }

    /**
     * administratie wordt in standaarddatabase bewaard
     *
     * @throws IOException
     */
    public void saveToDatabase() throws IOException {
        //todo opgave 4
        this.initDatabaseMedium();
        this.storageMediator.save(this.admin);
    }

}
