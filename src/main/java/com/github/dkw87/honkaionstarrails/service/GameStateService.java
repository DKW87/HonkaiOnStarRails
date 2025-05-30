package com.github.dkw87.honkaionstarrails.service;

import com.github.dkw87.honkaionstarrails.service.enumeration.GameState;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class GameStateService {

    public static volatile GameState gameState;

    private static final Logger LOGGER = LoggerFactory.getLogger(GameStateService.class);

    // polling in millis
    private static final int SLOW_POLL = 1000;
    private static final int NORMAL_POLL = 500;
    private static final int FAST_POLL = 100;

    private final Label stateLabel;
    private final GameMonitorService gameMonitorService;
    private final KeyInputService keyInputService;
    private final CombatMonitorService combatMonitorService;
    private final AtomicBoolean shutdownRequested = new AtomicBoolean(false);

    private ScheduledService<GameState> stateService;
    private GameState previousGameState;

    public GameStateService(Label statusLabel) {
        LOGGER.info("Initializing GameStateService...");
        stateLabel = statusLabel;
        this.gameMonitorService = new GameMonitorService();
        this.keyInputService = new KeyInputService();
        this.combatMonitorService = new CombatMonitorService();
        keyInputService.initialize();
        startMonitoring();
    }

    public void start() {
        if (stateService != null) {
            stateService.restart();
        }
    }

    public void stop() {
        if (Platform.isFxApplicationThread()) {
            if (stateService != null) {
                LOGGER.info("Unregistering GameStateService...");
                stateService.cancel();
            } else {
                LOGGER.info("Unable to unregister GameStateService, requesting threadsafe shutdown...");
                shutdownRequested.set(true);
            }
        }
        combatMonitorService.getMemoryReadingService().cleanup();
    }

    public KeyInputService getKeyInputService() {
        return keyInputService;
    }

    private void startMonitoring() {
        stateService = new ScheduledService<>() {

            @Override
            protected Task<GameState> createTask() {
                return new Task<>() {
                    @Override
                    protected GameState call() {

                        if (shutdownRequested.get()) {
                            threadSafeStop();
                            return setGameState(GameState.SHUTDOWN);
                        }

                        if (!gameMonitorService.isGameRunning()) {
                            combatMonitorService.getMemoryReadingService().cleanup();
                            return setGameState(GameState.NOT_FOUND);
                        }

                        if (gameMonitorService.isGameFocused()) {

                            if (combatMonitorService.runMonitor()) {
                                return setGameState(GameState.EXECUTING);
                            } else {
                                return setGameState(GameState.IDLE);
                            }
                        } else {
                            combatMonitorService.getMemoryReadingService().cleanup();
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
        switch (gameState) {
            case GameState.EXECUTING -> stateService.setPeriod(Duration.millis(FAST_POLL));
            case GameState.IDLE -> stateService.setPeriod(Duration.millis(NORMAL_POLL));
            default -> stateService.setPeriod(Duration.millis(SLOW_POLL));
        }
    }

    private GameState setGameState(GameState state) {
        logStateChange(state);
        gameState = state;
        return gameState;
    }

    private void updateLabel(GameState state) {
        stateLabel.setText(state.getLabelText());
        stateLabel.setStyle(state.getLabelStyle());
    }

    private void threadSafeStop() {
        Platform.runLater(() -> {
            if (stateService != null) {
                LOGGER.info("Unregistering GameStateService from safe thread...");
                stateService.cancel();
            }
        });
        combatMonitorService.getMemoryReadingService().cleanup();
    }

    private void logStateChange(GameState state) {
        if (state != previousGameState) {
            LOGGER.info("GameState changed to: {}", state.getLabelText());
            previousGameState = state;
        }
    }

}
