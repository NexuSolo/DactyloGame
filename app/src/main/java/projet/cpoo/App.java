/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package projet.cpoo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

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
        Properties prop = new Properties();
        try {
            File file = new File("config.properties");
            if(!file.exists()) {
                createProperties();
            }
            prop.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pseudo = prop.getProperty("pseudo", "joueur");
        ip = prop.getProperty("ip", "localhost");
        try {
            port = Integer.parseInt(prop.getProperty("port", "5000"));
        } catch (NumberFormatException e) {
            prop.setProperty("port", "5000");
            port = 5000;
        }
        launch();
    }

    public static void createProperties() {
        Properties prop = new Properties();
        try {
            prop.setProperty("pseudo", pseudo);
            prop.setProperty("ip", ip);
            prop.setProperty("port", String.valueOf(port));
            prop.store(new FileOutputStream("config.properties"), "Configuration du jeu");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        scene = new Scene(loadFXML("menu"),1280, 720);
        primaryStage.setMinHeight(675);
        primaryStage.setMinWidth(1200);
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
        createProperties();
    }

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        App.ip = ip;
        createProperties();
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        App.port = port;
        createProperties();
    }

}
