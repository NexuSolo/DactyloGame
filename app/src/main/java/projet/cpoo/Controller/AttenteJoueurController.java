package projet.cpoo.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import projet.cpoo.App;
import projet.cpoo.Message;
import projet.cpoo.Transmission;

public class AttenteJoueurController {
    private Socket socket;
    private Thread reception;
    List<Text> joueurs = new ArrayList<Text>();

    @FXML
    VBox listeJoueurVBox;

    @FXML
    private MenuButton langueMenuButton;

    @FXML
    private MenuItem francaisMenuItem;

    @FXML
    private MenuItem englishMenuItem;

    @FXML
    private CheckBox accentCheckBox;
    
    @FXML
    private void initialize() {
        try {
            socket = new Socket(App.getIp(), App.getPort());
            reception = new Thread(new Reception(this,socket));
            reception.start();
            //envoyer un message au serveur pour dire que le client est connecté
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
            map.put("pseudo", App.getPseudo());
            Message message = new Message(Transmission.CLIENT_CONNEXION, map);
            Gson gson = new Gson();
            String json = gson.toJson(message);
            out.println(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void lancer() throws IOException {
        Message message = new Message(Transmission.CLIENT_LANCER, null);
        envoiMessage(message);
    }

    @FXML
    private void modificationOption(ActionEvent e) throws IOException {
        if(e.getSource() == francaisMenuItem) {
            langueMenuButton.setText("Français");
        } else if(e.getSource() == englishMenuItem) {
            langueMenuButton.setText("English");
        }
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
        map.put("accent", accentCheckBox.isSelected());
        map.put("langue", langueMenuButton.getText());
        Message message = new Message(Transmission.CLIENT_OPTION, map);
        envoiMessage(message);
    }

    @FXML
    private void retour() throws IOException, InterruptedException {
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
        map.put("pseudo", App.getPseudo());
        Message message = new Message(Transmission.CLIENT_DECONNEXION, map);
        envoiMessage(message);
        reception.interrupt();
        reception.join();
        socket.close();
        App.setRoot("menu");
    }

    void envoiMessage(Message message) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
        Gson gson = new Gson();
        String json = gson.toJson(message);
        out.println(json);
    }

    void miseAJourOptions(boolean b, String langue) {
        Platform.runLater(() -> {
            accentCheckBox.setSelected(b);
            String s;
            try {
                s = new String(langue.getBytes(), "UTF-8");
                langueMenuButton.setText(s);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
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

    @SuppressWarnings("unchecked")
    private void traitement(Message message) throws IOException {
        switch(message.getTransmition()) {
            case SERVEUR_DECONNEXION:
                Thread.currentThread().interrupt();
                break;
            case SERVEUR_CONNEXION:
                LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) message.getMessage();
                miseAJourListeJoueur((ArrayList<String>) map.get("listeJoueur"));
                break;
            case SERVEUR_OPTION:
                LinkedTreeMap<String, Object> map2 = (LinkedTreeMap<String, Object>) message.getMessage();
                boolean accent = (boolean) map2.get("accent");
                String langue = (String) map2.get("langue");
                attenteJoueurController.miseAJourOptions(accent, langue);
                break;
            case SERVEUR_LANCER:
                App.setRoot("jeu");
                break;
            default:
                break;
        }
    }

    private void miseAJourListeJoueur(ArrayList<String> l) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                attenteJoueurController.listeJoueurVBox.getChildren().clear();
                Text joueurs = new Text("Joueurs :");
                joueurs.getStyleClass().add("textListeJoueur");
                attenteJoueurController.listeJoueurVBox.getChildren().add(joueurs);
                Pane p = new Pane();
                p.getStyleClass().add("separator");
                attenteJoueurController.listeJoueurVBox.getChildren().add(p);
                for (String s : l) {
                    Text t = new Text(s);
                    t.getStyleClass().add("textListeJoueur");
                    attenteJoueurController.listeJoueurVBox.getChildren().add(t);
                }
            }
        });
    }

}