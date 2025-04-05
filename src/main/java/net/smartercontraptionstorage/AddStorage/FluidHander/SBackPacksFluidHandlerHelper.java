//package net.smartercontraptionstorage.AddStorage.FluidHander;
//
//import com.simibubi.create.foundation.fluid.SmartFluidTank;
//import net.minecraft.core.HolderLookup;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraftforge.fluids.FluidStack;
//import net.minecraftforge.fluids.capability.IFluidHandler;
//import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock;
//import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlockEntity;
//import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
//import org.jetbrains.annotations.NotNull;
//
//public class SBackPacksFluidHandlerHelper extends FluidHandlerHelper{
//    @Override
//    public void addStorageToWorld(BlockEntity entity, SmartFluidTank helper) {
//        assert canCreateHandler(entity);
//        ((BackpackBlockEntity)entity).getStorageWrapper().getFluidHandler().ifPresent(handler -> {
//            handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
//            handler.fill(helper.getFluidInTank(0), IFluidHandler.FluidAction.EXECUTE);
//        });
//    }
//
//    @Override
//    public boolean canCreateHandler(Item comparedItem) {
//        return comparedItem instanceof BackpackItem;
//    }
//
//    @Override
//    public boolean canCreateHandler(Block block) {
//        return block instanceof BackpackBlock;
//    }
//
//    @Override
//    public boolean canCreateHandler(BlockEntity entity) {
//        return entity instanceof BackpackBlockEntity && ((BackpackBlockEntity)entity).getBackpackWrapper().getFluidHandler().isPresent();
//    }
//
//    @Override
//    public @NotNull SmartFluidTank createHandler(BlockEntity entity) {
//        assert canCreateHandler(entity);
//        return new BackPackFluidHelper(((BackpackBlockEntity)entity).getBackpackWrapper().getFluidHandler().get());
//    }
//
//    @Override
//    public String getName() {
//        return "SBackPacksFluidHandlerHelper";
//    }
//
//    @Override
//    public @NotNull SmartFluidTank deserialize(CompoundTag nbt) {
//        return new BackPackFluidHelper(nbt);
//    }
//
//    public static class BackPackFluidHelper extends FluidHelper {
//        public BackPackFluidHelper(IFluidHandler handler) {
//            super(handler.getTankCapacity(0));
//            fluid = handler.getFluidInTank(0);
//        }
//
//        public BackPackFluidHelper(CompoundTag nbt) {
//            super(nbt);
//        }
//
//        @Override
//        public boolean canFill(FluidStack fluid) {
//            return fluid.isFluidEqual(this.fluid) || this.fluid.isEmpty();
//        }
//
//        @Override
//        public void setFluid(int amount, FluidStack stack) {
//            if(fluid.isEmpty())
//                fluid = stack.copy();
//            fluid.setAmount(amount);
//        }
//
//        @Override
//        public int getAmount() {
//            return fluid.getAmount();
//        }
//
//        @Override
//        public CompoundTag serialize(CompoundTag nbt, HolderLookup.Provider provider) {
//            return nbt;
//        }
//    }
//}