package projet.cpoo.Model;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.io.InputStreamReader;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.application.Platform;
import projet.cpoo.GameData;
import projet.cpoo.Settings;

public class EntrainementModel extends JeuModel{

    private static int CHAR_PER_LINE = 30;
    private boolean modeTemps = Settings.isModeTemps();
    public boolean isModeTemps() {
        return modeTemps;
    }

    private int posMin = 0;
    private int motCorrect;
    
    public int getMotCorrect() {
        return motCorrect;
    }

    public int getPosMin() {
        return posMin;
    }

    // private int motComplete = 0;
    private double entreesClavier = 0;
    public double getEntreesClavier() {
        return entreesClavier;
    }

    private double lettresCorrectes = 0;
    public double getLettresCorrectes() {
        return lettresCorrectes;
    }

    private int motMax = Settings.getLIMITE_MAX();
    public int getMotMax() {
        return motMax;
    }

    private long tmpTemps = 0;
    private int derCharUtile = 0;

    public int getDerCharUtile() {
        return derCharUtile;
    }

    private static int TEMPS_MAX = Settings.getLIMITE_MAX();
    public static int getTEMPS_MAX() {
        return TEMPS_MAX;
    }

    

    
    public EntrainementModel(Observer o) {
        addObserver(o);
    }

    public void initialize() {
        try {
            modeTemps = Settings.isModeTemps();
            TEMPS_MAX = Settings.getLIMITE_MAX();
            motMax = Settings.getLIMITE_MAX();
            String fic = (Settings.getLangue() == Settings.Language.FR)?"liste_mots/liste_francais.txt":"liste_mots/liste_anglais.txt";
            BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResource(fic).openStream()));
            List<String> list = new ArrayList<String>();
            String text = reader.readLine();
            while (text != null) {
                text = new String(text.getBytes(),"UTF-8");
                list.add(text);
                text = reader.readLine();
            }
            if (!Settings.isAccents()) list = list.stream().filter((x) -> Pattern.matches(regAcc,x)).collect(Collectors.toList());
            Collections.shuffle(list);
            list = list.stream().filter(x -> x.length() == 4).toList();
            dictionnaire = list;
            initializeGame(dictionnaire);
            reader.close();
            timer = new Timer();
            enJeu = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initializeGame(List<String> list) {
        stringIter = list.iterator();
        for (int i = 0; i < 15 ; i++) {
            ajouteMot();
        }
    }

    protected void remplirMots() {
        for (int i = 0; i < 5; i++) {
            ajouteMot();
        }
    }

    private void ajouteMot() {
        if (stringIter.hasNext()) {
            nombreMots++;
            String s = stringIter.next();
            listeMots.add(s);
            updateView();
        }  
    }

    /**
     * Démarre le timer qui permet de compter le temps passé.
     */
    public final void timerStart() {
        start = true;        
        timer.schedule(new TimerTask() {
            
            @Override
            public final void run() {
                Platform.runLater( () -> {
                temps+= 1;
                if (modeTemps) updateModeTemps();
                updateView();
                });
            
            }
        },0,100);
    }


    /**
     * Vérifie si le dernier mot passé est correctement tapé
     * @return true si le dernier mot a bien été tapé et false sinon
     */
    protected boolean motCorrect(){
        String s = motAct;
        String mot = listeMots.get(motComplete);
        if(motAct.length() != mot.length()) return false;
        for(int i = 0; i < mot.length() ; i++) {
            String s1 = s.substring(i, i + 1);
            String s2 = mot.substring(i, i + 1);
            if(!s1.equals(s2)) {
                return false;
            }
        }
        motCorrect++;
        return true;
        // if(res != 0) changementVie(res);
    }

    protected final void incrementeMotComplete() {
        motComplete++;
    }

    @Override
    public boolean validationMot(boolean solo) {
        motCorrect();
        posMin++;
        derCharUtile += listeMots.get(motComplete).length() + 1;
        incrementeMotComplete();
        if(motComplete % 5 == 0) {
            derCharUtile = 0;
            if (motComplete >= 10) remplirMots();
        };
        resetPos();
        // removeMot();
        updateData();
        updateView();
        finDuJeu();
        // TODO Auto-generated method stub
        return true;
    }


    private void removeMot() {
        Platform.runLater( () -> {
            // listeMots.remove(0);
            updateView();
        });
    }

    protected final void resetPos(){
        motAct = "";
        firstTry = true;
    }

    /**
     * Met à jour les statistiques et vérifie si le temps est écoulé.
     */
    private final void updateModeTemps() {
        if (temps % (TEMPS_MAX/10) == 0) updateData();
        finDuJeu();
        updateView();
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
                GameData.addPrecList((int)(ratio*100));
                GameData.addWordList((int)temps/10);
            }
        }
    }

    public void ajoutChar(String c) {
        String mot = listeMots.get(motComplete);
        if (c.equals(" ")) {
            if (mot.length() == motAct.length()) lettresCorrectes++;
            return;
        }
        motAct += c;
        entreesClavier++;
        if(motAct.length() > mot.length()) {
            // validation = true;
            validationMot(true);
            validation = true;
            return;
        }
        else {
            if (motAct.equals(mot.substring(0, motAct.length()))) {
                lettresCorrectes++;
                //Pour avoir le temps en secondes
                long a = temps - tmpTemps;
                tmpTemps = temps;
                GameData.addFreqList((int)a);
            }
        }
        updateView();
    }

    public void retireChar() {
        if (motAct.length() > 0) {
            motAct = motAct.substring(0, motAct.length() - 1);
        }
    }
    
    /**
     * Met fin au jeu et passe à l'écran de fin si les conditions sont remplies
     * @throws IOException
     */
    private final void finDuJeu() {
        if ( ( modeTemps && temps >= TEMPS_MAX) || (!modeTemps && motMax - motCorrect  == 0) ) {
            timer.cancel();
            if(modeTemps) {
                GameData.addWordList(motComplete);
                double a = temps - tmpTemps;
                if (GameData.getFreqList().size() > 1 && a > GameData.getFreqList().get(GameData.getFreqList().size() - 1)) GameData.addFreqList((int)(temps-tmpTemps));
            }
            GameData.setMotComplete(motComplete);
            GameData.setTempsFinal(temps);
            enJeu = false;
            updateView();
        }
    }
    
}
