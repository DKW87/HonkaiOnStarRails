package com.github.dkw87.honkaionstarrails.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.AWTException;
import java.io.File;
import java.io.IOException;

public class ScreenshotService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenshotService.class);
    private final File screenshotsFolder;

    public ScreenshotService() {
        screenshotsFolder = new File("screenshots");
    }

    public BufferedImage takeScreenshot() {
        try {
            LOGGER.debug("Taking screenshot...");

            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

            return robot.createScreenCapture(screenRect);
        } catch (AWTException e) {
            LOGGER.error("Failed to create Robot: {}", e.getMessage());
        }
        return null;
    }

    public void saveImage(BufferedImage image, String fileName) {
        if (image == null) {
            LOGGER.error("Image is null");
            return;
        } else if (fileName == null) {
            LOGGER.error("FileName is null");
        }

        if (!screenshotsFolder.exists()) {
            screenshotsFolder.mkdirs();
        }

        try {
            File outputFile = new File(screenshotsFolder, fileName);
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            LOGGER.error("Failed to save screenshot: {}", e.getMessage());
        }
    }

}
