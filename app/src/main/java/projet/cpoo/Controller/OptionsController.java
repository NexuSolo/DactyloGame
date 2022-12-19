package projet.cpoo.Controller;

import java.io.IOException;
import java.net.Socket;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import projet.cpoo.App;

public class OptionsController {
    private Socket socket;

    @FXML
    private MenuButton langueMenuButton;
    @FXML
    private CheckBox accentCheckBox;
    @FXML
    private RadioButton tempsRadioButton;
    @FXML
    private RadioButton MotsRadioButton;
    @FXML
    private TextField tempsMotsTextField;
    @FXML
    private TextField pseudoField;
    @FXML
    private TextField ipField;
    @FXML
    private TextField portField;
    @FXML
    private MenuButton serveurMenuButton;
    @FXML
    private Circle circleEtat;

    private void testConnect(String ip, int port) {
        try {
            Thread t = new Thread( () -> {
                try {
                    socket = new Socket(ip, port);
                    socket.close();
                    circleEtat.setStyle("-fx-fill: green;");
                } catch (IOException e) {
                    circleEtat.setStyle("-fx-fill: red;");
                }
            });
            t.start();
            t.join(2000);
            if(t.isAlive()) {
                t.interrupt();
                circleEtat.setStyle("-fx-fill: red;");
            }
        }
        catch (Exception e) {
            System.out.println("Erreur de connexion");
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
            if(ipField.getText().equals("")) {

            }
            App.setIp(ipField.getText());
            testConnect(App.getIp(), App.getPort());
        });

        portField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if(newValue.equals("") || Integer.parseInt(newValue) < 0 || Integer.parseInt(newValue) > 65535)
                    App.setPort(5000);
                else
                    App.setPort(Integer.parseInt(newValue));
            } catch (NumberFormatException e) {
                App.setPort(5000);
            }
            testConnect(App.getIp(), App.getPort());
        });
    }

    @FXML
    private void serveurOVH() {
        serveurMenuButton.setText("Serveur OVH");
        ipField.setText("vps-1fb525ee.vps.ovh.net");
        portField.setText("5000");
        App.setIp("vps-1fb525ee.vps.ovh.net");
        App.setPort(5000);
        testConnect(App.getIp(), App.getPort());
    }
    
    @FXML
    private void retour() throws IOException {
        if(App.getPseudo().equals("")) {
            App.setPseudo("Joueur");
        }
        App.setRoot("menu");
    }
    
}