package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.mojang.serialization.*;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.supermartijn642.trashcans.TrashCans;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.smartercontraptionstorage.FunctionChanger;
import net.smartercontraptionstorage.SmarterContraptionStorage;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

public class MovingItemStorageType extends MountedItemStorageType<MovingItemStorage> {
    public static final String TAG = "localPos";
    public static final String TYPE = "MovingItemStorageType";
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(SmarterContraptionStorage.MODID);
    public static final RegistryEntry<MountedItemStorageType<?>,MovingItemStorageType> HELPER_STORAGE = REGISTRATE.mountedItemStorage("helper_storage",MovingItemStorageType::new).register();
    public static final MapCodec<MovingItemStorage> CODEC = new MapCodec<>() {
        @Override
        public <T> RecordBuilder<T> encode(MovingItemStorage input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            CompoundTag nbt;
            if(input.blockEntity != null) {
                if (input.helper.canDeserialize()) {
                    nbt = input.getHandler().serializeNBT(input.blockEntity.getLevel().registryAccess());
                } else {
                    input.helper.addStorageToWorld(Objects.requireNonNull(input.blockEntity), input.getHandler());
                    nbt = new CompoundTag();
                }
            }else nbt = new CompoundTag();
            nbt.putString("helper",input.helper.getName());
            return prefix.add(TYPE,NbtOps.INSTANCE.convertTo(ops, nbt));
        }

        @Override
        public <T> DataResult<MovingItemStorage> decode(DynamicOps<T> ops, MapLike<T> input) {
            if(ops.convertTo(NbtOps.INSTANCE, input.get(TYPE)) instanceof CompoundTag nbt) {
                StorageHandlerHelper helper = StorageHandlerHelper.findByName(nbt.getString("helper"));
                try {
                    ItemStackHandler handler = null;
                    BlockEntity blockEntity = FunctionChanger.getBlockEntity(NBTHelper.readBlockPos(nbt,TAG));
                    if(helper.canDeserialize() && blockEntity.getLevel() != null) {
                        handler = helper.deserialize(nbt,blockEntity.getLevel().registryAccess());
                    }else if(helper.canCreateHandler(blockEntity)){
                        handler = helper.createHandler(blockEntity);
                    }
                    MovingItemStorage storage = new MovingItemStorage(handler, helper);
                    storage.blockEntity = blockEntity;
                    return DataResult.success(storage);
                } catch (IllegalAccessException ignored) {
                    Utils.addError("Helper cannot create handler : " + helper.getName());
                    return DataResult.success(null);
                }
            }else return DataResult.error(() -> "Can not convert to CompoundTag ! Decode Failed !");
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.empty();
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
        MovingItemStorage storage = new MovingItemStorage(handler, helper);
        storage.blockEntity = blockEntity;
        return storage;
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
