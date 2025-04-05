package net.smartercontraptionstorage.Mixin.Contraption;

import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorage;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper;
import com.simibubi.create.content.contraptions.*;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.smartercontraptionstorage.AddStorage.ItemHandler.MovingItemStorage;
import net.smartercontraptionstorage.AddStorage.ItemHandler.MovingItemStorageType;
import net.smartercontraptionstorage.Interface.Changeable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

// TODO 全部的mixin可能都要重做
@Mixin(MountedStorageManager.class)
public abstract class MountedStorageManagerMixin implements Changeable {
    @Shadow(remap = false) private Map<BlockPos, MountedItemStorage> itemsBuilder;

    @Shadow(remap = false) private Map<BlockPos, MountedFluidStorage> fluidsBuilder;

    @Shadow(remap = false) protected MountedItemStorageWrapper items;
    @Shadow(remap = false) protected MountedFluidStorageWrapper fluids;
    @Shadow(remap = false) @Nullable protected MountedItemStorageWrapper fuelItems;
    // TODO 加上DumpHandler
//    @Unique public DumpHandler smarterContraptionStorage$handler;
//    @Unique
//    public Collection<IItemHandlerModifiable> smarterContraptionStorage$addDumpFillingHandler(Collection<IItemHandlerModifiable> list){
//        if(DumpHandler.isOpened()){
//            Collection<IItemHandlerModifiable> List = new ArrayList<>();
//            List.add(smarterContraptionStorage$handler);// ensure DumpHandler is the first
//            List.addAll(list);
//            return List;
//        }
//        return list;
//    }

    @Shadow(remap = false) protected abstract boolean isInitialized();

    /**
     * @author passplease
     * @reason to adjust the order of createHandler (first create fluidInventory) so that I can initialize dumpHandler
     */
//    @Overwrite(remap = false)
//    public void createHandlers(){
//        this.fluidInventory = this.wrapFluids(this.fluidStorage.values().stream().map(MountedFluidStorage::getFluidHandler).collect(Collectors.toList()));
//        smarterContraptionStorage$handler = new DumpHandler(fluidInventory);
//        Collection<MountedStorage> itemHandlers = this.storage.values();
//        this.inventory = this.wrapItems(smarterContraptionStorage$addDumpFillingHandler(itemHandlers.stream().map(MountedStorage::getItemHandler).toList()), false);
//        this.fuelInventory = this.wrapItems(smarterContraptionStorage$addDumpFillingHandler(itemHandlers.stream().filter(MountedStorage::canUseForFuel).map(MountedStorage::getItemHandler).toList()), true);
//    }
    @Inject(method = "initialize",at = @At(value = "HEAD"),remap = false)
    public void removeStorageFromWorld(CallbackInfo ci){
        if(!isInitialized()) {
            List<MountedItemStorage> needDoSomething = itemsBuilder.values().stream().filter(storage -> storage instanceof MovingItemStorage).toList();
            needDoSomething.forEach(storage -> ((MovingItemStorage) storage).doSomething());
            needDoSomething.forEach(storage -> ((MovingItemStorage) storage).finallyDo());
        }
    }

    @Inject(method = {"lambda$read$6","lambda$read$8"},at = @At("HEAD"),remap = false)
    public void writePos(HolderLookup.Provider registries, CompoundTag tag, CallbackInfo ci){
        BlockPos pos = NBTHelper.readBlockPos(tag,"pos");
        tag.getCompound("storage").getCompound(MovingItemStorageType.TYPE).put(MovingItemStorageType.TAG,NbtUtils.writeBlockPos(pos));
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
