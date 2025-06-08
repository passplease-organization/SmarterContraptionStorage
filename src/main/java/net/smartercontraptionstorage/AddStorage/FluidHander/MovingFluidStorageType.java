package net.smartercontraptionstorage.AddStorage.FluidHander;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
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
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.smartercontraptionstorage.AddStorage.ItemHandler.MovingItemStorageType;
import net.smartercontraptionstorage.FunctionChanger;
import net.smartercontraptionstorage.SmarterContraptionStorage;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Objects;

public class MovingFluidStorageType extends MountedFluidStorageType<MovingFluidStorage> {
    
    public static final RegistryEntry<MovingFluidStorageType> HELPER_STORAGE = SmarterContraptionStorage.REGISTRATE.mountedFluidStorage("helper_fluid_storage",MovingFluidStorageType::new).register();
    
    public static final Codec<MovingFluidStorage> CODEC = new Codec<>(){
        @Override
        public <T> DataResult<Pair<MovingFluidStorage, T>> decode(DynamicOps<T> ops, T input) {
            if(ops.convertTo(NbtOps.INSTANCE, input) instanceof CompoundTag nbt) {
                FluidHandlerHelper helper = FluidHandlerHelper.findByName(nbt.getString("helper"));
                try {
                    IFluidHandler handler = null;
                    BlockEntity blockEntity = FunctionChanger.getBlockEntity(NbtUtils.readBlockPos(nbt.getCompound(MovingItemStorageType.TAG)));
                    if(helper.canDeserialize()) {
                        handler = helper.deserialize(nbt);
                    }else if(helper.canCreateHandler(blockEntity)){
                        handler = helper.createHandler(blockEntity);
                    }
                    MovingFluidStorage storage = new MovingFluidStorage(handler, helper);
                    storage.blockEntity = blockEntity;
                    return DataResult.success(new Pair<>(storage,input));
                } catch (IllegalAccessException ignored) {
                    Utils.addError("Helper cannot create handler : " + helper.getName());
                    return DataResult.success(new Pair<>(null, input));
                }
            }else return DataResult.error(() -> "Can not convert to CompoundTag ! Decode Failed !");
        }

        @Override
        public <T> DataResult<T> encode(MovingFluidStorage input, DynamicOps<T> ops, T prefix) {
            CompoundTag nbt;
            if(input.helper.canDeserialize()) {
                nbt = input.getHelper().serializeNBT(input.getHandler());
            }else {
                input.helper.addStorageToWorld(Objects.requireNonNull(input.blockEntity), input.getHandler());
                nbt = new CompoundTag();
            }
            nbt.putString("helper",input.helper.getName());
            return DataResult.success(NbtOps.INSTANCE.convertTo(ops, nbt));
        }
    };
    
    protected MovingFluidStorageType() {
        super(CODEC);
    }

    @Override
    public @Nullable MovingFluidStorage mount(Level level, BlockState blockState, BlockPos blockPos, @Nullable BlockEntity blockEntity) {
        FluidHandlerHelper helper = FluidHandlerHelper.findSuitableHelper(blockEntity);
        if(helper == null)
            return null;
        return new MovingFluidStorage(helper.createHandler(blockEntity), helper);
    }

    public static void load(){}

    public static void register(){
        BuiltInRegistries.BLOCK.stream().filter(block -> {
            for (FluidHandlerHelper handlerHelper : FluidHandlerHelper.getHandlerHelpers()){
                if(handlerHelper.canCreateHandler(block))
                    return true;
            }
            return false;
        }).forEach(MovingFluidStorageType::register);
    }

    public static void registerTrashCan() {
        try{
            Class<?> trashcan = com.supermartijn642.trashcans.TrashCans.class;

            Field item_trash_can = trashcan.getDeclaredField("liquid_trash_can");
            register((Block) item_trash_can.get(trashcan));

            Field ultimate_trash_can = trashcan.getDeclaredField("ultimate_trash_can");
            register((Block) ultimate_trash_can.get(trashcan));
        } catch (NoSuchFieldException e) {
            Utils.addError("Unchecked Trash Can register !");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void register(Block block){
        MovingFluidStorageType.REGISTRY.register(block, HELPER_STORAGE.get());
    }
}
