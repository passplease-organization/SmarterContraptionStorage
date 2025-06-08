package net.smartercontraptionstorage.AddStorage.FluidHander;

import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.smartercontraptionstorage.Utils;
import net.smartercontraptionstorage.SmarterContraptionStorageConfig;
import org.jetbrains.annotations.NotNull;

public final class DumpHandler extends ItemStackHandler {
    public @NotNull CombinedTankWrapper fluidInventory;
    public DumpHandler(@NotNull CombinedTankWrapper fluidInventory){
        super(1);
        this.fluidInventory = fluidInventory;
    }
    public void setFluidInventory(@NotNull CombinedTankWrapper fluidInventory){
        this.fluidInventory = fluidInventory;
    }
    public static boolean isOpened(){
        return SmarterContraptionStorageConfig.AUTO_DUMPING.get();
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if(stack.getItem() instanceof BucketItem) {
            FluidStack fluidStack = Utils.getFluidByItem(stack).get(0);
            if (fluidInventory.drain(fluidStack, IFluidHandler.FluidAction.SIMULATE).isEmpty()) {
                if(!simulate)
                    fluidInventory.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                return Items.BUCKET.getDefaultInstance();
            } else return stack;
        }else {
            stack = stack.copy();
            Utils.forEachTankDo(stack, handler -> {
                FluidStack dumped;
                int dumpedCount;
                for (int i = 0; i < handler.getTanks(); i++) {
                    dumped = handler.getFluidInTank(i).copy();
                    // Something like MEK cannot drain all fluid in cans
                    if (!simulate) {
                        dumpedCount = fluidInventory.drain(dumped, IFluidHandler.FluidAction.EXECUTE).getAmount();
                    } else {
                        dumpedCount = fluidInventory.drain(dumped, IFluidHandler.FluidAction.SIMULATE).getAmount();
                    }
                    handler.getFluidInTank(i).shrink(dumpedCount);
                }
            });
            return stack;
        }
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 0;
    }
}