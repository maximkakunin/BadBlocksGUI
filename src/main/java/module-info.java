module com.kmprog.badblocksgui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.kmprog.badblocksgui to javafx.fxml;
    exports com.kmprog.badblocksgui;
}