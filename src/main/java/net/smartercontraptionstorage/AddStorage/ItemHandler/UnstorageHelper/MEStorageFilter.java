package net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper;

import appeng.api.networking.IGridNode;
import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.helpers.InterfaceLogic;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.smartercontraptionstorage.AddStorage.ItemHandler.AE2BusBlockHelper;

public class MEStorageFilter extends InitializeHelper{
    @Override
    public boolean canDoSomething(BlockEntity entity) {
        return entity instanceof InterfaceBlockEntity;
    }

    @Override
    public void doSomething(BlockEntity entity) {
        InterfaceLogic logic = ((InterfaceBlockEntity)entity).getInterfaceLogic();
        IGridNode node = logic.getActionableNode();
        if(node != null && node.isActive() && AE2BusBlockHelper.checkUpgrade(logic.getUpgrades(),AEItems.FUZZY_CARD.asItem()))
            normallyDo(entity);
    }
}
