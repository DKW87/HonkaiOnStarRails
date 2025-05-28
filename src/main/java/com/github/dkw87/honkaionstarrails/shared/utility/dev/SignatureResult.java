package com.github.dkw87.honkaionstarrails.shared.utility.dev;

public class SignatureResult {
    public final long address;
    public final String readableText;
    public final String hexBytes;
    public final String moduleName;
    public final String beforeContext;
    public final String afterContext;
    public final String beforeHex;
    public final String afterHex;

    public SignatureResult(long address, String readableText, String hexBytes, String moduleName,
                           String beforeContext, String afterContext, String beforeHex, String afterHex) {
        this.address = address;
        this.readableText = readableText;
        this.hexBytes = hexBytes;
        this.moduleName = moduleName;
        this.beforeContext = beforeContext;
        this.afterContext = afterContext;
        this.beforeHex = beforeHex;
        this.afterHex = afterHex;
    }

    @Override
    public String toString() {
        return String.format("Module: %s | Address: 0x%X\n" +
                        "Before: '%s'\n" +
                        "Match:  '%s'\n" +
                        "After:  '%s'\n" +
                        "Before Hex: %s\n" +
                        "Match Hex:  %s\n" +
                        "After Hex:  %s\n",
                moduleName, address, beforeContext, readableText, afterContext,
                beforeHex, hexBytes, afterHex);
    }

    public String getFullContext() {
        return beforeContext + readableText + afterContext;
    }

    public String getFullHexContext() {
        return beforeHex + " " + hexBytes + " " + afterHex;
    }
}