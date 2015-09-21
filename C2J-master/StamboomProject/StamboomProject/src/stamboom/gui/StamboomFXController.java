/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import stamboom.controller.StamboomController;
import stamboom.domain.Geslacht;
import stamboom.domain.Gezin;
import stamboom.domain.Persoon;
import stamboom.util.StringUtilities;

/**
 *
 * @author frankpeeters
 */
public class StamboomFXController extends StamboomController implements Initializable {

    //MENUs en TABs
    @FXML MenuBar menuBar;
    @FXML MenuItem miNew;
    @FXML MenuItem miOpen;
    @FXML MenuItem miSave;
    @FXML CheckMenuItem cmDatabase;
    @FXML MenuItem miClose;
    @FXML TabPane myTabs;
    @FXML Tab tabPersoon;
    @FXML Tab tabGezin;
    @FXML Tab tabPersoonInvoer;
    @FXML Tab tabGezinInvoer;
    @FXML Tab tabStamboom;

    //PERSOON
    @FXML ComboBox cbPersonen;
    @FXML TextField tfPersoonNr;
    @FXML TextField tfVoornamen;
    @FXML TextField tfTussenvoegsel;
    @FXML TextField tfAchternaam;
    @FXML TextField tfGeslacht;
    @FXML TextField tfGebDatum;
    @FXML TextField tfGebPlaats;
    @FXML ComboBox cbOuderlijkGezin;
    @FXML ListView lvAlsOuderBetrokkenBij;
    @FXML Button btStamboom;
    
    //GEZIN
    @FXML ComboBox cbGezinnen;
    @FXML TextField tfGezinNr;
    @FXML TextField tfOuder1;
    @FXML TextField tfOuder2;
    @FXML ListView lvKinderen;
    @FXML TextField tfHuwelijkInvoer1;
    @FXML TextField tfScheidingInvoer1;

    //INVOER GEZIN
    @FXML ComboBox cbOuder1Invoer;
    @FXML ComboBox cbOuder2Invoer;
    @FXML TextField tfHuwelijkInvoer;
    @FXML TextField tfScheidingInvoer;
    @FXML Button btOKGezinInvoer;
    @FXML Button btCancelGezinInvoer;
    
    //INVOER PERSOON
    @FXML TextField tfVoornamen1;
    @FXML TextField tfTussenvoegsel1;
    @FXML TextField tfAchternaam1;
    @FXML ComboBox cbGeslacht;
    @FXML TextField tfGebDatum1;
    @FXML TextField tfGebPlaats1;
    @FXML ComboBox cbOuderlijkGezin1;
    @FXML Button btOKPersoonInvoer;
    @FXML Button btCancelPersoonInvoer;
    
    //STAMBOOM
    @FXML TextArea stamboomText;

    //opgave 4
    private boolean withDatabase;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initComboboxes();
        withDatabase = true;
        this.createEmptyStamboom(null);
    }

    private void initComboboxes() {
        //todo opgave 3 
        cbPersonen.setItems(this.getAdministratie().getObservablePersonen());
        cbGezinnen.setItems(this.getAdministratie().getObservableGezinnen());
        cbOuderlijkGezin.setItems(this.getAdministratie().getObservableGezinnen());
        cbOuder1Invoer.setItems(this.getAdministratie().getObservablePersonen());
        cbOuder2Invoer.setItems(this.getAdministratie().getObservablePersonen());
        cbOuderlijkGezin1.setItems(this.getAdministratie().getObservableGezinnen());
        cbGeslacht.setItems(this.getAdministratie().getObservableGeslachten());
    }

    public void selectPersoon(Event evt) {
        Persoon persoon = (Persoon) cbPersonen.getSelectionModel().getSelectedItem();
        showPersoon(persoon);
    }

    private void showPersoon(Persoon persoon) {
        if (persoon == null) {
            clearTabPersoon();
        } else {
            tfPersoonNr.setText(persoon.getNr() + "");
            tfVoornamen.setText(persoon.getVoornamen());
            tfTussenvoegsel.setText(persoon.getTussenvoegsel());
            tfAchternaam.setText(persoon.getAchternaam());
            tfGeslacht.setText(persoon.getGeslacht().toString());
            tfGebDatum.setText(StringUtilities.datumString(persoon.getGebDat()));
            tfGebPlaats.setText(persoon.getGebPlaats());
            if (persoon.getOuderlijkGezin() != null) {
                cbOuderlijkGezin.getSelectionModel().select(persoon.getOuderlijkGezin());
            } else {
                cbOuderlijkGezin.getSelectionModel().clearSelection();
            }

            //todo opgave 3
            lvAlsOuderBetrokkenBij.setItems((ObservableList) persoon.getAlsOuderBetrokkenIn());
        }
    }

    public void setOuders(Event evt) {
        if (tfPersoonNr.getText().isEmpty()) {
            return;
        }
        Gezin ouderlijkGezin = (Gezin) cbOuderlijkGezin.getSelectionModel().getSelectedItem();
        if (ouderlijkGezin == null) {
            return;
        }

        int nr = Integer.parseInt(tfPersoonNr.getText());
        Persoon p = getAdministratie().getPersoon(nr);
        getAdministratie().setOuders(p, ouderlijkGezin);
    }

    public void selectGezin(Event evt) {
        // todo opgave 3
        Gezin gezin = (Gezin) cbGezinnen.getSelectionModel().getSelectedItem();
        showGezin(gezin);

    }

    private void showGezin(Gezin gezin) {
        // todo opgave 3
        if (gezin == null) {
            clearTabGezin();
        } else {
            tfGezinNr.setText(gezin.getNr() + "");
            tfOuder1.setText(gezin.getOuder1().getNaam());
            tfOuder2.setText(gezin.getOuder2().getNaam());
            lvKinderen.setItems(gezin.getObservableKinderen());
            
            if (gezin.getHuwelijksdatum() == null)
            {
                tfHuwelijkInvoer1.clear();
            }
            else
            {
                tfHuwelijkInvoer1.setText(StringUtilities.datumString(gezin.getHuwelijksdatum()));
            }
            
            if (gezin.getScheidingsdatum() == null)
            {
                tfScheidingInvoer1.clear();
            }
            else
            {
                tfScheidingInvoer1.setText(StringUtilities.datumString(gezin.getScheidingsdatum()));
            }
        }
    }

    public void setHuwdatum(Event evt) {
        // todo opgave 3
        Gezin gezin = (Gezin) cbGezinnen.getSelectionModel().getSelectedItem();
        
        if (gezin != null)
        {
            Calendar c = createCalendar(tfHuwelijkInvoer1.getText());
            
            if (c != null)
            {
                getAdministratie().setHuwelijk(gezin, c);
            }
            else
            {
                tfHuwelijkInvoer1.clear();
            }
        }
    }

    public void setScheidingsdatum(Event evt) {
        // todo opgave 3
        Gezin gezin = (Gezin) cbGezinnen.getSelectionModel().getSelectedItem();
        
        if (gezin != null)
        {
            Calendar c = createCalendar(tfScheidingInvoer1.getText());
            
            if (c != null)
            {
                getAdministratie().setScheiding(gezin, c);
            }
            else
            {
                tfScheidingInvoer1.clear();
            }
        }
    }

    public void cancelPersoonInvoer(Event evt) {
        // todo opgave 3
        clearTabPersoonInvoer();
    }

    public void okPersoonInvoer(Event evt) {
        // todo opgave 3
        stamboom.domain.Geslacht geslacht;
        if (cbGeslacht.getValue() == "MAN")
        {
            geslacht = Geslacht.MAN;
        }
        else
        {
            geslacht = Geslacht.VROUW;
        }
        
        Calendar c = createCalendar(tfGebDatum1.getText());
        
        Gezin ouderlijkGezin = null;
        
        for (Gezin g : getAdministratie().getGezinnen())
        {
            if (g.toString() == cbOuderlijkGezin1.getValue())
            {
                ouderlijkGezin = g;
            }
        }
        
        if (c != null)
        {
            List<String> voornamenList = new ArrayList();
            for(String voornaam : tfVoornamen1.getText().trim().split(" "))
            {
                if (voornaam.trim().length() > 0)
                {
                    voornamenList.add(voornaam);
                }
            }
            
            String[] voornamen = voornamenList.toArray(new String[voornamenList.size()]);
            
            Persoon testPersoon = getAdministratie().addPersoon(geslacht, voornamen, tfAchternaam1.getText(), tfTussenvoegsel1.getText(), c, tfGebPlaats1.getText(), ouderlijkGezin);
            
            if (testPersoon != null)
            {
                System.out.println("Nieuw persoon: "+ testPersoon.toString());
            }
        }
        else
        {
            tfGebDatum1.clear();
        }
        
        clearTabPersoonInvoer();
        getAdministratie().UpdateObservableLists();
        initComboboxes();
    }

    public void okGezinInvoer(Event evt) {
        Persoon ouder1 = (Persoon) cbOuder1Invoer.getSelectionModel().getSelectedItem();
        if (ouder1 == null) {
            showDialog("Warning", "eerste ouder is niet ingevoerd");
            return;
        }
        Persoon ouder2 = (Persoon) cbOuder2Invoer.getSelectionModel().getSelectedItem();
        Calendar huwdatum;
        try {
            huwdatum = StringUtilities.datum(tfHuwelijkInvoer.getText());
        } catch (IllegalArgumentException exc) {
            showDialog("Warning", "huwelijksdatum :" + exc.getMessage());
            return;
        }
        Gezin g;
        if (huwdatum != null) {
            g = getAdministratie().addHuwelijk(ouder1, ouder2, huwdatum);
            if (g == null) {
                showDialog("Warning", "Invoer huwelijk is niet geaccepteerd");
            } else {
                Calendar scheidingsdatum = null;
                try {
                    scheidingsdatum = StringUtilities.datum(tfScheidingInvoer.getText());
                } catch (IllegalArgumentException exc) {
                    showDialog("Warning", "scheidingsdatum :" + exc.getMessage());
                }
                
                if(scheidingsdatum != null)
                {
                    getAdministratie().setScheiding(g, scheidingsdatum);
                }
            }
        } else {
            g = getAdministratie().addOngehuwdGezin(ouder1, ouder2);
            if (g == null) {
                showDialog("Warning", "Invoer ongehuwd gezin is niet geaccepteerd");
            }
        }

        clearTabGezinInvoer();
        getAdministratie().UpdateObservableLists();
        initComboboxes();
    }

    public void cancelGezinInvoer(Event evt) {
        clearTabGezinInvoer();
    }

    
    public void showStamboom(Event evt) {
        // todo opgave 3
        Persoon persoon = (Persoon) cbPersonen.getSelectionModel().getSelectedItem();
        
        if (persoon != null)
        {
            myTabs.getSelectionModel().select(tabStamboom);
            stamboomText.setText(persoon.stamboomAlsString());
        }
    }

    public void createEmptyStamboom(Event evt) {
        this.clearAdministratie();
        clearTabs();
        initComboboxes();
    }

    
    public void openStamboom(Event evt) {
        // todo opgave 3
        if(!withDatabase)
        {
            FileChooser fc = new FileChooser();
            File stamboom = fc.showOpenDialog(null);

            try
            {
                deserialize(stamboom);
            }
            catch (IOException ex)
            {
                System.out.println("Couldn't open file.");
            }
        }
        else
        {
            try
            {
                super.loadFromDatabase();
            }
            catch(Exception ex)
            {
                
            }
        }
        getAdministratie().UpdateObservableLists();
        initComboboxes();
    }

    
    public void saveStamboom(Event evt) {
        // todo opgave 3
        if(!withDatabase)
        {
            File stamboom = new File("Stamboom");
            if(stamboom.exists())
            {
                stamboom.delete();
            }
            try
            {
                serialize(stamboom);
            }
            catch (IOException ex)
            {
                System.out.println("Couldn't save file.");
            }
            System.out.println("Stamboom saved.");
        }
        else
        {
            try
            {
                super.saveToDatabase();
            }
            catch(IOException ex)
            {
                System.out.println(ex.getMessage());
            }
        }
    }

    
    public void closeApplication(Event evt) {
        saveStamboom(evt);
        getStage().close();
    }

   
    public void configureStorage(Event evt) {
        withDatabase = cmDatabase.isSelected();
    }

 
    public void selectTab(Event evt) {
        Object source = evt.getSource();
        if (source == tabPersoon) {
            clearTabPersoon();
        } else if (source == tabGezin) {
            clearTabGezin();
        } else if (source == tabPersoonInvoer) {
            clearTabPersoonInvoer();
        } else if (source == tabGezinInvoer) {
            clearTabGezinInvoer();
        }
    }

    private void clearTabs() {
        clearTabPersoon();
        clearTabPersoonInvoer();
        clearTabGezin();
        clearTabGezinInvoer();
    }

    
    private void clearTabPersoonInvoer() {
        //todo opgave 3
    tfVoornamen1.clear();
    tfTussenvoegsel1.clear();
    tfAchternaam1.clear();
    cbGeslacht.getSelectionModel().clearSelection();
    tfGebDatum1.clear();
    tfGebPlaats1.clear();
    cbOuderlijkGezin1.getSelectionModel().clearSelection();
    //btOKPersoonInvoer;
    //btCancelPersoonInvoer;
    }

    
    private void clearTabGezinInvoer() {
        //todo opgave 3
    cbOuder1Invoer.getSelectionModel().clearSelection();
    cbOuder2Invoer.getSelectionModel().clearSelection();
    tfHuwelijkInvoer.clear();
    tfScheidingInvoer.clear();
    //btOKGezinInvoer
    //btCancelGezinInvoer

    }

    private void clearTabPersoon() {
        cbPersonen.getSelectionModel().clearSelection();
        tfPersoonNr.clear();
        tfVoornamen.clear();
        tfTussenvoegsel.clear();
        tfAchternaam.clear();
        tfGeslacht.clear();
        tfGebDatum.clear();
        tfGebPlaats.clear();
        cbOuderlijkGezin.getSelectionModel().clearSelection();
        lvAlsOuderBetrokkenBij.setItems(FXCollections.emptyObservableList());
    }

    
    private void clearTabGezin() {
        // todo opgave 3
    cbGezinnen.getSelectionModel().clearSelection();
    tfGezinNr.clear();
    tfOuder1.clear();
    tfOuder2.clear();
    lvKinderen.setItems(FXCollections.emptyObservableList());
    tfHuwelijkInvoer1.clear();
    tfScheidingInvoer1.clear();
    }

    private void showDialog(String type, String message) {
        Stage myDialog = new Dialog(getStage(), type, message);
        myDialog.show();
    }

    private Stage getStage() {
        return (Stage) menuBar.getScene().getWindow();
    }

    private Calendar createCalendar(String dateString)
    {
        Calendar c;
        
        try
        {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            Date parsed = df.parse(dateString);

            c = Calendar.getInstance();
            c.setTime(parsed);
        }
        catch (Exception ex)
        {
            c = null;
        }
        
        return c;
    }
}
