package net.smartercontraptionstorage;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Function;

public final class FunctionChanger {
    /**
     * To serialize some MountedStorage,
     * value set in MountedStorageManagerMixin, Contraption
     */
    private static Function<BlockPos, BlockEntity> getBlockEntity;

    private static boolean decoding = false;

    public static void setGetBlockEntity(Function<BlockPos, BlockEntity> getBlockEntity) {
        FunctionChanger.getBlockEntity = getBlockEntity;
        decoding = true;
    }

    public static void clearGetBlockEntity() {
        FunctionChanger.getBlockEntity = null;
        decoding = false;
    }

    public static BlockEntity getBlockEntity(BlockPos pos){
        if(decoding)
            return getBlockEntity.apply(pos);
        else throw new IllegalCallerException("Not decoding MountedStorageManager !");
    }
}