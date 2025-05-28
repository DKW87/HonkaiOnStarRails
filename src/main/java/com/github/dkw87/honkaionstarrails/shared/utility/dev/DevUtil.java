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
        return scanAllModules(memoryService, hexSignature, scanSize);
    }

    public static List<SignatureResult> scanForSignature(MemoryReadingService memoryService,
                                                         String hexSignature) {
        return scanAllModules(memoryService, hexSignature, 100 * 1024 * 1024);
    }

    public static List<SignatureResult> scanSpecificModule(MemoryReadingService memoryService,
                                                           String hexSignature,
                                                           String moduleName,
                                                           int scanSize) {
        List<SignatureResult> results = new ArrayList<>();

        if (!memoryService.isInitialized()) {
            System.out.println("Memory service not initialized");
            return results;
        }

        byte[] signatureBytes = hexStringToBytes(hexSignature);
        if (signatureBytes == null) {
            System.out.println("Invalid hex signature format");
            return results;
        }

        Long moduleBase = memoryService.getModuleBaseAddresses(moduleName);
        if (moduleBase == null) {
            System.out.println("Could not find module: " + moduleName);
            return results;
        }

        System.out.println("Scanning module: " + moduleName + " (0x" + Long.toHexString(moduleBase) + ")");
        results.addAll(scanModuleMemory(memoryService, moduleBase, moduleName, signatureBytes, scanSize));

        return results;
    }

    public static List<SignatureResult> scanAllModules(MemoryReadingService memoryService,
                                                       String hexSignature,
                                                       int scanSize) {
        List<SignatureResult> results = new ArrayList<>();

        if (!memoryService.isInitialized()) {
            System.out.println("Memory service not initialized");
            return results;
        }

        byte[] signatureBytes = hexStringToBytes(hexSignature);
        if (signatureBytes == null) {
            System.out.println("Invalid hex signature format");
            return results;
        }

        String[] commonModules = {
                "GameAssembly.dll",
                "UnityPlayer.dll",
                "User32.dll",
                "ntdll.dll",
                "kernel32.dll"
        };

        System.out.println("High-performance signature scan (16MB chunks)...");

        for (String moduleName : commonModules) {
            Long moduleBase = memoryService.getModuleBaseAddresses(moduleName);
            if (moduleBase != null) {
                System.out.println("Scanning: " + moduleName);
                results.addAll(scanModuleMemory(memoryService, moduleBase, moduleName, signatureBytes, scanSize));
            }
        }

        System.out.println("Total scan complete. Found " + results.size() + " matches across all modules.");
        return results;
    }

    private static List<SignatureResult> scanModuleMemory(MemoryReadingService memoryService,
                                                          long moduleBase,
                                                          String moduleName,
                                                          byte[] signatureBytes,
                                                          int scanSize) {
        List<SignatureResult> results = new ArrayList<>();
        int chunkSize = 16 * 1024 * 1024; // 16MB chunks - optimal for high-end system
        int contextSize = 50; // bytes before and after to capture

        for (long offset = 0; offset < scanSize; offset += chunkSize) {
            long currentAddress = moduleBase + offset;

            byte[] memoryChunk = readMemoryChunk(memoryService, currentAddress, chunkSize);
            if (memoryChunk == null) continue;

            for (int i = 0; i <= memoryChunk.length - signatureBytes.length; i++) {
                if (matchesSignature(memoryChunk, i, signatureBytes)) {
                    long foundAddress = currentAddress + i;

                    // Get context around the found signature
                    String beforeContext = readContextBefore(memoryService, foundAddress, contextSize);
                    String matchText = readAsString(memoryService, foundAddress, signatureBytes.length);
                    String afterContext = readContextAfter(memoryService, foundAddress + signatureBytes.length, contextSize);

                    // Get hex context
                    String beforeHex = readAsHex(memoryService, foundAddress - contextSize, contextSize);
                    String matchHex = bytesToHexString(signatureBytes);
                    String afterHex = readAsHex(memoryService, foundAddress + signatureBytes.length, contextSize);

                    results.add(new SignatureResult(foundAddress, matchText, matchHex, moduleName,
                            beforeContext, afterContext, beforeHex, afterHex));
                    System.out.println("Found in " + moduleName + " at: 0x" + Long.toHexString(foundAddress));
                }
            }
        }

        return results;
    }

    private static String readContextBefore(MemoryReadingService memoryService, long address, int size) {
        StringBuilder result = new StringBuilder();
        long startAddress = Math.max(0, address - size); // Don't go below 0

        for (long addr = startAddress; addr < address; addr++) {
            try {
                byte b = memoryService.readByteFromAddress(addr);
                if (b >= 32 && b <= 126) {
                    result.append((char) b);
                } else {
                    result.append('.');
                }
            } catch (Exception e) {
                result.append('?');
            }
        }

        return result.toString();
    }

    private static String readContextAfter(MemoryReadingService memoryService, long address, int size) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < size; i++) {
            try {
                byte b = memoryService.readByteFromAddress(address + i);
                if (b == 0) break; // Stop at null terminator

                if (b >= 32 && b <= 126) {
                    result.append((char) b);
                } else {
                    result.append('.');
                }
            } catch (Exception e) {
                result.append('?');
                break;
            }
        }

        return result.toString();
    }

    private static String readAsHex(MemoryReadingService memoryService, long address, int size) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < size; i++) {
            try {
                byte b = memoryService.readByteFromAddress(address + i);
                if (i > 0) result.append(" ");
                result.append(String.format("%02X", b & 0xFF));
            } catch (Exception e) {
                if (i > 0) result.append(" ");
                result.append("??");
            }
        }

        return result.toString();
    }

    public static List<SignatureResult> scanForText(MemoryReadingService memoryService,
                                                    String textSignature,
                                                    int scanSize) {
        String hexSignature = textToHexString(textSignature);
        System.out.println("Converted text '" + textSignature + "' to hex: " + hexSignature);
        return scanAllModules(memoryService, hexSignature, scanSize);
    }

    public static List<SignatureResult> scanForText(MemoryReadingService memoryService,
                                                    String textSignature) {
        return scanForText(memoryService, textSignature, 100 * 1024 * 1024);
    }

    public static List<SignatureResult> scanForTextInModule(MemoryReadingService memoryService,
                                                            String textSignature,
                                                            String moduleName) {
        String hexSignature = textToHexString(textSignature);
        return scanSpecificModule(memoryService, hexSignature, moduleName, 100 * 1024 * 1024);
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

    public static void listAvailableModules(MemoryReadingService memoryService) {
        String[] commonModules = {
                "GameAssembly.dll",
                "UnityPlayer.dll",
                "User32.dll",
                "ntdll.dll",
                "kernel32.dll",
                "msvcrt.dll",
                "d3d11.dll",
                "xinput1_3.dll"
        };

        System.out.println("Available modules:");
        for (String moduleName : commonModules) {
            Long moduleBase = memoryService.getModuleBaseAddresses(moduleName);
            if (moduleBase != null) {
                System.out.println("✓ " + moduleName + " (0x" + Long.toHexString(moduleBase) + ")");
            } else {
                System.out.println("✗ " + moduleName + " (not found)");
            }
        }
    }

    public static void exampleUsage() {
        MemoryReadingService memoryService = new MemoryReadingService();

        if (memoryService.initialize()) {
            listAvailableModules(memoryService);
            System.out.println();

            // Scan for signature with full context analysis
            long startTime = System.currentTimeMillis();
            List<SignatureResult> results = scanForText(memoryService, "user_info\":{\"uid\":\"");
            long endTime = System.currentTimeMillis();

            System.out.println("Found " + results.size() + " matches in " + (endTime - startTime) + "ms");

            // Analyze results to find patterns and suggest better signatures
            analyzeResults(results);

            // Example: scan for username and see context
            System.out.println("\n=== USERNAME SCAN ===");
            List<SignatureResult> nameResults = scanForText(memoryService, "Longdikjohnson");
            for (SignatureResult result : nameResults) {
                System.out.println("Username context:");
                System.out.println("Before: '" + result.beforeContext + "'");
                System.out.println("After:  '" + result.afterContext + "'");
                System.out.println("Full context: '" + result.getFullContext() + "'");
                System.out.println();
            }

            memoryService.cleanup();
        }
    }

    public static String textToHex(String text) {
        return textToHexString(text);
    }

    public static void analyzeResults(List<SignatureResult> results) {
        System.out.println("\n=== SIGNATURE ANALYSIS ===");
        System.out.println("Found " + results.size() + " matches");

        for (int i = 0; i < results.size(); i++) {
            SignatureResult result = results.get(i);
            System.out.println("\n--- Match " + (i + 1) + " ---");
            System.out.println(result.toString());

            // Suggest longer signatures
            String longerSignature = result.beforeContext.length() >= 20 ?
                    result.beforeContext.substring(result.beforeContext.length() - 20) + result.readableText :
                    result.beforeContext + result.readableText;

            if (longerSignature.length() > result.readableText.length()) {
                System.out.println("Suggested longer signature: '" + longerSignature + "'");
                System.out.println("As hex: " + textToHex(longerSignature));
            }
        }

        // Look for common patterns
        if (results.size() > 1) {
            System.out.println("\n=== PATTERN ANALYSIS ===");
            analyzeCommonPatterns(results);
        }
    }

    private static void analyzeCommonPatterns(List<SignatureResult> results) {
        System.out.println("Looking for common before/after patterns...");

        // Check for common prefixes in before context
        String[] beforeContexts = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            beforeContexts[i] = results.get(i).beforeContext;
        }
        String commonBefore = findCommonSuffix(beforeContexts);

        if (commonBefore.length() > 5) {
            System.out.println("Common prefix pattern: '" + commonBefore + "'");
        }

        // Check for common suffixes in after context
        String[] afterContexts = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            afterContexts[i] = results.get(i).afterContext;
        }
        String commonAfter = findCommonPrefix(afterContexts);

        if (commonAfter.length() > 5) {
            System.out.println("Common suffix pattern: '" + commonAfter + "'");
        }

        // Identify which result might be the "real" one
        System.out.println("\nWhich match is likely the real player data:");
        for (int i = 0; i < results.size(); i++) {
            SignatureResult result = results.get(i);
            int score = 0;

            // Score based on context clues
            if (result.beforeContext.contains("player") || result.afterContext.contains("player")) score += 2;
            if (result.beforeContext.contains("data") || result.afterContext.contains("data")) score += 1;
            if (result.moduleName.equals("GameAssembly.dll")) score += 1;
            if (result.afterContext.contains("level") || result.afterContext.contains("health")) score += 2;

            System.out.println("Match " + (i + 1) + " (" + result.moduleName + ") - Confidence: " + score + "/5");
        }
    }

    private static String findCommonSuffix(String[] strings) {
        if (strings.length == 0) return "";

        String first = strings[0];
        int commonLength = 0;

        for (int i = 1; i <= first.length(); i++) {
            String suffix = first.substring(first.length() - i);
            boolean allMatch = true;

            for (String str : strings) {
                if (!str.endsWith(suffix)) {
                    allMatch = false;
                    break;
                }
            }

            if (allMatch) {
                commonLength = i;
            } else {
                break;
            }
        }

        return commonLength > 0 ? first.substring(first.length() - commonLength) : "";
    }

    private static String findCommonPrefix(String[] strings) {
        if (strings.length == 0) return "";

        String first = strings[0];
        int commonLength = 0;

        for (int i = 1; i <= first.length(); i++) {
            String prefix = first.substring(0, i);
            boolean allMatch = true;

            for (String str : strings) {
                if (!str.startsWith(prefix)) {
                    allMatch = false;
                    break;
                }
            }

            if (allMatch) {
                commonLength = i;
            } else {
                break;
            }
        }

        return commonLength > 0 ? first.substring(0, commonLength) : "";
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
