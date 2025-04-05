package net.smartercontraptionstorage.AddStorage;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface NeedDealWith{
    /**
    * Method for doing something to help other handlers to initialize,
     * such as toolbox to help trashcan initialize
    * */
    void doSomething(BlockEntity entity);
    /**
     * Method for using data which come from <code> doSomething</code> to initialize handler itself
     * */
    void finallyDo();
}
