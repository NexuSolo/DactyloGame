package projet.cpoo.Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import projet.cpoo.Settings;

public abstract class ControllerJeu {
    protected static int CHAR_PER_LINE = 30;
    protected int pos = 0;
    protected int motComplete = 0;
    protected long temps = 0;
    protected Timer timer = new Timer();
    protected int vies = 50;
    protected int nombreMots = 0;
    protected int motRestant = 1;
    protected int niveau = Settings.getNiveau();
    protected boolean start = false;
    protected boolean circonflexe = false;
    protected boolean trema = false;
    protected boolean soin = false;
    protected boolean firstTry = true;
    
    protected Iterator<String> stringIter;
    protected String tmpIter =  null;



    @FXML
    protected HBox ligne_1;

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

    

    @FXML
    protected HBox ligne_act = ligne_1;

    @FXML
    protected void initialize() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResource("liste_mots/liste_francais.txt").openStream()));
            List<String> list = new ArrayList<String>();
            String text = reader.readLine();
            while (text != null) {
                text = new String(text.getBytes(),"UTF-8");
                list.add(text);
                text = reader.readLine();
            }
            Collections.shuffle(list);
            initializeGame(list);
            pos = 0;
            reader.close();
            initializeText();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected abstract void initializeGame(List<String> list);

    protected abstract void initializeText();

    protected String formatString(String text,boolean shift) {
        if (shift) return toAccent(text).toUpperCase();
        else return toAccent(text); 
    }

    protected boolean updateActualLine(){
        pos = 0;
        if (ligne_act == ligne_1) {ligne_act = ligne_2;
        System.out.println("ex L1");}
        else if (ligne_act == ligne_2) ligne_act = ligne_3;
        else return false;
        return true;
    }

    protected abstract void validationMot();

    protected String inputToChars(KeyEvent e) {
        System.out.println("code = " + e.getCode());
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

    protected void isAccent(KeyEvent e) {
        KeyCode code = e.getCode();
        switch (code) {
            case DEAD_CIRCUMFLEX : if (!e.isShiftDown()) { System.out.println("Circ true");
                circonflexe = true;}
            else trema = true; break;
            case DEAD_DIAERESIS : trema = true;
            default : break;
        }
    }

    protected String toAccent(String text) {
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

    protected abstract int addWordtoLine(String s,HBox line,boolean soin);

    protected boolean addLine() {
        int pos = 0;
        ligne_1.getChildren().clear();
        ligne_1.getChildren().addAll(ligne_2.getChildren());
        ligne_2.getChildren().clear();
        ligne_2.getChildren().addAll(ligne_3.getChildren());
        ligne_3.getChildren().clear();
        if (tmpIter != null) {
            pos += addWordtoLine(tmpIter,ligne_3,false);
            ligne_act.getChildren().add(new Text(" "));
            pos++;
        }
        while(stringIter.hasNext()) {
            String text = stringIter.next();
            if(pos + text.length() > CHAR_PER_LINE) {
                tmpIter = text;
                return true;
            }
            pos += addWordtoLine(text,ligne_3,false);
            ligne_act.getChildren().add(new Text(" "));
            pos++;
        }
        return false;
    }

    
    protected boolean isAccentedChar(String s) {
        switch (s) {
            case "\u00f9" : return true;
            case "\u00e0" : return true;
            case "\u00e9" : return true;
            case "\u00e8" : return true;
            case "\u00e7" : return true;
            default : return false;
        }
    }

}