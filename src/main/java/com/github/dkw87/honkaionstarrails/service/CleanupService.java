package com.github.dkw87.honkaionstarrails.service;

public class CleanupService {

    private final GameStateService gameStateService;
    private final KeyInputService keyInputService;

    public CleanupService(GameStateService gameStateService, KeyInputService keyInputService) {
        this.gameStateService = gameStateService;
        this.keyInputService = keyInputService;
        addShutdownHookToJVM();
    }

    public void unregister() {
        System.out.println("Cleaning up KeyInputService");
        keyInputService.unregister();

        System.out.println("Cleaning up GameStateService");
        gameStateService.stop();
    }

    // in case of application freeze or unexpected shutdown to try and clean up these components anyway
    private void addShutdownHookToJVM() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                unregister();
            } catch (Exception e) {
                System.err.println("Failed to unregister due to: " + e.getMessage());
            }
        }));
    }

}
