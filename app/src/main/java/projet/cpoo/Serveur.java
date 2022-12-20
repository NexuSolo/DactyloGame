package projet.cpoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.io.PrintWriter;
public class Serveur {

    public static void main (String[] args) {
        Map<Socket,String> sockets = new HashMap<Socket,String>();
        try {
            try (ServerSocket server = new ServerSocket(5000)) {
                System.out.println(server.getInetAddress());
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
}

class ClientThread implements Runnable {
    private Socket client;
    private Map<Socket,String> sockets;

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
                System.out.println(line);
                Gson gson = new Gson();
                Message message = gson.fromJson(line, Message.class);
                traitement(message);
                if (message.getTransmition() == Transmission.CLIENT_DECONNEXION) {
                    break;
                }
            }
        } catch (IOException e) {
            sockets.remove(client);
            e.printStackTrace();
        }
    }

    private void traitement(Message message) throws IOException {
        if(message.getTransmition() == Transmission.CLIENT_CONNEXION) {
            sockets.put(client, (String) message.getMessage());
            listeJoueurs();
        }
        if(message.getTransmition() == Transmission.CLIENT_DECONNEXION) {
            sockets.remove(client);
            client.close();
            listeJoueurs();
        }
    }

    private void listeJoueurs() throws IOException {
        List<String> joueurs = new ArrayList<String>();
        for(Socket socket : sockets.keySet()) {
            joueurs.add(sockets.get(socket));
        }
        for(Socket socket : sockets.keySet()) {
            Message m = new Message(Transmission.SERVEUR_CONNEXION, joueurs);
            Gson gson = new Gson();
            String json = gson.toJson(m);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            out.println(json);
        }
    }
}