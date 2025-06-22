package com.github.dkw87.honkaionstarrails.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.AWTException;

public class ScreenshotService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenshotService.class);

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

}
