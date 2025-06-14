package net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper;

import appeng.api.networking.IGridNode;
import appeng.blockentity.networking.ControllerBlockEntity;
import appeng.core.definitions.AEBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Consumer;

public class AEControllerBlock extends InitializeHelper{
    @Override
    public boolean canDoSomething(BlockEntity entity) {
        return entity instanceof ControllerBlockEntity;
    }

    @Override
    public void doSomething(BlockEntity entity) {
        IGridNode node = ((ControllerBlockEntity) entity).getActionableNode();
        if(node != null && node.isActive())
            normallyDo(entity);
    }

    @Override
    public void registerBlock(Consumer<Block> register) {
        register.accept(AEBlocks.CONTROLLER.block());
    }
}
