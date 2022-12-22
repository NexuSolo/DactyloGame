package projet.cpoo.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
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

import javax.swing.text.html.HTMLDocument.HTMLReader.CharacterAction;


public class JeuxController {
    private static int CHAR_PER_LINE = 10;
    private int pos = 0;
    private int posMin = 0;
    private int motComplete = 0;
    private double entreesClavier = 0;
    private double lettresCorrectes = 0;
    private int motMax = 3;
    private int tmpTemps = 0;
    private int derCharUtile = 0;
    private boolean modeTemps;
    private static int TEMPS_MAX = Settings.getTEMPS_MAX();
    private int temps = TEMPS_MAX;
    private Timer timer = new Timer();
    private boolean start = false;
    private boolean circonflexe = false;
    private boolean trema = false;


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
            // pos += addWordtoLine("ï", ligne_act);
            // ligne_act.getChildren().add(new Text(" "));
            for (int i = 0 ; i < 100 ; i++) {
                String text = new String(reader.readLine().getBytes(),"UTF-8");
                list.add(text);
            }
            System.out.println("et é");
            // Collections.shuffle(list);
            //TODO Enlever les trucs qui facilitent les tests
                // list = list.stream().filter((x -> x.length() < 9)).toList();
            stringIter = list.iterator();
            while(stringIter.hasNext()) {
                //faire un string text qui est egal a stringIter.next() avec le codage utf-8
                String text = new String(stringIter.next().getBytes(),"UTF-8");

                // for (char c : text.toCharArray()) {
                //     String s = new String(Character.toString(c).getBytes(), "UTF-8");
                //     System.out.print(" " + s);
                // }
                System.out.println();
                System.out.println(text);
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

        }
        catch (Exception e) {
            System.out.println("ERROR + ligne = "+ligne_act);
            e.printStackTrace();
        }

    }

    private void timerStart() {
        start = true;
        if (modeTemps) {
            texte_restant.setText("Temps restant");
            timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater( () -> {
                        temps-= 0.1;
                        updateTempsRestant();
                        if (tempsPasse()% (TEMPS_MAX/10) == 0) updateData();
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
    private int addWordtoLine(String s,HBox line) {
        int pos = 0;
        for(char c : s.toCharArray()) {
                Text t = new Text(String.valueOf(c));
                // System.out.println("char = " + String.valueOf(c));
                t.setFont(new Font("Arial",12));
                t.getStyleClass().add("text-to-do");
                line.getChildren().add(t);
                pos++;
            }
        return pos;
    }

    private boolean updateActualLine(){
        pos = 0;
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
            if (t.getStyleClass().contains("text-error") || t.getStyleClass().contains("text-skipped")) return false;
            i--;
        }
        return true;
    }

    private void updatePrecision() {
        double ratio = lettresCorrectes/entreesClavier;
        // System.out.println("Prec : " + (ratio*100) + " LC " + (lettresCorrectes) + " EC "+(entreesClavier));
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
                double ratio = lettresCorrectes/entreesClavier;
                System.out.println("case print : " + (tempsPasse/(TEMPS_MAX/10) -1));
                GameData.setPrecisionList((int)(ratio*100),tempsPasse/(TEMPS_MAX/10) - 1);
                GameData.setWordList(motComplete,tempsPasse/(TEMPS_MAX/10) - 1);
            }
        }
        else {
            int diviseur =(motMax/10);
            if (diviseur == 0) diviseur = 1;
            if (motComplete % diviseur == 0) {
                double ratio = lettresCorrectes/entreesClavier;
                GameData.setPrecisionList((int)(ratio*100),(motComplete/diviseur)-1);
            }
        }
    }

    private int skipMot() {
        int tmp = pos;
        while (tmp <= CHAR_PER_LINE) {
            Text t = (Text) ligne_act.getChildren().get(tmp);
            t.getStyleClass().remove("text-to-do");
            t.getStyleClass().add("text-skipped");
            System.out.print(" " + t.getText());
            if (t.getText().equals(" ")) return tmp;
            tmp++;
        }
        return tmp;
    }

    private void finDuJeu() throws IOException {
        if (modeTemps && temps <= 0) {
            timer.cancel();
            GameData.setMotComplete(motComplete);
            App.setRoot("statistiques");
        }
        else if (!modeTemps && motMax - motComplete  == 0) App.setRoot("statistiques");
    }


    private boolean isAccentedChar(String s) {
        switch (s) {
            case "à" : return true;
            case "ç" : return true;
            case "é" : return true;
            case "è" : return true;
            case "ù" : return true;
            default : return false;
        }
    }

    private void isAccent(KeyCode code) {
        switch (code) {
            case DEAD_CIRCUMFLEX : circonflexe = true; break;
            case DEAD_DIAERESIS : trema = true;
            default : break;
        }
    }

    private String formatString(String text) {
        switch (text) {
            case "a" : if (circonflexe){
                System.out.println("vyuijopk â é" +'\u00a9');
                return "â";
            }
            else if (trema) return "ä";
            break;
            case "e" : if (circonflexe) return "ê";
            else if (trema) return "ë";
            break;
            case "u" : if (circonflexe) return "û";
            else if (trema) return "ü";
            break;
            case "i" : if (circonflexe) return "î";
            else if (trema) return "ï";
            break;
            case "o" : if (circonflexe) return "ô";
            else if (trema) return "ö";
            break;
            default : break;
        }
        return text;
    }

    @FXML
    private void keyDetect(KeyEvent e) {
        // System.out.println(" char = " + formatString(e.getText()));
        if(e.getCode().isLetterKey() || isAccentedChar(e.getCharacter())) {
            if (!start) timerStart();
            entreesClavier++;
            if(pos >= CHAR_PER_LINE || pos >= ligne_act.getChildren().size() ) {
                pos = 0;
                posMin = 0;
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
            System.out.println("T text = " + t.getText() + " char = " + formatString(e.getText()));
            if(t.getText().equals(formatString(e.getText()))) {
                t.getStyleClass().remove("text-to-do");
                t.getStyleClass().add("text-done");
                pos++;
                if (derCharUtile < pos) {
                    lettresCorrectes++;
                    derCharUtile = pos;
                    int a = tempsPasse() - tmpTemps;
                    tmpTemps = tempsPasse();
                    GameData.addFreqList(a);
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
            circonflexe = false;
            trema = false;
        }
        else if(e.getCode().isWhitespaceKey()) {
            circonflexe = false;
            trema = false;
            Text t = (Text) ligne_act.getChildren().get(pos);
            if(!t.getText().equals(" ")) {
                // t.getStyleClass().remove("text-to-do");
                int tmp = skipMot();
                System.out.println("Tmp " + tmp);
                pos = tmp;
                System.out.println("pos min = " + posMin) ;
                // t.getStyleClass().add("text-error");
            }
            else {
                if (motCorrect(pos-1)) {
                    posMin = pos + 1;
                    motComplete++;
                    if (!modeTemps) {
                        updateData();
                    }
                }
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
            circonflexe = false;
            trema = false;
            if (e.getCode().equals(KeyCode.BACK_SPACE)) {
                System.out.println("Pos = " + pos + "Pos min = " + posMin);
                if(pos > 0 && pos > posMin) {
                    pos--;
                    Text t = (Text) ligne_act.getChildren().get(pos);
                    if(t.getStyleClass().contains("text-done")) lettresCorrectes--;
                    t.getStyleClass().remove("text-skipped");
                    t.getStyleClass().remove("text-done");
                    t.getStyleClass().remove("text-error");
                    t.getStyleClass().remove("space-error");
                    t.getStyleClass().add("text-to-do");
                }
            }
            else isAccent(e.getCode());
        }
        updatePrecision();
    }

}
