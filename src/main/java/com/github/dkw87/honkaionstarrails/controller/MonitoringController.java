package com.github.dkw87.honkaionstarrails.controller;

import com.github.dkw87.honkaionstarrails.service.MonitoringService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MonitoringController {
    @FXML
    private Label monitoringLabel;
    @FXML
    private Label welcomeText;

    private MonitoringService monitoringService;

    @FXML
    private void initialize() {
        monitoringService = new MonitoringService(monitoringLabel);
        monitoringService.start();
    }

    @FXML
    private void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    private void shutdown() {
        monitoringService.stop();
    }

}