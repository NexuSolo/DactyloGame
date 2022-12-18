package projet.cpoo.Controller;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class JeuxController {
    private int pos = 0;
    
    @FXML
    private HBox ligne_1;

    @FXML
    private HBox ligne_2;

    @FXML
    private HBox ligne_3;

    @FXML
    private void initialize() {
        String text = "Hello World!";
        for(char c : text.toCharArray()) {
            Text t = new Text(String.valueOf(c));
            t.getStyleClass().add("text-to-do");
            ligne_1.getChildren().add(t);
        }
    }

    @FXML
    private void keyDetect(KeyEvent e) {
        if(e.getCode().isLetterKey()) {
            Text t = (Text) ligne_1.getChildren().get(pos);
            if(t.getText().equals(e.getText())) {
                t.getStyleClass().remove("text-to-do");
                t.getStyleClass().add("text-done");
                pos++;
            }
            else if (t.getText().equals(" ")) {
                t.getStyleClass().remove("text-to-do");
                t.getStyleClass().add("space-error");
                pos++;
            }
            else {
                t.getStyleClass().remove("text-to-do");
                t.getStyleClass().add("text-error");
                pos++;
            }
        }
        else if(e.getCode().isWhitespaceKey()) {
            Text t = (Text) ligne_1.getChildren().get(pos);
            if(!t.getText().equals(" ")) {
                t.getStyleClass().remove("text-to-do");
                t.getStyleClass().add("text-error");
            }
            pos++;
        } else {
            e.getCode();
            if (e.getCode().equals(KeyCode.BACK_SPACE)) {
                if(pos > 0) {
                    pos--;
                    Text t = (Text) ligne_1.getChildren().get(pos);
                    t.getStyleClass().remove("text-done");
                    t.getStyleClass().remove("text-error");
                    t.getStyleClass().remove("space-error");
                    t.getStyleClass().add("text-to-do");
                }
            }
        }
    }
    
}
