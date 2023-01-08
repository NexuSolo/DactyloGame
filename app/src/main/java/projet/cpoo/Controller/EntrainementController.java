package projet.cpoo.Controller;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import projet.cpoo.App;
import projet.cpoo.Model.EntrainementModel;

import java.io.*;
import java.util.List;
import java.util.Observable;



public class EntrainementController extends ControllerJeu {
    private int tmpMots = 0;

    /* (non-Javadoc)
     * @see projet.cpoo.Controller.ControllerJeu#initialize()
     */
    @FXML
    protected final void initialize() {
        ligne_act = ligne_1;
        model = new EntrainementModel(this);
        model.initialize();
        initializeText();
    }

    /* (non-Javadoc)
     * @see projet.cpoo.Controller.ControllerJeu#initializeText()
     */
    protected final void initializeText() {
        if(((EntrainementModel)model).isModeTemps()) textHG.setText("Mot compl\u00e9t\u00e9s");
        else textHG.setText("Temps pass\u00e9");
        textHM.setText("Pr\u00e9cision");
        if(((EntrainementModel) model).isModeTemps()){
            updateTempsRestant();
            textHD.setText("Temps restant");
        }
        else {
            updateMotRestant();
            textHD.setText("Mots restants");
        }
    }



    /* (non-Javadoc)
     * @see projet.cpoo.Controller.ControllerJeu#addWordtoLine(java.lang.String, javafx.scene.layout.HBox, boolean)
     */
    protected int addWordtoLine(String s,HBox line,boolean soin) {
        int pos = 0;
        for(char c : s.toCharArray()) {
                Text t = new Text(String.valueOf(c));
                t.getStyleClass().add("text-to-do");
                if (soin) t.getStyleClass().add("text-life");
                line.getChildren().add(t);
                pos++;
        }
        line.getChildren().add(new Text(" "));
        if (line == ligne_1) nombreMotLigne_1++;
        else if (line == ligne_2) nombreMotLigne_2++;
        else nombreMotLigne_3++;
        return pos;
    }

    private final void updatePrecision() {
        double ratio = ((EntrainementModel) model).getLettresCorrectes()/((EntrainementModel) model).getEntreesClavier();
        textBM.setText(String.valueOf((int)(ratio*100) + "%"));
    }

    private final void updateMotComplete() {
        textBG.setText(String.valueOf(((EntrainementModel) model).getMotCorrect()));
    }
    private final void updateTempsPasse() {
        textBG.setText(String.valueOf((int)(model.getTemps() * 0.1)));
    }

    private final void updateMotRestant() {
        textBD.setText(String.valueOf(((EntrainementModel) model).getMotMax() - ((EntrainementModel) model).getMotCorrect()));
    }

    private final void updateTempsRestant() {
        textBD.setText(String.valueOf((((EntrainementModel) model).getTEMPS_MAX()-((EntrainementModel) model).getTemps())/10));
    }

    /**
     * Met fin au jeu et passe à l'écran de fin 
     * @throws IOException
     */
    private final void finDuJeu() throws IOException {
        App.setRoot("statsEntrainement");
    }

    /**
     * Prend un appui de touche, le compare avec le caractère qui est attendu et met à jour l'affichage en conséquence
     * @param e l'évenement qui correspond à l'appui de touche
     */
    public void keyDetect(KeyEvent e) {
        if(!model.isStart()) {
            model.timerStart();
        }
        if(e.getCode().isLetterKey() || isAccentedChar(inputToChars(e)) || inputToChars(e) == "-"){
            String s = formatString(e.getText(),e.isShiftDown());
            model.ajoutChar(s);
            circonflexe = false;
            trema = false;
        }
        else if(e.getCode() == KeyCode.SPACE) {
            circonflexe = false;
            trema = false;
            if(model.validationMot()) validationMot();
            return;
        }
        else {
            circonflexe = false;
            trema = false;
            if (e.getCode().equals(KeyCode.BACK_SPACE)) {
                model.retireChar();
                retireChar();
            } else isAccent(e);
        }
        updateMot();
    }

    /**
     * Met à jour le texte dans l'interface graphique
     */
    private void updateMot() {
        String mot = model.getListeMots().get(((EntrainementModel) model).getPosMin());
        String motAct = model.getMotAct();
        for (int i = 0; i < mot.length(); i++) {
            HBox ligne;
            int nbMot = model.getMotComplete();
            if (nbMot < 5) ligne = ligne_1;
            else ligne = ligne_2;
            int pos = ((EntrainementModel) model).getDerCharUtile() + i;
            if (pos >= ligne.getChildren().size()) return;
            Text t = (Text) ligne.getChildren().get(pos);
            t.getStyleClass().clear();
            if (i >= motAct.length()) {
                    t.getStyleClass().add("text-to-do");
            }
            else {
                t.getStyleClass().remove("text-to-do");
                String s1 = motAct.substring(i, i + 1);
                String s2 = mot.substring(i, i + 1);
                if(!s1.equals(s2)) {
                    t.getStyleClass().add("text-error");
                }
                else {
                    t.getStyleClass().add("text-done");
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see projet.cpoo.Controller.ControllerJeu#validationMot(boolean)
     */
    @Override
    protected final void validationMot() {
        return;
    }

    @Override
    // Mise à jour de la vue.
    public void update(Observable o, Object arg) {
        if (model.isEnJeu()) {
            updateActualLine();
            int nbMot = model.getMotComplete();
            if (nbMot >= 5 && ligne_act != ligne_2) updateActualLine();
            if (nbMot >= 10 && nbMot % 5 == 0 && tmpMots != nbMot) addLine();
            updateListeMots();
            tmpMots = nbMot;
            if(((EntrainementModel) model).isModeTemps()){
                updateMotComplete();
                updateTempsRestant();
            }
            else{ 
                updateMotRestant();
                updateTempsPasse();
            }
            updatePrecision();
        }
        else {
            try {
                finDuJeu();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Ajoute des mots à l'affichage graphique
     */
    private void updateListeMots() {
        int size = model.getListeMots().size();
        if (tailleListTMP < size) {
            List<String> toAdd = model.getListeMots().subList(tailleListTMP, size);
            for (int i = 0; i < toAdd.size(); i++) {
                HBox ligne = selectLine();
                addWordtoLine(toAdd.get(i), ligne, false);
            }
        }
        tailleListTMP = model.getListeMots().size();
    }

    /**
     * Il supprime le dernier caractère du mot actuel et change la couleur du texte à la couleur par
     * défaut
     */
    protected final void retireChar() {
        if (motAct.length() > 0) {
            HBox ligne;
            int nbMot = model.getMotComplete();
            if (nbMot < 5) ligne = ligne_1;
            else if (nbMot < 10) ligne = ligne_2;
            else ligne = ligne_3;
            Text t = (Text) ligne.getChildren().get(((EntrainementModel) model).getDerCharUtile() + motAct.length());
            t.getStyleClass().remove("text-done");
            t.getStyleClass().remove("text-error");
            t.getStyleClass().remove("space-error");
            t.getStyleClass().add("text-to-do");
        }
    }
}
