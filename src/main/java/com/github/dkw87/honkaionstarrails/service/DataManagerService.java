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
    private final Rectangle enemyTurnLabelLocation;
    private final Rectangle waveCounterLocation;
    private final Rectangle charOneHealthLocation;
    private final Rectangle charTwoHealthLocation;
    private final Rectangle charThreeHealthLocation;
    private final Rectangle charFourHealthLocation;
    private final CombatData combatData;
    private final MemoryReadingService memoryReadingService;
    private final ScreenshotService screenshotService;
    private final OCRService ocrService;

    private volatile boolean shutdownRequested;

    private int lastAnalyzedTurn;
    private Long gameassemblyModule;

    public DataManagerService() {
        LOGGER.info("Initializing DataManagerService...");
        combatData = CombatData.getInstance();
        memoryReadingService = MemoryReadingService.getInstance();
        screenshotService = ScreenshotService.getInstance();
        ocrService = OCRService.getInstance();
        enemyTurnLabelLocation = new Rectangle(1773, 1045, 255, 35);
        waveCounterLocation = new Rectangle(100, 13, 50, 25);
        charOneHealthLocation = new Rectangle(266, 1007, 80, 22);
        charTwoHealthLocation = new Rectangle(507, 1007, 80, 22);
        charThreeHealthLocation = new Rectangle(749, 1007, 80, 22);
        charFourHealthLocation = new Rectangle(990, 1007, 80, 22);
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
        long startTime = System.currentTimeMillis();
        storeOffsets();
//        threadSleep(50);
//        combatData.setCurrentTurnImage(screenshotService.takeScreenshot(null));
//        screenshotService.saveImage(combatData.getCurrentTurnImage(), String.format("turn_%d", combatData.getTurn()));
        storeWaveCounter();
        boolean enemyTurn = isEnemyTurn();
        storeCharacterVitals();
        long elapsedTime = System.currentTimeMillis() - startTime;
        LOGGER.debug("Turn {} succesfully analyzed in {}MS!", combatData.getTurn(), elapsedTime);
//        LOGGER.debug("Took and saved screenshot.");
        LOGGER.debug("!Generating report!");
        LOGGER.debug("*** Combat Information ***");
        LOGGER.debug("* Wave {}/{}", combatData.getCurrentWave(), combatData.getTotalWaves());
        LOGGER.debug("* Whos turn: {}", (enemyTurn ? "Enemy" : "Player"));
        LOGGER.debug("* Amount of enemies this turn: {}.", combatData.getAmountOfEnemies());
        LOGGER.debug("* Amount of skill points this turn: {}.", combatData.getCurrentSkillpoints());
        LOGGER.debug("*** Character Information ***");
        LOGGER.debug("* Character one current health: {}", combatData.getCharOneCurrentHealth());
        LOGGER.debug("* Character two current health: {}", combatData.getCharTwoCurrentHealth());
        LOGGER.debug("* Character three current health: {}", combatData.getCharThreeCurrentHealth());
        LOGGER.debug("* Character four current health: {}", combatData.getCharFourCurrentHealth());
        LOGGER.debug("!End of report!");
        LOGGER.debug("Waiting for next turn to start analyzing again...");
        lastAnalyzedTurn = combatData.getTurn();
//        combatData.getCurrentTurnImage().flush();
    }

    private void storeCharacterVitals() {
        BufferedImage charOneHealthImage = screenshotService.takeScreenshot(charOneHealthLocation);
        BufferedImage charTwoHealthImage = screenshotService.takeScreenshot(charTwoHealthLocation);
        BufferedImage charThreeHealthImage = screenshotService.takeScreenshot(charThreeHealthLocation);
        BufferedImage charFourHealthImage = screenshotService.takeScreenshot(charFourHealthLocation);

        String charOneHealth = sanitizeToKeepNumbers(ocrService.getTextFromImage(charOneHealthImage));
        String charTwoHealth = sanitizeToKeepNumbers(ocrService.getTextFromImage(charTwoHealthImage));
        String charThreeHealth = sanitizeToKeepNumbers(ocrService.getTextFromImage(charThreeHealthImage));
        String charFourHealth = sanitizeToKeepNumbers(ocrService.getTextFromImage(charFourHealthImage));

        combatData.setCharOneCurrentHealth(Integer.parseInt(charOneHealth));
        combatData.setCharTwoCurrentHealth(Integer.parseInt(charTwoHealth));
        combatData.setCharThreeCurrentHealth(Integer.parseInt(charThreeHealth));
        combatData.setCharFourCurrentHealth(Integer.parseInt(charFourHealth));
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
        String textToFind = "enemy's turn";
        BufferedImage imageToScan = screenshotService.takeScreenshot(enemyTurnLabelLocation);
        return ocrService.doesImageContainText(textToFind, imageToScan);
    }

    private void storeWaveCounter() {
        BufferedImage imageToScan = screenshotService.takeScreenshot(waveCounterLocation);
        String waveInfo = ocrService.getTextFromImage(imageToScan);

        combatData.setCurrentWave(Character.getNumericValue(waveInfo.charAt(0)));
        combatData.setTotalWaves(Character.getNumericValue(waveInfo.charAt(2)));
    }

    private String sanitizeToKeepNumbers(String text) {
        return text.replaceAll("[^0-9]", "").trim();
    }

}
