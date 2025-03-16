module com.example.pogoda {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.pogoda to javafx.fxml;
    exports com.example.pogoda;
}