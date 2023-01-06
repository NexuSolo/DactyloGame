package projet.cpoo.Controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import projet.cpoo.App;
import projet.cpoo.GameData;
import projet.cpoo.Settings;
import projet.cpoo.TypeMot;
import projet.cpoo.Model.SoloModel;

public class SoloController extends ControllerJeu{    
    protected int vies = 500;
    private int motRestant = 10;
    private int niveau = Settings.getNiveau();
    private boolean mortSubite = Settings.isMortSubite();
    protected List<TypeMot> listeTypeMots = new LinkedList<TypeMot>();
    protected SoloModel model;
    protected int tailleListTMP = 0;
    
    protected int nombreMotLigne_1 = 0;

    protected int nombreMotLigne_2 = 0;

    protected int nombreMotLigne_3 = 0;
    @FXML
    private Text textMotComplete;

    @FXML
    protected void initialize() {
        ligne_act = ligne_1;
        if(mortSubite) vies = 1;
        model = new SoloModel(this);
        model.initialize();
        System.out.println("init model "+ model);
        initializeText();
        // textHG.setText(" Vies restantes");
        // textHD.setText(" Mots avant prochain niveau ");
        // textHM.setText(" Niveau ");
        // updateListeMots();
        // initializeGame();
        // initialize();
        // super.initialize();
        ligne_act = ligne_1;
    }

    private final void setStats() {
        updateVies();
        updateMotNiveau();
        updateMotComplete();
        updateNiveau();
    } 

    private final void updateNiveau() {
        textBM.setText(String.valueOf(model.getNiveau()));
    }

    protected final void updateVies() {
        textBG.setText(String.valueOf(model.getVies()));
    }

    private final void updateMotNiveau() {
        textBD.setText(String.valueOf(model.getMotRestant()));
    }

    private final void updateMotComplete() {
        textMotComplete.setText("Mot compl\u00e9t\u00e9s " + String.valueOf(model.getMotComplete()));
    }


    private HBox selectLine() {
        if (nombreMotLigne_1 < 5 ) return ligne_1;
        if (nombreMotLigne_2 < 5) return ligne_2;
        return ligne_3;
    }

    protected final void removeMot(){
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



    
    protected final void validationMot(boolean solo) {
        removeMot();
        rearrangeCol();
        updateMotNiveau();
        updateVies();
    }

    private final void finDuJeu() throws IOException {
            // timer.cancel();
            // GameData.setMotComplete(motComplete);
            // GameData.setTempsFinal((System.currentTimeMillis()-temps)*0.01);
            // GameData.setNiveauFinal(niveau);
            App.setRoot("statsSolo");
    }

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
        // try {if (!jeuVide()) soin = ligne_1.getChildren().get(0).getStyleClass().contains("text-life");
        // } catch (Exception e) { e.printStackTrace();}
        return pos;
    }


    private void updateMot() {
        String mot = model.getListeMots().get(0);
        String motAct = model.getMotAct();
        boolean soin = model.getListeTypeMots().get(0) == TypeMot.WORD_LIFE;
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

    private void retireChar() {
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
            if(model.validationMot(true))validationMot(true);
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

    

    private boolean ligneFull(HBox ligne) {
        if (ligne == ligne_1) return nombreMotLigne_1 > 5;
        if (ligne == ligne_2) return nombreMotLigne_2 > 5;
        return nombreMotLigne_3 > 5;
    }

    protected final void initializeGame() {
        String text;
        TypeMot type;
        for (int i = 0; i < 5 ; i++) {
            text = model.getListeMots().get(i);
            type = model.getListeTypeMots().get(i);
            if(ligneFull(ligne_act)) {
                pos = 0;
                if (!updateActualLine()) {
                    tmpIter = text;
                    break;
                }
            }
            addWordtoLine(text, ligne_act,type == TypeMot.WORD_LIFE);
            ligne_act.getChildren().add(new Text(" "));
        }
    }
    

    @Override
    protected final void initializeGame(List<String> list) {
        String text;
        stringIter = list.iterator();
        for (int i = 0; i < 5 ; i++) {
            text = stringIter.next();
            if(ligneFull(ligne_act)) {
                if (!updateActualLine()) {
                    tmpIter = text;
                    break;
                }
            }
            int r = new Random().nextInt(10);
            TypeMot type = (r == 0)?TypeMot.WORD_LIFE:TypeMot.WORD_NEUTRAL;
            listeMots.add(text);
            listeTypeMots.add(type);
            addWordtoLine(text, ligne_act,type == TypeMot.WORD_LIFE);
            ligne_act.getChildren().add(new Text(" "));
        }
        soin = listeTypeMots.get(0) == TypeMot.WORD_LIFE;
    }

    @Override
    protected final void initializeText() {
        System.out.println("appl IT");
        textHG.setText(" Vies restantes");
        textHD.setText(" Mots avant prochain niveau ");
        textHM.setText(" Niveau ");
        setStats();
    }

    private final void decrementeNombreLigne(HBox ligne) {
        if (ligne == ligne_1) nombreMotLigne_1--;
        else if (ligne == ligne_2) nombreMotLigne_2--;
        else nombreMotLigne_3--;
    }

    private final void incrementeNombreLigne(HBox ligne) {
        if (ligne == ligne_1) {nombreMotLigne_1++;
            System.out.println("Add l1  incrNbr : " + nombreMotLigne_1 );
        }
        else if (ligne == ligne_2) nombreMotLigne_2++;
        else nombreMotLigne_3++;
    }

    private final void popMot(HBox ligne_bas,HBox ligne_haut) {
        int i = 0;
        Text t = (Text) ligne_bas.getChildren().get(i);
        List <Node> list = new LinkedList<>();
        while (!t.getText().equals(" ") || i >= ligne_bas.getChildren().size()) {
            list.add(t);
            ligne_bas.getChildren().remove(t);
            t = (Text) ligne_bas.getChildren().get(i);
        }
        ligne_bas.getChildren().remove(t); if (t.getText().equals(" ")) 
        ligne_haut.getChildren().addAll(list);
        ligne_haut.getChildren().add(new Text(" "));
        decrementeNombreLigne(ligne_bas);
        incrementeNombreLigne(ligne_haut);
    }   

    protected final void rearrangeCol() {
        if(nombreMotLigne_2 > 0) popMot(ligne_2, ligne_1);
        if(nombreMotLigne_3 > 0) popMot(ligne_3, ligne_2);
    }

    @Override
    public void update(Observable o, Object arg) {
            if (model.isEnJeu()) {
                if(model.isValidation()){validationMot(true); 
                    model.setValidation(false); }
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
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            // TODO Auto-generated method stub
        
    }

    private void updateListeMots() {
        int size = model.getListeMots().size();
        if (tailleListTMP < size) {
            List<String> toAdd = model.getListeMots().subList(tailleListTMP, size);
            List<TypeMot> toAddType = model.getListeTypeMots().subList(tailleListTMP, model.getListeTypeMots().size());
            for (int i = 0; i < toAdd.size(); i++) {
                HBox ligne = selectLine();
                addWordtoLine(toAdd.get(i), ligne, toAddType.get(i) == TypeMot.WORD_LIFE);
            }
        }
        tailleListTMP = model.getListeMots().size();
    }
}
