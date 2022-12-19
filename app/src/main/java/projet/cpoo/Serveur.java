package projet.cpoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.InputStreamReader;
public class Serveur {

    public static void main (String[] args) {
        ArrayList<Socket> sockets = new ArrayList<Socket>();
        try {
            try (ServerSocket server = new ServerSocket(5000)) {
                System.out.println(server.getInetAddress());
                System.out.println("Serveur en écoute...");
                while (true) {
                    Socket client = server.accept();
                    sockets.add(client);
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
    private ArrayList<Socket> sockets;

    public ClientThread(Socket client, ArrayList<Socket> sockets) {
        this.client = client;
        this.sockets = sockets;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Message reçu: " + message);
            }
            reader.close();
            client.close();
            sockets.remove(client);
            System.out.println("Client déconnecté");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}