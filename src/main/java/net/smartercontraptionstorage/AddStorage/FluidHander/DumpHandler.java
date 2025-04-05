package net.smartercontraptionstorage.AddStorage.FluidHander;

import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
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
        return SmarterContraptionStorageConfig.autoDumping();
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if(isOpened()){
            if(stack.getItem() instanceof BucketItem) {
                FluidStack fluidHandler = Utils.getFluidByItem(stack).get(0);
                if (fluidInventory.fill(fluidHandler, IFluidHandler.FluidAction.SIMULATE) == 0)
                    fluidInventory.fill(fluidHandler, IFluidHandler.FluidAction.EXECUTE);
                else return stack;
                return Items.BUCKET.getDefaultInstance();
            }else Utils.forEachTankDo(stack,handler -> {
                FluidStack dumped;
                int dumpedCount;
                for (int i = 0; i < handler.getTanks(); i++) {
                    dumped = handler.getFluidInTank(i).copy();
                    dumped.setAmount(handler.drain(dumped, IFluidHandler.FluidAction.SIMULATE).getAmount());
                    // Something like MEK cannot drain all fluid in cans
                    dumpedCount = fluidInventory.fill(dumped, IFluidHandler.FluidAction.EXECUTE);
                    dumped.setAmount(dumpedCount);
                    handler.drain(dumped, IFluidHandler.FluidAction.EXECUTE);
                }
            });
        }
        return stack;
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