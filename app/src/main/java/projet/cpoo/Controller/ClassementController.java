package projet.cpoo.Controller;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import projet.cpoo.App;

public class ClassementController {

    @FXML
    private Text vousAvezText;

    @FXML
    private VBox classementVBox;

    public ClassementController(List<String> classement, List<Integer> score, boolean gagner) {
        if(gagner) {
            vousAvezText.setText("Vous avez gagné !");
        }
        else {
            vousAvezText.setText("Vous avez perdu !");
        }
        for(int i = 0; i < classement.size(); i++) {
            Text text = new Text(classement.get(i) + " : " + score.get(i));
            classementVBox.getChildren().add(text);
        }
    }

    @FXML
    private void retour() throws IOException {
        App.getSocket().close();
        App.setRoot("menu");
    }
    
}
