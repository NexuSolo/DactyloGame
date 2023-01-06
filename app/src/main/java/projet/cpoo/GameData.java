package projet.cpoo;

import java.util.LinkedList;
import java.util.List;

public abstract class GameData {

    // Création d'une nouvelle liste d'entiers appelée precList.
    private static List<Integer> precList = new LinkedList<Integer>();

    public static List<Integer> getPrecList() {
        return new LinkedList<Integer>(precList);
    }

    public static void addPrecList(int val) {
        GameData.precList.add(val);
    }

    /**
     * Cette fonction réinitialise la precList à une nouvelle LinkedList
     */
    public static void resetPrecList() {
        GameData.precList = new LinkedList<Integer>();
    }

    private static List<Integer> freqList = new LinkedList<Integer>();

    /**
     * Il renvoie un nouvel objet LinkedList qui contient les mêmes éléments que l'objet freqList
     * 
     * @return Un nouvel objet LinkedList est renvoyé.
     */
    public static List<Integer> getFreqList() {
        return new LinkedList<Integer>(freqList);
    }

    /**
     * Il ajoute la valeur de la variable val à la fin de la ArrayList freqList
     * 
     * @param val La valeur à ajouter à la liste
     */
    public static void addFreqList(int val) {
        GameData.freqList.add(val);
    }

    /**
     * Cette fonction réinitialise la liste des fréquences à une liste vide
     */
    public static void resetFreqList() {
        GameData.freqList = new LinkedList<Integer>();
    }

    // Création d'une nouvelle liste d'entiers appelée wordList.
    private static List<Integer> wordList = new LinkedList<Integer>();

    /**
     * Cette fonction renvoie une liste d'entiers qui sont les mots de la liste de mots
     * 
     * @return Un nouvel objet LinkedList qui contient le wordList.
     */
    public static List<Integer> getWordList() {
        return new LinkedList<Integer>(wordList);
    }

    /**
     * Il ajoute la valeur de la variable val à la liste de mots ArrayList
     * 
     * @param val La valeur du mot à ajouter à la liste.
     */
    public static void addWordList(int val) {
        GameData.wordList.add(val);
    }

    /**
     * Cette fonction réinitialise la liste de mots à une nouvelle LinkedList
     */
    public static void resetWordList() {
        GameData.wordList = new LinkedList<Integer>();
    }

    // Une variable qui est utilisée pour stocker le nombre de mots qui ont été complétés.
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

    // Une variable qui est utilisée pour stocker le nombre de mots qui ont été complétés.
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

    // Une variable qui est utilisée pour stocker le niveau atteitnt par le joueur.
    private static int niveauFinal = 0;

    public static int getNiveauFinal() {
        return niveauFinal;
    }

    public static void setNiveauFinal(int niveauFinal) {
        GameData.niveauFinal = niveauFinal;
    }

    // Une méthode qui réinitialise la valeur de la variable niveauFinal à la
    // valeur définie niveau dans la classe Settings.
    public static void resetNiveauFinal() {
        GameData.niveauFinal = Settings.getNiveau();
    }

    // Réinitialiser toutes les variables de la classe.
    public static void resetAll() {
        resetFreqList();
        resetPrecList();
        resetWordList();
        resetMotComplete();
        resetNiveauFinal();
        resetTempsFinal();
    }

}
