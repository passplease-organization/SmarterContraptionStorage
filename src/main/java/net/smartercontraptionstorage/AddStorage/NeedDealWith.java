package net.smartercontraptionstorage.AddStorage;

import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;

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

    default void doSomething(BlockEntity entity,Map<BlockPos, MountedItemStorage> itemsBuilder){
        doSomething(entity);
    }

    /**
     * Distinct from function up, this function is for fluid
     * */
    default void doSomething(BlockEntity entity,Map<BlockPos,MountedFluidStorage> fluidBuilder,Map<BlockPos, MountedItemStorage> itemsBuilder){
        doSomething(entity);
    }

    default void finallyDo(Map<BlockPos, MountedItemStorage> itemsBuilder){
        finallyDo();
    }

    /**
     * Distinct from function up, this function is for fluid
     * */
    default void finallyDo(Map<BlockPos, MountedFluidStorage> fluidBuilder, Map<BlockPos, MountedItemStorage> itemsBuilder){
        finallyDo();
    }
}
