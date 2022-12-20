package projet.cpoo.Controller;

import java.net.Socket;

import com.google.gson.Gson;

import javafx.fxml.FXML;
import projet.cpoo.App;
import projet.cpoo.Message;
import projet.cpoo.Transmission;

public class AttenteJoueurController {
    private Socket socket;
    
    @FXML
    private void initialize() {
        try {
            socket = new Socket(App.getIp(), App.getPort());
            System.out.println("Connexion établie");
            Message message = new Message(Transmission.CLIENT_CONNEXION, App.getPseudo());
            Gson gson = new Gson();
            String json = gson.toJson(message);
            socket.getOutputStream().write(json.getBytes());
            System.out.println("Message envoyé");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
