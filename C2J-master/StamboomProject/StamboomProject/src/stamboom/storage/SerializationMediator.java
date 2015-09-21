/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import stamboom.domain.Administratie;

public class SerializationMediator implements IStorageMediator {

    private Properties props;

    /**
     * creation of a non configured serialization mediator
     */
    public SerializationMediator() {
        props = null;
    }

    @Override
    public Administratie load() throws IOException {
        if (!isCorrectlyConfigured()) {
            throw new RuntimeException("Serialization mediator isn't initialized correctly.");
        }
        
        try
        {
            // todo opgave 2       
            java.io.InputStream inputstream = new java.io.FileInputStream(props.getProperty("file"));
            java.io.ObjectInputStream in = new java.io.ObjectInputStream(inputstream);
        
            Administratie admin = (Administratie) in.readObject();
        
            return admin;
        }
        catch (IOException e)
        {
            System.out.println("Exception: " + e.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Exception: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void save(Administratie admin) throws IOException {
        
        if (!isCorrectlyConfigured()) {
            throw new RuntimeException("Serialization mediator isn't initialized correctly.");
        }
        try
        {
            // todo opgave 2
            java.io.FileOutputStream outputstream = new java.io.FileOutputStream(props.getProperty("file"));
            java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(null);
            out.writeObject((admin));
        }
        catch (IOException e)
        {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Override
    public boolean configure(Properties props) {
        this.props = props;
        return isCorrectlyConfigured();
    }

    @Override
    public Properties config() {
        return props;
    }

    /**
     *
     * @return true if config() contains at least a key "file" and the
     * corresponding value is a File-object, otherwise false
     */
    @Override
    public boolean isCorrectlyConfigured() {
        if (props == null) {
            return false;
        }
        if (props.containsKey("file")) {
            return props.get("file") instanceof File;
        } else {
            return false;
        }
    }
}
