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
     * Checks if the game is in combat by reading memory values
     *
     * @return true if in combat based on memory values
     */
    public boolean isInCombat() {
        if (processHandle == null || moduleBaseAddresses.isEmpty()) {
            if (!initialize()) {
                return false;
            }
        }

        Long gameModuleBase = moduleBaseAddresses.get(CombatOffsets.GAME_ASSEMBLY_MODULE);
        if (gameModuleBase == null) {
            System.out.println("Game module base address not found");
            return false;
        }


        // Read combat state indicators
        byte combatStart = readByteFromAddress(gameModuleBase + CombatOffsets.COMBAT_START);
        byte combatReady = readByteFromAddress(gameModuleBase + CombatOffsets.COMBAT_READY);


        // Logic to determine if in combat based on memory values
        // According to screenshots, value of 1 likely indicates combat is active
        if (combatStart > 0 || combatReady > 0) {
            System.out.println("combat started broski!!");
            return true;
        } else {
            System.out.println("combat has not yet started");
            return false;
        }
    }

    /**
     * Gets the current skill points available in combat
     *
     * @return number of skill points or -1 if reading failed
     */
    public int getSkillPoints() {
        if (processHandle == null || moduleBaseAddresses.isEmpty()) {
            if (!initialize()) {
                return -1;
            }
        }

        Long gameModuleBase = moduleBaseAddresses.get(CombatOffsets.GAME_ASSEMBLY_MODULE);
        if (gameModuleBase == null) {
            System.out.println("Game module base address not found");
            return -1;
        }

        // Follow pointer chain to get to skill points
        long address = gameModuleBase + CombatOffsets.SKILLPOINTS_BASE;

        // Read pointer at base address
        address = readLongFromAddress(address);
        if (address == 0) {
            return -1;
        }

        // Follow the chain of offsets to reach the final value
        for (int i = 0; i < CombatOffsets.SKILLPOINTS_PTR_CHAIN.length - 1; i++) {
            address = readLongFromAddress(address + CombatOffsets.SKILLPOINTS_PTR_CHAIN[i]);
            if (address == 0) {
                return -1;
            }
        }

        // Read the final value (int) at the last offset
        return readIntFromAddress(address + CombatOffsets.SKILLPOINTS_PTR_CHAIN[CombatOffsets.SKILLPOINTS_PTR_CHAIN.length - 1]);
    }

    /**
     * Read a byte from the specified memory address
     */
    private byte readByteFromAddress(long address) {
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
    private int readIntFromAddress(long address) {
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
    private long readLongFromAddress(long address) {
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
}