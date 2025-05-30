package com.github.dkw87.honkaionstarrails.shared.utility.dev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.MouseInfo;
import java.awt.Point;

public class DevUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevUtil.class);

    public static void showMousePosition() throws InterruptedException {
        Thread.sleep(2500);
        Point mousePosition = MouseInfo.getPointerInfo().getLocation();
        LOGGER.info("Mouse is at: X={}, Y={}", mousePosition.x, mousePosition.y);
    }

}