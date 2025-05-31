package com.github.dkw87.honkaionstarrails.shared.utility.dev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.MouseInfo;
import java.awt.Point;
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
        LOGGER.info("Mouse is at: X={}, Y={}", mousePosition.x, mousePosition.y);
    }

}