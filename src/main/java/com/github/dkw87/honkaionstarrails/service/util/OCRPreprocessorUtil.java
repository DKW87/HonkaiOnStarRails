package com.github.dkw87.honkaionstarrails.service.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;

public class OCRPreprocessorUtil {

    public static BufferedImage preprocessForOCR(BufferedImage original) {
        BufferedImage gray = toGrayscale(original);
        BufferedImage thresholded = applyThreshold(gray);
        return upscale(thresholded, 2);
    }

    public static BufferedImage toGrayscale(BufferedImage img) {
        BufferedImage gray = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = gray.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return gray;
    }

    public static BufferedImage applyThreshold(BufferedImage img) {
        BufferedImage thresholded = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int value = (r < 128) ? 0 : 255;
                int newRgb = (value << 16) | (value << 8) | value;
                thresholded.setRGB(x, y, newRgb);
            }
        }
        return thresholded;
    }

    public static BufferedImage upscale(BufferedImage img, int scaleFactor) {
        int w = img.getWidth() * scaleFactor;
        int h = img.getHeight() * scaleFactor;
        Image tmp = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2 = resized.createGraphics();
        g2.drawImage(tmp, 0, 0, null);
        g2.dispose();
        return resized;
    }


}
