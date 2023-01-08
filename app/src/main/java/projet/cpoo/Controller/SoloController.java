package projet.cpoo.Controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import projet.cpoo.App;
import projet.cpoo.Settings;
import projet.cpoo.TypeMot;
import projet.cpoo.Model.SoloModel;

public class SoloController extends ControllerJeu{    
    protected int vies = 50;
    private boolean mortSubite = Settings.isMortSubite();
    protected List<TypeMot> listeTypeMots = new LinkedList<TypeMot>();
    
    
    @FXML
    private Text textMotComplete;

    @FXML
    // Méthode appelée lors de la création du contrôleur.
    protected void initialize() {
        ligne_act = ligne_1;
        if(mortSubite) vies = 1;
        model = new SoloModel(this);
        model.initialize();
        initializeText();
        ligne_act = ligne_1;
    }
    
    @Override
    protected final void initializeText() {
        textHG.setText(" Vies restantes");
        textHD.setText(" Mots avant prochain niveau ");
        textHM.setText(" Niveau ");
        setStats();
    }

    /**
     * Cette fonction met à jour les statistiques du jeu.
     */
    protected void setStats() {
        updateVies();
        updateMotNiveau();
        updateMotComplete();
        updateNiveau();
    } 

    private final void updateNiveau() {
        textBM.setText(String.valueOf(((SoloModel) model).getNiveau()));
    }

    protected  void updateVies() {
        textBG.setText(String.valueOf(((SoloModel) model).getVies()));
    }

    protected void updateMotNiveau() {
        textBD.setText(String.valueOf(((SoloModel) model).getMotRestant()));
    }

    private final void updateMotComplete() {
        textMotComplete.setText("Mot compl\u00e9t\u00e9s " + String.valueOf(model.getMotComplete()));
    }

    private final void decrementeNombreLigne(HBox ligne) {
        if (ligne == ligne_1) nombreMotLigne_1--;
        else if (ligne == ligne_2) nombreMotLigne_2--;
        else nombreMotLigne_3--;
    }

    private final void incrementeNombreLigne(HBox ligne) {
        if (ligne == ligne_1) {nombreMotLigne_1++;
        }
        else if (ligne == ligne_2) nombreMotLigne_2++;
        else nombreMotLigne_3++;
    }

    /**
     * Il supprime le premier mot d'une ligne et l'ajoute à la ligne précédente
     * 
     * @param ligne_bas la ligne du bas
     * @param ligne_haut la ligne du haut
     */
    private final void popMot(HBox ligne_bas,HBox ligne_haut) {
        Text t = (Text) ligne_bas.getChildren().get(0);
        List <Node> list = new LinkedList<>();
        while (!t.getText().equals(" ") || 0 >= ligne_bas.getChildren().size()) {
            list.add(t);
            ligne_bas.getChildren().remove(t);
            t = (Text) ligne_bas.getChildren().get(0);
        }
        ligne_bas.getChildren().remove(t); if (t.getText().equals(" ")) 
        ligne_haut.getChildren().addAll(list);
        ligne_haut.getChildren().add(new Text(" "));
        decrementeNombreLigne(ligne_bas);
        incrementeNombreLigne(ligne_haut);
    }   
    /**
     * Fait reculer les mots dans l'affichage en faisant passer le premier de la ligne vers la ligne du haut
     * 
     */
    protected final void rearrangeCol() {
        if(nombreMotLigne_2 > 0) popMot(ligne_2, ligne_1);
        if(nombreMotLigne_3 > 0) popMot(ligne_3, ligne_2);
    }

    /**
     * Supprime le mot de l'écran, réorganise les colonnes, met à jour le niveau et les vies.
     */
    protected void validationMot() {
        removeMot();
        rearrangeCol();
        updateMotNiveau();
        updateVies();
    }

    /**
     * Passe à l'écran de fin du jeu
     */
    private final void finDuJeu() throws IOException {
        App.setRoot("statsSolo");
    }
    
    // Ajouter un mot à une ligne.
    protected int addWordtoLine(String s,HBox line,boolean soin) {
        if(mortSubite) soin = false;
        nombreMots++;
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


    /**
    * Met à jour le texte dans l'interface graphique
    */
    private void updateMot() {
        String mot = model.getListeMots().get(0);
        String motAct = model.getMotAct();
        boolean soin = ((SoloModel) model).getListeTypeMots().get(0) == TypeMot.WORD_LIFE;
        for (int i = 0; i < mot.length(); i++) {
            Text t = (Text) ligne_1.getChildren().get(i);
            t.getStyleClass().clear();
            if (i >= motAct.length()) {
                if(soin) {
                    t.getStyleClass().add("text-life");
                }
                else {
                    t.getStyleClass().add("text-to-do");
                }
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

    /**
     * Supprime le premier mot de la première ligne
     */
    protected void removeMot(){
        int i = 0;
        Text t = (Text) ligne_1.getChildren().get(i);
        while (!t.getText().equals(" ")) {
            t.getStyleClass().clear();
            i++;
            t = (Text) ligne_1.getChildren().get(i);
        }
        ligne_1.getChildren().remove(0, i+1);
        nombreMotLigne_1--;
    }

    private void updateListeMots() {
        int size = model.getListeMots().size();
        if (tailleListTMP < size) {
            List<String> toAdd = model.getListeMots().subList(tailleListTMP, size);
            List<TypeMot> toAddType = ((SoloModel) model).getListeTypeMots().subList(tailleListTMP, ((SoloModel) model).getListeTypeMots().size());
            for (int i = 0; i < toAdd.size(); i++) {
                HBox ligne = selectLine();
                addWordtoLine(toAdd.get(i), ligne, toAddType.get(i) == TypeMot.WORD_LIFE);
            }
        }
        tailleListTMP = model.getListeMots().size();
    }

    /**
     * Prend un appui de touche, le compare avec le caractère qui est attendu et met à jour l'affichage en conséquence
     * @param e l'évenement qui correspond à l'appui de touche
     */
    public void keyDetect(KeyEvent e) {
        if(!model.isStart()) {
            model.timerStart();
            model.setTemps(System.currentTimeMillis());
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

    @Override
    // Mise à jour de la vue.
    public void update(Observable o, Object arg) {
        if (model.isEnJeu()) {
            if(model.isValidation()){
                validationMot(); 
                model.setValidation(false);
            }
            updateListeMots();
            updateMot();
            updateMotComplete();
            updateMotNiveau();
            updateVies();
            updateNiveau();
        }
        else {
            try {
                finDuJeu();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
