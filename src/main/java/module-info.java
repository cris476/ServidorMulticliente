module com.servidor {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.servidor to javafx.fxml;
    exports com.servidor;
}
