package projet.cpoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
public class Serveur {
    private static ParametrePartie parametrePartie = new ParametrePartie(false, "Français");
    protected static boolean partieEnCours = false;
    protected static Stack<String> classementStack = new Stack<String>();
    public static void main (String[] args) {
        Map<Socket,ClientThread> sockets = new HashMap<Socket,ClientThread>();
        try {
            try (ServerSocket server = new ServerSocket(Integer.valueOf(args[0]))) {
                System.out.println(server.getLocalSocketAddress());
                System.out.println("Serveur en écoute...");
                while (true) {
                    Socket client = server.accept();
                    System.out.println("Client connecté");
                    //Creation d'un thread pour chaque client
                    Thread t = new Thread(new ClientThread(client, sockets));
                    t.start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ParametrePartie getParametrePartie() {
        return new ParametrePartie(parametrePartie.isAccent(), parametrePartie.getLangue());
    }

    public static void setParametrePartie(ParametrePartie p) {
        parametrePartie = new ParametrePartie(p.isAccent(), p.getLangue());
    }
}

class ClientThread implements Runnable {
    private Socket client;
    private Map<Socket,ClientThread> sockets;
    private String pseudo;
    private int vie = 50;
    private boolean enJeu = true;
    private static List<String> dictionnaire = new ArrayList<String>(); //static
    private List<String> listeMots = new ArrayList<String>();
    private List<TypeMot> listeTypeMots = new ArrayList<TypeMot>();
    private boolean premierCoup = true;
    private boolean soin = false;
    private boolean attaque = false;
    private boolean trema = false;
    private boolean circonflexe = false;
    private String motAct = "";

    public ClientThread(Socket client, Map<Socket,ClientThread> sockets) {
        this.client = client;
        this.sockets = sockets;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(LocalTime.now() + " : " + line + " from " + client);
                Gson gson = new Gson();
                Message message = gson.fromJson(line, Message.class);
                traitement(message);
                if (message.getTransmition() == Transmission.CLIENT_DECONNEXION) {
                    break;
                }
            }
        }
        catch (SocketException e) {
            System.out.println("Client déconnecté");
            sockets.remove(client);
            try {
                listeJoueurs();
                Thread.interrupted();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        catch (IOException e) {
            sockets.remove(client);
            e.printStackTrace();
        }
        
    }

    @SuppressWarnings("unchecked")
    private void traitement(Message message) throws IOException {
        if(message.getTransmition() == Transmission.CLIENT_CONNEXION_SERVER_RUN) {
            Message m;
            if(Serveur.partieEnCours) {
                LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
                map.put("bool",true);
                m = new Message(Transmission.SERVER_CONNEXION_SERVER_RUN, map);
            }
            else {
                LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
                map.put("bool",false);
                m = new Message(Transmission.SERVER_CONNEXION_SERVER_RUN, map);
            }
            envoiMessage(client, m);
        }
        if(message.getTransmition() == Transmission.CLIENT_CONNEXION) {
            LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) message.getMessage();
            pseudo = (String) map.get("pseudo");
            sockets.put(client, this);
            listeJoueurs();
            miseAJourOptions();
        }
        if(message.getTransmition() == Transmission.CLIENT_DECONNEXION) {
            sockets.remove(client);
            client.close();
            listeJoueurs();
        }
        if(message.getTransmition() == Transmission.CLIENT_OPTION) {
            LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) message.getMessage();
            Serveur.setParametrePartie(new ParametrePartie((boolean) map.get("accent"), (String) map.get("langue")));
            miseAJourOptions();
        }
        if(message.getTransmition() == Transmission.CLIENT_LANCER) {
            if(true || sockets.keySet().size() > 1) {
                lancementPartie();
            }
            else {
                //envoyer un message d'erreur
            }
        }
        if(message.getTransmition() == Transmission.CLIENT_LETTRE) {
            LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) message.getMessage();
            receptionLettre((String) map.get("lettre"));
        }
        if(message.getTransmition() == Transmission.CLIENT_VALIDATION) {
            updateVie();
            LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
            map.put("vie",vie);
            Message m = new Message(Transmission.CHANGEMENT_VIE,map);
            envoiMessage(client, m);
            resetMot();
            
            Message m2 = new Message(Transmission.SERVEUR_MOT_SUIVANT, null);
            envoiMessage(client, m2);
        }
    }

    protected final void resetMot() throws IOException{
        listeMots.remove(0);
        listeTypeMots.remove(0);
        motAct = "";
        premierCoup = true;
        if (listeMots.size() < 3) {
            nouveauMot(client, null);
        }
        TypeMot type = listeTypeMots.get(0);
        soin = type == TypeMot.WORD_LIFE;
        attaque = type == TypeMot.WORD_ATTACK;
    }

    private void miseAJourOptions() {
        for(Socket socket : sockets.keySet()) {
            LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
            map.put("accent", Serveur.getParametrePartie().isAccent());
            map.put("langue", Serveur.getParametrePartie().getLangue());
            Message m = new Message(Transmission.SERVEUR_OPTION, map);
            try {
                envoiMessage(socket, m);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void listeJoueurs() throws IOException {
        List<String> joueurs = new ArrayList<String>();
        for(Socket socket : sockets.keySet()) {
            joueurs.add(sockets.get(socket).pseudo);
        }
        for(Socket socket : sockets.keySet()) {
            if(sockets.get(socket).enJeu) {
                LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
                map.put("listeJoueur", joueurs);
                Message m = new Message(Transmission.SERVEUR_CONNEXION, map);
                envoiMessage(socket, m);
            }
        }
    }

    private void envoiMessage(Socket socket, Message message) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(message);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"),true);
        out.println(json);
    }

    private void nouveauMot(Socket socket,String s) throws IOException {
        if (s == null) s = motAleatoire();
        if (listeMots.size() > 15) validationMot(socket);
        System.out.println(s + " " + socket);
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
        Random rand = new Random();
        int nombreAleatoire = rand.nextInt(10 * sockets.size());
        if(nombreAleatoire == 0 ) {
            map.put(s,TypeMot.WORD_ATTACK);
        }
        else if(nombreAleatoire == 1) {
            map.put(s,TypeMot.WORD_LIFE);
        }
        else {
            map.put(s,TypeMot.WORD_TO_DO);
        }
        sockets.get(socket).listeMots.add(s);
        sockets.get(socket).listeTypeMots.add((TypeMot) map.get(s));
        if(listeMots.size() == 1) {
            soin = nombreAleatoire == 1;
            attaque = nombreAleatoire == 0;
        }
        Message m = new Message(Transmission.SERVEUR_MOT, map);
        envoiMessage(socket, m);
    }

    private void lancementPartie() throws IOException {
        Serveur.partieEnCours = true;
        for(Socket socket : sockets.keySet()) {
            Message m = new Message(Transmission.SERVEUR_LANCER, null);
            envoiMessage(socket, m);
        }
        creationPartie();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (Socket socket : sockets.keySet()) {
                    if(sockets.get(socket).enJeu) {
                        try {
                            nouveauMot(socket,null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                System.out.println();
                if(sockets.size() == 0) {
                    this.cancel();
                }
            }
        }, 5000,5000);
        TypeMot type = listeTypeMots.get(0);
        soin = type == TypeMot.WORD_LIFE;
        attaque = type == TypeMot.WORD_ATTACK;
    }

    private void creationPartie() throws IOException {
        String fic = (Serveur.getParametrePartie().getLangue().equals("Français"))?"liste_mots/liste_francais.txt":"liste_mots/liste_anglais.txt";
        BufferedReader r = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResource(fic).openStream()));
        String text = r.readLine();
        while (text != null) {
            text = new String(text.getBytes(),"UTF-8");
            if (text.length() < 7) dictionnaire.add(text);
            text = r.readLine();
        }
        if (!Serveur.getParametrePartie().isAccent()) dictionnaire = dictionnaire.stream().filter((x) -> Pattern.matches("^*[a-zA-Z-]*",x)).collect(Collectors.toList());
        for(Socket socket : sockets.keySet()) {
            List<String> mots = new ArrayList<String>();
            List<TypeMot> typeMots = new ArrayList<TypeMot>();
            LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
            for(int i = 0; i < 8; i++) {
                Random rand = new Random();
                int nombreAleatoire = rand.nextInt(10 * sockets.size());
                if(nombreAleatoire == 0) {
                    map.put(String.valueOf(i), TypeMot.WORD_ATTACK);
                    typeMots.add(TypeMot.WORD_ATTACK);
                }
                else if(nombreAleatoire == 1) {
                    map.put(String.valueOf(i), TypeMot.WORD_LIFE);
                    typeMots.add(TypeMot.WORD_LIFE);
                }
                else {
                    map.put(String.valueOf(i), TypeMot.WORD_TO_DO);
                    typeMots.add(TypeMot.WORD_TO_DO);
                }
                mots.add(motAleatoire());
            }
            sockets.get(socket).listeMots = mots;
            sockets.get(socket).listeTypeMots = typeMots;
            map.put("listeMot", mots);
            Message m = new Message(Transmission.SERVEUR_LISTE_MOT, map);
            envoiMessage(socket, m);
            LinkedTreeMap<String, Object> map2 = new LinkedTreeMap<String, Object>();
            List<String> classement = sockets.keySet().stream().sorted((x,y) -> sockets.get(x).vie - sockets.get(y).vie).filter(x -> sockets.get(x).enJeu).map(x -> sockets.get(x).pseudo + " : " + sockets.get(x).vie).toList();
            map2.put("liste", classement);
            Message m2 = new Message(Transmission.SERVEUR_CLASSEMENT, map2);
            envoiMessage(socket, m2);
        }
        
    }

    private String motAleatoire() throws IOException {
        Random rand = new Random();
        System.out.println("Taille dictio = " + dictionnaire.size() + " ip : " + client);
        int nombreAleatoire = rand.nextInt(dictionnaire.size());
        return dictionnaire.get(nombreAleatoire);
    }

    private void validationMot(Socket s) throws IOException {
        trema = false;
        circonflexe = false;
        Message m = new Message(Transmission.SERVEUR_VALIDATION, null);
        envoiMessage(s, m);
    }

    protected String toAccent(String text) {
        switch (text) {
            case "a" : if (circonflexe) return "\u00e2";
            else if (trema) return "\u00e4";
            break;
            case "e" : if (circonflexe) return "\u00ea";
            else if (trema) return "\u00eb";
            break;
            case "u" : if (circonflexe) return "\u00fb";
            else if (trema) return "\u00fc";
            break;
            case "i" : if (circonflexe) return "\u00ee";
            else if (trema) return "\u00ef";
            break;
            case "o" : if (circonflexe) return "u00f4";
            else if (trema) return "u00f6";
            break;
            default : break;
        }
        return text;
    }

    private void receptionLettre(String s) throws IOException {
        s = toAccent(s);
        if (s.equals(" ")) {
            validationMot(client);
        }
        else if(s.equals("backspace")) {
            trema = false;
            circonflexe = false;
            receptionBackspace();
        }
        else if (s.equals("circonflexe")) {
            circonflexe = true;
            trema = false;
        }
        else if (s.equals("trema")) {
            circonflexe = false;
            trema = true;
        }
        else {
            if(motAct.length() == listeMots.get(0).length()) {
                motAct += s;
                validationMot(client);
            }
            else {
                String mot = listeMots.get(0);
                String nextChar = mot.substring(motAct.length(),motAct.length() + 1 );
                if (mot.length() >= motAct.length() + 2 ) {
                    String tmp = new String(mot.substring(motAct.length(),motAct.length() + 2).getBytes(),"UTF-8");
                    if (isAccentedChar(tmp)) {
                        nextChar = tmp;
                    }
                }
                if(s.equals(nextChar)) {
                    motAct += nextChar;
                    Message m = new Message(Transmission.SERVEUR_LETTRE_VALIDE, null);
                    envoiMessage(client, m);
                }
                else {
                    motAct += s;
                    premierCoup = false;
                    Message m = new Message(Transmission.SERVEUR_LETTRE_INVALIDE, null);
                    envoiMessage(client, m);
                }
                circonflexe = false;
                trema = false;
            }
        }
    }

    protected boolean isAccentedChar(String s) {
        switch (s) {
            case "\u00f9" : return true;
            case "\u00e0" : return true;
            case "\u00e9" : return true;
            case "\u00e8" : return true;
            case "\u00e7" : return true;
            default : return false;
        }
    }

    private void updateVie() throws IOException {
        String s = motAct;
        int res = 0;
        String mot = listeMots.get(0);
        int j = 0;
        for(int i = 0; i < s.length() ; i++) {
            String s1 = s.substring(i, i + 1);
            String s2 = mot.substring(j, j + 1);
            if(!s1.equals(s2)) {
                res--;
                if(attaque) attaque = false;
            }
            else if (premierCoup) {
                if (soin) {
                    res++;
                }
            }
            j++;
            if (j >= mot.length()) {
                break;
            }
        }
        res -= Math.abs(mot.length() - s.length());
        if(premierCoup && attaque) attaque();
        if(res != 0) changementVie(res);
    }

    private void attaque() throws IOException{
        String mot = listeMots.get(0);
        for (Socket s : sockets.keySet()) {
            ClientThread ct = sockets.get(s);
            if (s != client && ct.enJeu) {
                ct.nouveauMot(ct.client, mot);
            }
        }
    }

    private void receptionBackspace() throws IOException {
        if(motAct.length() > 0) {
            premierCoup = false;
            int len = 1;
            motAct = motAct.substring(0, motAct.length() - len);
            LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
            if (soin) map.put("type",TypeMot.WORD_LIFE);
            else if (attaque) map.put("type",TypeMot.WORD_ATTACK);
            else map.put("type",TypeMot.WORD_TO_DO);
            Message m = new Message(Transmission.SERVEUR_BACKSPACE, map);
            envoiMessage(client, m);
        }
    }

    private void changementVie(int i) throws IOException {
        vie += i;
        if(vie <= 0) {
            enJeu = false;
            LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
            List<String> listeJoueurs = sockets.keySet().stream().map(x -> sockets.get(x).pseudo).toList();
            map.put("listeJoueurs", listeJoueurs);
            List<Integer> listeScore = sockets.keySet().stream().map(x -> sockets.get(x).vie).toList();
            map.put("listeScore", listeScore);
            Message m = new Message(Transmission.SERVEUR_PERDU, map);
            envoiMessage(client, m);
            int enVie = sockets.keySet().stream().map(x -> sockets.get(x).enJeu).filter(x -> x).toList().size();
            if(enVie == 1 ) {
                LinkedTreeMap<String, Object> map2 = new LinkedTreeMap<String, Object>();
                List<String> listeJoueurs2 = sockets.keySet().stream().map(x -> sockets.get(x).pseudo).toList();
                map2.put("listeJoueurs", listeJoueurs2);
                List<Integer> listeScore2 = sockets.keySet().stream().map(x -> sockets.get(x).vie).toList();
                map2.put("listeScore", listeScore2);
                Message m3 = new Message(Transmission.SERVEUR_GAGNER, map2);
                for(Socket socket : sockets.keySet()) {
                    if(sockets.get(socket).enJeu) {
                        sockets.get(socket).enJeu = false;
                        envoiMessage(socket, m3);
                    }
                    if(!socket.isClosed()) {
                        socket.close();
                    }
                }
                resetServeur();
            }
            else {
                LinkedTreeMap<String, Object> map2 = new LinkedTreeMap<String, Object>();
                List<String> liste = sockets.keySet().stream().sorted((x,y) -> sockets.get(x).vie - sockets.get(y).vie).filter(x -> sockets.get(x).enJeu).map(x -> sockets.get(x).pseudo + " : " + sockets.get(x).vie).toList();
                map2.put("liste", liste);
                Message m2 = new Message(Transmission.SERVEUR_CLASSEMENT, map2);
                for(Socket socket : sockets.keySet()) {
                   if(sockets.get(socket).enJeu) {
                       envoiMessage(socket, m2);
                   }
                }
            }

        }
        else {
            LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
            List<String> liste = sockets.keySet().stream().sorted((x,y) -> sockets.get(x).vie - sockets.get(y).vie).filter(x -> sockets.get(x).enJeu).map(x -> sockets.get(x).pseudo + " : " + sockets.get(x).vie).toList();
            map.put("liste", liste);
            Message m = new Message(Transmission.SERVEUR_CLASSEMENT, map);
            for(Socket socket : sockets.keySet()) {
                envoiMessage(socket, m);
            }
            
        }
    }

    private void resetServeur() throws IOException {
        Serveur.setParametrePartie(new ParametrePartie(false, "Français"));
        Serveur.partieEnCours = false;
        sockets.clear();
    }

}