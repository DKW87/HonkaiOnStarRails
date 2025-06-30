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
        tesseract.setDatapath("tesseract-data/fast");
        tesseract.setOcrEngineMode(1); // LSTM only
        tesseract.setPageSegMode(7); // single line
        tesseract.setVariable("user_defined_dpi", "300");
        tesseract.setVariable("classify_enable_learning", "0");
        tesseract.setVariable("classify_enable_adaptive_matcher", "0");
    }

    protected int scanForOneWordInteger(BufferedImage img) {
        try {
            tesseract.setVariable("tessedit_char_whitelist", "0123456789");
            tesseract.setVariable("load_number_dawg", "1");
            tesseract.setVariable("load_freq_dawg", "0");
            tesseract.setPageSegMode(8);

            String result = tesseract.doOCR(img).trim();
            LOGGER.debug("OCR result is: {}", result);

            return Integer.parseInt(result);
        } catch (TesseractException e) {
            LOGGER.error("Tesseract exception", e);
            return -1;
        }
    }

    protected String scanForOneLineString(BufferedImage img) {
        try {
            tesseract.setVariable("tessedit_char_whitelist", "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuYvVwWxXyYzZ' ");
            tesseract.setVariable("load_number_dawg", "0");
            tesseract.setVariable("load_freq_dawg", "1");
            tesseract.setPageSegMode(7);

            String readString = tesseract.doOCR(img).toLowerCase().trim();
            LOGGER.debug("OCR result is: {}", readString);

            return readString;
        } catch (TesseractException e) {
            LOGGER.error("Tesseract exception: ", e);
            return null;
        }
    }

    protected String scanForOneLineCounter(BufferedImage img) {
        try {
            tesseract.setVariable("tessedit_char_whitelist", "0123456789/");
            tesseract.setVariable("load_number_dawg", "1");
            tesseract.setVariable("load_freq_dawg", "0");
            tesseract.setPageSegMode(7);

            String result = tesseract.doOCR(img).toLowerCase().trim();
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
