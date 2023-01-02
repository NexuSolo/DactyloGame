package projet.cpoo.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import projet.cpoo.App;
import projet.cpoo.GameData;
import projet.cpoo.Settings;

import java.io.*;
import java.util.List;
import java.util.TimerTask;



public class EntrainementController extends ControllerJeu {
    private static int CHAR_PER_LINE = 30;
    private boolean modeTemps;
    private int posMin = 0;
    private int motComplete = 0;
    private double entreesClavier = 0;
    private double lettresCorrectes = 0;
    private int motMax = Settings.getLIMITE_MAX();
    private int tmpTemps = 0;
    private int derCharUtile = 0;
    private static int TEMPS_MAX = Settings.getLIMITE_MAX();
    protected int temps;

    

    


    



    /* (non-Javadoc)
     * @see projet.cpoo.Controller.ControllerJeu#initialize()
     */
    @FXML
    protected final void initialize() {
        ligne_act = ligne_1;
        TEMPS_MAX = Settings.getLIMITE_MAX();
        motMax = Settings.getLIMITE_MAX();
        modeTemps = Settings.isModeTemps();
        super.initialize();
        ligne_act = ligne_1;

    }

    /* (non-Javadoc)
     * @see projet.cpoo.Controller.ControllerJeu#initializeGame(java.util.List)
     */
    protected final void initializeGame(List<String> list) {
        String text;
        stringIter = list.iterator();
        while(stringIter.hasNext()) {
            text = stringIter.next();
            if(pos + text.length() > CHAR_PER_LINE) {
                System.out.println("pos = " + pos);
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
    }

    /* (non-Javadoc)
     * @see projet.cpoo.Controller.ControllerJeu#initializeText()
     */
    protected final void initializeText() {
        textHG.setText("Mot compl\u00e9t\u00e9s");
        textHM.setText("Pr\u00e9cision");
        if(modeTemps){ 
            updateTempsRestant();
            textHD.setText("Temps restant");
        }
        else {
            updateMotRestant();
            textHD.setText("Mots restants");
        }
    }

    /**
     * Démarre le timer qui permet de compter le temps passé.
     */
    private final void timerStart() {
        start = true;        
        timer.schedule(new TimerTask() {
            
            @Override
            public final void run() {
                Platform.runLater( () -> {
                temps+= 1;
                if (modeTemps) updateModeTemps();
                });
            }
        },0,100);
        if(modeTemps) textBG.setText("0");
    }

    /**
     * Met à jour les statistiques et vérifie si le temps est écoulé.
     */
    private final void updateModeTemps() {
        updateTempsRestant();
        if (temps % (TEMPS_MAX/10) == 0) updateData();
        try {
            finDuJeu();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
 
    
    /* (non-Javadoc)
     * @see projet.cpoo.Controller.ControllerJeu#addWordtoLine(java.lang.String, javafx.scene.layout.HBox, boolean)
     */
    protected int addWordtoLine(String s,HBox line,boolean soin) {
        int pos = 0;
        for(char c : s.toCharArray()) {
                Text t = new Text(String.valueOf(c));
                t.getStyleClass().add("text-to-do");
                line.getChildren().add(t);
                pos++;
        }
        return pos;
    }

    // Appel raccourci de addWordtoLine 
    private final int addWordtoLine(String s,HBox line) {
        return addWordtoLine(s, line,false);
    }
  

   

    /**
     * Vérifie si le dernier mot passé est correctement tapé
     * @param finMot représente la position de la fin du mot à traiter
     * @return true si le dernier mot a bien été tapé et false sinon
     */
    private final boolean motCorrect(int finMot){
        int i = finMot;
        while (i >=0) {
            Text t = (Text) ligne_act.getChildren().get(i);
            if (t.getText().equals(" ")) return true;
            if (t.getStyleClass().contains("text-error") || t.getStyleClass().contains("text-skipped")) return false;
            i--;
        }
        return true;
    }

    private final void updatePrecision() {
        double ratio = lettresCorrectes/entreesClavier;
        textBM.setText(String.valueOf((int)(ratio*100) + "%"));
    }

    private final void updateMotComplete() {
        textBG.setText(String.valueOf(motComplete));
    }

    private final void updateMotRestant() {
        textBD.setText(String.valueOf(motMax-motComplete));
    }

    private final void updateTempsRestant() {
        textBD.setText(String.valueOf((TEMPS_MAX-temps)/10));
    }

  

    /**
     * Met à jour les statistiques et les stocke pour l'écran de fin
     */
    private final void updateData() {
        if(modeTemps) {
            int diviseur = TEMPS_MAX/100;
            if (diviseur == 0) diviseur = 1;
            if(temps % diviseur == 0) {
                double ratio = lettresCorrectes/entreesClavier;
                GameData.addPrecList((int)(ratio*100));
                GameData.addWordList(motComplete);
            }
        }
        else {
            int diviseur =(motMax/10);
            if (diviseur == 0) diviseur = 1;
            if (motComplete % diviseur == 0) {
                double ratio = lettresCorrectes/entreesClavier;
                System.out.println("case print : " + (motComplete) + " Temps = " + temps);
                GameData.addPrecList((int)(ratio*100));
                GameData.addWordList(temps/10);
            }
        }
    }

    /**
     * Déplace le curseur jusqu'au début du prochain mot
     * @return la position du prochain mot
     */
    private final int skipMot() {
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

    /**
     * Met fin au jeu et passe à l'écran de fin si les conditions sont remplies
     * @throws IOException
     */
    private final void finDuJeu() throws IOException {
        if ( ( modeTemps && temps >= TEMPS_MAX) || (!modeTemps && motMax - motComplete  == 0) ) {
            timer.cancel();
            if(modeTemps) {
                GameData.addWordList(motComplete);
                double a = temps - tmpTemps;
                if (GameData.getFreqList().size() > 1 && a > GameData.getFreqList().get(GameData.getFreqList().size() - 1)) GameData.addFreqList(temps-tmpTemps);
            }
            GameData.setMotComplete(motComplete);
            GameData.setTempsFinal(temps);
            App.setRoot("statsEntrainement");
        }
    }

    /**
     * Recule le curseur tant que le curseur est sur un caractère qui a été passé
     * @return
     */
    private final int posBack() {
        Text t = (Text) ligne_act.getChildren().get(pos);
        System.out.println("texte = " + t.getText() + "contains " + t.getStyleClass().contains("text-skipped"));
        if(t.getStyleClass().contains("text-skipped")) {
            System.out.println("skipped");
            int tmp = pos;
            while (t.getStyleClass().contains("text-skipped")) {
                tmp--; 
                t = (Text) ligne_act.getChildren().get(tmp);
            }
            return pos-tmp; 
        }
        else return 1;
    }

    /**
     * Place le curseur au début de la prochaine ligne et met à jour les lignes affichées si nécéssaire. 
     */
    private final void changeLine() {
        if(pos >= CHAR_PER_LINE || pos >= ligne_act.getChildren().size() ) {
            pos = 0;
            posMin = 0;
            derCharUtile = 0;
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
    }

    /**
     * Prend un appui de touche, le compare avec le caractère qui est attendu et met à jour l'affichage en conséquence
     * @param e l'évenement qui correspond à l'appui de touche
     */
    @FXML
    private final void keyDetect(KeyEvent e) {
        if(e.getCode().isLetterKey() || isAccentedChar(inputToChars(e)) || inputToChars(e) == "-"){
            if (!start) timerStart();
            entreesClavier++;
            changeLine();
            Text t = (Text) ligne_act.getChildren().get(pos);
            if(t.getText().equals(formatString(e.getText(),e.isShiftDown()))) {
                t.getStyleClass().remove("text-to-do");
                t.getStyleClass().add("text-done");
                pos++;
                if (derCharUtile < pos) {
                    lettresCorrectes++;
                    derCharUtile = pos;
                    //Pour avoir le temps en secondes
                    int a = temps - tmpTemps;
                    tmpTemps = temps;
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
        else if(e.getCode() == KeyCode.SPACE) {
            circonflexe = false;
            trema = false;
            changeLine();
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
                if (!modeTemps) updateMotRestant();
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
                    posBack();
                    Text t = (Text) ligne_act.getChildren().get(pos);
                    if(t.getStyleClass().contains("text-done")) lettresCorrectes--;
                    t.getStyleClass().remove("text-skipped");
                    t.getStyleClass().remove("text-done");
                    t.getStyleClass().remove("text-error");
                    t.getStyleClass().remove("space-error");
                    t.getStyleClass().add("text-to-do");
                }
            }
            else isAccent(e);
        }
        updatePrecision();
    }

    /* (non-Javadoc)
     * @see projet.cpoo.Controller.ControllerJeu#validationMot(boolean)
     */
    @Override
    protected final void validationMot(boolean solo) {
        return;
    }

}
