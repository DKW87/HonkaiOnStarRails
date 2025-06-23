package com.github.dkw87.honkaionstarrails.service;

import com.github.dkw87.honkaionstarrails.repository.memory.CombatData;
import com.github.dkw87.honkaionstarrails.service.constant.MemoryConst;
import com.github.dkw87.honkaionstarrails.service.constant.chain.CombatPtrChains;
import com.github.dkw87.honkaionstarrails.service.constant.offset.CombatOffsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

public class DataManagerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataManagerService.class);

    private final Object workSignal = new Object();
    private final CombatData combatData;
    private final MemoryReadingService memoryReadingService;
    private final ScreenshotService screenshotService;

    private volatile boolean shutdownRequested;

    private int lastAnalyzedTurn;
    private Long gameassemblyModule;

    public DataManagerService() {
        LOGGER.info("Initializing DataManagerService...");
        combatData = CombatData.getInstance();
        memoryReadingService = MemoryReadingService.getInstance();
        screenshotService = ScreenshotService.getInstance();
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

                    while (gameIsInCombat() && !newTurnToAnalyzeIsAvailable() && !shutdownRequested) {
                        readUpdateTurnInformation();
                        synchronized (workSignal) {
                            workSignal.wait();
                        }
                    }

                    LOGGER.debug("Analyzing turn {}", combatData.getTurn());
                    updateCombatData();

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
        gameassemblyModule = memoryReadingService.getModuleBaseAddresses(MemoryConst.GAME_ASSEMBLY_MODULE);

        long turnAddress = memoryReadingService.readLongFromAddress(gameassemblyModule + CombatOffsets.TURN_COUNTER);
        int turnResult = memoryReadingService.followPTRChain(turnAddress, CombatPtrChains.TURN_COUNTER);

        combatData.setTurn(turnResult);
    }

    private boolean newTurnToAnalyzeIsAvailable() {
        return (combatData.getTurn() > lastAnalyzedTurn);
    }

    private void updateCombatData() {
        storeOffsets();
        threadSleep(50);
        combatData.setCurrentTurnImage(screenshotService.takeScreenshot());
        screenshotService.saveImage(combatData.getCurrentTurnImage(), String.format("turn_%d", combatData.getTurn()));
        LOGGER.debug("Turn {} succesfully analyzed!", combatData.getTurn());
        LOGGER.debug("Took and saved screenshot.");
        LOGGER.debug("Amount of enemies this turn: {}.", combatData.getAmountOfEnemies());
        LOGGER.debug("Amount of skill points this turn: {}.", combatData.getCurrentSkillpoints());
        LOGGER.debug("Waiting for next turn to start analyzing again.");
        lastAnalyzedTurn = combatData.getTurn();
        combatData.getCurrentTurnImage().flush();
    }

    private void storeOffsets() {
        combatData.setCurrentSkillpoints(readFromGameMemory(CombatOffsets.SKILLPOINTS, CombatPtrChains.SKILLPOINTS));
        combatData.setAmountOfEnemies(readFromGameMemory(CombatOffsets.AMOUNT_OF_ENEMIES, CombatPtrChains.AMOUNT_OF_ENEMIES));
    }

    private int readFromGameMemory(long offset, int[] ptrChain) {
        long address = memoryReadingService.readLongFromAddress(gameassemblyModule + offset);
        return memoryReadingService.followPTRChain(address, ptrChain);
    }

    private void threadSleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.error("Thread sleep interrupted");
        }
    }

}
