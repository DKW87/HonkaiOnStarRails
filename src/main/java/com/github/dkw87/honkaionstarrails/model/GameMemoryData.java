package com.github.dkw87.honkaionstarrails.model;

import com.github.dkw87.honkaionstarrails.model.enumeration.MemoryType;
import com.github.dkw87.honkaionstarrails.model.utility.GameMemoryDataReaderUtil;
import lombok.Getter;

@Getter
public class GameMemoryData {

    private final Long offset;
    private final Integer[] ptrChain;
    private final MemoryType memoryType;
    private final GameMemoryDataReaderUtil dataReaderUtil;

    public GameMemoryData(Long offset,
                          Integer[] ptrChain,
                          MemoryType memoryType) {
        this.offset = offset;
        this.ptrChain = ptrChain;
        this.memoryType = memoryType;
        this.dataReaderUtil = GameMemoryDataReaderUtil.getInstance();
    }

    public Object readFromMemory() {
        return dataReaderUtil.readFromMemory(this);
    }

}
