package fr.cpoo;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class MenuController {

    @FXML
    private void highlightButton(MouseEvent e) throws IOException {
        Button button = (Button) e.getSource();
        button.setStyle("-fx-background-color: #6a6f75");
    }

    @FXML
    private void removeHighlightButton(MouseEvent e) throws IOException { 
        Button button = (Button) e.getSource();
        button.setStyle("-fx-background-color: #54585d");
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
