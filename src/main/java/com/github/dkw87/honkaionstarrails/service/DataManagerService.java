package com.github.dkw87.honkaionstarrails.service;

import com.github.dkw87.honkaionstarrails.repository.memory.CombatData;
import com.github.dkw87.honkaionstarrails.service.constant.GameMemoryConst;
import com.github.dkw87.honkaionstarrails.service.constant.chain.CombatPtrChains;
import com.github.dkw87.honkaionstarrails.service.constant.offset.CombatOffsets;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.github.dkw87.honkaionstarrails.service.util.OCRPreprocessorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used primarily to get, process and store all combat related data.
 * Works on a separate thread 'DataManagerService Thread'.
 * Is notified of work by the GameStateService.
 * TODO: Class is getting too big and needs refactoring
 */
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
        charOneHealthLocation = new Rectangle(273, 1006, 80, 26);
        charTwoHealthLocation = new Rectangle(512, 1006, 80, 26);
        charThreeHealthLocation = new Rectangle(751, 1006, 80, 26);
        charFourHealthLocation = new Rectangle(992, 1006, 80, 26);
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
        gameassemblyModule = memoryReadingService.getModuleBaseAddresses(GameMemoryConst.GAME_ASSEMBLY_MODULE);
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
        BufferedImage charOneHealthImage = OCRPreprocessorUtil.preprocessForOCR(screenshotService.takeScreenshot(charOneHealthLocation));
        BufferedImage charTwoHealthImage = OCRPreprocessorUtil.preprocessForOCR(screenshotService.takeScreenshot(charTwoHealthLocation));
        BufferedImage charThreeHealthImage = OCRPreprocessorUtil.preprocessForOCR(screenshotService.takeScreenshot(charThreeHealthLocation));
        BufferedImage charFourHealthImage = OCRPreprocessorUtil.preprocessForOCR(screenshotService.takeScreenshot(charFourHealthLocation));

        screenshotService.saveImage(charOneHealthImage, "charOneHealth");
        screenshotService.saveImage(charTwoHealthImage, "charTwoHealth");
        screenshotService.saveImage(charThreeHealthImage, "charThreeHealth");
        screenshotService.saveImage(charFourHealthImage, "charFourHealth");

        int charOneHealth = ocrService.scanForOneWordInteger(charOneHealthImage);
        int charTwoHealth = ocrService.scanForOneWordInteger(charTwoHealthImage);
        int charThreeHealth = ocrService.scanForOneWordInteger(charThreeHealthImage);
        int charFourHealth = ocrService.scanForOneWordInteger(charFourHealthImage);

        combatData.setCharOneCurrentHealth(charOneHealth);
        combatData.setCharTwoCurrentHealth(charTwoHealth);
        combatData.setCharThreeCurrentHealth(charThreeHealth);
        combatData.setCharFourCurrentHealth(charFourHealth);
    }

    private void storeOffsets() {
        combatData.setCurrentSkillpoints(readFromGameMemory(CombatOffsets.SKILLPOINTS, CombatPtrChains.SKILLPOINTS));
        combatData.setAmountOfEnemies(readFromGameMemory(CombatOffsets.AMOUNT_OF_ENEMIES, CombatPtrChains.AMOUNT_OF_ENEMIES));
    }

    private int readFromGameMemory(long offset, Integer[] ptrChain) {
        long address = memoryReadingService.readLongFromAddress(gameassemblyModule + offset);
        return memoryReadingService.followPtrChainToInt(address, ptrChain);
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
        BufferedImage imageToScan = OCRPreprocessorUtil.preprocessForOCR(screenshotService.takeScreenshot(enemyTurnLabelLocation));
        screenshotService.saveImage(imageToScan, "enemyTurn");
        String ocrResult = ocrService.scanForOneLineString(imageToScan);
        return ocrResult.equals(textToFind);
    }

    private void storeWaveCounter() {
        BufferedImage imageToScan = OCRPreprocessorUtil.preprocessForOCR(screenshotService.takeScreenshot(waveCounterLocation));
        String waveInfo = ocrService.scanForOneLineCounter(imageToScan);
        screenshotService.saveImage(imageToScan, "waveCounter");

        combatData.setCurrentWave(Character.getNumericValue(waveInfo.charAt(0)));
        combatData.setTotalWaves(Character.getNumericValue(waveInfo.charAt(2)));
    }

}
