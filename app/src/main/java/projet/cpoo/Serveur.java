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

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.InputStreamReader;
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
    private List<String> dictionnaire = new ArrayList<String>(); //static
    private List<String> listeMots = new ArrayList<String>();
    private List<TypeMot> listeTypeMots = new ArrayList<TypeMot>();
    private boolean premierCoup = true;
    private String motAct = "";
    int accents = 0;

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
                System.out.println(LocalTime.now() + " : " + line);
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
        if(message.getTransmition() == Transmission.CLIENT_CONNEXION) {
            LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) message.getMessage();
            System.out.println("map : " + map);
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
            if(sockets.keySet().size() > 1) {
                lancementPartie();
            }
            else {
                //envoyer un message d'erreur
            }
        }
        if(message.getTransmition() == Transmission.CLIENT_LETTRE) {
            LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) message.getMessage();
            receptionLettre((String) map.get("lettre"),(String) map.get("lettre2"));
        }
        if(message.getTransmition() == Transmission.CLIENT_VALIDATION) {
            updateVie();
            LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
            map.put("vie",vie);
            Message m = new Message(Transmission.CHANGEMENT_VIE,map);
            envoiMessage(client, m);
            listeMots.remove(0);
            listeTypeMots.remove(0);
            motAct = "";
            accents = 0;
            Message m2 = new Message(Transmission.SERVEUR_MOT_SUIVANT, null);
            envoiMessage(client, m2);
        }
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
            LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
            map.put("listeJoueur", joueurs);
            Message m = new Message(Transmission.SERVEUR_CONNEXION, map);
            envoiMessage(socket, m);
        }
    }

    private void envoiMessage(Socket socket, Message message) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(message);
        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
        out.println(json);
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
                            String s = motAleatoire();
                            System.out.println(s + " " + socket + " " + sockets.keySet().size());
                            LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
                            Random rand = new Random();
                            int nombreAleatoire = rand.nextInt(10 * sockets.size());
                            if(nombreAleatoire == 0) {
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
                            Message m = new Message(Transmission.SERVEUR_MOT, map);
                            envoiMessage(socket, m);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(sockets.size() == 0) {
                    this.cancel();
                }
            }
        }, 5000,5000);
    }

    private void creationPartie() throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResource("liste_mots/liste_francais.txt").openStream()));
        String text = r.readLine();
        while (text != null) {
            text = new String(text.getBytes(),"UTF-8");
            dictionnaire.add(text);
            text = r.readLine();
        }
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
        int nombreAleatoire = rand.nextInt(dictionnaire.size());
        return dictionnaire.get(nombreAleatoire);
    }

    private void receptionLettre(String s,String s2) throws IOException {
        if(s.equals(" ")) {
            updateVie();
            listeMots.remove(0);
            listeTypeMots.remove(0);
            premierCoup = true;
            Message m = new Message(Transmission.SERVEUR_MOT_SUIVANT, null);
            motAct = "";
            accents = 0;
            envoiMessage(client, m);
        }
        else if(s.equals("backspace")) {
            receptionBackspace();
        }
        else {
            if(motAct.length() == listeMots.get(0).length()) {
                changementVie(-1);
                motAct = "";
                accents = 0;
                listeMots.remove(0);
                listeTypeMots.remove(0);
                premierCoup = true;
                Message m = new Message(Transmission.SERVEUR_MOT_SUIVANT, null);
                envoiMessage(client, m);
            }
            else {
                String mot = listeMots.get(0);
                String nextChar = mot.substring(motAct.length() + accents,motAct.length() + 1 +accents);
                if (mot.length() >= motAct.length() + 2 + accents) {
                    String tmp = new String(mot.substring(motAct.length()+accents,motAct.length() + 2 + accents).getBytes(),"UTF-8");
                    if (isAccentedChar(tmp)) {
                        accents++;
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
                    Message m = new Message(Transmission.SERVEUR_LETTRE_INVALIDE, null);
                    envoiMessage(client, m);
                }
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
        System.out.println("lens " + motAct.length() +" "+ mot.length());
        int j = 0;
        for(int i = 0; i < s.length() ; i++) {
            String s1 = s.substring(i, i + 1);
            String s2 = mot.substring(j, j + 1);
            System.out.println("s1 = " + s1 + " s2 = " + s2); 
            if(!s1.equals(s2)) {
                System.out.println("res --");
                res--;
            }
            j++;
            if (j >= mot.length()) {
                System.out.println("break");
                break;
            }
        }
        res -= Math.abs(mot.length() - s.length());
        System.out.println("res = " + res);
        if(res != 0) changementVie(res);
    }

    private void receptionBackspace() throws IOException {
        if(motAct.length() > 0) {
            premierCoup = false;
            int len = 1;
            motAct = motAct.substring(0, motAct.length() - len);
            Message m = new Message(Transmission.SERVEUR_BACKSPACE, null);
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
                        break;
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
        client.close();
    }

}