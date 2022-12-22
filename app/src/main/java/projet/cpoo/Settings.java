package projet.cpoo;

public abstract class Settings {

    private static String langue = "fr";

    public static String getLangue() {
        return langue;
    }

    private static boolean modeTemps = true;

    public static boolean isModeTemps() {
        return modeTemps;
    }

    private static int nbMots = 300;

    public static int getNbMots() {
        return nbMots;
    }

    public static void setFrench() {
        Settings.langue = "fr";
    }

    public static void setEnglish() {
        Settings.langue = "en";
    }

    public static void setModeTemps() {
        Settings.modeTemps = true;
    }

    public static void setModeMots() {
        Settings.modeTemps = false;
    }

    private static int LIMITE_MAX = 50;

    public static int getLIMITE_MAX() {
        return LIMITE_MAX;
    }

    public static void setLIMITE_MAX(int LIMITE_MAX) {
        Settings.LIMITE_MAX = LIMITE_MAX;
    }
}
