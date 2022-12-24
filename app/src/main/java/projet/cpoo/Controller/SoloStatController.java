package projet.cpoo.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import projet.cpoo.GameData;

public class SoloStatController extends StatsController {

    
    @FXML
    AnchorPane anchor;

    @FXML
    GridPane gridPane;

    @FXML
    Button button;

    @FXML
    Text congrats;

    @FXML
    Text statsText;

    @FXML
    protected final void initialize() {
        button.setFocusTraversable(false);
        anchor.setFocusTraversable(true);
        anchor.requestFocus();
        setText();
    }
    

    protected final void setText() {
        super.setText();
        statsText.setText("Vous avez surv\u00e9cu "+ GameData.getTempsFinal()/10 + " secondes et atteint le niveau " + GameData.getNiveauFinal() +  " f\u00e9lictiations ");

    }

    protected final void setGraph() {
        
    }
}
