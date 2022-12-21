package projet.cpoo;

import java.util.LinkedList;
import java.util.List;

public abstract class GameData {
    
    public static final int LEN_MIN = 10;
    private static int precisionList[] = new int [LEN_MIN];

    public static int[] getPrecisionList() {
        return precisionList.clone();
    }

    public static void setPrecisionList(int val,int index) {
        GameData.precisionList[index] = val;
    }

    public static void resetPrecisionList() {
        resetPrecisionList(LEN_MIN);
    }

    public static void resetPrecisionList(int length) {
        GameData.precisionList = new int[length];
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
    
    public static void resetAll() {
        resetFreqList();
        resetPrecisionList();
    }

    private static int wordList[] = new int [LEN_MIN];

    public static int[] getWordList() {
        return wordList.clone();
    }

    public static void setWordList(int val,int index) {
        GameData.wordList[index] = val;
    }

    public static void resetWordList() {
        resetWordList(LEN_MIN);
    }

    public static void resetWordList(int length) {
        GameData.wordList = new int[length];
    }

    private static int motComplete = 0;

    public static int getMotComplete() {
        return motComplete;
    }

    public static void setMotComplete(int motComplete) {
        GameData.motComplete = motComplete;
    }

    
}
