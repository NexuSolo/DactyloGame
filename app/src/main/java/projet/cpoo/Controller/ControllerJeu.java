package projet.cpoo.Controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observer;
import java.util.Timer;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import projet.cpoo.Model.JeuModel;

public abstract class ControllerJeu implements Observer {
    protected int pos = 0;
    protected int motComplete = 0;
    protected long temps = 0;
    protected Timer timer = new Timer();
    protected int vies = 50;
    protected int nombreMots = 0;
    protected int motRestant = 1;
    protected boolean start = false;
    protected boolean circonflexe = false;
    protected boolean trema = false;
    protected boolean soin = false;
    protected boolean firstTry = true;
    protected List<String> dictionnaire;
    protected List<String> listeMots = new ArrayList<String>();
    protected String motAct = "";
    protected Iterator<String> stringIter;
    protected int tailleListTMP = 0;
    protected int nombreMotLigne_1 = 0;
    protected int nombreMotLigne_2 = 0;
    protected int nombreMotLigne_3 = 0;
    protected JeuModel model = null;


    @FXML
    protected HBox ligne_1;

    @FXML
    protected HBox ligne_act = ligne_1;

    @FXML
    protected HBox ligne_2;

    @FXML
    protected HBox ligne_3;

    @FXML
    protected Text textHG;

    @FXML
    protected Text textHM;

    @FXML
    protected Text textHD;

    @FXML
    protected Text textBG;

    @FXML
    protected Text textBM;

    @FXML
    protected Text textBD;



    /**
     * Fonction d'initialisation de la liste des mots pour la partie en cours
     * Cette liste est choisie en fonction de la langue et de la présence des accents
     * Ces options sont toutes les deux sélectionnables dans les Options 
     */
    @FXML
    protected abstract void initialize();


    //Fonction abstraite qui initialise les textes de statistiques
    protected abstract void initializeText();

    //Fonction qui prend un charactère et le modifie si certaines conditions sont remplies
    protected final String formatString(String text,boolean shift) {
        if (shift) return toAccent(text).toUpperCase();
        else return toAccent(text);
    }

    //Fonction change quelle ligne référence l'attribut "ligne_act"
    protected final boolean updateActualLine(){
        if (ligne_act == ligne_1) ligne_act = ligne_2;
        else if (ligne_act == ligne_2) ligne_act = ligne_3;
        else return false;
        return true;
    }

    //Fonction abstraite qui a pour but de traiter le mot actuel et de passer au mot suivant
    protected abstract void validationMot();

    //Fonction qui prend une entrée de clavier et renvoie un String correspondant au charactère voulu
    protected final String inputToChars(KeyEvent e) {
        switch(e.getCode()) {
            case DIGIT2 : return "\u00e9";
            case DIGIT7 : return "\u00e8";
            case DIGIT9 : return "\u00e7";
            case DIGIT0 : return "\u00e0";
            case DIGIT6 : return  "-";
            case SUBTRACT : return  "-";
            case UNDEFINED : return "\u00f9";
            default : return e.getCharacter();
        }
    }

    //Fonction qui met à jour les booleens d'accents en fonction de la touche appuyée
    protected final void isAccent(KeyEvent e) {
        KeyCode code = e.getCode();
        switch (code) {
            case DEAD_CIRCUMFLEX : if (!e.isShiftDown()) circonflexe = true;
            else trema = true; break;
            case DEAD_DIAERESIS : trema = true;
            default : break;
        }
    }

    //Fonction qui prend un charactère et renvoie sa version avec accent si la touche précédente était un accent
    protected final String toAccent(String text) {
        switch (text) {
            case "a" : if (circonflexe) return "\u00e2";
            else if (trema) return "\u00e4";
            break;
            case "e" : if (circonflexe) return "\u00ea";
            else if (trema) return "\u00eb";
            break;
            case "u" : if (circonflexe) return "\u00fb";
            else if (trema) return "\u00fc";
            break;
            case "i" : if (circonflexe) return "\u00ee";
            else if (trema) return "\u00ef";
            break;
            case "o" : if (circonflexe) return "\u00f4";
            else if (trema) return "\u00f6";
            break;
            default : break;
        }
        return text;
    }

    /**
     * Fonction qui rajoute un mot à la ligne d'affichage
     * @param s String a ajouter
     * @param line HBox d'affichage dans laquelle le mot va être ajouté
     * @param soin booléen pour la coloration du mot 
     */
    protected abstract int addWordtoLine(String s,HBox line,boolean soin);

    /**
     * Fonction qui fait défiler du jeu et affiche la prochaine ligne de texte 
     * @return true une ligne a pu être ajoutée et remplie et false sinon
     */
    protected final boolean addLine() {
        ligne_1.getChildren().clear();
        ligne_1.getChildren().addAll(ligne_2.getChildren());
        ligne_2.getChildren().clear();
        ligne_2.getChildren().addAll(ligne_3.getChildren());
        ligne_3.getChildren().clear();
        return false;
    }


    /** Prend une chaine de caractère et dit si elle correspond à un des caractères {é,è,à,ç,ù}
     * @param s La chaine de caractère a tester
     * @return true si s appartient au caractères mentionné
     */
    protected final boolean isAccentedChar(String s) {
        switch (s) {
            case "\u00f9" : return true;
            case "\u00e0" : return true;
            case "\u00e9" : return true;
            case "\u00e8" : return true;
            case "\u00e7" : return true;
            default : return false;
        }
    }

    // Retire les classes css d'un caractère.
    protected void retireChar() {
        String motAct = model.getMotAct();
        if (motAct.length() > 0) {
            Text t = (Text) ligne_1.getChildren().get(pos);
            t.getStyleClass().remove("text-skipped");
            t.getStyleClass().remove("text-done");
            t.getStyleClass().remove("text-error");
            t.getStyleClass().remove("space-error");
            t.getStyleClass().add("text-to-do");
            if (soin) t.getStyleClass().add("text-life");
        }
    }

    // Une méthode qui renvoie la HBox adéquate en fonction du nombre de mot par ligne.
    protected HBox selectLine() {
        if (nombreMotLigne_1 < 5 ) return ligne_1;
        if (nombreMotLigne_2 < 5) return ligne_2;
        return ligne_3;
    }

}
