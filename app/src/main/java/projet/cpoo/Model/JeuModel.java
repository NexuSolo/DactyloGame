package projet.cpoo.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Timer;

import projet.cpoo.Settings;

public abstract class JeuModel extends Observable {
    protected static int CHAR_PER_LINE = 30;
    protected int pos = 0;
    protected int motComplete = 0;
    protected long temps = 0;
    protected boolean validation = false;
    

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
    protected List<String> dictionnaire;
    protected List<String> listeMots = new ArrayList<String>();
  

    protected String motAct = "";
   

    protected Iterator<String> stringIter;
    protected String regAcc ="^*[a-zA-Z-]*";
    protected boolean enJeu = true;

   

    /**
     * Initialisation du model avec les valeurs par défaut.
     */
    public abstract void initialize();

    //Fonction abstraite qui crée une configuration de base à partir d'une liste de mots triée dans l'ordre voulu
    protected abstract void initializeGame(List<String> list);

    //Fonction abstraite qui a pour but de traiter le mot actuel et de passer au mot suivant
    public abstract boolean validationMot();
    
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
            return;
        }
        else {
            if (!motAct.equals(mot.substring(0, motAct.length()))) {
                firstTry = false;
            }
        }
    }

    //Supprime le dernier caractère de la chaine mot actuel.
    public void retireChar() {
        if (motAct.length() > 0) {
            firstTry = false;
            motAct = motAct.substring(0, motAct.length() - 1);
        }
    }

    public abstract void timerStart();

    /**
     * Lorsque le modèle change, informez les observateurs que le modèle a changé.
     */
    protected void updateView() {
        setChanged();
        notifyObservers();
    }

    public long getTemps() {
        return temps;
    }

    public void setTemps(long temps) {
        this.temps = temps;
    }

    public void setValidation(boolean validation) {
        this.validation = validation;
    }

    public boolean isValidation() {
        return validation;
    }


    public boolean isStart() {
        return start;
    }

    public int getMotComplete() {
        return motComplete;
    }

    public String getMotAct() {
        return motAct;
    }

    public boolean isEnJeu() {
        return enJeu;
    }
    
    public List<String> getListeMots() {
        return listeMots;
    }

    public void setFirstTry(boolean firstTry) {
        this.firstTry = firstTry;
    }
    
    public void setSoin(boolean soin) {
        this.soin = soin;
    }
}
