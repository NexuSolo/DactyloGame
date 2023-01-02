package projet.cpoo.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.google.gson.Gson;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import projet.cpoo.App;
import projet.cpoo.Message;
import projet.cpoo.Transmission;

public class ClassementController implements Initializable {
    List<String> classement;
    boolean gagner;

    @FXML
    private Text vousAvezText;

    @FXML
    private VBox classementVBox;

    @FXML
    Button retourButton;

    public ClassementController(List<String> classement, boolean gagner) {
        this.classement = classement;
        this.gagner = gagner;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(gagner) {
            vousAvezText.setText("Vous avez gagné !");
        }
        else {
            vousAvezText.setText("Vous avez perdu !");
        }
        for(int i = 0; i < classement.size(); i++) {
            Text text = new Text(classement.get(i));
            text.getStyleClass().add("textListeJoueur");
            classementVBox.getChildren().add(text);
        }
        retourButton.setOnAction(e -> {
            try {
                retour();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    @FXML
    private void retour() throws IOException {
        Message m = new Message(Transmission.CLIENT_DECONNEXION,null);
        Gson gson = new Gson();
        String json = gson.toJson(m);
        App.getSocket().getOutputStream().write(json.getBytes());
        App.getSocket().close();
        App.setRoot("menu");
    }

}
