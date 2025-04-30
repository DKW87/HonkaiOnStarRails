package com.github.dkw87.honkaionstarrails.service;

import com.github.dkw87.honkaionstarrails.service.monitor.GameMonitorService;
import com.github.dkw87.honkaionstarrails.shared.enumeration.GameState;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.util.Duration;


public class GameStateService {

    public static volatile GameState gameState;

    // polling in millis
    private static final int SLOW_POLL = 1000;
    private static final int NORMAL_POLL = 500;
    private static final int FAST_POLL = 100;

    private final Label stateLabel;
    private final GameMonitorService gameMonitorService;

    private ScheduledService<GameState> stateService;

    public GameStateService(Label statusLabel) {
        stateLabel = statusLabel;
        this.gameMonitorService = new GameMonitorService();
        startMonitoring();
    }

    public void start() {
        if (stateService != null) {
            stateService.restart();
        }
    }

    public void stop() {
        if (stateService != null) {
            stateService.cancel();
        }
    }

    private void startMonitoring() {
        stateService = new ScheduledService<>() {

            @Override
            protected Task<GameState> createTask() {
                return new Task<>() {
                    @Override
                    protected GameState call() {
                        if (!gameMonitorService.isGameRunning()) {
                            return setGameState(GameState.NOT_FOUND);
                        }

                        if (gameMonitorService.isGameFocused()) {
                            return setGameState(GameState.IDLE);
                        } else {
                            return setGameState(GameState.FOUND);
                        }
                    }
                };
            }
        };

        stateService.setPeriod(Duration.millis(SLOW_POLL));

        stateService.setOnSucceeded(event -> {
            GameState monitorStatus = stateService.getValue();
            updateLabel(monitorStatus);
            adjustPollingByState();
        });
    }

    private void adjustPollingByState() {
        if (gameState == GameState.NOT_FOUND || gameState == GameState.FOUND) {
            stateService.setPeriod(Duration.millis(SLOW_POLL));
        } else {
            stateService.setPeriod(Duration.millis(NORMAL_POLL));
        }
    }

    private GameState setGameState(GameState state) {
        gameState = state;
        return gameState;
    }

    private void updateLabel(GameState state) {
        stateLabel.setText(state.getLabelText());
        stateLabel.setStyle(state.getLabelStyle());
    }


}
