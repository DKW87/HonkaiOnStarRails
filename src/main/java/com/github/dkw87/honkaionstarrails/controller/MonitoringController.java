package com.github.dkw87.honkaionstarrails.controller;

import com.github.dkw87.honkaionstarrails.service.GameStateService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MonitoringController {
    @FXML
    private Label monitoringLabel;

    private GameStateService gameStateService;

    @FXML
    private void initialize() {
        gameStateService = new GameStateService(monitoringLabel);
        gameStateService.start();
    }

    // todo: implement properly when closing the program
    private void shutdown() {
        gameStateService.stop();
    }

}