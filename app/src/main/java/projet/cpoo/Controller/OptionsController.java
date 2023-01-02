package projet.cpoo.Controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import projet.cpoo.Settings;
import projet.cpoo.Settings.Language;

public class OptionsController {
    private Socket socket;

    @FXML
    private MenuButton langueMenuButton;
    @FXML
    private CheckBox accentCheckBox; 
    @FXML
    private CheckBox msCheckBox;
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
        pseudoField.setText(Settings.getPseudo());
        ipField.setText(Settings.getIp());
        portField.setText(Integer.toString(Settings.getPort()));
        testConnect(Settings.getIp(), Settings.getPort());
        langueMenuButton.setText(Settings.Language.languageToString(Settings.getLangue()));
        accentCheckBox.setSelected(Settings.isAccents());
        msCheckBox.setSelected(Settings.isMortSubite());
        if (Settings.isModeTemps()) {
            tempsRadioButton.setSelected(true);
            nombreDeText.setText("Nombre de secondes :");
        }
        else {
            MotsRadioButton.setSelected(true);
            nombreDeText.setText("Nombre de mots :");
        }
        spin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 300, 60));
        if (Settings.isModeTemps()) spin.getValueFactory().setValue(Settings.getLIMITE_MAX()/10);
        else spin.getValueFactory().setValue(Settings.getLIMITE_MAX());
        spin.valueProperty().addListener((obs, oldValue, newValue) -> { int val = ((Integer) newValue).intValue();
            if (Settings.isModeTemps())
                val *= 10;
            System.out.println("Set val : " + val);
            Settings.setLIMITE_MAX(val);
        });
        
        pseudoField.textProperty().addListener((observable, oldValue, newValue) -> Settings.setPseudo(newValue));

        ipField.setOnAction(event -> {
            Settings.setIp(ipField.getText());
            testConnect(Settings.getIp(), Settings.getPort());
        });

        portField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if(newValue.equals("") || Integer.parseInt(newValue) < 0 || Integer.parseInt(newValue) > 65535)
                Settings.setPort(5000);
                else
                Settings.setPort(Integer.parseInt(newValue));
            } catch (NumberFormatException e) {
                Settings.setPort(5000);
            }
            testConnect(Settings.getIp(), Settings.getPort());
        });
    }

    @FXML
    private void serveurOVH() {
        serveurMenuButton.setText("Serveur OVH");
        ipField.setText("vps-1fb525ee.vps.ovh.net");
        portField.setText("5000");
        Settings.setIp("vps-1fb525ee.vps.ovh.net");
        Settings.setPort(5000);
        testConnect(Settings.getIp(), Settings.getPort());
    }

    @FXML
    private void langueFrancais() throws UnsupportedEncodingException {
        Settings.setLangue(Language.FR);
        String s = new String("Français".getBytes(), "utf-8");
        langueMenuButton.setText(s);
    }

    @FXML
    private void langueEnglish() {
        Settings.setLangue(Language.EN);
        langueMenuButton.setText("English");
    }

    @FXML
    private void accent() {
        Settings.setAccents(accentCheckBox.isSelected());
    }

    @FXML
    private void mortSubite() {
        if(msCheckBox.isSelected()) {
            Settings.setMortSubite(true);
        }
        else {
            Settings.setMortSubite(false);
        }
        System.out.println("Mort subite = " + Settings.isMortSubite());
    }

    @FXML
    @SuppressWarnings("unchecked")
    private void temps() {
        tempsRadioButton.setSelected(true);
        MotsRadioButton.setSelected(false);
        nombreDeText.setText("Nombre de secondes :");
        Settings.setModeTemps();
        Settings.setLIMITE_MAX(60);
        spin.getValueFactory().setValue(Settings.getLIMITE_MAX()/10);
    }

    @FXML
    @SuppressWarnings("unchecked")
    private void mots() {
        tempsRadioButton.setSelected(false);
        MotsRadioButton.setSelected(true);
        nombreDeText.setText("Nombre de mots :");
        Settings.setModeMots();
        Settings.setLIMITE_MAX(30);
        spin.getValueFactory().setValue(Settings.getLIMITE_MAX());
    }
    
    @FXML
    private void retour() throws IOException {
        if(Settings.getPseudo().equals("")) {
            Settings.setPseudo("Joueur");
        }
        App.setRoot("menu");
    }
    
}