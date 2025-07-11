package com.github.dkw87.honkaionstarrails.model.utility;

import com.github.dkw87.honkaionstarrails.model.GameMemoryData;
import com.github.dkw87.honkaionstarrails.service.MemoryReadingService;

public class GameMemoryDataReaderUtil {

    private static final String MEMORY_MODULE = "GameAssembly.dll";

    private GameMemoryDataReaderUtil() {}

    public Object readFromMemory(GameMemoryData data) {
        return data.getPtrChain() == null
                ? fromOffset(data)
                : withPtrChain(data);
    }

    private Object fromOffset(GameMemoryData data) {
        final MemoryReadingService mrService = MemoryReadingService.getInstance();
        final Long module = mrService.getModuleBaseAddresses(MEMORY_MODULE);

        return switch (data.getMemoryType()) {
            case BYTE -> mrService.readByteFromAddress(module + data.getOffset());
            case INT -> mrService.readIntFromAddress(module + data.getOffset());
            case LONG -> mrService.readLongFromAddress(module + data.getOffset());
        };
    }

    private Object withPtrChain(GameMemoryData data) {
        final MemoryReadingService mrService = MemoryReadingService.getInstance();
        final Long module = mrService.getModuleBaseAddresses(MEMORY_MODULE);

        return switch (data.getMemoryType()) {
            case BYTE -> bytePtrChain(mrService, module, data);
            case INT -> intPtrChain(mrService, module, data);
            case LONG -> longPtrChain(mrService, module, data);
        };
    }

    private Object bytePtrChain(MemoryReadingService mrService, Long module, GameMemoryData data) {
        final Long address = mrService.readLongFromAddress(module + data.getOffset());
        return mrService.followPtrChainToByte(address, data.getPtrChain());
    }

    private Object intPtrChain(MemoryReadingService mrService, Long module, GameMemoryData data) {
        final Long address = mrService.readLongFromAddress(module + data.getOffset());
        return mrService.followPtrChainToInt(address, data.getPtrChain());
    }

    private Object longPtrChain(MemoryReadingService mrService, Long module, GameMemoryData data) {
        final Long address = mrService.readLongFromAddress(module + data.getOffset());
        return mrService.followPtrChainToLong(address, data.getPtrChain());
    }

    public static GameMemoryDataReaderUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final GameMemoryDataReaderUtil INSTANCE = new GameMemoryDataReaderUtil();
    }

}
