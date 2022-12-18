package projet.cpoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * www.codeurjava.com
 */
public class Serveur {

    public static void main(String[] test) {

        final ServerSocket serveurSocket;
        final Socket clientSocket;
        final BufferedReader in;

        try {
            serveurSocket = new ServerSocket(5000);
            clientSocket = serveurSocket.accept();
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Thread recevoir = new Thread(() -> {
                String msg;
                    try {
                        msg = in.readLine();
                        // tant que le client est connecté
                        while (msg != null) {
                            System.out.println("Client : " + msg);
                            msg = in.readLine();
                            //TODO : Traitement serveur
                        }
                        // sortir de la boucle si le client a déconecté
                        System.out.println("Client déconecté");
                        // fermer le flux et la session socket
                        clientSocket.close();
                        serveurSocket.close();
                    } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            recevoir.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}