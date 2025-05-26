package com.github.dkw87.honkaionstarrails.shared.utility.dev;

import com.github.dkw87.honkaionstarrails.service.MemoryReadingService;

import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class DevUtil {

    public static List<SignatureResult> scanForSignature(MemoryReadingService memoryService,
                                                         String hexSignature,
                                                         int scanSize) {
        List<SignatureResult> results = new ArrayList<>();

        if (!memoryService.isInitialized()) {
            System.out.println("Memory service not initialized");
            return results;
        }

        // Convert hex string to byte array
        byte[] signatureBytes = hexStringToBytes(hexSignature);
        if (signatureBytes == null) {
            System.out.println("Invalid hex signature format");
            return results;
        }

        // Get game module base address
        Long moduleBase = memoryService.getModuleBaseAddresses("GameAssembly.dll");
        if (moduleBase == null) {
            System.out.println("Could not find GameAssembly.dll base address");
            return results;
        }

        System.out.println("Scanning for signature: " + hexSignature);
        System.out.println("Starting from: 0x" + Long.toHexString(moduleBase));
        System.out.println("Scan size: " + (scanSize / (1024 * 1024)) + " MB");

        // Scan memory in chunks for performance
        int chunkSize = 1024 * 1024; // 1MB chunks
        for (long offset = 0; offset < scanSize; offset += chunkSize) {
            long currentAddress = moduleBase + offset;

            // Read chunk of memory
            byte[] memoryChunk = readMemoryChunk(memoryService, currentAddress, chunkSize);
            if (memoryChunk == null) continue;

            // Search for signature in this chunk
            for (int i = 0; i <= memoryChunk.length - signatureBytes.length; i++) {
                if (matchesSignature(memoryChunk, i, signatureBytes)) {
                    long foundAddress = currentAddress + i;

                    // Read more context around the found signature
                    String readableText = readAsString(memoryService, foundAddress, signatureBytes.length + 50);
                    String hexBytes = bytesToHexString(signatureBytes);

                    results.add(new SignatureResult(foundAddress, readableText, hexBytes));
                    System.out.println("Found match at: 0x" + Long.toHexString(foundAddress));
                }
            }
        }

        System.out.println("Scan complete. Found " + results.size() + " matches.");
        return results;
    }

    public static List<SignatureResult> scanForSignature(MemoryReadingService memoryService,
                                                         String hexSignature) {
        return scanForSignature(memoryService, hexSignature, 50 * 1024 * 1024);
    }

    public static List<SignatureResult> scanForText(MemoryReadingService memoryService,
                                                    String textSignature,
                                                    int scanSize) {
        String hexSignature = textToHexString(textSignature);
        System.out.println("Converted text '" + textSignature + "' to hex: " + hexSignature);
        return scanForSignature(memoryService, hexSignature, scanSize);
    }

    public static List<SignatureResult> scanForText(MemoryReadingService memoryService,
                                                    String textSignature) {
        return scanForText(memoryService, textSignature, 50 * 1024 * 1024);
    }

    private static byte[] hexStringToBytes(String hexString) {
        try {
            String[] hexBytes = hexString.trim().split("\\s+");
            byte[] bytes = new byte[hexBytes.length];

            for (int i = 0; i < hexBytes.length; i++) {
                bytes[i] = (byte) Integer.parseInt(hexBytes[i], 16);
            }
            return bytes;
        } catch (Exception e) {
            System.out.println("Error parsing hex string: " + e.getMessage());
            return null;
        }
    }

    private static String textToHexString(String text) {
        StringBuilder hexString = new StringBuilder();
        byte[] textBytes = text.getBytes();

        for (int i = 0; i < textBytes.length; i++) {
            if (i > 0) hexString.append(" ");
            hexString.append(String.format("%02X", textBytes[i] & 0xFF));
        }

        return hexString.toString();
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(String.format("%02X", bytes[i] & 0xFF));
        }
        return sb.toString();
    }

    private static byte[] readMemoryChunk(MemoryReadingService memoryService, long address, int size) {
        try {
            return memoryService.readMemoryChunk(address, size);
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean matchesSignature(byte[] memory, int position, byte[] signature) {
        if (position + signature.length > memory.length) {
            return false;
        }

        for (int i = 0; i < signature.length; i++) {
            if (memory[position + i] != signature[i]) {
                return false;
            }
        }
        return true;
    }

    private static String readAsString(MemoryReadingService memoryService, long address, int maxLength) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < maxLength; i++) {
            try {
                byte b = memoryService.readByteFromAddress(address + i);
                if (b == 0) break; // Null terminator

                // Only add printable ASCII characters
                if (b >= 32 && b <= 126) {
                    result.append((char) b);
                } else {
                    result.append('.');
                }
            } catch (Exception e) {
                break;
            }
        }

        return result.toString();
    }

    public static void exampleUsage() {
        MemoryReadingService memoryService = new MemoryReadingService();

        if (memoryService.initialize()) {
            // Method 1: Scan using hex signature
            String hexSignature = "75 73 65 72 5F 69 6E 66 6F 22 3A 7B 22 75 69 64 22 3A 22";
            List<SignatureResult> hexResults = scanForSignature(memoryService, hexSignature);

            // Method 2: Scan using readable text (much easier!)
            String textSignature = "user_info\":{\"uid\":\"";
            List<SignatureResult> textResults = scanForText(memoryService, textSignature);

            // Both methods give the same results
            System.out.println("Hex scan found: " + hexResults.size() + " matches");
            System.out.println("Text scan found: " + textResults.size() + " matches");

            // Print results from text scan
            for (SignatureResult result : textResults) {
                System.out.println(result);
            }

            memoryService.cleanup();
        }
    }

    public static String textToHex(String text) {
        return textToHexString(text);
    }

    public static void testTextToHex() {
        String[] testStrings = {
                "user_info\":{\"uid\":\"",
                "Longdikjohnson",
                "health",
                "ultimate"
        };

        System.out.println("Text to Hex conversion examples:");
        for (String text : testStrings) {
            String hex = textToHex(text);
            System.out.println("'" + text + "' -> " + hex);
        }
    }

    public static void showMousePosition() throws InterruptedException {
        Thread.sleep(2500);
        Point mousePosition = MouseInfo.getPointerInfo().getLocation();
        System.out.println("Mouse is at: X=" + mousePosition.x + ", Y=" + mousePosition.y);
    }

}
