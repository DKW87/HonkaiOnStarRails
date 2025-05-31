module com.github.dkw87.honkaionstarrails {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.sun.jna.platform;
    requires com.sun.jna;
    requires java.desktop;
    requires com.github.kwhat.jnativehook;
    requires org.slf4j;

    exports com.github.dkw87.honkaionstarrails;
    exports com.github.dkw87.honkaionstarrails.controller;
    exports com.github.dkw87.honkaionstarrails.service;
    exports com.github.dkw87.honkaionstarrails.service.win32interface;
    exports com.github.dkw87.honkaionstarrails.service.enumeration;

    opens com.github.dkw87.honkaionstarrails to javafx.fxml;
    opens com.github.dkw87.honkaionstarrails.controller to javafx.fxml;
}