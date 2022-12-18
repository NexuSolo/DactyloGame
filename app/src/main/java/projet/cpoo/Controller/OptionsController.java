package projet.cpoo.Controller;

import java.io.IOException;

import javafx.fxml.FXML;
import projet.cpoo.App;

public class OptionsController {

    @FXML
    private void retour() throws IOException {
        System.out.println("yo");
        App.setRoot("menu");
    } 
    
}