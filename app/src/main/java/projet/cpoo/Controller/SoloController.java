package projet.cpoo.Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import projet.cpoo.Settings;

public class SoloController {
    
    private int pos = 0;
    private int motComplete = 0;
    private int temps = 0;
    private Iterator<String> stringIter;
    private Timer timer = new Timer();
    private int vies = 50;
    private int nombreMots = 0;
    private int motRestant = 1;
    private int niveau = Settings.getNiveau();
    private boolean start = false;
    private boolean circonflexe = false;
    private boolean trema = false;
    private boolean soin = false;
    private boolean firstTry = true;


    @FXML
    HBox ligne;

    @FXML
    Text nbVies;
    
    @FXML
    Text numNiveau;

    @FXML
    Text motProchainNiv;


    @FXML
    private void initialize() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResource("liste_mots/liste_francais.txt").openStream()));
            List<String> list = new ArrayList<String>();
            String text = reader.readLine();
            while (text != null) {
                text = new String(text.getBytes(),"UTF-8");
                list.add(text);
                text = reader.readLine();
            }
            stringIter = list.iterator();
            Collections.shuffle(list);
            int r = new Random().nextInt(10);
            for (int i = 0; i < 3 ; i++) {
                text = stringIter.next();
                pos += addWordtoLine(text, ligne,r==0);
                ligne.getChildren().add(new Text(" "));
                pos++;
            }
            setStats();
            pos = 0;
            soin = ligne.getChildren().get(0).getStyleClass().contains("text-life");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        updateNiveau();
    } 

    private void updateNiveau() {
        numNiveau.setText(String.valueOf(niveau));
    }

    private void updateVies() {
        nbVies.setText(String.valueOf(vies));
    }

    private void updateMotNiveau() {
        motProchainNiv.setText(String.valueOf(motRestant));
    }
    

    private void timerStart() {
        start = true;
        double coeff = 1/(double)niveau;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater( () -> {
                if (stringIter.hasNext()) {
                    String s = stringIter.next();
                    int r = new Random().nextInt(10);
                    addWordtoLine(s, ligne,r == 0);
                    ligne.getChildren().add(new Text(" "));
                }
                });
            }
        },0,(int)(5000*coeff));
    }
    
    private void validationMot() {
        int i = 0;
        nombreMots--;
        updateMotNiveau();
        Text t = (Text) ligne.getChildren().get(i);
        int tmpVie = vies;
        while (!t.getText().equals(" ")) {
            if (firstTry && soin) vies++;
            if (!t.getStyleClass().contains("text-done")) vies--;
            i++;
            t = (Text) ligne.getChildren().get(i);
        }
        ligne.getChildren().remove(0, i+1);
        updateVies();
        if (nombreMots < 3) {
            int r = new Random().nextInt(10);
            addWordtoLine(stringIter.next(), ligne,r==0);
            ligne.getChildren().add(new Text(" "));
        }
        if (tmpVie == vies) {
            motRestant--;
        }
        updateMotNiveau();
        if(motRestant <= 0) {
            motRestant = 5;
            upNiveau();
        }
        pos = 0;
        firstTry = true;
        soin = ligne.getChildren().get(0).getStyleClass().contains("text-life");
        // mots bleus qui régen
    }

    private int addWordtoLine(String s,HBox line,boolean soin) {
        if (nombreMots >= 5) validationMot();
        nombreMots++;
        int pos = 0;
        for(char c : s.toCharArray()) {
                Text t = new Text(String.valueOf(c));
                t.getStyleClass().add("text-to-do");
                if (soin) t.getStyleClass().add("text-life");
                line.getChildren().add(t);
                pos++;
        }
        return pos;
    }

    private String formatString(String text,boolean shift) {
        if (shift) return toAccent(text).toUpperCase();
        else return toAccent(text); 
    }

    private String toAccent(String text) {
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
            case "o" : if (circonflexe) return "u00f4";
            else if (trema) return "u00f6";
            break;
            default : break;
        }
        return text;
    }

    private String inputToChars(KeyEvent e) {
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

    private boolean isAccentedChar(String s) {
        switch (s) {
            case "\u00f9" : return true;
            case "\u00e0" : return true;
            case "\u00e9" : return true;
            case "\u00e8" : return true;
            case "\u00e7" : return true;
            default : return false;
        }
    }

    public void keyDetect(KeyEvent e) {
        if(e.getCode().isLetterKey() || isAccentedChar(inputToChars(e)) || inputToChars(e) == "-"){
            if(!start) timerStart();
            Text t = (Text) ligne.getChildren().get(pos);
            if(t.getText().equals(formatString(e.getText(),e.isShiftDown()))) {
                t.getStyleClass().remove("text-to-do");
                t.getStyleClass().remove("text-life");
                t.getStyleClass().add("text-done");
                pos++;
            }
            else if (t.getText().equals(" ")) {
                validationMot();
                pos = 0;
            }
            else {
                firstTry = false;
                t.getStyleClass().remove("text-to-do");
                t.getStyleClass().remove("text-life");
                t.getStyleClass().add("text-error");
                pos++;
            }
        }
        else if(e.getCode() == KeyCode.SPACE){
            validationMot();
            pos = 0;
        }
        else {
            if (e.getCode().equals(KeyCode.BACK_SPACE)) {
                if(pos > 0) {
                    firstTry = false;
                    pos--;
                    Text t = (Text) ligne.getChildren().get(pos);
                    t.getStyleClass().remove("text-skipped");
                    t.getStyleClass().remove("text-done");
                    t.getStyleClass().remove("text-error");
                    t.getStyleClass().remove("space-error");
                    t.getStyleClass().add("text-to-do");
                    if (soin) t.getStyleClass().add("text-life");
                }
            }
        }
    }
}
