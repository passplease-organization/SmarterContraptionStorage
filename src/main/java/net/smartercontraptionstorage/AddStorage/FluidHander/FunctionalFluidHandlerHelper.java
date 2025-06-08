package net.smartercontraptionstorage.AddStorage.FluidHander;

import com.buuz135.functionalstorage.block.FluidDrawerBlock;
import com.buuz135.functionalstorage.block.tile.FluidDrawerTile;
import com.buuz135.functionalstorage.fluid.BigFluidHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FunctionalFluidHandlerHelper extends FluidHandlerHelper{
    @Deprecated
    public static final String Slot = Integer.toString(DefaultSlot);
    public static final String LOCKED = "Locked";
    public static final String VOID = "Void";
    public static final String CREATIVE = "Creative";
    @Override
    public void addStorageToWorld(BlockEntity entity, IFluidHandler tank) {
        assert canCreateHandler(entity);
        if(tank instanceof BigFluidHandler handler)
            ((FluidDrawerTile)entity).getFluidHandler().deserializeNBT(handler.serializeNBT());
    }

    @Override
    public boolean canCreateHandler(Item comparedItem) {
        return comparedItem instanceof FluidDrawerBlock.DrawerItem;
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
    public @NotNull IFluidHandler createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        return ((FluidDrawerTile)entity).getFluidHandler();
    }

    @Override
    public @NotNull CompoundTag serializeNBT(IFluidHandler handler) {
        if(handler instanceof BigFluidHandler drawer){
            CompoundTag nbt = drawer.serializeNBT();
            nbt.putBoolean(LOCKED, drawer.isDrawerLocked());
            nbt.putBoolean(VOID,drawer.isDrawerVoid());
            nbt.putBoolean(CREATIVE,drawer.isDrawerCreative());
            return nbt;
        }else return new CompoundTag();
    }

    @Override
    public String getName() {
        return "FunctionalFluidHandlerHelper";
    }

    @Override
    public @NotNull BigFluidHandler deserialize(CompoundTag nbt) {
        BigFluidHandler drawer = new BigFluidHandler(1,1) {
            @Override
            public void onChange() {}

            boolean locked;

            @Override
            public boolean isDrawerLocked() {
                return locked;
            }

            boolean Void;

            @Override
            public boolean isDrawerVoid() {
                return Void;
            }

            private boolean creative;

            @Override
            public boolean isDrawerCreative() {
                return creative;
            }

            @Override
            public void deserializeNBT(CompoundTag nbt) {
                super.deserializeNBT(nbt);
                locked = nbt.getBoolean(LOCKED);
                Void = nbt.getBoolean(VOID);
                creative = nbt.getBoolean(CREATIVE);
            }
        };
        drawer.deserializeNBT(nbt);
        return drawer;
    }

    @Deprecated
    public static class FluidDrawerHandler extends FluidHelper{
        public final FluidStack filter;
        @Deprecated
        public FluidDrawerHandler(BigFluidHandler handler) {
            super(handler.getTankCapacity(DefaultSlot));
            fluid = handler.getFluidInTank(DefaultSlot);
            filter = Arrays.stream(handler.getFilterStack()).toList().get(DefaultSlot);
        }

        @Deprecated
        public FluidDrawerHandler(CompoundTag nbt){
            super(nbt);
            filter = FluidStack.loadFluidStackFromNBT(nbt);
        }

        @Override
        public boolean canFill(FluidStack fluid) {
            return filter.isFluidEqual(fluid) || filter.isEmpty() && (this.fluid.isFluidEqual(fluid) || this.fluid.isEmpty());
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
        protected CompoundTag serialize(CompoundTag tag) {
            filter.writeToNBT(tag);
            return tag;
        }
    }
}