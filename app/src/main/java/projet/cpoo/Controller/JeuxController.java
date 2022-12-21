package projet.cpoo.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import projet.cpoo.App;
import projet.cpoo.GameData;
import projet.cpoo.Settings;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class JeuxController {
    private static int CHAR_PER_LINE = 40;
    private int pos = 0; 
    private int posMin = 0;
    private int motComplete = 0;
    private double entreesClavier = 0;
    private double lettresCorrectes = 0;
    private int motMax = 3;
    private int moyFluidite = 0;
    private int tmpTemps = 0;
    private int derCharUtile = 0;
    private boolean modeTemps;
    private static int TEMPS_MAX = 100;
    private int temps = TEMPS_MAX;
    private Timer timer = new Timer();
    
    
    @FXML
    private GridPane gridPane;

    @FXML
    private Text stat_mot;
    
    @FXML
    private Text stat_prec;

    @FXML
    private Text texte_restant;

    @FXML
    private Text stat_restant;

    @FXML
    private HBox ligne_1;

    @FXML
    private HBox ligne_2;

    @FXML
    private HBox ligne_3;

    @FXML
    private HBox ligne_act = ligne_1;

    private Iterator<String> stringIter;
    private String tmpIter =  null;


    @FXML
    private void initialize() {
        try {
            modeTemps = Settings.isModeTemps();
            ligne_act = ligne_1;
            BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResource("liste_mots/liste_francais.txt").openStream()));
            List<String> list = new ArrayList<String>();
            for (int i = 0 ; i < 100 ; i++) {
                String text = reader.readLine();
                list.add(text);
            }
            Collections.shuffle(list);
            //TODO Enlever les trucs qui facilitent les tests
            list = list.stream().filter((x -> x.length() < 9)).toList();
            stringIter = list.iterator();
            while(stringIter.hasNext()) {
                String text = stringIter.next();
                if(pos + text.length() > CHAR_PER_LINE) {
                    pos = 0;
                    if (!updateActualLine()) {
                        tmpIter = text;
                        break;
                    }
                }
                pos += addWordtoLine(text, ligne_act);
                ligne_act.getChildren().add(new Text(" "));
                pos++;
            }
            reader.close();
            ligne_act = ligne_1;
            if (modeTemps) {
                texte_restant.setText("Temps restant");
                timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater( () -> {
                            temps-= 0.1;
                            updateTempsRestant();
                            updateData();
                            try { 
                                finDuJeu();
                            }
                            catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                    }
                        },0,100);
                        stat_mot.setText("0");
                }
        }
        catch (Exception e) {
            System.out.println("ERROR + ligne = "+ligne_act);
            e.printStackTrace();
        }
        
    }
    private int addWordtoLine(String s,HBox line) {
        int pos = 0;
        for(char c : s.toCharArray()) {
                Text t = new Text(String.valueOf(c));
                t.getStyleClass().add("text-to-do");
                line.getChildren().add(t);
                pos++;
            }
        return pos;
    } 

    private boolean updateActualLine(){
        if (ligne_act == ligne_1) ligne_act = ligne_2;
        else if (ligne_act == ligne_2) ligne_act = ligne_3;
        else return false;
        return true;
    }

    private boolean addLine() {
        int pos = 0;
        ligne_1.getChildren().clear();
        ligne_1.getChildren().addAll(ligne_2.getChildren());
        ligne_2.getChildren().clear();
        ligne_2.getChildren().addAll(ligne_3.getChildren());
        ligne_3.getChildren().clear();
        if (tmpIter != null) {
            pos += addWordtoLine(tmpIter,ligne_3);
            ligne_act.getChildren().add(new Text(" "));
            pos++;
        }
        while(stringIter.hasNext()) {
            String text = stringIter.next();
            if(pos + text.length() > CHAR_PER_LINE) {
                tmpIter = text;                
                return true;
            }
            pos += addWordtoLine(text,ligne_3);
            ligne_act.getChildren().add(new Text(" "));
            pos++;
        }
        return false;
    }

    private boolean motCorrect(int finMot){
        int i = finMot;
        while (i >=0) {
            Text t = (Text) ligne_act.getChildren().get(i);
            System.out.print(t.getText()+" ");
            if (t.getText().equals(" ")) return true;
            if (t.getStyleClass().contains("text-error")) return false; 
            i--;
        }
        return true;
    }

    private void updatePrecision() {
        double ratio = lettresCorrectes/entreesClavier;
        stat_prec.setText(String.valueOf((int)(ratio*100) + "%"));
    }

    private void updateMotComplete() {
        stat_mot.setText(String.valueOf(motComplete));
    }

    private void updateMotRestant() {
        stat_restant.setText(String.valueOf(motMax-motComplete));
    }

    private void updateTempsRestant() {
        stat_restant.setText(String.valueOf(temps/10));
    }

    private int tempsPasse() {
        return TEMPS_MAX - temps;
    }

    private void updateData() {
        if(modeTemps) {
            int tempsPasse = tempsPasse();
            int diviseur = TEMPS_MAX/100;
            if (diviseur == 0) diviseur = 1;
            if(tempsPasse % diviseur == 0) {
                System.out.println("LC = " + lettresCorrectes);
                double ratio = lettresCorrectes/entreesClavier;
                System.out.println("Add : " + (ratio*100));
                GameData.setPrecisionList((int)(ratio*100),(tempsPasse/(TEMPS_MAX/10))-1);
            }
        }
        else {
            int diviseur =(motMax/10);
            if (diviseur == 0) diviseur = 1;
            if (motComplete % diviseur == 0) {
                double ratio = lettresCorrectes/entreesClavier;
                System.out.println("Add : " + (ratio*100));
                GameData.setPrecisionList((int)(ratio*100),(motComplete/diviseur)-1);
            }
        }
    }

    private void finDuJeu() throws IOException { 
        System.out.println("diff = " + ( motMax - motComplete) );
        if (modeTemps && temps <= 0) {
            timer.cancel();
            App.setRoot("statistiques");
        }
        else if (!modeTemps && motMax - motComplete  == 0) App.setRoot("statistiques");
    }


    @FXML
    private void keyDetect(KeyEvent e) {
        if(e.getCode().isLetterKey()) {
            entreesClavier++;
            if(pos >= CHAR_PER_LINE || pos >= ligne_act.getChildren().size() ) {
                pos = 0;
                if (!updateActualLine()) { 
                    if (!addLine()) {
                        return;
                    }
                }
                else if (ligne_act == ligne_3) {
                    addLine();
                    ligne_act = ligne_2;
                }
            }
            Text t = (Text) ligne_act.getChildren().get(pos);
            if(t.getText().equals(e.getText())) {
                t.getStyleClass().remove("text-to-do");
                t.getStyleClass().add("text-done");
                pos++;
                if (derCharUtile < pos) {
                    lettresCorrectes++;
                    derCharUtile = pos;
                    int a = tempsPasse() - tmpTemps;
                    tmpTemps = tempsPasse();
                    GameData.addFreqList(a);
                    System.out.println("temps = "+ tempsPasse() + "temps entre 2 touches = " + a);
                }
            }
            else if (t.getText().equals(" ")) {
                t.getStyleClass().remove("text-to-do");
                t.getStyleClass().add("space-error");
                pos++;
            }
            else {
                t.getStyleClass().remove("text-to-do");
                t.getStyleClass().add("text-error");
                pos++;
            }
        }
        else if(e.getCode().isWhitespaceKey()) {
            Text t = (Text) ligne_act.getChildren().get(pos);
            if(!t.getText().equals(" ")) {
                t.getStyleClass().remove("text-to-do");
                t.getStyleClass().add("text-error");
            }
            else {
                if (motCorrect(pos-1)) {
                    posMin = pos + 1;
                    motComplete++;
                    if (!modeTemps) {
                        updateData();
                    }
                }
                // lettresCorrectes++;
                updateMotComplete();
                updateMotRestant();
                try { 
                    finDuJeu();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }                
                pos++;
        } else {
            e.getCode();
            if (e.getCode().equals(KeyCode.BACK_SPACE)) {
                if(pos > 0 && pos > posMin) {
                    pos--;
                    Text t = (Text) ligne_act.getChildren().get(pos);
                    if(t.getStyleClass().contains("text-done")) lettresCorrectes--;
                    t.getStyleClass().remove("text-done");
                    t.getStyleClass().remove("text-error");
                    t.getStyleClass().remove("space-error");
                    t.getStyleClass().add("text-to-do");
                }
            }
        }
        updatePrecision();
    }
    
}
