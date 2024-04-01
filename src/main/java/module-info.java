module com.example.mathfunctions {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.mathfunctions to javafx.fxml;
    exports com.example.mathfunctions;
}