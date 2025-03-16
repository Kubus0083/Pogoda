module com.example.pogoda {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.pogoda to javafx.fxml;
    exports com.example.pogoda;
}