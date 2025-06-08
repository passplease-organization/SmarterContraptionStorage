package net.smartercontraptionstorage.AddStorage.FluidHander;

import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorage;
import com.simibubi.create.api.contraption.storage.fluid.WrapperMountedFluidStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MovingFluidStorage extends WrapperMountedFluidStorage<IFluidHandler> {
    public final @NonNull FluidHandlerHelper helper;

    public BlockEntity blockEntity;

    protected MovingFluidStorage(IFluidHandler handler, @NotNull FluidHandlerHelper helper) {
        super(MovingFluidStorageType.HELPER_STORAGE.get(), handler);
        this.helper = helper;
    }

    @Override
    public void unmount(Level level, BlockState blockState, BlockPos blockPos, @Nullable BlockEntity blockEntity) {
        helper.addStorageToWorld(helper.canCreateHandler(blockEntity) ? blockEntity : this.blockEntity,getHandler());
    }

    public @NotNull FluidHandlerHelper getHelper() {
        return helper;
    }

    public IFluidHandler getHandler(){
        return wrapped;
    }

    protected @Nullable NeedDealWith getDeal(){
        if(helper instanceof NeedDealWith)
            return  ((NeedDealWith) helper);
        else if(getHandler() instanceof NeedDealWith)
            return  ((NeedDealWith) getHandler());
        return null;
    }

    public void doSomething(Map<BlockPos, MountedFluidStorage> fluidBuilder,Map<BlockPos, MountedItemStorage> itemsBuilder){
        NeedDealWith deal = getDeal();
        if(deal != null)
            deal.doSomething(blockEntity,fluidBuilder,itemsBuilder);
    }

    public void finallyDo(Map<BlockPos, MountedFluidStorage> fluidBuilder, Map<BlockPos, MountedItemStorage> itemsBuilder){
        NeedDealWith deal = getDeal();
        if(deal != null)
            deal.finallyDo(fluidBuilder,itemsBuilder);
    }
}
