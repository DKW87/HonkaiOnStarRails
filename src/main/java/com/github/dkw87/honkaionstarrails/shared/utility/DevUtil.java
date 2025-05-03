package com.github.dkw87.honkaionstarrails.shared.utility;

import java.awt.MouseInfo;
import java.awt.Point;

public class DevUtil {

    public static void showMousePosition() throws InterruptedException {
        Thread.sleep(2500);
        Point mousePosition = MouseInfo.getPointerInfo().getLocation();
        System.out.println("Mouse is at: X=" + mousePosition.x + ", Y=" + mousePosition.y);
    }

}
