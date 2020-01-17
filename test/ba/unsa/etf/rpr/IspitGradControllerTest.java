package ba.unsa.etf.rpr;


import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class IspitGradControllerTest {
    Stage theStage;
    GradController ctrl;

    @Start
    public void start(Stage stage) throws Exception {
        GeografijaDAO dao = GeografijaDAO.getInstance();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"));
        ctrl = new GradController(null, dao.drzave());
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
    public void testPoljeZagadjenost(FxRobot robot) {
        Slider fld = robot.lookup("#sliderZagadjenost").queryAs(Slider.class);
        assertNotNull(fld);
    }

    @Test
    public void testMinMax(FxRobot robot) {
        Slider sld = robot.lookup("#sliderZagadjenost").queryAs(Slider.class);
        assertEquals(10, sld.getMax());
        assertEquals(1, sld.getMin());
    }

    @Test
    public void testPostavljaZagadjenost(FxRobot robot) {
        Platform.runLater(() -> theStage.close());

        Drzava drzava = new Drzava();
        Grad grad = new Grad(0, "Sarajevo", 350000, drzava, 4); // Zagađenost je posljednji parametar

        GeografijaDAO dao = GeografijaDAO.getInstance();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"));
        ctrl = new GradController(grad, dao.drzave());
        loader.setController(ctrl);

        Platform.runLater(() -> {
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage xstage = new Stage();
            xstage.setTitle("Grad");
            xstage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            xstage.setResizable(false);
            xstage.show();
            xstage.toFront();
        });

        // Čekamo da se pojavi prozor
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Da li je vrijednost slidera ispravna?
        Slider sld = robot.lookup("#sliderZagadjenost").queryAs(Slider.class);
        double value = sld.getValue();

        // Klik na dugme ok
        robot.clickOn("#btnOk");

        assertEquals(4, value);
    }

    @Test
    public void testVracaZagadjenost(FxRobot robot) {
        // Upisujemo grad
        robot.clickOn("#fieldNaziv");
        robot.write("Sarajevo");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("350000");

        Slider sld = robot.lookup("#sliderZagadjenost").queryAs(Slider.class);
        sld.setValue(8);

        // Klik na dugme ok
        robot.clickOn("#btnOk");

        Grad grad = ctrl.getGrad();
        assertEquals(8, grad.getZagadjenost());
    }
}
