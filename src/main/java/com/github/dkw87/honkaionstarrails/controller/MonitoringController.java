package com.github.dkw87.honkaionstarrails.controller;

import com.github.dkw87.honkaionstarrails.service.MonitoringService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MonitoringController {
    @FXML
    private Label monitoringLabel;

    private MonitoringService monitoringService;

    @FXML
    private void initialize() {
        monitoringService = new MonitoringService(monitoringLabel);
        monitoringService.start();
    }

    private void shutdown() {
        monitoringService.stop();
    }

}