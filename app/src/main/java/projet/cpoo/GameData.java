package projet.cpoo;

import java.util.LinkedList;
import java.util.List;

public abstract class GameData {

    private static int precisionList[] = new int [10];
    public static final int LEN_MIN = 10;
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

    public static void resetFreqList(int length) {
        GameData.freqList = new LinkedList<Integer>();
    }
    
}
