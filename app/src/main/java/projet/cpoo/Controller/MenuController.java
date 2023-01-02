package projet.cpoo.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import projet.cpoo.App;
import projet.cpoo.Message;
import projet.cpoo.Transmission;

public class MenuController {
    @FXML
    private Button entrainementButton;
    @FXML
    private Button soloButton;
    @FXML
    private Button multijoueurButton;
    @FXML
    private Button optionsButton;
    @FXML
    private Button quitterButton;
    @FXML
    private Polygon entrainementArrow;
    @FXML
    private Polygon soloArrow;
    @FXML
    private Polygon multijoueurArrow;
    @FXML
    private Polygon optionsArrow;
    @FXML
    private Polygon quitterArrow;
    @FXML
    private Text erreurConnexionMenu;

    @FXML
    private void highlightButton(MouseEvent e) throws IOException {
        Button button = (Button) e.getSource();
        if(button == soloButton) {
            soloArrow.setVisible(true);
        }
        else if(button == multijoueurButton) {
            multijoueurArrow.setVisible(true);
        }
        else if(button == optionsButton) {
            optionsArrow.setVisible(true);
        }
        else if(button == quitterButton) {
            quitterArrow.setVisible(true);
        }
        else if(button == entrainementButton) {
            entrainementArrow.setVisible(true);
        }
    }

    @FXML
    private void removeHighlightButton(MouseEvent e) throws IOException { 
        Button button = (Button) e.getSource();
        if(button == soloButton) {
            soloArrow.setVisible(false);
        }
        else if(button == multijoueurButton) {
            multijoueurArrow.setVisible(false);
        }
        else if(button == optionsButton) {
            optionsArrow.setVisible(false);
        }
        else if(button == quitterButton) {
            quitterArrow.setVisible(false);
        }
        else if(button == entrainementButton) {
            entrainementArrow.setVisible(false);
        }
    }

    @FXML
    private void switchToEntrainement() throws IOException {
        App.setRoot("entrainement");
    }

    @FXML
    private void switchToSolo() throws IOException {
        App.setRoot("solo");
    }

    @FXML
    private void switchToMultijoueur() throws IOException {
        try(Socket socket = new Socket(App.getIp(), App.getPort())) {
            Message message = new Message(Transmission.CLIENT_CONNEXION_SERVER_RUN, null);
            Gson gson = new Gson();
            String json = gson.toJson(message);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            out.println(json);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String s = in.readLine();
            Message m = gson.fromJson(s, Message.class);
            LinkedTreeMap map = (LinkedTreeMap) m.getMessage();
            boolean b = (boolean) map.get("bool");
            if(b) {
                new Thread(() -> {
                    try {
                        erreurConnexionMenu.setOpacity(1);
                        Thread.sleep(4000);
                        erreurConnexionMenu.setOpacity(0);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }).start();
            }
            else {
                socket.close();
                App.setRoot("attenteJoueur");
            }
        } catch (IOException e) {
            new Thread(() -> {
                try {
                    erreurConnexionMenu.setOpacity(1);
                    Thread.sleep(4000);
                    erreurConnexionMenu.setOpacity(0);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }).start();
        }
    }

    @FXML
    private void switchToOptions() throws IOException {
        App.setRoot("opt2");
    }

    @FXML
    private void quit() throws IOException {
        System.exit(0);
    }

}
