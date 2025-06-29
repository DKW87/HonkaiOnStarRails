package com.github.dkw87.honkaionstarrails.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

public class OCRService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OCRService.class);

    private final Tesseract tesseract;

    private OCRService() {
        LOGGER.info("Initializing OCRService...");
        tesseract = new Tesseract();
        tesseract.setLanguage("eng");
        tesseract.setDatapath("tesseract-data");
        tesseract.setVariable("user_defined_dpi", "150");
    }

    protected boolean doesImageContainText(String text, BufferedImage image) {
        try {
            LOGGER.debug("Performing OCR for text: {}...", text);
            String readString = tesseract.doOCR(image).toLowerCase();
            LOGGER.debug("OCR returned: {}", readString);
            return readString.contains(text);
        } catch (TesseractException e) {
            LOGGER.error("Tesseract exception: ", e);
            return false;
        }
    }

    protected String getTextFromImage(BufferedImage image) {
        try {
            LOGGER.debug("Performing OCR on image...");
            String result = tesseract.doOCR(image).toLowerCase();
            LOGGER.debug("OCR returned: {}", result);
            return result;
        } catch (TesseractException e) {
            LOGGER.error("Tesseract exception: ", e);
            return null;
        }
    }

    public static OCRService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final OCRService INSTANCE = new OCRService();
    }

}
