package projet.cpoo.Controller;

import java.io.IOException;
import java.net.Socket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import projet.cpoo.App;

public class OptionsController {
    private Socket socket;

    @FXML
    private TextField pseudoField;
    @FXML
    private TextField ipField;
    @FXML
    private TextField portField;
    @FXML
    private MenuButton menuButtonServeur;
    @FXML
    private Circle circleEtat;

    private void testConnect(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            circleEtat.setStyle("-fx-fill: green;");
            socket.close();
        } catch (IOException e) {
            circleEtat.setStyle("-fx-fill: red;");
        }
    }

    @FXML
    private void initialize() {
        pseudoField.setText(App.getPseudo());
        ipField.setText(App.getIp());
        portField.setText(Integer.toString(App.getPort()));
        testConnect(App.getIp(), App.getPort());

        pseudoField.textProperty().addListener((observable, oldValue, newValue) -> App.setPseudo(newValue));

        ipField.setOnAction(event -> {
            App.setIp(ipField.getText());
            testConnect(App.getIp(), App.getPort());
        });

        portField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if(newValue.equals("")) newValue = "5000";
                App.setPort(Integer.parseInt(newValue));
            } catch (NumberFormatException e) {
                App.setPort(5000);
            }
            testConnect(App.getIp(), Integer.parseInt(newValue));
        });
    }
    
    @FXML
    private void retour() throws IOException {
        if(App.getPseudo().equals("")) {
            App.setPseudo("Joueur");
        }
        App.setRoot("menu");
    }
    
}