package com.github.dkw87.honkaionstarrails.service;

import com.github.dkw87.honkaionstarrails.service.constant.CombatOffsets;
import com.github.dkw87.honkaionstarrails.service.win32interface.PsapiExtended;
import com.github.dkw87.honkaionstarrails.service.win32interface.User32Extended;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;

import java.util.HashMap;
import java.util.Map;

public class MemoryReadingService {

    private WinNT.HANDLE processHandle;
    private int processId;
    private Map<String, Long> moduleBaseAddresses = new HashMap<>();

    public MemoryReadingService() {
        // Process handle will be initialized when game is detected
    }

    /**
     * Initialize memory reading by opening a handle to the game process
     *
     * @return true if successfully attached to the game process
     */
    public boolean initialize() {
        if (GameMonitorService.gameWindow == null) {
            System.out.println("Game window not found");
            return false;
        }

        IntByReference pid = new IntByReference();
        User32Extended.INSTANCE.GetWindowThreadProcessId(GameMonitorService.gameWindow, pid);
        processId = pid.getValue();

        if (processId <= 0) {
            System.out.println("Failed to get process ID");
            return false;
        }

        // Open process with required access rights for memory reading
        processHandle = Kernel32.INSTANCE.OpenProcess(
                Kernel32.PROCESS_VM_READ | Kernel32.PROCESS_QUERY_INFORMATION,
                false,
                processId
        );

        if (processHandle == null) {
            System.out.println("Failed to open process handle: " + Native.getLastError());
            return false;
        }

        // Get module base addresses
        if (!findModuleBaseAddress()) {
            System.out.println("Failed to find game module base address");
            cleanup();
            return false;
        }

        System.out.println("Successfully attached to game process with ID: " + processId);
        System.out.println("GameAssembly.dll base address: 0x" +
                Long.toHexString(moduleBaseAddresses.get(CombatOffsets.GAME_ASSEMBLY_MODULE)));
        return true;
    }

    /**
     * Finds and stores the base addresses of key modules
     */
    private boolean findModuleBaseAddress() {
        // Enumerate modules
        WinDef.HMODULE[] hModules = new WinDef.HMODULE[1024];
        IntByReference lpcbNeeded = new IntByReference();

        boolean enumSuccess = PsapiExtended.INSTANCE.EnumProcessModules(
                processHandle,
                hModules,
                hModules.length * Native.getNativeSize(WinDef.HMODULE.class),
                lpcbNeeded
        );

        if (!enumSuccess) {
            System.out.println("Failed to enumerate modules: " + Native.getLastError());
            return false;
        }

        int modulesCount = lpcbNeeded.getValue() / Native.getNativeSize(WinDef.HMODULE.class);
        System.out.println("Found " + modulesCount + " modules");

        // Just find the largest module (GameAssembly.dll)
        long largestModuleSize = 0;
        long largestModuleAddress = 0;
        int largestModuleIndex = -1;

        for (int i = 0; i < modulesCount; i++) {
            try {
                Psapi.MODULEINFO info = new Psapi.MODULEINFO();
                boolean success = PsapiExtended.INSTANCE.GetModuleInformation(
                        processHandle,
                        hModules[i],
                        info,
                        info.size()
                );

                if (success && info.SizeOfImage > largestModuleSize) {
                    largestModuleSize = info.SizeOfImage;
                    largestModuleAddress = Pointer.nativeValue(hModules[i].getPointer());
                    largestModuleIndex = i;
                }
            } catch (Exception e) {
                // Skip this module if we can't get its info
            }
        }

        if (largestModuleAddress != 0) {
            System.out.println("Found largest module (GameAssembly.dll) at 0x" +
                    Long.toHexString(largestModuleAddress) +
                    " size: " + (largestModuleSize / (1024 * 1024)) + " MB");

            moduleBaseAddresses.put(CombatOffsets.GAME_ASSEMBLY_MODULE, largestModuleAddress);
            return true;
        }

        System.out.println("Failed to find any modules with size information");
        return false;
    }

    /**
     * Gets the current skill points available in combat
     *
     * @return number of skill points or -1 if reading failed
     */
    public int getSkillPoints() {
        if (!isInitialized()) return -1;

        Long gameModuleBase = moduleBaseAddresses.get(CombatOffsets.GAME_ASSEMBLY_MODULE);
        if (!gameModuleExists(gameModuleBase)) return -1;

        // Start with the base pointer
        long address = gameModuleBase + CombatOffsets.SKILLPOINTS_BASE;

        // Read first pointer
        address = readLongFromAddress(address);
        if (address == 0) return -1;

        // Follow chain EXCEPT for the last offset
        for (int i = 0; i < CombatOffsets.SKILLPOINTS_PTR_CHAIN.length - 1; i++) {
            // Get next address to read from
            long nextAddr = address + CombatOffsets.SKILLPOINTS_PTR_CHAIN[i];

            // If this is the last step, read the int value instead of a pointer
            if (i == CombatOffsets.SKILLPOINTS_PTR_CHAIN.length - 2) {
                return readIntFromAddress(nextAddr);
            }

            // Otherwise, follow the pointer
            address = readLongFromAddress(nextAddr);
            if (address == 0) return -1;
        }
        return -1;
    }

    /**
     * Read a byte from the specified memory address
     */
    public byte readByteFromAddress(long address) {
        Memory buffer = new Memory(1);
        boolean success = Kernel32.INSTANCE.ReadProcessMemory(
                processHandle,
                new Pointer(address),
                buffer,
                1,
                null
        );

        if (!success) {
            int error = Native.getLastError();
            System.out.println("Failed to read byte from address: 0x" + Long.toHexString(address) +
                    " Error: " + error);
            throw new RuntimeException("Read failed");
        }

        return buffer.getByte(0);
    }

    /**
     * Read an integer (4 bytes) from the specified memory address
     */
    public int readIntFromAddress(long address) {
        Memory buffer = new Memory(4);
        boolean success = Kernel32.INSTANCE.ReadProcessMemory(
                processHandle,
                new Pointer(address),
                buffer,
                4,
                null
        );

        if (!success) {
            System.out.println("Failed to read int from address: 0x" + Long.toHexString(address) +
                    " Error: " + Native.getLastError());
            return -1;
        }

        return buffer.getInt(0);
    }

    /**
     * Read a long (8 bytes) from the specified memory address (for pointer values)
     */
    public long readLongFromAddress(long address) {
        Memory buffer = new Memory(8);
        boolean success = Kernel32.INSTANCE.ReadProcessMemory(
                processHandle,
                new Pointer(address),
                buffer,
                8,
                null
        );

        if (!success) {
            System.out.println("Failed to read pointer from address: 0x" + Long.toHexString(address) +
                    " Error: " + Native.getLastError());
            return 0;
        }

        return buffer.getLong(0);
    }

    public boolean isInitialized() {
        if (processHandle == null || moduleBaseAddresses.isEmpty()) {
            return initialize();
        }
        return true;
    }

    public boolean gameModuleExists(Long gameModuleBase) {
        if (gameModuleBase == null) {
            System.out.println("Game module base address not found");
            return false;
        }
        return true;
    }

    /**
     * Clean up resources
     */
    public void cleanup() {
        if (processHandle != null) {
            System.out.println("Cleaning up process handle: " + processHandle);
            Kernel32.INSTANCE.CloseHandle(processHandle);
            processHandle = null;
        }
    }
    
    public Long getModuleBaseAddresses(String module) {
        return moduleBaseAddresses.get(module);
    }
    
}