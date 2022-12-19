/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package projet.cpoo;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;
    private static String pseudo = "Joueur";
    private static String ip = "localhost";
    private static int port = 5000;

    public static void main(String[] args) {
        launch();
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        scene = new Scene(loadFXML("menu"), 1240, 720);
        primaryStage.setMinHeight(650);
        primaryStage.setMinWidth(1160);
        primaryStage.setScene(scene);
        primaryStage.show();
        // primaryStage.toFront();
        //La fonction toFront() ne fonctionne pas chez moi donc j'utilise a la place :
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setAlwaysOnTop(false);
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static String getPseudo() {
        return pseudo;
    }

    public static void setPseudo(String pseudo) {
        App.pseudo = pseudo;
    }

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        App.ip = ip;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        App.port = port;
    }

}
