package projet.cpoo.Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class SoloController {
    
    private int pos = 0;
    private int motComplete = 0;
    private int temps = 0;
    private Iterator<String> stringIter;
    private Timer timer = new Timer();
    private int vies = 50;
    private int nombreMots = 0;
    private boolean start = false;
    private boolean circonflexe = false;
    private boolean trema = false;


    @FXML
    HBox ligne;

    @FXML
    Text nbVies;
    
    @FXML
    Text numNiveau;

    @FXML
    Text motProchainNiveau;


    @FXML
    private void initialize() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResource("liste_mots/liste_francais.txt").openStream()));
            List<String> list = new ArrayList<String>();
            String text = new String(reader.readLine().getBytes(),"UTF-8");
            while (text != null) {
                text = new String(text.getBytes(),"UTF-8");
                list.add(text);
                text = reader.readLine();
            }
            stringIter = list.iterator();
            for (int i = 0; i < 3 ; i++) {
                text = stringIter.next();
                pos += addWordtoLine(text, ligne);
                ligne.getChildren().add(new Text(" "));
                pos++;
            }
            // setStats();
            timerStart();
        } catch (Exception e) {
        }
    }

    // public void 
    

    private void timerStart() {
        start = true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater( () -> {
                if (stringIter.hasNext()) {
                    String s = stringIter.next();
                    addWordtoLine(s, ligne);
                    ligne.getChildren().add(new Text(" "));
                }
                });
            }
        },0,1000);
    }
    
    private void validationMot() {
        //add nouv si nbmot < 3
        // mots bleus qui régen
    }

    private int addWordtoLine(String s,HBox line) {
        if (nombreMots == 5) validationMot();
        int pos = 0;
        for(char c : s.toCharArray()) {
                Text t = new Text(String.valueOf(c));
                t.getStyleClass().add("text-to-do");
                line.getChildren().add(t);
                pos++;
        }
        return pos;
    }

    public void keyDetect(KeyEvent e) {
        System.out.println("e");
    }
}
