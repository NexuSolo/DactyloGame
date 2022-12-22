package projet.cpoo.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import javafx.fxml.FXML;
import projet.cpoo.App;
import projet.cpoo.Message;

import java.io.InputStreamReader;

public class MultijoueurController {
    private Socket socket;

    @FXML
    private void initialize() {
        socket = App.getSocket();
        Thread reception = new Thread(new ReceptionJeux(socket));
        reception.start();
    }
    
}

class ReceptionJeux implements Runnable {
    private Socket socket;

    public ReceptionJeux(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                Gson gson = new Gson();
                Message message = gson.fromJson(line, Message.class);
                traitement(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void traitement(Message message) {
        switch (message.getTransmition()) {
            case SERVEUR_DECONNEXION:
                Thread.currentThread().interrupt();
                break;
            case SERVEUR_LISTE_MOT :
                LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) message.getMessage();
                affichageListMot(map);
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private void affichageListMot(LinkedTreeMap<String, Object> t) {
        List<String> listeMot = (List<String>) t.get("listeMot");
        int i = 0;
        for (String mot : listeMot) {
            if((boolean) t.get(Integer.toString(i++))) {
                System.out.println("BLEU" + mot);
            }
            System.out.println(mot);
        }
    }

}