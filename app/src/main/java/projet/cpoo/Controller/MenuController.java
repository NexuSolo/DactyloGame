package projet.cpoo.Controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import projet.cpoo.App;

public class MenuController {
    @FXML
    private Button soloButton;
    @FXML
    private Button multijoueurButton;
    @FXML
    private Button optionsButton;
    @FXML
    private Button quitterButton;
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
        button.setStyle("-fx-background-color: #6a6f75");
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
    }

    @FXML
    private void removeHighlightButton(MouseEvent e) throws IOException { 
        Button button = (Button) e.getSource();
        button.setStyle("-fx-background-color: #54585d");
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
    }

    @FXML
    private void switchToSolo() throws IOException {
        App.setRoot("solo");
    }

    @FXML
    private void switchToMultijoueur() throws IOException {
        App.setRoot("multijoueur");
    }
    
    @FXML
    private void quit() throws IOException {
        System.exit(0);
    }

    @FXML
    private void switchToOptions() throws IOException {
        App.setRoot("options");
    }

}
