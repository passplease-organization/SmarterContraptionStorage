package net.smartercontraptionstorage.AddStorage.FluidHander;

import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.smartercontraptionstorage.AddStorage.SerializableHandler;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class FluidHandlerHelper implements SerializableHandler<SmartFluidTank> {
    public static final String DESERIALIZE_MARKER = "FluidHandlers";
    public static final SmartFluidTank NULL_HANDLER = new SmartFluidTank(0,null){
        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return 0;
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return FluidStack.EMPTY;
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    };
    /**
     * Due to we can only add one tank to the contraption for each entity, so we add DefaultSlot of muti-slots entity
     * */
    public static final int DefaultSlot = 0;
    protected static final ArrayList<BlockEntity> BlockEntityList = new ArrayList<>();
    public static boolean canUseAsStorage(@NotNull Item comparedItem){
        return canUseAsStorage(comparedItem,Block.byItem(comparedItem));
    }
    public static boolean canUseAsStorage(@NotNull Block comparedBlock){
        return canUseAsStorage(comparedBlock.asItem(),comparedBlock);
    }
    private static final Set<FluidHandlerHelper> HandlerHelpers = new HashSet<>();
    public static void register(@NotNull FluidHandlerHelper handlerHelper){
        HandlerHelpers.add(handlerHelper);
    }
    public static boolean canUseAsStorage(@Nullable Item comparedItem, @Nullable Block comparedBlock){
        if(comparedItem == null || comparedBlock == null)
            return false;
        for(FluidHandlerHelper handlerHelper : HandlerHelpers)
            if(handlerHelper.canCreateHandler(comparedItem))
                return true;
        if(comparedBlock == Blocks.AIR)
            return false;
        for(FluidHandlerHelper handlerHelper : HandlerHelpers)
            if(handlerHelper.canCreateHandler(comparedBlock))
                return true;
        return false;
    }
    public static boolean canUseAsStorage(BlockEntity entity){
        for(FluidHandlerHelper handlerHelper : HandlerHelpers)
            if(handlerHelper.canCreateHandler(entity))
                return true;
        return false;
    }
    public static @Nullable FluidHandlerHelper findSuitableHelper(BlockEntity entity){
        for(FluidHandlerHelper handlerHelper : HandlerHelpers)
            if(handlerHelper.canCreateHandler(entity))
                return handlerHelper;
        return null;
    }

    public static @Nonnull FluidHandlerHelper findByName(String name){
        FluidHandlerHelper h = HandlerHelpers.stream().filter((helper) -> Objects.equals(helper.getName(), name)).findFirst().orElse(null);
        if(h == null)
            Utils.addWarning("Invalid fluid handler name: " + name);
        return Objects.requireNonNull(h);
    }

    public static void clearData() {
        BlockEntityList.clear();
    }

    public boolean sendPacket(){
        return false;
    }
    public void tick(Entity entity, BlockPos pos, boolean isRemote){}
    public abstract void addStorageToWorld(BlockEntity entity,SmartFluidTank helper);
    public abstract boolean canCreateHandler(Item comparedItem);
    public abstract boolean canCreateHandler(Block block);
    public abstract boolean canCreateHandler(BlockEntity entity);
    public abstract @NotNull SmartFluidTank createHandler(BlockEntity entity);
    public static Set<FluidHandlerHelper> getHandlerHelpers() {
        return HandlerHelpers;
    }
    public static abstract class FluidHelper extends SmartFluidTank{
        public FluidHelper(int capacity){
            super(capacity,null);
        }
        public FluidHelper(CompoundTag nbt, HolderLookup.Provider provider){
            super(nbt.getInt("capacity"),null);
            super.readFromNBT(provider,nbt);
        }
        public abstract boolean canFill(FluidStack fluid);
        public abstract void setFluid(int amount,FluidStack stack);
        @Override
        public int fill(FluidStack resource, FluidAction action){
            if(canFill(resource)){
                int sum = resource.getAmount() + getAmount();
                if(sum <= capacity){
                    if(action.execute())
                        setFluid(sum,resource);
                    return resource.getAmount();
                }else {
                    sum = capacity - getAmount();
                    if(action.execute())
                        setFluid(capacity,resource);
                    return sum;
                }
            }else return 0;
        }
        public abstract int getAmount();
        @Override
        public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action){
            FluidStack toExtract = fluid.copy();
            toExtract.setAmount(Math.min(maxDrain,getAmount()));
            if(action.execute())
                setFluid(getAmount() - toExtract.getAmount(),fluid);
            return toExtract;
        }

        @Override
        public CompoundTag writeToNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            CompoundTag tag = serialize(super.writeToNBT(provider,nbt), provider);
            tag.putInt("capacity",capacity);
            return tag;
        }
        public abstract CompoundTag serialize(CompoundTag nbt,HolderLookup.Provider provider);
        @Override
        public int getFluidAmount() {
            return getAmount();
        }
    }
}