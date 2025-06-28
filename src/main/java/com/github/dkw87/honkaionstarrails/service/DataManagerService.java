package com.github.dkw87.honkaionstarrails.service;

import com.github.dkw87.honkaionstarrails.repository.memory.CombatData;
import com.github.dkw87.honkaionstarrails.service.constant.MemoryConst;
import com.github.dkw87.honkaionstarrails.service.constant.chain.CombatPtrChains;
import com.github.dkw87.honkaionstarrails.service.constant.offset.CombatOffsets;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataManagerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataManagerService.class);

    private final Object workSignal = new Object();
    private final CombatData combatData;
    private final MemoryReadingService memoryReadingService;
    private final ScreenshotService screenshotService;
    private final OCRService ocrService;

    private volatile boolean shutdownRequested;

    private int lastAnalyzedTurn;
    private Long gameassemblyModule;
    private Rectangle enemyTurnLabelLocation;

    public DataManagerService() {
        LOGGER.info("Initializing DataManagerService...");
        combatData = CombatData.getInstance();
        memoryReadingService = MemoryReadingService.getInstance();
        screenshotService = ScreenshotService.getInstance();
        ocrService = OCRService.getInstance();
        enemyTurnLabelLocation = new Rectangle(1773, 1045, 255, 35);
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
                        resetOnEndCombat();
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

                    if (gameIsInCombat() && newTurnToAnalyzeIsAvailable() && !shutdownRequested) {
                        LOGGER.debug("Analyzing turn {}", combatData.getTurn());
                        updateCombatData();
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

    private void resetOnEndCombat() {
        lastAnalyzedTurn = 0;
    }

    public void notifyOfWork() {
        synchronized (workSignal) {
            workSignal.notify();
        }
    }

    private boolean gameIsInCombat() {
        return combatData.isInCombat();
    }

    private void readUpdateTurnInformation() {
        gameassemblyModule = memoryReadingService.getModuleBaseAddresses(MemoryConst.GAME_ASSEMBLY_MODULE);
        combatData.setTurn(readFromGameMemory(CombatOffsets.TURN_COUNTER, CombatPtrChains.TURN_COUNTER));
    }

    private boolean newTurnToAnalyzeIsAvailable() {
        return (combatData.getTurn() > lastAnalyzedTurn);
    }

    private void updateCombatData() {
        storeOffsets();
//        threadSleep(50);
//        combatData.setCurrentTurnImage(screenshotService.takeScreenshot(null));
//        screenshotService.saveImage(combatData.getCurrentTurnImage(), String.format("turn_%d", combatData.getTurn()));
        boolean enemyTurn = isEnemyTurn();

        LOGGER.debug("Turn {} succesfully analyzed!", combatData.getTurn());
//        LOGGER.debug("Took and saved screenshot.");
        LOGGER.debug("Who's turn: {}", (enemyTurn ? "Enemy" : "Player"));
        LOGGER.debug("Amount of enemies this turn: {}.", combatData.getAmountOfEnemies());
        LOGGER.debug("Amount of skill points this turn: {}.", combatData.getCurrentSkillpoints());
        LOGGER.debug("Waiting for next turn to start analyzing again.");
        lastAnalyzedTurn = combatData.getTurn();
//        combatData.getCurrentTurnImage().flush();
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

    private boolean isEnemyTurn() {
        String textToContain = "enemy's turn";
        BufferedImage imageRegionToSearch = screenshotService.takeScreenshot(enemyTurnLabelLocation);
        return ocrService.doesImageContainText(textToContain, imageRegionToSearch);
    }

}
