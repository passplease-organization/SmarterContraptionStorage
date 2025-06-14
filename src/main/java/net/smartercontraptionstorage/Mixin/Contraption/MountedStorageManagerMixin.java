package net.smartercontraptionstorage.Mixin.Contraption;

import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorage;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper;
import com.simibubi.create.content.contraptions.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.smartercontraptionstorage.AddStorage.FluidHander.DumpHandler;
import net.smartercontraptionstorage.AddStorage.FluidHander.FluidHandlerHelper;
import net.smartercontraptionstorage.AddStorage.FluidHander.MovingFluidStorage;
import net.smartercontraptionstorage.AddStorage.ItemHandler.MovingItemStorage;
import net.smartercontraptionstorage.AddStorage.ItemHandler.MovingItemStorageType;
import net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper;
import net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper.InitializeHelper;
import net.smartercontraptionstorage.Interface.Changeable;
import net.smartercontraptionstorage.Settable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(MountedStorageManager.class)
public abstract class MountedStorageManagerMixin implements Changeable {
    @Shadow(remap = false) protected Map<BlockPos, MountedItemStorage> itemsBuilder;

    @Shadow(remap = false) protected Map<BlockPos, MountedFluidStorage> fluidsBuilder;

    @Shadow(remap = false) protected MountedItemStorageWrapper items;
    @Shadow(remap = false) protected MountedFluidStorageWrapper fluids;
    @Shadow(remap = false) @Nullable protected MountedItemStorageWrapper fuelItems;

    @Shadow(remap = false) protected abstract boolean isInitialized();

    @Inject(method = "initialize",at = @At(value = "HEAD"),remap = false)
    public void removeStorageFromWorld(CallbackInfo ci){
        if(!isInitialized()) {
            List<MountedItemStorage> needDoSomething = itemsBuilder.values().stream().filter(storage -> storage instanceof MovingItemStorage).toList();
            needDoSomething.forEach(storage -> ((MovingItemStorage) storage).doSomething(itemsBuilder));
            needDoSomething.forEach(storage -> ((MovingItemStorage) storage).finallyDo(itemsBuilder));
            itemsBuilder.entrySet().removeIf(storage -> storage.getValue() instanceof MovingItemStorage && ((MovingItemStorage)storage.getValue()).helper instanceof InitializeHelper);
            StorageHandlerHelper.clearData();
            List<MountedFluidStorage> needDoSomethingFluid = fluidsBuilder.values().stream().filter(storage -> storage instanceof MovingFluidStorage).toList();
            needDoSomethingFluid.forEach(storage -> ((MovingFluidStorage) storage).doSomething(fluidsBuilder,itemsBuilder));
            needDoSomethingFluid.forEach(storage -> ((MovingFluidStorage) storage).finallyDo(fluidsBuilder,itemsBuilder));
            FluidHandlerHelper.clearData();
        }
    }

    @Inject(method = "initialize",at = @At("RETURN"),remap = false)
    public void setDumpHandler(CallbackInfo ci){
        if(DumpHandler.isOpened())
            ((Settable)items).set(new DumpHandler(fluids));
    }

    @Inject(method = {"lambda$read$7","lambda$read$9"},at = @At("HEAD"),remap = false)
    public void writePos(CompoundTag tag, CallbackInfo ci){
        BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("pos"));
        tag.getCompound("storage").put(MovingItemStorageType.TAG,NbtUtils.writeBlockPos(pos));
    }

    @Override
    public void set(Object object) {}

    @Override
    public void set(String parameterName, Object object) {
        if(parameterName.equals("storage") && object instanceof Map) {
            this.itemsBuilder = (Map<BlockPos, MountedItemStorage>) object;
        }
    }

    @Nullable
    @Override
    public Object get(String name) {
        return switch (name){
            case "storage" -> itemsBuilder;
            case "fluidStorage" -> fluidsBuilder;
            case "inventory" -> items;
            case "fluidInventory" -> fluids;
            case "fuelInventory" -> fuelItems;
            default -> null;
        };
    }
}
