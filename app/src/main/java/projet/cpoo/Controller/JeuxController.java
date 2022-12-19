package projet.cpoo.Controller;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.awt.Desktop;  
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.print.attribute.standard.PrinterMessageFromOperator;  

public class JeuxController {
    private static int CHAR_PER_LINE = 20;
    private int pos = 0;
    
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
                for(char c : text.toCharArray()) {
                // for(char c : text.toCharArray()) {
                    Text t = new Text(String.valueOf(c));
                    t.getStyleClass().add("text-to-do");
                    ligne_act.getChildren().add(t);
                    pos++;
                }
                ligne_act.getChildren().add(new Text("  "));
                pos++;
            }
            // for(String text : list) {
            //     if(pos + text.length() > CHAR_PER_LINE) {
            //         pos = 0;
            //         if (!updateActualLine()) break;
            //     }
            //     for(char c : m.toCharArray()) {
            //     // for(char c : text.toCharArray()) {
            //         Text t = new Text(String.valueOf(c));
            //         t.getStyleClass().add("text-to-do");
            //         ligne_act.getChildren().add(t);
            //         pos++;
            //     }
            //     ligne_act.getChildren().add(new Text("  "));
            //     pos++;
            // }
            reader.close();
            ligne_act = ligne_1;
        }
        catch (Exception e) {
            System.out.println("ERROR + ligne = "+ligne_act);
            // e.printStackTrace();
        }
        
    }

    private boolean updateActualLine(){
        if (ligne_act == ligne_1) ligne_act = ligne_2;
        else if (ligne_act == ligne_2) ligne_act = ligne_3;
        else return false;
        return true;
    }

    // private boolean addLine() {
    //     int pos = 0;
    //     HBox tmpLigne_3 = new HBox();
    //     while(stringIter.hasNext()) {
    //         String text = stringIter.next();
    //         if(pos + text.length() > CHAR_PER_LINE) {
    //             HBox tmp = ligne_2;
    //             ligne_2 = ligne_3;
    //             this.ligne_3 = tmpLigne_3;
    //             return true;
    //         }
    //         System.out.println("Nouveau mot = " + text);
    //         for(char c : text.toCharArray()) {
    //             Text t = new Text(String.valueOf(c));
    //             t.getStyleClass().add("text-to-do");
    //             tmpLigne_3.getChildren().add(t);
    //             pos++;
    //         }
    //         ligne_act.getChildren().add(new Text(" "));
    //         pos++;
    //     }
    //     return false;
    // }

    @FXML
    private void keyDetect(KeyEvent e) {
        if(e.getCode().isLetterKey()) {
            // System.out.println("Nb char = "+pos+"max = "+CHAR_PER_LINE+" line = "+ligne_act + "ligneMax = "+ligne_act.getChildren().size());
            if(pos >= 20 || pos >= ligne_act.getChildren().size() ) {
                pos = 0;
                if (!updateActualLine()) { return;
                    // if (!addLine()) {
                    //     return;
                    // }
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
                pos++;
        } else {
            e.getCode();
            if (e.getCode().equals(KeyCode.BACK_SPACE)) {
                if(pos > 0) {
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
