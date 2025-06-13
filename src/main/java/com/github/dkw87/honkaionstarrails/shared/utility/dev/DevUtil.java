package com.github.dkw87.honkaionstarrails.shared.utility.dev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Robot;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Rectangle;
import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

public class DevUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevUtil.class);

    public void readActiveThreads() {
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        LOGGER.debug("Active threads ({}): {}",
                threads.size(),
                threads.stream()
                        .map(t -> String.format("%s (daemon: %s)", t.getName(), t.isDaemon()))
                        .sorted()
                        .collect(Collectors.joining(", "))
        );
    }


    public static void showMousePosition() throws InterruptedException {
        Thread.sleep(2500);
        Point mousePosition = MouseInfo.getPointerInfo().getLocation();
        LOGGER.debug("Mouse is at: X={}, Y={}", mousePosition.x, mousePosition.y);
    }

    public static void takeScreenshot() {
        try {
            LOGGER.debug("Taking screenshot in 3 seconds...");
            Thread.sleep(3000);

            Robot robot = new Robot();

            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenshot = robot.createScreenCapture(screenRect);

            File screenshotsDir = new File("screenshots");
            if (!screenshotsDir.exists()) {
                screenshotsDir.mkdirs();
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String filename = "screenshot_" + timestamp + ".png";
            File outputFile = new File(screenshotsDir, filename);

            ImageIO.write(screenshot, "png", outputFile);

            LOGGER.debug("Screenshot saved: {}", outputFile.getAbsolutePath());

            Thread.sleep(10000);

        } catch (AWTException e) {
            LOGGER.error("Failed to create Robot: {}", e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Failed to save screenshot: {}", e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.error("Failed to take screenshot as thread was interrupted: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

}