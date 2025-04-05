package net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper;

import appeng.api.networking.IGridNode;
import appeng.blockentity.networking.ControllerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

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
}
