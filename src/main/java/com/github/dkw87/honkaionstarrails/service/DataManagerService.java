package com.github.dkw87.honkaionstarrails.service;

import com.github.dkw87.honkaionstarrails.repository.memory.CombatData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataManagerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataManagerService.class);

    private final Object workSignal = new Object();
    private final CombatData combatData;

    private volatile boolean shutdownRequested;

    public DataManagerService() {
        LOGGER.info("Initializing DataManagerService...");
        combatData = CombatData.getInstance();
        startManaging();
    }

    public void stop() {
        LOGGER.info("Stopping DataManagerService...");
        shutdownRequested = true;
        synchronized (workSignal) {
            workSignal.notify();
        }
    }

    private void startManaging() {
        Thread managingThread = new Thread(() -> {
            while (!shutdownRequested) {
                try {
                    while (!gameIsInCombat() && !shutdownRequested) {
                        synchronized (workSignal) {
                            workSignal.wait();
                        }
                    }


                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "DataManagerService Thread");
        managingThread.setDaemon(true);
        managingThread.start();
    }

    public void notifyGameIsInCombat() {
        synchronized (workSignal) {
            workSignal.notify();
        }
    }

    private boolean gameIsInCombat() {
        return combatData.isInCombat();
    }

    private void readUpdateTurnInformation() {
        // TODO
    }

    private boolean newTurnToAnalyzeIsAvailable() {
        return (combatData.getTurn() > combatData.getLastAnalyzedTurn());
    }

}
