package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.supermartijn642.trashcans.TrashCans;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.ItemStackHandler;
import net.smartercontraptionstorage.FunctionChanger;
import net.smartercontraptionstorage.SmarterContraptionStorage;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MovingItemStorageType extends MountedItemStorageType<MovingItemStorage> {
    public static final String TAG = "localPos";
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(SmarterContraptionStorage.MODID);
    public static final RegistryEntry<MovingItemStorageType> HELPER_STORAGE = REGISTRATE.mountedItemStorage("helper_storage",MovingItemStorageType::new).register();
    public static final Codec<MovingItemStorage> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<MovingItemStorage, T>> decode(DynamicOps<T> ops, T input) {
            if(ops.convertTo(NbtOps.INSTANCE, input) instanceof CompoundTag nbt) {
                StorageHandlerHelper helper = StorageHandlerHelper.findByName(nbt.getString("helper"));
                try {
                    ItemStackHandler handler = null;
                    BlockEntity blockEntity = FunctionChanger.getBlockEntity(NbtUtils.readBlockPos(nbt.getCompound(TAG)));
                    if(helper.canDeserialize()) {
                        handler = helper.deserialize(nbt);
                    }else if(helper.canCreateHandler(blockEntity)){
                        handler = helper.createHandler(blockEntity);
                    }
                    MovingItemStorage storage = new MovingItemStorage(handler, helper);
                    storage.blockEntity = blockEntity;
                    return DataResult.success(new Pair<>(storage,input));
                } catch (IllegalAccessException ignored) {
                    Utils.addError("Helper cannot create handler : " + helper.getName());
                    return DataResult.success(new Pair<>(null, input));
                }
            }else return DataResult.error(() -> "Can not convert to CompoundTag ! Decode Failed !");
        }

        @Override
        public <T> DataResult<T> encode(MovingItemStorage input, DynamicOps<T> ops, T prefix) {
            CompoundTag nbt;
            if(input.helper.canDeserialize()) {
                nbt = input.getHandler().serializeNBT();
            }else {
                input.helper.addStorageToWorld(Objects.requireNonNull(input.blockEntity), input.getHandler());
                nbt = new CompoundTag();
            }
            nbt.putString("helper",input.helper.getName());
            return DataResult.success(NbtOps.INSTANCE.convertTo(ops, nbt));
        }
    };
    protected MovingItemStorageType() {
        super(CODEC);
    }

    @Override
    public @Nullable MovingItemStorage mount(Level level, BlockState blockState, BlockPos blockPos, @Nullable BlockEntity blockEntity) {
        StorageHandlerHelper helper = StorageHandlerHelper.findSuitableHelper(blockEntity);
        if(helper == null)
            return null;
        ItemStackHandler handler = helper.createHandler(blockEntity);
        return new MovingItemStorage(handler, helper);
    }

    public static void register() {
        BuiltInRegistries.BLOCK.stream().filter(block -> {
            for (StorageHandlerHelper handlerHelper : StorageHandlerHelper.getHandlerHelpers()){
                if(handlerHelper.allowControl(block))
                    return true;
            }
            return false;
        }).forEach(MovingItemStorageType::register);
        ModList list = ModList.get();
        if(list.isLoaded(SmarterContraptionStorage.TrashCans)){
            register(TrashCans.item_trash_can);
            register(TrashCans.ultimate_trash_can);
        }
    }

    private static void register(Block block){
        MountedItemStorageType.REGISTRY.register(block, HELPER_STORAGE.get());
    }
}
