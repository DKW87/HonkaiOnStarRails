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

    private ScreenshotService() {
        screenshotsFolder = new File("screenshots");
    }

    protected BufferedImage takeScreenshot(Rectangle region) {
        try {
            LOGGER.debug("Taking screenshot...");

            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

            return region != null ? robot.createScreenCapture(region) : robot.createScreenCapture(screenRect);
        } catch (AWTException e) {
            LOGGER.error("Failed to create Robot: {}", e.getMessage());
        }
        return null;
    }

    protected void saveImage(BufferedImage image, String fileName) {
        if (image == null) {
            LOGGER.error("Image is null");
            return;
        } else if (fileName == null) {
            LOGGER.error("FileName is null");
        }

        if (!screenshotsFolder.exists()) {
            screenshotsFolder.mkdirs();
        }

        String extension = ".png";
        fileName = fileName + extension;

        try {
            File outputFile = new File(screenshotsFolder, fileName);
            ImageIO.write(image, extension.replace(".", ""), outputFile);
        } catch (IOException e) {
            LOGGER.error("Failed to save screenshot: {}", e.getMessage());
        }
    }

    public static ScreenshotService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final ScreenshotService INSTANCE = new ScreenshotService();
    }

}
