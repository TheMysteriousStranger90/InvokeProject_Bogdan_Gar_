module org.invokeproject {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.invokeproject to javafx.fxml;
    exports org.invokeproject;
}