package com.github.dkw87.honkaionstarrails.shared.utility.dev;

public class SignatureResult {

    public final long address;
    public final String readableText;
    public final String hexBytes;

    public SignatureResult(long address, String readableText, String hexBytes) {
        this.address = address;
        this.readableText = readableText;
        this.hexBytes = hexBytes;
    }

    @Override
    public String toString() {
        return String.format("Address: 0x%X | Text: '%s' | Hex: %s",
                address, readableText, hexBytes);
    }
}
