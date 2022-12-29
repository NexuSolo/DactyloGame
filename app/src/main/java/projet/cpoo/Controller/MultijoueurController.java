package projet.cpoo.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.CharsetDecoder;
import java.util.List;

import com.google.common.graph.ElementOrder.Type;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import projet.cpoo.App;
import projet.cpoo.Message;
import projet.cpoo.Transmission;
import projet.cpoo.TypeMot;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public final class MultijoueurController extends SoloController{
    private Socket socket;
    protected int positionMot = 0;
    @FXML
    protected VBox classementVBox;
    protected Thread reception;

    @FXML
    protected final void initialize() {
        socket = App.getSocket();
        reception = new Thread(new ReceptionJeux(this));
        initializeText();
        reception.start();
    }

    protected Socket getSocket() {
        return socket;
    }

    protected void ajoutMot(String s, TypeMot typeMot) throws UnsupportedEncodingException {
        for (char c : s.toCharArray()) {
            Text t = new Text(String.valueOf(c));
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
            if (nombreMotLigne_1 < 5) {
                ligne_1.getChildren().add(t);
            } else if (nombreMotLigne_2 < 5) {
                ligne_2.getChildren().add(t);
            } else if (nombreMotLigne_3 < 5) {
                ligne_3.getChildren().add(t);
            }
        }
        Text espace = new Text(" ");
        if (nombreMotLigne_1 < 5) {
            ligne_1.getChildren().add(espace);
            nombreMotLigne_1++;
        } else if (nombreMotLigne_2 < 5) {
            ligne_2.getChildren().add(espace);
            nombreMotLigne_2++;
        } else if (nombreMotLigne_3 < 5) {
            ligne_3.getChildren().add(espace);
            nombreMotLigne_3++;
        }
    }

    protected final boolean motDegats(){
        Message m;
        m = new Message(Transmission.CLIENT_VALIDATION,null);
        try {
            envoiMessage(socket, m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void envoiMessage(Socket socket, Message message) throws IOException {
        System.out.println("Message envoye " + socket);
        Gson gson = new Gson();
        String json = gson.toJson(message);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"),true);
        out.println(json);
        System.out.println("Fin Message envoye ");

    }

    protected final void remplirMots() {

    }

    protected void mapAccent(KeyEvent e,LinkedTreeMap<String, Object> map) {
        KeyCode code = e.getCode();
        switch (code) {
            case DEAD_CIRCUMFLEX : if (!e.isShiftDown()) map.put("lettre","circonflexe");
            else map.put("lettre","trema"); break;
            case DEAD_DIAERESIS : map.put("lettre","trema");
            default : break;
        }
    }

    public void keyDetect(KeyEvent e) {
        Message m;
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
        if(e.getCode().isLetterKey() || isAccentedChar(inputToChars(e)) || inputToChars(e) == "-"){
            System.out.println("Text = " + formatString(e.getText(),e.isShiftDown()));
            map.put("lettre",formatString(e.getText(),e.isShiftDown()));
        
        }
        else if (e.getCode() == KeyCode.BACK_SPACE) {
            map.put("lettre","backspace");
        }
        else if(e.getCode() == KeyCode.SPACE){
            // validationMot(false);
            // updateVies();
            // return;
            map.put("lettre"," ");
        }
        else mapAccent(e, map);
        if (map.keySet().size() == 0) return;
        m = new Message(Transmission.CLIENT_LETTRE,map);
        try {
            envoiMessage(socket, m);
        } catch (Exception ex) {
            ex.printStackTrace();
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
            BufferedReader in = new BufferedReader(new InputStreamReader(multijoueurController.getSocket().getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                // System.out.println(line);
                Gson gson = new Gson();
                Message message = gson.fromJson(line, Message.class);
                traitement(message);
            }
        } catch (IOException e) {
            try {
                App.getSocket().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void traitement(Message message) throws IOException {
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
            case SERVEUR_MOT_SUIVANT :
                Platform.runLater( () -> {
                    multijoueurController.positionMot = 0;
                    multijoueurController.pos = 0;

                });
                break;
            case SERVEUR_VALIDATION : 
                Platform.runLater( () -> {
                    multijoueurController.validationMot(false);
                    multijoueurController.updateVies();
                    multijoueurController.positionMot = 0;
                    multijoueurController.pos = 0;
                });
                break;
            case SERVEUR_LETTRE_VALIDE :
                Text tV = (Text)multijoueurController.ligne_1.getChildren().get(multijoueurController.positionMot++);
                tV.getStyleClass().remove("text-attack");
                tV.getStyleClass().remove("text-life");
                tV.getStyleClass().add("text-done");
                break;
            case SERVEUR_LETTRE_INVALIDE :
                Text tI = (Text)multijoueurController.ligne_1.getChildren().get(multijoueurController.positionMot++);
                tI.getStyleClass().remove("text-attack");
                tI.getStyleClass().remove("text-life");
                tI.getStyleClass().add("text-error");
                break;
            case SERVEUR_BACKSPACE :
                Text t = (Text)multijoueurController.ligne_1.getChildren().get(--multijoueurController.positionMot);
                LinkedTreeMap<String, Object> mapType = (LinkedTreeMap<String, Object>) message.getMessage();
                TypeMot type = TypeMot.valueOf((String) mapType.get("type"));
                t.getStyleClass().remove("text-done");
                t.getStyleClass().remove("text-error");
                if (type == TypeMot.WORD_ATTACK) t.getStyleClass().add("text-attack");
                if (type == TypeMot.WORD_LIFE) t.getStyleClass().add("text-life");
                
                break;
            case SERVEUR_CLASSEMENT :
                    miseAJourClassement((LinkedTreeMap<String, Object>) message.getMessage());
                break;
            case CHANGEMENT_VIE :
                LinkedTreeMap<String, Object> mapMess = (LinkedTreeMap<String, Object>) message.getMessage();
                multijoueurController.vies = ((Double)mapMess.get("vie")).intValue();
                multijoueurController.updateVies();
                break;
            case SERVEUR_PERDU :
                finDePartie(false,(LinkedTreeMap<String, Object>) message.getMessage());
                break;
            case SERVEUR_GAGNER :
                finDePartie(true,(LinkedTreeMap<String, Object>) message.getMessage());
                break;
            default:
                break;
        }
    }
    
    @SuppressWarnings("unchecked")
    private void miseAJourClassement(LinkedTreeMap<String, Object> message) {
        Platform.runLater(() -> {
            multijoueurController.classementVBox.getChildren().clear();
            Text t = new Text("Classement");
            t.getStyleClass().add("textListeJoueur");
            multijoueurController.classementVBox.getChildren().add(t);
            Pane pane = new Pane();
            pane.getStyleClass().add("separator");
            multijoueurController.classementVBox.getChildren().add(pane);
            List<String> joueurs = (List<String>) message.get("liste");
            for (String joueur : joueurs) {
                Text text = new Text(joueur);
                text.getStyleClass().add("textListeJoueur");
                multijoueurController.classementVBox.getChildren().add(text);
            }
        });
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

    @SuppressWarnings("unchecked")
    private void finDePartie(boolean gagner, LinkedTreeMap<String, Object> t) {
        List<String> listeJoueurs = (List<String>) t.get("listeJoueurs");
        List<Integer> listeScore = (List<Integer>) t.get("listeScore");
        try {
            if(gagner) {
                ClassementController c = new ClassementController(listeJoueurs,listeScore,true);
                App.setRoot("classement",c);
            } else {
                ClassementController c = new ClassementController(listeJoueurs,listeScore,false);
                App.setRoot("classement",c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}