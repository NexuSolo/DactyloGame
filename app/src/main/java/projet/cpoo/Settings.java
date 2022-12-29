package projet.cpoo;

public abstract class Settings {

    public static enum language { FR,EN };

    private static language langue = language.FR;

    public static language getLangue() {
        return langue;
    }

    public static void setLangue(language langue) {
        Settings.langue = langue;
        System.out.println("langue set to " + langue);
    }

    private static boolean modeTemps = true;

    public static boolean isModeTemps() {
        return modeTemps;
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

    private static int niveau = 1;

    public static void setNiveau(int niveau) {
        Settings.niveau = niveau;
    }

    public static int getNiveau() {
        return Settings.niveau;
    }
    
    private static boolean accents = true;

    public static boolean isAccents() {
        return accents;
    }

    public static void setAccents(boolean accents) {
        Settings.accents = accents;
    }

}
