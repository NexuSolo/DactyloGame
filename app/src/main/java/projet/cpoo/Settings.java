package projet.cpoo;

public abstract class Settings {

    private static boolean modeTemps = true;
    private static int LIMITE_MAX = 50;
    private static Language langue = Language.FR;
    private static boolean accents = true;
    private static boolean mortSubite = false;
    private static String pseudo = "Joueur";
    private static String ip = "localhost";
    private static int port = 5000;

    private static int niveau = 1;

    public static void setModeTemps(boolean modeTemps) {
        Settings.modeTemps = modeTemps;
    }

    public static String getPseudo() {
        return pseudo;
    }

    public static void setPseudo(String pseudo) {
        Settings.pseudo = pseudo;
    }

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        Settings.ip = ip;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        Settings.port = port;
    }    
    
    public static enum Language { FR,EN;
        public static String languageToString(Language language) {
            switch (language) {
                case FR:
                    return "Français";
                    case EN:
                    return "English";
                default : return "Français";
            }
        }
    };


    public static Language getLangue() {
        return langue;
    }

    public static void setLangue(Language langue) {
        Settings.langue = langue;
        System.out.println("langue set to " + langue);
    }


    public static boolean isModeTemps() {
        return modeTemps;
    }

    public static void setModeTemps() {
        Settings.modeTemps = true;
    }

    public static void setModeMots() {
        Settings.modeTemps = false;
    }


    public static int getLIMITE_MAX() {
        return LIMITE_MAX;
    }

    public static void setLIMITE_MAX(int LIMITE_MAX) {
        Settings.LIMITE_MAX = LIMITE_MAX;
    }


    public static void setNiveau(int niveau) {
        Settings.niveau = niveau;
    }

    public static int getNiveau() {
        return Settings.niveau;
    }
    

    public static boolean isAccents() {
        return accents;
    }

    public static void setAccents(boolean accents) {
        Settings.accents = accents;
    }


    public static boolean isMortSubite() {
        return mortSubite;
    }

    public static void setMortSubite(boolean mortSubite) {
        Settings.mortSubite = mortSubite;
    }


}
