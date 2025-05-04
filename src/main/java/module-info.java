module com.github.dkw87.honkaionstarrails {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.sun.jna.platform;
    requires com.sun.jna;
    requires java.desktop;
    requires com.github.kwhat.jnativehook;

    opens com.github.dkw87.honkaionstarrails to javafx.fxml;
    exports com.github.dkw87.honkaionstarrails;
    exports com.github.dkw87.honkaionstarrails.controller;
    exports com.github.dkw87.honkaionstarrails.service;
    opens com.github.dkw87.honkaionstarrails.controller to javafx.fxml;
}