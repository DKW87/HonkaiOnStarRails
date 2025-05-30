package com.github.dkw87.honkaionstarrails.controller;

import com.github.dkw87.honkaionstarrails.service.GameStateService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitoringController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringController.class);

    @FXML
    private Label monitoringLabel;

    private GameStateService gameStateService;

    @FXML
    private void initialize() {
        LOGGER.info("Initializing MonitoringController...");
        gameStateService = new GameStateService(monitoringLabel);
        gameStateService.start();
    }

    public GameStateService getGameStateService() {
        return gameStateService;
    }

}