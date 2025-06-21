package com.github.dkw87.honkaionstarrails.service;

import com.github.dkw87.honkaionstarrails.service.enumeration.GameState;
import com.github.dkw87.honkaionstarrails.shared.utility.dev.AOBScannerUtil;
import javafx.application.Platform;
import javafx.scene.control.Label;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameStateService {

    public static volatile GameState gameState;

    private static final Logger LOGGER = LoggerFactory.getLogger(GameStateService.class);

    // polling in millis
    private static final int SLOW_POLL = 1000;
    private static final int NORMAL_POLL = 500;
    private static final int FAST_POLL = 100;

    @Getter
    private final KeyInputService keyInputService;
    private final Label stateLabel;
    private final GameMonitorService gameMonitorService;
    private final CombatMonitorService combatMonitorService;

    private volatile boolean shutdownRequested;
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

    public void stop() {
        LOGGER.info("Stopping GameStateService...");
        shutdownRequested = true;
        combatMonitorService.getMemoryReadingService().cleanup();
    }

    private void startMonitoring() {
        Thread monitoringThread = new Thread(() -> {
            while (!shutdownRequested) {
                try {
                    // Your existing logic from createTask().call()
                    GameState newState;

                    if (!gameMonitorService.isGameRunning()) {
                        combatMonitorService.getMemoryReadingService().cleanup();
                        newState = setGameState(GameState.NOT_FOUND);
                    } else if (gameMonitorService.isGameFocused()) {
                        if (combatMonitorService.runMonitor()) {
                            newState = setGameState(GameState.EXECUTING);
                            AOBScannerUtil.scanForPattern();
                        } else {
                            newState = setGameState(GameState.IDLE);
                        }
                    } else {
                        combatMonitorService.getMemoryReadingService().cleanup();
                        newState = setGameState(GameState.FOUND);
                    }

                    Platform.runLater(() -> updateLabel(newState));

                    // Sleep based on current state
                    int sleepTime = switch (gameState) {
                        case EXECUTING -> FAST_POLL;
                        case IDLE -> NORMAL_POLL;
                        default -> SLOW_POLL;
                    };

                    Thread.sleep(sleepTime);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "GameStateService Thread");

        monitoringThread.setDaemon(true);
        monitoringThread.start();
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

    private void logStateChange(GameState state) {
        if (state != previousGameState) {
            LOGGER.info("GameState changed to: {}", state.getLabelText());
            previousGameState = state;
        }
    }

}
