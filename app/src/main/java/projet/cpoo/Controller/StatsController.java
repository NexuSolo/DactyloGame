package projet.cpoo.Controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import projet.cpoo.App;
import projet.cpoo.GameData;

public abstract class StatsController {
    
    @FXML
    AnchorPane anchor;

    @FXML
    GridPane gridPane;

    @FXML
    Button button;

    @FXML
    Text congrats; 

    @FXML 
    protected void initialize() {
        button.setFocusTraversable(false);
        anchor.setFocusTraversable(true);
        anchor.requestFocus();
        setGraph();
        setText();
    }

    /**
     * Cette fonction appelle toutes les fonctions de graphiques à créer pour 
     * l'écran de statistiques.
     */
    protected abstract void setGraph();

   /**
    * Cette fonction écrit le texte de l'étiquette de félicitations sur une chaîne contenant le
    * nombre de mots saisis, le temps nécessaire pour les saisir et le nombre de mots par minute.
    */
    protected void setText(){
        int mots = GameData.getMotComplete();
        int temps = (int)(GameData.getTempsFinal()/10);
        int mpm = (int) ((60*mots)/temps);
        String s = "Bravo vous avez tape " + mots + " mots en " + temps + " secondes";
        s += "\nCela fait donc " + mpm + " mots par minute"; 
        congrats.setText(s);
    }

    @FXML
    public void retour() throws IOException {
        GameData.resetAll();
        App.setRoot("menu");
    }
    @FXML
    public void keyPressed(KeyEvent e) throws IOException {
        if (e.getCode().equals(KeyCode.ESCAPE)) {
            retour();
        }    
    }
}
