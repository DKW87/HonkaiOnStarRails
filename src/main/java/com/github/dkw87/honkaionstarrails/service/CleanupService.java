package com.github.dkw87.honkaionstarrails.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Makes sure all traces of this program are removed from memory to avoid being flagged
 * by any anti-cheat software program (when not using this for HSR).
 * The shutdown hook in the JVM should ensure these are being properly cleaned up
 * in the event the program crashes or is force closed by the user.
 */
public class CleanupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CleanupService.class);

    private final GameStateService gameStateService;
    private final KeyInputService keyInputService;

    public CleanupService(GameStateService gameStateService, KeyInputService keyInputService) {
        LOGGER.info("Registering CleanupService...");
        this.gameStateService = gameStateService;
        this.keyInputService = keyInputService;
        addShutdownHookToJVM();
    }

    public void unregister() {
        keyInputService.unregister();
        gameStateService.stop();
    }

    // in case of application freeze or unexpected shutdown to try and clean up these components anyway
    private void addShutdownHookToJVM() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                unregister();
            } catch (Exception e) {
                LOGGER.error("Shutdown hook failed to clean up", e);
            }
        },"JVM Shutdown Thread"));
    }

}
