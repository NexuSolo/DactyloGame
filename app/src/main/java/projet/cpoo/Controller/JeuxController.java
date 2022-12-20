package projet.cpoo.Controller;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class JeuxController {
    private static int CHAR_PER_LINE = 40;
    private int pos = 0;
    private int posMin = 0;
    private int motComplete = 0;

    @FXML 
    private HBox ligne_stat;
    
    @FXML
    private HBox ligne_1;

    @FXML
    private HBox ligne_2;

    @FXML
    private HBox ligne_3;

    private HBox ligne_act = ligne_1;
    private Iterator<String> stringIter;
    private String tmpIter =  null;


    @FXML
    private void initialize() {
        try {
            ligne_act = ligne_1;
            BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResource("liste_mots/liste_francais.txt").openStream()));
            List<String> list = new ArrayList<String>();
            for (int i = 0 ; i < 100 ; i++) {
                String text = reader.readLine();
                list.add(text);
            }
            Collections.shuffle(list);
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
            ligne_stat.getChildren().add(new Text("Mots completes : "));
            ligne_stat.getChildren().add(new Text(String.valueOf(motComplete)));
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

    public void updateMotComplete() {
        ligne_stat.getChildren().remove(ligne_stat.getChildren().size() - 1);
        ligne_stat.getChildren().add(new Text(String.valueOf(motComplete)));
    }

    @FXML
    private void keyDetect(KeyEvent e) {
        if(e.getCode().isLetterKey()) {
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
                }
                updateMotComplete();
            }                
                pos++;
        } else {
            e.getCode();
            if (e.getCode().equals(KeyCode.BACK_SPACE)) {
                if(pos > 0 && pos > posMin) {
                    pos--;
                    Text t = (Text) ligne_act.getChildren().get(pos);
                    t.getStyleClass().remove("text-done");
                    t.getStyleClass().remove("text-error");
                    t.getStyleClass().remove("space-error");
                    t.getStyleClass().add("text-to-do");
                }
            }
        }
    }
    
}
