package projet.cpoo.Controller;

import java.io.IOException;
import java.net.Socket;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import projet.cpoo.App;

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
            socket.close();
            App.setRoot("attenteJoueur");
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
        App.setRoot("options");
    }

    @FXML
    private void quit() throws IOException {
        System.exit(0);
    }

}
