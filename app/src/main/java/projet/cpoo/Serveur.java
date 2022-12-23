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
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.InputStreamReader;
import java.io.PrintWriter;
public class Serveur {
    private static ParametrePartie parametrePartie = new ParametrePartie(false, "Français");
    public static void main (String[] args) {
        Map<Socket,String> sockets = new HashMap<Socket,String>();
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
    private Map<Socket,String> sockets;
    private List<String> listeMots = new ArrayList<String>();
    int positionMot = 0;
    int vie = 10;

    public ClientThread(Socket client, Map<Socket,String> sockets) {
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
            sockets.put(client, (String) map.get("pseudo"));
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
            lancementPartie();
        }
        if(message.getTransmition() == Transmission.CLIENT_LETTRE) {
            LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) message.getMessage();
            receptionLettre((String) map.get("lettre"));
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
            joueurs.add(sockets.get(socket));
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
                        Message m = new Message(Transmission.SERVEUR_MOT, map);
                        envoiMessage(socket, m);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 3000,3000);
    }

    private void creationPartie() throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResource("liste_mots/liste_francais.txt").openStream()));
        while (r.readLine() != null) {
            listeMots.add(r.readLine());
        }
        for(Socket socket : sockets.keySet()) {
            List<String> mots = new ArrayList<String>();
            LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
            for(int i = 0; i < 8; i++) {
                Random rand = new Random();
                int nombreAleatoire = rand.nextInt(10 * sockets.size());
                if(nombreAleatoire == 0) {
                    map.put(String.valueOf(i), TypeMot.WORD_ATTACK);
                }
                else if(nombreAleatoire == 1) {
                    map.put(String.valueOf(i), TypeMot.WORD_LIFE);
                }
                else {
                    map.put(String.valueOf(i), TypeMot.WORD_TO_DO);
                }
                mots.add(motAleatoire());
            }
            map.put("listeMot", mots);
            Message m = new Message(Transmission.SERVEUR_LISTE_MOT, map);
            envoiMessage(socket, m);
        }
        
    }

    private String motAleatoire() throws IOException {
        Random rand = new Random();
        int nombreAleatoire = rand.nextInt(listeMots.size());
        return listeMots.get(nombreAleatoire);
    }

    private void receptionLettre(String s) throws IOException {
        String mot = listeMots.get(0);
        if(s.equals(" ")) {
            positionMot = 0;
            listeMots.remove(0);
            if(positionMot != mot.length()) {
                vie -= mot.length() - positionMot;
            }
            Message m = new Message(Transmission.SERVEUR_MOT_SUIVANT, null);
            envoiMessage(client, m);
        }
        else if(positionMot < mot.length()) {
            positionMot++;
            Message m;
            if(s.equals(mot.substring(positionMot,positionMot+1))) {
                m = new Message(Transmission.SERVEUR_LETTRE_VALIDE, null);
            }
            else {
                m = new Message(Transmission.SERVEUR_LETTRE_INVALIDE, null);
            }
            envoiMessage(client, m);
        }
        else {
            positionMot = 0;
            listeMots.remove(0);
            vie -= 1;
            Message m = new Message(Transmission.SERVEUR_MOT_SUIVANT, null);
            envoiMessage(client, m);
        }
    }

    private void receptionBackspace() throws IOException {
        String mot = listeMots.get(0);
        if(positionMot > 0) {
            positionMot--;
            String s = mot.substring(positionMot,positionMot+1);
            LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
            map.put("lettre", s); 
            Message m = new Message(Transmission.SERVEUR_BACKSPACE,map);
            envoiMessage(client, m);
        }
    }

}