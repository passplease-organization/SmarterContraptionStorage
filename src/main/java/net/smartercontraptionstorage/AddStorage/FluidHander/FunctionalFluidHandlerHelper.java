package net.smartercontraptionstorage.AddStorage.FluidHander;

import com.buuz135.functionalstorage.block.FluidDrawerBlock;
import com.buuz135.functionalstorage.block.tile.FluidDrawerTile;
import com.buuz135.functionalstorage.fluid.BigFluidHandler;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FunctionalFluidHandlerHelper extends FluidHandlerHelper{
    public static final String Slot = Integer.toString(DefaultSlot);
    public static final String Locked = "Locked" + Slot;
    @Override
    public void addStorageToWorld(BlockEntity entity, SmartFluidTank helper) {
        assert canCreateHandler(entity);
        FluidDrawerTile Entity = (FluidDrawerTile)entity;
        RegistryAccess registryAccess = Entity.getLevel().registryAccess();
        CompoundTag nbt = Entity.getFluidHandler().serializeNBT(registryAccess);
        nbt.put(Slot, helper.getFluid().save(registryAccess));
        nbt.put(Locked,((FluidDrawerHandler)helper).filter.save(registryAccess));
        Entity.getFluidHandler().deserializeNBT(registryAccess,nbt);
    }

    @Override
    public boolean canCreateHandler(Item comparedItem) {
        return comparedItem instanceof FluidDrawerBlock.FluidDrawerItem;
    }

    @Override
    public boolean canCreateHandler(Block block) {
        return block instanceof FluidDrawerBlock;
    }

    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof FluidDrawerTile;
    }

    @Override
    public @NotNull SmartFluidTank createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        return new FluidDrawerHandler(((FluidDrawerTile) entity).getFluidHandler());
    }

    @Override
    public String getName() {
        return "FunctionalFluidHandlerHelper";
    }

    @Override
    public SmartFluidTank deserialize(CompoundTag nbt, HolderLookup.Provider provider) throws IllegalAccessException {
        return new FluidDrawerHandler(nbt,provider);
    }

    public static class FluidDrawerHandler extends FluidHelper{
        public final FluidStack filter;
        public FluidDrawerHandler(BigFluidHandler handler) {
            super(handler.getTankCapacity(DefaultSlot));
            fluid = handler.getFluidInTank(DefaultSlot);
            filter = Arrays.stream(handler.getFilterStack()).toList().get(DefaultSlot);
        }

        public FluidDrawerHandler(CompoundTag nbt, HolderLookup.Provider provider) {
            super(nbt,provider);
            filter = FluidStack.parseOptional(provider,nbt);
        }

        @Override
        public boolean canFill(FluidStack fluid) {
            return FluidStack.isSameFluidSameComponents(fluid,filter) || filter.isEmpty() && (FluidStack.isSameFluidSameComponents(fluid,filter) || this.fluid.isEmpty());
        }

        public void setFluid(int amount, FluidStack stack){
            if(fluid.isEmpty())
                fluid = stack.copy();
            fluid.setAmount(amount);
        }

        @Override
        public int getAmount() {
            return fluid.getAmount();
        }

        @Override
        public CompoundTag serialize(CompoundTag tag, HolderLookup.Provider provider) {
            filter.save(provider,tag);
            return tag;
        }
    }
}