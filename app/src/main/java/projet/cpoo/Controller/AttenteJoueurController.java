package projet.cpoo.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStreamReader;

import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import projet.cpoo.App;
import projet.cpoo.Message;
import projet.cpoo.Transmission;

public class AttenteJoueurController {
    private Socket socket;
    private Thread reception;
    List<Text> joueurs = new ArrayList<Text>();

    @FXML VBox listeJoueurVBox;
    
    @FXML
    private void initialize() {
        try {
            socket = new Socket(App.getIp(), App.getPort());
            reception = new Thread(new Reception(this,socket));
            reception.start();
            //envoyer un message au serveur pour dire que le client est connecté
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            Message message = new Message(Transmission.CLIENT_CONNEXION, App.getPseudo());
            Gson gson = new Gson();
            String json = gson.toJson(message);
            out.println(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void retour() throws IOException, InterruptedException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
        Message message = new Message(Transmission.CLIENT_DECONNEXION, App.getPseudo());
        Gson gson = new Gson();
        String json = gson.toJson(message);
        out.println(json);
        reception.interrupt();
        reception.join();
        socket.close();
        App.setRoot("menu");
    }
}

//Thread de reception de message du serveur
class Reception implements Runnable {
    AttenteJoueurController attenteJoueurController;
    Object message;
    Socket socket;

    public Reception(AttenteJoueurController attenteJoueurController, Socket socket) {
        this.attenteJoueurController = attenteJoueurController;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                Gson gson = new Gson();
                Message message = gson.fromJson(line, Message.class);
                traitement(message);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void traitement(Message message) {
        if (message.getTransmition() == Transmission.SERVEUR_DECONNEXION) {
            Thread.currentThread().interrupt();
        }
        if(message.getTransmition() == Transmission.SERVEUR_CONNEXION) {
            miseAJourListeJoueur((ArrayList<String>) message.getMessage());
        }
    }

    private void miseAJourListeJoueur(ArrayList<String> l) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                attenteJoueurController.listeJoueurVBox.getChildren().clear();
                for (String s : l) {
                    Text t = new Text(s);
                    attenteJoueurController.listeJoueurVBox.getChildren().add(t);
                }
            }
        });
    }

}