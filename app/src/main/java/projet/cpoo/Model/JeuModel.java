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
    public long getTemps() {
        return temps;
    }

    public void setTemps(long temps) {
        this.temps = temps;
    }
    protected boolean validation = false;

    public void setValidation(boolean validation) {
        this.validation = validation;
    }

    public boolean isValidation() {
        return validation;
    }

    protected Timer timer = new Timer();
    protected int vies = 50;
    protected int nombreMots = 0;
    protected int motRestant = 1;
    protected int niveau = Settings.getNiveau();
    protected boolean start = false;
    public boolean isStart() {
        return start;
    }

    protected boolean circonflexe = false;
    protected boolean trema = false;
    protected boolean soin = false;
    protected boolean firstTry = true;
    protected List<String> dictionnaire;
    protected List<String> listeMots = new ArrayList<String>();
  

    protected String motAct = "";
    public String getMotAct() {
        return motAct;
    }

    protected Iterator<String> stringIter;
    protected String tmpIter =  null;
    protected String regAcc ="^*[a-zA-Z-]*";
    protected boolean enJeu = true;

    public boolean isEnJeu() {
        return enJeu;
    }
    
    public List<String> getListeMots() {
        return listeMots;
    }

    public abstract void initialize();

    //Fonction abstraite qui crée une configuration de base à partir d'une liste de mots triée dans l'ordre voulu
    protected abstract void initializeGame(List<String> list);

    //Fonction abstraite qui a pour but de traiter le mot actuel et de passer au mot suivant
    public abstract boolean validationMot(boolean solo);

    public void ajoutChar(String c) {
        motAct += c;
        String mot = listeMots.get(0);
        if(motAct.length() > mot.length()) {
            firstTry = false;
            validationMot(true);
            return;
        }
        else {
            if (!motAct.equals(mot.substring(0, motAct.length()))) {
                firstTry = false;
            }
        }
    }

    public void retireChar() {
        if (motAct.length() > 0) {
            firstTry = false;
            motAct = motAct.substring(0, motAct.length() - 1);
        }
    }

    public abstract void timerStart();

    protected void updateView() {
        setChanged();
        notifyObservers();
    }

    public int getMotComplete() {
        return motComplete;
    }
}
