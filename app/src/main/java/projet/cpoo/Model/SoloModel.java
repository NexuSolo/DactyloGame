package projet.cpoo.Model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Observer;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.sun.javafx.application.PlatformImpl;

import javafx.application.Platform;
import projet.cpoo.GameData;
import projet.cpoo.Settings;
import projet.cpoo.TypeMot;

public class SoloModel extends JeuModel {
    
    private int motRestant = 10;
    private int niveau = Settings.getNiveau();
    private boolean mortSubite = Settings.isMortSubite();
    protected List<TypeMot> listeTypeMots = new LinkedList<TypeMot>();
    
    
    public SoloModel() {
    }
    
    @SuppressWarnings("deprecated")
    public SoloModel(Observer o) {
        addObserver(o);
    }

   

  
    @Override
    /**
     * Initialisation du model avec les valeurs par défaut.
     */
    public void initialize() {
        try {
            PlatformImpl.startup(() -> {});
            if(mortSubite) vies = 1;
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
            dictionnaire = list;
            initializeGame(dictionnaire);
            reader.close();
            timer = new Timer();
            enJeu = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    *Remplit la liste de mots du jeu à partir d'une liste list
    */
    protected void initializeGame(List<String> list) {
        stringIter = list.iterator();
        for (int i = 0; i < 5 ; i++) {
            ajouteMot();
        }
        soin = listeTypeMots.get(0) == TypeMot.WORD_LIFE;
    }

    /**
    *
    *Ajoute un mot à la fin de la liste des mots
    */
    private void ajouteMot() {
            if (stringIter.hasNext()) {
                if (nombreMots >=15) validationMot();
                nombreMots++;
                String s = stringIter.next();
                int r = new Random().nextInt(10);
                TypeMot type = (r == 0)?TypeMot.WORD_LIFE:TypeMot.WORD_NEUTRAL;
                listeMots.add(s);
                listeTypeMots.add(type);
                updateView();
            }  
    }

    /**
    *Supprime le premier mot de la liste des mots
    */
    private void removeMot() {
        Platform.runLater( () -> {
            listeMots.remove(0);
            listeTypeMots.remove(0);
            soin = listeTypeMots.get(0) == TypeMot.WORD_LIFE;
            updateView();
        });
    }  
    
    /**
    *Ajoute un mot à la liste des mots si elle n'en possède pas assez
    */
    protected void remplirMots() {
        if (nombreMots < 8) {
            ajouteMot();
        }
    }

    /**
    *Lancement du chrono qui ajoute périodiquement des mots
    */
    public final void timerStart() {
        start = true;
        double coeff = 3000 *  Math.pow(0.9,niveau);
        timer.schedule(new TimerTask() {
            
            @Override
            public final void run() {
                Platform.runLater(() ->ajouteMot());
            }
        },(int) coeff,(int)coeff);
    }

    /**
    *Parcours le mot validé et altère la vie du joueur si nécessaire
    */
    protected boolean motDegats(){
        String s = motAct;
        int res = 0;
        String mot = listeMots.get(0);
        int j = 0;
        for(int i = 0; i < s.length() ; i++) {
            String s1 = s.substring(i, i + 1);
            String s2 = mot.substring(j, j + 1);
            if(!s1.equals(s2)) {
                res--;
            }
            else if (firstTry) {
                if (soin) {
                    res++;
                }
            }
            j++;
            if (j >= mot.length()) {
                break;
            }
        }
        res -= Math.abs(mot.length() - s.length());
        vies+=res;
        return res >= 0;
    }

    protected boolean jeuVide() {
        return listeMots.size() == 0;
    }

    /**
     * Cette fonction retourne vrai si la liste de mots est vide, faux sinon
     * 
     * @return La taille de la listeMots.
     */
    private final void upNiveau() {
        niveau++;
        timer.cancel();
        timer = new Timer();
        timerStart();

    }

    /**
     * Si le nombre de mots restant à taper est inférieur ou égal à zéro, réglez le nombre de mots
     * restant à taper sur 10 et augmentez le niveau
     */
    protected final void monteeNiveau() {
        if(motRestant <= 0) {
            motRestant = 10;
            upNiveau();
        }
    }

    /**
    *Appelle la fin du jeu si la vie du joueur est trop basse
    */
    protected final void end() {
        if (vies <= 0) {
            try {
                finDuJeu();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
    *Reset élément nécessaires pour le parcours d'un nouveau mot
    */
    protected final void resetPos(){
        motAct = "";
        firstTry = true;
    }

    /**
    * Ensemble des étapes nécessaires lorsqu'un mot est validé
    */
    public final boolean validationMot() {
        if(jeuVide()) return false;
        nombreMots--;
        boolean b = motDegats();
        removeMot();
        remplirMots();
        incrementeMotComplete(b);
        monteeNiveau();
        resetPos();
        end();
        updateView();
        return true;
    }

    public void test() {
        vies = 0;
    }

    protected final void incrementeMotComplete(boolean b) {
        if (b) {
            motRestant--;
            motComplete++;
        }
    }

    /**
    *Termine la partie et mets à jour les statistiques de la partie
    */
    private final void finDuJeu(){
        timer.cancel();
        GameData.setMotComplete(motComplete);
        GameData.setTempsFinal((System.currentTimeMillis()-temps)*0.01);
        GameData.setNiveauFinal(niveau);
        enJeu = false;
        notifyObservers();
    }
    /**
     * Ajout d'un caractère à la fin de le chaine actuelle
     * Valide le mot si nécessaire
     */
    public void ajoutChar(String c) {
        motAct += c;
        String mot = listeMots.get(0);
        if(motAct.length() > mot.length()) {
            firstTry = false;
            validationMot();
            validation = true;
            return;
        }
        else {
            if (!motAct.equals(mot.substring(0, motAct.length()))) {
                firstTry = false;
            }
        }
    }

    /**
    *Supprime le dernier caractère de la chaine mot actuel.
    *
    */
    public void retireChar() {
        if (motAct.length() > 0) {
            firstTry = false;
            motAct = motAct.substring(0, motAct.length() - 1);
        }
    }

    public int getVies() {
        return vies;
    }

    public int getNiveau() {
        return niveau;
    }

    public int getMotRestant() {
        return motRestant;
    }

    public int getNombreMots() {
        return nombreMots;
    }

    

    public List<TypeMot> getListeTypeMots() {
        return listeTypeMots;
    }

    public boolean isEnJeu() {
        return enJeu;
    }
}
