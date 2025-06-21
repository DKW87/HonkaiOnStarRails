package com.github.dkw87.honkaionstarrails.shared.utility.dev;

import com.github.dkw87.honkaionstarrails.service.GameMonitorService;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
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
            LOGGER.warn("Thread was interrupted before it could make a screenshot: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public static void takeGameWindowScreenshot() {
        try {
            WinDef.HWND gameWindow = GameMonitorService.gameWindow;

            if (gameWindow == null) {
                LOGGER.debug("GameMonitorService.gameWindow is null");
                return;
            }

            LOGGER.debug("Taking screenshot of GameWindow in 3 seconds...");
            Thread.sleep(3000);

            // Get window bounds
            WinDef.RECT windowRect = new WinDef.RECT();
            User32.INSTANCE.GetWindowRect(gameWindow, windowRect);

            LOGGER.debug("Window bounds: {},{} to {},{}",
                    windowRect.left, windowRect.top, windowRect.right, windowRect.bottom);

            // Calculate window dimensions
            int x = windowRect.left;
            int y = windowRect.top;
            int width = windowRect.right - windowRect.left;
            int height = windowRect.bottom - windowRect.top;

            // Capture the window area
            Robot robot = new Robot();
            Rectangle captureArea = new Rectangle(x, y, width, height);
            BufferedImage screenshot = robot.createScreenCapture(captureArea);

            // Create screenshots directory
            File screenshotsDir = new File("screenshots");
            if (!screenshotsDir.exists()) {
                screenshotsDir.mkdirs();
            }

            // Generate filename
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String filename = "gamewindow_" + width + "x" + height + "_" + timestamp + ".png";
            File outputFile = new File(screenshotsDir, filename);

            // Save the screenshot
            ImageIO.write(screenshot, "png", outputFile);
            System.out.println("Game window screenshot saved: " + outputFile.getAbsolutePath());
            System.out.println("Check if it's a static image or actual live game content!");

            // Sleep for 10 seconds
            Thread.sleep(10000);

        } catch (Exception e) {
            LOGGER.error("Failed to capture game window: {}", e.getMessage());
        }
    }

}