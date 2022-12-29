package projet.cpoo.Controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
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

public class SoloController extends ControllerJeu{    
    protected int vies = 50;
    private int nombreMots = 0;
    private int motRestant = 10;
    private int niveau = Settings.getNiveau();
    
    protected int nombreMotLigne_1 = 0;

    protected int nombreMotLigne_2 = 0;

    protected int nombreMotLigne_3 = 0;
    @FXML
    private Text textMotComplete;

    @FXML
    protected void initialize() {
        ligne_act = ligne_1;
        super.initialize();
        ligne_act = ligne_1;
        pos = 0;

    }

    private void upNiveau() {
        niveau++;
        updateNiveau();
        timer.cancel();
        timer = new Timer();
        timerStart();

    }


    private void setStats() {
        updateVies();
        updateMotNiveau();
        updateMotComplete();
        updateNiveau();
    } 

    private void updateNiveau() {
        textBM.setText(String.valueOf(niveau));
    }

    protected final void updateVies() {
        textBG.setText(String.valueOf(vies));
    }

    private void updateMotNiveau() {
        textBD.setText(String.valueOf(motRestant));
    }

    private void updateMotComplete() {
        textMotComplete.setText("Mot compl\u00e9t\u00e9s " + String.valueOf(motComplete));
    }

    private void timerStart() {
        start = true;
        double coeff = 3000 *  Math.pow(0.9,niveau);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater( () -> {
                    
                if (stringIter.hasNext()) {
                    String s = stringIter.next();
                    int r = new Random().nextInt(10);
                    HBox ligne = selectLine();
                    addWordtoLine(s, ligne,r == 0);
                    ligne.getChildren().add(new Text(" "));
                }  
                });
            }
        },(int) coeff,(int)coeff);
    }

    private HBox selectLine() {
        if (nombreMotLigne_1 < 5 ) return ligne_1;
        if (nombreMotLigne_2 < 5) return ligne_2;
        return ligne_3;
    }

    protected boolean jeuVide() {
        return ligne_1.getChildren().size() == 0;
    }

    protected boolean motDegats(){
        int i = 0;
        Text t = (Text) ligne_1.getChildren().get(i);
        int tmpVie = vies;
        while (!t.getText().equals(" ")) {
            if (firstTry && soin && t.getStyleClass().contains("text-done")) vies++;
            if (!t.getStyleClass().contains("text-done")) vies--;
            i++;
            t = (Text) ligne_1.getChildren().get(i);
        }
        return tmpVie <= vies;
    }

    
    protected void removeMot(){
        int i = 0;
        Text t = (Text) ligne_1.getChildren().get(i);
        while (!t.getText().equals(" ")) {
            i++;
            t = (Text) ligne_1.getChildren().get(i);
        }
        ligne_1.getChildren().remove(0, i+1);
        nombreMotLigne_1--;
    }

    protected void remplirMots() {
        if (nombreMots < 8) {
            int r = new Random().nextInt(10);
            HBox ligne = selectLine();
            addWordtoLine(stringIter.next(), ligne,r==0);
            ligne.getChildren().add(new Text(" "));
        }
    }

    protected void incrementeMotComplete(boolean b) {
        if (b) {
            motRestant--;
            motComplete++;
            updateMotComplete();
        }
    }

    protected void monteeNiveau() {
        if(motRestant <= 0) {
            motRestant = 10;
            upNiveau();
        }
    }

    protected void resetPos(){
        pos = 0;
        firstTry = true;
        if(ligne_1.getChildren().size() != 0) soin = ligne_1.getChildren().get(0).getStyleClass().contains("text-life");
    }

    protected void end() {
        if (vies <= 0) {
            try {
                finDuJeu();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    protected void validationMot(boolean solo) {
        if(jeuVide()) return;
        nombreMots--;
        boolean b = motDegats();
        removeMot();
        remplirMots();
        incrementeMotComplete(b);
        rearrangeCol();
        monteeNiveau();
        resetPos();
        updateMotNiveau();
        updateMotNiveau();
        updateVies();
        end();
    }

    private void finDuJeu() throws IOException {
            timer.cancel();
            GameData.setMotComplete(motComplete);
            GameData.setTempsFinal((System.currentTimeMillis()-temps)*0.01);
            GameData.setNiveauFinal(niveau);
            App.setRoot("statsSolo");
    }

    protected int addWordtoLine(String s,HBox line,boolean soin) {
        if (nombreMots >= 15) validationMot(true);
        nombreMots++;
        int pos = 0;
        for(char c : s.toCharArray()) {
                Text t = new Text(String.valueOf(c));
                t.getStyleClass().add("text-to-do");
                if (soin) t.getStyleClass().add("text-life");
                line.getChildren().add(t);
                pos++;
        }
        if (line == ligne_1) nombreMotLigne_1++;
        else if (line == ligne_2) nombreMotLigne_2++;
        else nombreMotLigne_3++;
        try {if (!jeuVide()) soin = ligne_1.getChildren().get(0).getStyleClass().contains("text-life");
        } catch (Exception e) { e.printStackTrace();}
        return pos;
    }


    public void keyDetect(KeyEvent e) {
        if(!start) {
            timerStart();
            temps = System.currentTimeMillis();
        }
        if(e.getCode().isLetterKey() || isAccentedChar(inputToChars(e)) || inputToChars(e) == "-"){
            Text t = (Text) ligne_1.getChildren().get(pos);
            if(t.getText().equals(formatString(e.getText(),e.isShiftDown()))) {
                t.getStyleClass().remove("text-to-do");
                t.getStyleClass().remove("text-life");
                t.getStyleClass().add("text-done");
                pos++;
            }
            else if (t.getText().equals(" ")) {
                validationMot(true);
                pos = 0;
            }
            else {
                firstTry = false;
                t.getStyleClass().remove("text-to-do");
                t.getStyleClass().remove("text-life");
                t.getStyleClass().add("text-error");
                pos++;
            }
            circonflexe = false;
            trema = false;
        }
        else if(e.getCode() == KeyCode.SPACE){
            circonflexe = false;
            trema = false;
            validationMot(true);
            pos = 0;
        }
        else {
            circonflexe = false;
            trema = false;
            if (e.getCode().equals(KeyCode.BACK_SPACE)) {
                if(pos > 0) {
                    firstTry = false;
                    pos--;
                    Text t = (Text) ligne_1.getChildren().get(pos);
                    t.getStyleClass().remove("text-skipped");
                    t.getStyleClass().remove("text-done");
                    t.getStyleClass().remove("text-error");
                    t.getStyleClass().remove("space-error");
                    t.getStyleClass().add("text-to-do");
                    if (soin) t.getStyleClass().add("text-life");
                }
            }
            else isAccent(e);
        }
    }

    private boolean ligneFull(HBox ligne) {
        if (ligne == ligne_1) return nombreMotLigne_1 > 5;
        if (ligne == ligne_2) return nombreMotLigne_2 > 5;
        return nombreMotLigne_3 > 5;
    }

    

    @Override
    protected void initializeGame(List<String> list) {
        String text;
        stringIter = list.iterator();
        for (int i = 0; i < 5 ; i++) {
            text = stringIter.next();
            if(ligneFull(ligne_act)) {
                pos = 0;
                if (!updateActualLine()) {
                    tmpIter = text;
                    break;
                }
            }
            int r = new Random().nextInt(10);
            pos += addWordtoLine(text, ligne_act,r==0);
            ligne_act.getChildren().add(new Text(" "));
            pos++;
        }
    }

    @Override
    protected void initializeText() {
        textHG.setText(" Vies restantes");
        textHD.setText(" Mots avant prochain niveau ");
        textHM.setText(" Niveau ");
        setStats();
    }

    private void decrementeNombreLigne(HBox ligne) {
        if (ligne == ligne_1) nombreMotLigne_1--;
        else if (ligne == ligne_2) nombreMotLigne_2--;
        else nombreMotLigne_3--;
    }

    private void incrementeNombreLigne(HBox ligne) {
        if (ligne == ligne_1) {nombreMotLigne_1++;
            System.out.println("Add l1  incrNbr : " + nombreMotLigne_1 );
        }
        else if (ligne == ligne_2) nombreMotLigne_2++;
        else nombreMotLigne_3++;
    }

    private void popMot(HBox ligne_bas,HBox ligne_haut) {
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

    protected void rearrangeCol() {
        if(nombreMotLigne_2 > 0) popMot(ligne_2, ligne_1);
        if(nombreMotLigne_3 > 0) popMot(ligne_3, ligne_2);
    }
}
