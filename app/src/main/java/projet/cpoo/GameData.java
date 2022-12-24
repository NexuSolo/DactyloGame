package projet.cpoo;

import java.util.LinkedList;
import java.util.List;

public abstract class GameData {
    

    private static List<Integer> precList = new LinkedList<Integer>();

    public static  List<Integer> getPrecList() {
        return new LinkedList<Integer>(precList);
    }

    public static void addPrecList(int val) {
        GameData.precList.add(val);
    }

    public static void resetPrecList() {
        GameData.precList = new LinkedList<Integer>();
    }

    private static List<Integer> freqList = new LinkedList<Integer>();

    public static  List<Integer> getFreqList() {
        return new LinkedList<Integer>(freqList);
    }

    public static void addFreqList(int val) {
        GameData.freqList.add(val);
    }

    public static void resetFreqList() {
        GameData.freqList = new LinkedList<Integer>();
    }
    
  

    private static List<Integer> wordList = new LinkedList<Integer>();

    public static  List<Integer> getWordList() {
        return new LinkedList<Integer>(wordList);
    }

    public static void addWordList(int val) {
        GameData.wordList.add(val);
    }

    public static void resetWordList() {
        GameData.wordList = new LinkedList<Integer>();
    }

    private static int motComplete = 0;

    public static int getMotComplete() {
        return motComplete;
    }

    public static void setMotComplete(int motComplete) {
        GameData.motComplete = motComplete;
    }

    public static void resetMotComplete() {
        GameData.motComplete = 0;
    }

    private static double tempsFinal = 0;

    public static double getTempsFinal() {
        return tempsFinal;
    }

    public static void setTempsFinal(double tempsFinal) {
        GameData.tempsFinal = tempsFinal;
    }

    public static void resetTempsFinal() {
        GameData.tempsFinal = 0;
    }

    private static int niveauFinal = 0;

    public static int getNiveauFinal() {
        return niveauFinal;
    }

    public static void setNiveauFinal(int niveauFinal) {
        GameData.niveauFinal = niveauFinal;
    }
    
    public static void resetNiveauFinal() {
        GameData.niveauFinal = Settings.getNiveau();
    }

    public static void resetAll() {
        resetFreqList();
        resetPrecList();
        resetWordList();
        resetMotComplete();
        resetNiveauFinal();
        resetTempsFinal();
    }
    
}
