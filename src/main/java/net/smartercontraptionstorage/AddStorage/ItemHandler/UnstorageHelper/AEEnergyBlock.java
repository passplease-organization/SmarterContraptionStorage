package net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper;

import appeng.api.networking.IGridNode;
import appeng.blockentity.networking.CreativeEnergyCellBlockEntity;
import appeng.blockentity.networking.EnergyCellBlockEntity;
import appeng.core.definitions.AEBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Consumer;

public class AEEnergyBlock extends InitializeHelper{
    @Override
    public boolean canDoSomething(BlockEntity entity) {
        return entity instanceof EnergyCellBlockEntity || entity instanceof CreativeEnergyCellBlockEntity;
    }

    @Override
    public void doSomething(BlockEntity entity) {
        IGridNode node;
        node = entity instanceof EnergyCellBlockEntity ? ((EnergyCellBlockEntity) entity).getActionableNode() : ((CreativeEnergyCellBlockEntity)entity).getActionableNode();
        if (node != null && node.isActive())
            normallyDo(entity);
    }

    @Override
    public void registerBlock(Consumer<Block> register) {
        register.accept(AEBlocks.DENSE_ENERGY_CELL.block());
        register.accept(AEBlocks.ENERGY_CELL.block());
        register.accept(AEBlocks.CREATIVE_ENERGY_CELL.block());
    }
}
