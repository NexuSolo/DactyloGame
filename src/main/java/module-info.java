module fr.cpoo {
    requires javafx.controls;
    requires javafx.fxml;

    opens fr.cpoo to javafx.fxml;
    exports fr.cpoo;
}
