package projet.cpoo.Controller;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
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
    private TextField pseudoField;
    @FXML
    private TextField ipField;
    @FXML
    private TextField portField;
    @FXML
    private MenuButton serveurMenuButton;
    @FXML
    private Circle circleEtat;
    @SuppressWarnings("rawtypes")
    @FXML
    private Spinner spin;
    @FXML
    private Text nombreDeText;

    private void testConnect(String ip, int port) {
        try {
            Thread t = new Thread(() -> {
                try {
                    socket = new Socket(ip, port);
                    socket.close();
                    circleEtat.setStyle("-fx-fill: green;");
                }
                catch (ConnectException e) {

                }
                catch (IOException e) {
                    circleEtat.setStyle("-fx-fill: red;");
                }
            });
            t.start();
            t.join(250);
            if (t.isAlive()) {
                t.interrupt();
                circleEtat.setStyle("-fx-fill: red;");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @FXML
    private void initialize() {
        pseudoField.setText(App.getPseudo());
        ipField.setText(App.getIp());
        portField.setText(Integer.toString(App.getPort()));
        testConnect(App.getIp(), App.getPort());
        langueMenuButton.setText(App.getLangue());
        accentCheckBox.setSelected(App.isAccent());
        if (App.getMode().equals("temps")) {
            tempsRadioButton.setSelected(true);
            nombreDeText.setText("Nombre de secondes :");
        }
        else {
            MotsRadioButton.setSelected(true);
            nombreDeText.setText("Nombre de mots :");
        }
        spin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 300, 60));
        spin.getValueFactory().setValue(App.getModenbr());
        spin.valueProperty().addListener((obs, oldValue, newValue) -> App.setModenbr(((Integer) newValue).intValue()));
        
        pseudoField.textProperty().addListener((observable, oldValue, newValue) -> App.setPseudo(newValue));

        ipField.setOnAction(event -> {
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
    private void langueFrancais() {
        langueMenuButton.setText("Français");
        App.setLangue("Français");
    }

    @FXML
    private void langueEnglish() {
        langueMenuButton.setText("English");
        App.setLangue("English");
    }

    @FXML
    private void accent() {
        if(accentCheckBox.isSelected()) {
            App.setAccent(true);
        }
        else {
            App.setAccent(false);
        }
    }

    @FXML
    private void temps() {
        tempsRadioButton.setSelected(true);
        MotsRadioButton.setSelected(false);
        nombreDeText.setText("Nombre de secondes :");
        App.setMode("temps");
    }

    @FXML
    private void mots() {
        tempsRadioButton.setSelected(false);
        MotsRadioButton.setSelected(true);
        nombreDeText.setText("Nombre de mots :");
        App.setMode("mots");
    }
    
    @FXML
    private void retour() throws IOException {
        if(App.getPseudo().equals("")) {
            App.setPseudo("Joueur");
        }
        App.setRoot("menu");
    }
    
}