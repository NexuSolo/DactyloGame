package projet.cpoo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

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

    private static void createProperties() {
        Properties prop = new Properties();
        try {
            prop.setProperty("pseudo", Settings.getPseudo());
            prop.setProperty("ip", Settings.getIp());
            prop.setProperty("port", String.valueOf(Settings.getPort()));
            prop.setProperty("langue", Settings.getLangue().toString());
            prop.setProperty("accent", String.valueOf(Settings.isAccents()));
            prop.setProperty("modeTemps", String.valueOf(Settings.isModeTemps()));
            prop.setProperty("LIMITE_MAX", String.valueOf(Settings.getLIMITE_MAX()));
            prop.setProperty("mort_subite", String.valueOf(Settings.isMortSubite()));
            prop.store(new FileOutputStream(System.getProperty("user.home") + File.separator + ".Dactylo/config.properties"), "Configuration du jeu");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadProperties() {
        try {
            File file = new File(System.getProperty("user.home") + File.separator + ".Dactylo");
            if(!file.exists()) {
                file.mkdir();
            }
            file = new File(System.getProperty("user.home") + File.separator + ".Dactylo/config.properties");
            if(!file.exists()) {
                file.createNewFile();
                createProperties();
            }
            Properties prop = new Properties();
            prop.load(new FileInputStream(System.getProperty("user.home") + File.separator + ".Dactylo/config.properties"));
            Settings.setPseudo(prop.getProperty("pseudo", "joueur"));
            Settings.setIp(prop.getProperty("ip", "localhost"));
            try {
                Settings.setPort(Integer.parseInt(prop.getProperty("port", "5000")));
                if(Settings.getPort() < 0 || Settings.getPort() > 65535) {
                    prop.setProperty("port", "5000");
                    Settings.setPort(5000);
                }
            } catch (NumberFormatException e) {
                prop.setProperty("port", "5000");
                Settings.setPort(5000);
            }
            String langue = prop.getProperty("langue", "FR");
            if(langue.equals("FR")) {
                Settings.setLangue(Settings.Language.FR);
            } else if(langue.equals("EN")) {
                Settings.setLangue(Settings.Language.EN);
            } else {
                prop.setProperty("langue", "FR");
                Settings.setLangue(Settings.Language.FR);
            }
            try {
                Settings.setAccents(Boolean.parseBoolean(prop.getProperty("accent", "true")));
            } catch (NumberFormatException e) {
                prop.setProperty("accent", "true");
                Settings.setAccents(true);
            }
            try {
                Settings.setModeTemps(Boolean.parseBoolean(prop.getProperty("momodeTempsde", "true")));
            } catch (NumberFormatException e) {
                prop.setProperty("modeTemps", "true");
                Settings.setModeTemps(true);
            }
            try {
                Settings.setLIMITE_MAX(Integer.parseInt(prop.getProperty("LIMITE_MAX", "60")));
                if(Settings.getLIMITE_MAX() < 0) {
                    prop.setProperty("LIMITE_MAX", "60");
                    Settings.setLIMITE_MAX(60);
                }
            }
            catch (NumberFormatException e) {
                prop.setProperty("LIMITE_MAX", "60");
                Settings.setLIMITE_MAX(60);
            }
            try {
                Settings.setMortSubite(Boolean.parseBoolean(prop.getProperty("mort_subite", "false")));
            } catch (NumberFormatException e) {
                prop.setProperty("mort_subite", "false");
                Settings.setMortSubite(false);
            }
            createProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static enum Language {
        FR, EN;

        public static String languageToString(Language language) {
            switch (language) {
                case FR:
                    return "Français";
                case EN:
                    return "English";
                default:
                    return "Français";
            }
        }
    };

    public static void setModeTemps(boolean modeTemps) {
        Settings.modeTemps = modeTemps;
        createProperties();
    }

    public static String getPseudo() {
        return pseudo;
    }

    public static void setPseudo(String pseudo) {
        Settings.pseudo = pseudo;
        createProperties();
    }

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        Settings.ip = ip;
        createProperties();
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        Settings.port = port;
        createProperties();
    }

    public static Language getLangue() {
        return langue;
    }

    public static void setLangue(Language langue) {
        Settings.langue = langue;
        createProperties();
    }

    public static boolean isModeTemps() {
        return modeTemps;
    }

    public static void setModeTemps() {
        Settings.modeTemps = true;
        createProperties();
    }

    public static void setModeMots() {
        Settings.modeTemps = false;
        createProperties();
    }

    public static int getLIMITE_MAX() {
        return LIMITE_MAX;
    }

    public static void setLIMITE_MAX(int LIMITE_MAX) {
        Settings.LIMITE_MAX = LIMITE_MAX;
        createProperties();
    }

    public static void setNiveau(int niveau) {
        Settings.niveau = niveau;
        createProperties();
    }

    public static int getNiveau() {
        return Settings.niveau;
    }

    public static boolean isAccents() {
        return accents;
    }

    public static void setAccents(boolean accents) {
        Settings.accents = accents;
        createProperties();
    }

    public static boolean isMortSubite() {
        return mortSubite;
    }

    public static void setMortSubite(boolean mortSubite) {
        Settings.mortSubite = mortSubite;
        createProperties();
    }

}
