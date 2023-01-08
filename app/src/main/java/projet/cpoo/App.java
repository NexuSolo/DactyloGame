package projet.cpoo;

import java.io.IOException;
import java.net.Socket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;
    private static Socket socket;

    public static void main(String[] args) {
        Settings.loadProperties();
        launch();
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        scene = new Scene(loadFXML("menu"),1280, 720);
        primaryStage.setMinHeight(675);
        primaryStage.setMinWidth(1200);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest( event -> {
            System.exit(0);
        });
        primaryStage.show();
        // primaryStage.toFront();
        //La fonction toFront() ne fonctionne pas chez moi donc j'utilise a la place :
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setAlwaysOnTop(false);
    }

    public static void setRoot(String fxml) throws IOException {
        Platform.runLater(() -> {
            try {
                scene.setRoot(loadFXML(fxml));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void setRoot(String fxml, Object controller) throws IOException {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource(fxml + ".fxml"));
            fxmlLoader.setController(controller);
            try {
                scene.setRoot(fxmlLoader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static Socket getSocket() {
        return socket;
    }

    public static void setSocket(Socket socket) {
        App.socket = socket;
    }

}
