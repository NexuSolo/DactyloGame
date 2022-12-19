package projet.cpoo.Controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Polygon;
import projet.cpoo.App;

public class MenuController {
    @FXML
    private Button entrainementButton;
    @FXML
    private Button soloButton;
    @FXML
    private Button multijoueurButton;
    @FXML
    private Button optionsButton;
    @FXML
    private Button quitterButton;
    @FXML
    private Polygon entrainementArrow;
    @FXML
    private Polygon soloArrow;
    @FXML
    private Polygon multijoueurArrow;
    @FXML
    private Polygon optionsArrow;
    @FXML
    private Polygon quitterArrow;

    @FXML
    private void highlightButton(MouseEvent e) throws IOException {
        Button button = (Button) e.getSource();
        if(button == soloButton) {
            soloArrow.setVisible(true);
        }
        else if(button == multijoueurButton) {
            multijoueurArrow.setVisible(true);
        }
        else if(button == optionsButton) {
            optionsArrow.setVisible(true);
        }
        else if(button == quitterButton) {
            quitterArrow.setVisible(true);
        }
        else if(button == entrainementButton) {
            entrainementArrow.setVisible(true);
        }
    }

    @FXML
    private void removeHighlightButton(MouseEvent e) throws IOException { 
        Button button = (Button) e.getSource();
        if(button == soloButton) {
            soloArrow.setVisible(false);
        }
        else if(button == multijoueurButton) {
            multijoueurArrow.setVisible(false);
        }
        else if(button == optionsButton) {
            optionsArrow.setVisible(false);
        }
        else if(button == quitterButton) {
            quitterArrow.setVisible(false);
        }
        else if(button == entrainementButton) {
            entrainementArrow.setVisible(false);
        }
    }

    @FXML
    private void switchToEntrainement() throws IOException {
        App.setRoot("entrainement");
    }

    @FXML
    private void switchToSolo() throws IOException {
        App.setRoot("solo");
    }

    @FXML
    private void switchToMultijoueur() throws IOException {
        App.setRoot("attenteJoueur");
    }

    @FXML
    private void switchToOptions() throws IOException {
        App.setRoot("options");
    }

    @FXML
    private void quit() throws IOException {
        System.exit(0);
    }

}
