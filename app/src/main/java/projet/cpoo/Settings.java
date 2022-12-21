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

    private static int nbMots = 60;

    public static int getNbMots() {
        return nbMots;
    }

    public void setFrench() {
        Settings.langue = "fr";
    }

    public void setEnglish() {
        Settings.langue = "en";
    }

    public void setModeTemps() {
        Settings.modeTemps = true;
    }

    public void setModeMots() {
        Settings.modeTemps = false;
    }
}
