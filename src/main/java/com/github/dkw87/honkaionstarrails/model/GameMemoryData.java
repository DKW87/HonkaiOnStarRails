package com.github.dkw87.honkaionstarrails.model;

import com.github.dkw87.honkaionstarrails.model.enumeration.MemoryType;
import com.github.dkw87.honkaionstarrails.service.MemoryReadingService;

public class GameMemoryData {

    private static final String MEMORY_MODULE = "GameAssembly.dll";

    private final Long offset;
    private final Integer[] ptrChain;
    private final MemoryType memoryType;

    public GameMemoryData(Long offset,
                          Integer[] ptrChain,
                          MemoryType memoryType) {
        this.offset = offset;
        this.ptrChain = ptrChain;
        this.memoryType = memoryType;
    }

    public Object readFromMemory() {
        return ptrChain == null
                ? fromOffset()
                : withPtrChain();
    }

    private Object fromOffset() {
        final MemoryReadingService memoryReadingService = MemoryReadingService.getInstance();
        final Long module = memoryReadingService.getModuleBaseAddresses(MEMORY_MODULE);

        return switch (this.memoryType) {
            case BYTE -> memoryReadingService.readByteFromAddress(module + this.offset);
            case INT -> memoryReadingService.readIntFromAddress(module + this.offset);
            case LONG -> memoryReadingService.readLongFromAddress(module + this.offset);
        };
    }

    // TODO create module not found and getModuleBaseAddress with validations in memoryreadingService
    private Object withPtrChain() {
        final MemoryReadingService memoryReadingService = MemoryReadingService.getInstance();
        final Long module = memoryReadingService.getModuleBaseAddresses(MEMORY_MODULE);

        return switch (this.memoryType) {
            case BYTE -> bytePtrChain(memoryReadingService, module);
            case INT -> intPtrChain(memoryReadingService, module);
            case LONG -> longPtrChain(memoryReadingService, module);
        };
    }

    private Object bytePtrChain(MemoryReadingService memoryReadingService, Long module) {
        return null; // TODO implement
    }

    private Object intPtrChain(MemoryReadingService memoryReadingService, Long module) {
        final Long address = memoryReadingService.readLongFromAddress(module + this.offset);
        return memoryReadingService.followPTRChain(address, this.ptrChain);
    }

    private Object longPtrChain(MemoryReadingService memoryReadingService, Long module) {
        return null; // TODO implement
    }


}
