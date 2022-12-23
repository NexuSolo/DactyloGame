package projet.cpoo.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import projet.cpoo.App;
import projet.cpoo.Message;
import projet.cpoo.TypeMot;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public final class MultijoueurController {
    private Socket socket;

    @FXML
    private HBox ligne_1;

    @FXML
    private HBox ligne_2;

    @FXML
    private HBox ligne_3;

    @FXML
    private void initialize() {
        socket = App.getSocket();
        Thread reception = new Thread(new ReceptionJeux(this));
        reception.start();
    }

    protected Socket getSocket() {
        return socket;
    }

    protected void ajoutMot(String s, TypeMot typeMot) throws UnsupportedEncodingException {
        s = new String(s.getBytes(), "UTF-8");
        Text t = new Text(s);
        switch (typeMot) {
            case WORD_TO_DO :
                t.getStyleClass().add("text-to-do");
                break;
            case WORD_CORRECT :
                t.getStyleClass().add("text-done");
                break;
            case WORD_WRONG :
                t.getStyleClass().add("text-error");
                break;
            case WORD_LIFE :
                t.getStyleClass().add("text-life");
                break;
            case WORD_ATTACK :
                t.getStyleClass().add("text-attack");
                break;
            default:
                break;
        }
        Text espace = new Text(" ");
        if (ligne_1.getChildren().size() < 10) {
            ligne_1.getChildren().add(t);
            ligne_1.getChildren().add(espace);
        } else if (ligne_2.getChildren().size() < 10) {
            ligne_2.getChildren().add(t);
            ligne_2.getChildren().add(espace);
        } else if (ligne_3.getChildren().size() < 10) {
            ligne_3.getChildren().add(t);
            ligne_3.getChildren().add(espace);
        }
    }
    
}

class ReceptionJeux implements Runnable {
    private MultijoueurController multijoueurController;

    public ReceptionJeux(MultijoueurController multijoueurController) {
        this.multijoueurController = multijoueurController;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(multijoueurController.getSocket().getInputStream()));
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
    private void traitement(Message message) throws UnsupportedEncodingException {
        switch (message.getTransmition()) {
            case SERVEUR_DECONNEXION:
                Thread.currentThread().interrupt();
                break;
            case SERVEUR_LISTE_MOT :
                LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) message.getMessage();
                Platform.runLater(() -> {
                try {
                    affichageListMot(map);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }});
                break;
            case SERVEUR_MOT :
                LinkedTreeMap<String, Object> map2 = (LinkedTreeMap<String, Object>) message.getMessage();
                String s = map2.keySet().iterator().next();
                TypeMot typeMot = TypeMot.valueOf((String) map2.get(s));
                Platform.runLater( () -> {
                    try {
                        multijoueurController.ajoutMot(s, typeMot);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                });
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private void affichageListMot(LinkedTreeMap<String, Object> t) throws UnsupportedEncodingException {
        List<String> listeMot = (List<String>) t.get("listeMot");
        for(int i = 0; i < listeMot.size(); i++) {
            TypeMot typeMot = TypeMot.valueOf((String) t.get("" + i));
            System.out.println(listeMot.get(i) + " " + typeMot);
            multijoueurController.ajoutMot(listeMot.get(i), typeMot);
        }
    }

}