package ba.unsa.etf.rpr;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class IspitGlavnaTest {
    Stage theStage;
    GlavnaController ctrl;

    @Start
    public void start (Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/glavna.fxml"));
        ctrl = new GlavnaController();
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.setTitle("Grad");
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.setResizable(false);
        stage.show();

        stage.toFront();

        theStage = stage;
    }


    @Test
    public void testDodajGradZagadjenost(FxRobot robot) {
        ctrl.resetujBazu();

        // Otvaranje forme za dodavanje
        robot.clickOn("#btnDodajGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Postoji li fieldNaziv
        robot.clickOn("#fieldNaziv");
        robot.write("Sarajevo");

        robot.clickOn("#fieldBrojStanovnika");
        robot.write("350000");

        Slider sld = robot.lookup("#sliderZagadjenost").queryAs(Slider.class);
        sld.setValue(9);

        // Klik na dugme Ok
        robot.clickOn("#btnOk");

        // Da li je Sarajevo dodano u bazu?
        GeografijaDAO dao = GeografijaDAO.getInstance();
        assertEquals(6, dao.gradovi().size());

        Grad sarajevo = null;
        for(Grad grad : dao.gradovi())
            if (grad.getNaziv().equals("Sarajevo"))
                sarajevo = grad;
        assertNotNull(sarajevo);
        assertEquals(9, sarajevo.getZagadjenost());
    }

    @Test
    public void testIzmijeniGradZagadjenost(FxRobot robot) {
        ctrl.resetujBazu();

        // 7 ne smije biti default zagadjenost za Graz jer je to "varanje"
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Grad graz = dao.nadjiGrad("Graz");
        assertNotEquals(7, graz.getZagadjenost());

        // Mijenjamo grad Graz
        robot.clickOn("Graz");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        Slider sld = robot.lookup("#sliderZagadjenost").queryAs(Slider.class);
        sld.setValue(7);

        // Klik na dugme Ok
        robot.clickOn("#btnOk");

        // Da li je promijenjen broj stanovnika Graza?
        graz = dao.nadjiGrad("Graz");
        assertEquals(7, graz.getZagadjenost());
    }

    @Test
    public void testKolonaNadmorska(FxRobot robot) {
        // Postavljamo nadmorsku visinu za Graz da možemo testirati tabelu
        ctrl.resetujBazu();

        robot.clickOn("Graz");
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        Slider sld = robot.lookup("#sliderZagadjenost").queryAs(Slider.class);
        sld.setValue(7);

        // Klik na dugme Ok
        robot.clickOn("#btnOk");

        TableView<Grad> tableViewGradovi = robot.lookup("#tableViewGradovi").queryAs(TableView.class);
        assertNotNull(tableViewGradovi);

        boolean found = false;
        for (TableColumn column : tableViewGradovi.getColumns()) {
            if (column.getText().equals("Zagađenost")) {
                found = true;
            }
        }
        assertTrue(found);
    }



    @Test
    public void testZapisi(FxRobot robot) {
        ctrl.resetujBazu();

        // Brišemo fajl ako postoji
        File xml = new File("geografija.xml");
        xml.delete();

        robot.clickOn("#btnZapisi");

        xml = new File("geografija.xml");
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(xml.getPath())));
        } catch (IOException e) {
            fail("Nije uspjelo čitanje XML datoteke");
        }

        assertTrue(content.contains("1899055"));
        assertTrue(content.contains("Velika Britanija"));
        assertTrue(content.contains("zagadjenost"));
    }

    @Test
    public void testZapisiFormat(FxRobot robot) {
        ctrl.resetujBazu();

        // Brišemo fajl ako postoji
        File xml = new File("geografija.xml");
        xml.delete();

        robot.clickOn("#btnZapisi");

        xml = new File("geografija.xml");
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(xml.getPath())));
        } catch (IOException e) {
            fail("Nije uspjelo čitanje XML datoteke");
        }

        assertTrue(content.contains("<string>Beč</string>"));
        assertTrue(content.contains("<string>Francuska</string>"));
        assertTrue(content.contains("<int>8825000</int>"));
        assertTrue(content.contains("<void property=\"zagadjenost\">"));
        assertTrue(content.contains("<string>Graz</string>"));
    }


    @Test
    public void testZapisiDodajGrad(FxRobot robot) {
        ctrl.resetujBazu();

        // Otvaranje forme za dodavanje
        robot.clickOn("#btnDodajGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        robot.clickOn("#fieldNaziv");
        robot.write("Sarajevo");

        robot.clickOn("#fieldBrojStanovnika");
        robot.write("370000");

        Slider sld = robot.lookup("#sliderZagadjenost").queryAs(Slider.class);
        sld.setValue(9);

        // Klik na dugme Ok
        robot.clickOn("#btnOk");

        // Brišemo fajl ako postoji
        File xml = new File("geografija.xml");
        xml.delete();

        robot.clickOn("#btnZapisi");

        xml = new File("geografija.xml");
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(xml.getPath())));
        } catch (IOException e) {
            fail("Nije uspjelo čitanje XML datoteke");
        }

        assertTrue(content.contains("Sarajevo"));
        assertTrue(content.contains("370000"));
        assertTrue(content.contains("<int>9</int>"));
    }


    @Test
    public void testZapisiDodajDrzavu(FxRobot robot) {
        ctrl.resetujBazu();

        // Otvaranje forme za dodavanje
        robot.clickOn("#btnDodajDrzavu");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        robot.clickOn("#fieldNaziv");
        robot.write("Bosna i Hercegovina");

        // Glavni grad će biti automatski izabran kao prvi

        // Klik na dugme Ok
        robot.clickOn("#btnOk");

        // Brišemo fajl ako postoji
        File xml = new File("geografija.xml");
        xml.delete();

        robot.clickOn("#btnZapisi");

        xml = new File("geografija.xml");
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(xml.getPath())));
        } catch (IOException e) {
            fail("Nije uspjelo čitanje XML datoteke");
        }

        assertTrue(content.contains("Bosna i Hercegovina"));
    }


    @Test
    public void testZapisiDodajDrzavuGradDeserijalizacija(FxRobot robot) {
        ctrl.resetujBazu();

        // Otvaranje forme za dodavanje
        robot.clickOn("#btnDodajDrzavu");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        robot.clickOn("#fieldNaziv");
        robot.write("Bosna i Hercegovina");

        // Glavni grad će biti automatski izabran kao prvi

        // Klik na dugme Ok
        robot.clickOn("#btnOk");


        // Otvaranje forme za dodavanje Grada
        robot.clickOn("#btnDodajGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        robot.clickOn("#fieldNaziv");
        robot.write("Sarajevo");

        // Biramo državu Bosna i Hercegovina
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Bosna i Hercegovina");

        robot.clickOn("#fieldBrojStanovnika");
        robot.write("370000");

        Slider sld = robot.lookup("#sliderZagadjenost").queryAs(Slider.class);
        sld.setValue(9);

        // Klik na dugme Ok
        robot.clickOn("#btnOk");

        // Brišemo fajl ako postoji
        File xml = new File("geografija.xml");
        xml.delete();

        robot.clickOn("#btnZapisi");

        Geografija geografija = null;
        try {
            XMLDecoder decoder = new XMLDecoder(new FileInputStream("geografija.xml"));
            geografija = (Geografija)decoder.readObject();
            decoder.close();
        } catch (FileNotFoundException e) {
            fail("Dekodiranje XML datoteke nije uspjelo");
        }

        Grad sarajevo = null;
        for (Grad g : geografija.getGradovi()) {
            if (g.getNaziv().equals("Sarajevo"))
                sarajevo = g;
        }

        assertNotNull(sarajevo);
        assertEquals("Bosna i Hercegovina", sarajevo.getDrzava().getNaziv());
    }
}
