package net.smartercontraptionstorage.Mixin.Contraption;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorage;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.smartercontraptionstorage.*;
import net.smartercontraptionstorage.Interface.Changeable;
import net.smartercontraptionstorage.Interface.Gettable;
import net.smartercontraptionstorage.Render.Overlay;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(Contraption.class)
public abstract class ContraptionMixin implements Gettable {
    @Shadow(remap = false) protected MountedStorageManager storage;
    @Shadow(remap = false) protected List<MutablePair<StructureTemplate.StructureBlockInfo, MovementContext>> actors;
    @Shadow(remap = false) protected abstract BlockPos toLocalPos(BlockPos globalPos);

    @Shadow(remap = false) protected Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks;

    @Shadow(remap = false) protected abstract MountedStorageManager getStorageForSpawnPacket();

    @Shadow(remap = false) public AbstractContraptionEntity entity;
    @Shadow(remap = false) public Map<BlockPos, BlockEntity> presentBlockEntities;
    @Unique protected Map<Overlay,List<BlockPos>> smarterContraptionStorage$orderedBlocks = new HashMap<>();

    @Unique protected List<BlockPos> smarterContraptionStorage$removedBlocks = new ArrayList<>();
    @Inject(method = "searchMovedStructure",at = @At("RETURN"),remap = false)
    public void changeOrdinary(Level world, BlockPos pos, Direction forcedDirection, CallbackInfoReturnable<Boolean> cir){
        Changeable storage = (Changeable) this.storage;
        Map<BlockPos, MountedStorage> storages = (Map<BlockPos, MountedStorage>) storage.get("storage");
        assert storages != null;
        Map<BlockPos, MountedStorage> newStorage = new LinkedHashMap<>();
        smarterContraptionStorage$removedBlocks.forEach(storages::remove);
        if(!smarterContraptionStorage$orderedBlocks.isEmpty()) {
            List<MutablePair<StructureTemplate.StructureBlockInfo, MovementContext>> newActors = new LinkedList<>();
            Overlay.forEachSequentially((overlay) -> {
                List<BlockPos> list = smarterContraptionStorage$orderedBlocks.get(overlay);
                if(list != null)
                    for (BlockPos blockPos : list) {
                        Optional<MutablePair<StructureTemplate.StructureBlockInfo, MovementContext>> actor = actors.stream().filter((act) -> act.getLeft().pos.equals(blockPos)).findFirst();
                        // I think there is no any block could be used as actor and container at the same time
                        // So use if-else to reduce computation
                        // * Toolbox and Backpack could be used both two
                        if(actor.isPresent()) {
                            newActors.add(actor.get());
                            actors.remove(actor.get());
                        }else storages.computeIfPresent(blockPos, (p, s) -> {
                            newStorage.putIfAbsent(p,s);
                            return null;
                        });
                    }
            });
            newActors.addAll(actors);
            this.actors = newActors;
        }
        newStorage.putAll(storages);
        if(!smarterContraptionStorage$removedBlocks.isEmpty() || !newStorage.isEmpty())
            storage.set("storage", newStorage);
    }
    @Inject(method = "addBlock",at = @At("RETURN"),remap = false)
    public void addBlock(BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair, CallbackInfo ci){
        if(pair.getRight() instanceof ContraptionControlsBlockEntity entity && entity.getLevel() != null) {
            // pos may be not equal to entity.getBlockPos() !!!
            // I think it's bug, but I'm not very sure right now.
            pos = entity.getBlockPos();
            Level level = entity.getLevel();
            if (((Gettable) entity).get("overlay") instanceof Overlay overlay) {
                List<Block> orderedBlock = new ArrayList<>();
                List<BlockPos> list = smarterContraptionStorage$orderedBlocks.getOrDefault(overlay,new ArrayList<>());
                Arrays.stream(Utils.getAroundedBlockPos(pos)).forEach(p -> orderedBlock.add(level.getBlockState(p).getBlock()));
                Utils.searchBlockPos(pos,
                        (thisPos, initialPos) -> orderedBlock.contains(this.smarterContraptionStorage$getBlockAt(level,thisPos)),
                        (thisPos, initialPos, nowValue, returnValue) -> !Boolean.FALSE.equals(nowValue) && !Boolean.FALSE.equals(returnValue),
                        (checkedBlocks, initialPos, returnValue) -> {
                            if (Boolean.TRUE.equals(returnValue)) {
                                checkedBlocks.forEach((p, b) -> {
                                    if(b)
                                        list.add(this.toLocalPos(p));
                                });
                                return true;
                            }
                            return false;
                        },
                        true
                );
            }
            if(Utils.canBeControlledItem(entity.filtering.getFilter().getItem()) && SmarterContraptionStorageConfig.getDefaultOpen(entity.disabled)){
                Block filterBlock = Block.byItem(entity.filtering.getFilter().getItem());
                Utils.searchBlockPos(pos,
                        (thisPos,initialPos) -> this.smarterContraptionStorage$getBlockAt(level,thisPos) == filterBlock,
                        (thisPos,initialPos,nowValue,returnValue) -> !Boolean.FALSE.equals(nowValue) && !Boolean.FALSE.equals(returnValue),
                        (checkedBlocks,initialPos,returnValue) -> {
                            if(Boolean.TRUE.equals(returnValue)) {
                                checkedBlocks.forEach((p, b) -> {
                                    if(b)
                                        smarterContraptionStorage$removedBlocks.add(this.toLocalPos(p));
                                });
                                return true;
                            }
                            return false;
                        },
                        true
                );
            }
        }
    }
    @Unique
    protected Block smarterContraptionStorage$getBlockAt(Level level, BlockPos pos){
        BlockPos localPos = this.toLocalPos(pos);
        if(this.blocks.containsKey(localPos))
            return this.blocks.get(localPos).state.getBlock();
        else return level.getBlockState(pos).getBlock();
    }

    @ForFunctionChanger(method = "deserialize")
    @Inject(method = "readNBT",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/MountedStorageManager;read(Lnet/minecraft/nbt/CompoundTag;Ljava/util/Map;Z)V"),remap = false)
    public void help_deserialize(Level world, CompoundTag nbt, boolean spawnData, CallbackInfo ci){
        FunctionChanger.getBlockEntity = (pos) -> {
            try {
                BlockEntity entity;
                if(spawnData) {
                    BlockPos localPos = toLocalPos(pos);
                    entity = presentBlockEntities.get(localPos);
                } else {
                    StructureTemplate.StructureBlockInfo info = blocks.get(pos);
                    CompoundTag tag = info.nbt;
                    if(tag != null) {
                        tag.putInt("x", info.pos.getX());
                        tag.putInt("y", info.pos.getY());
                        tag.putInt("z", info.pos.getZ());
                        entity = BlockEntity.loadStatic(info.pos,info.state,tag);
                    } else return null;
                }
                if(entity != null){
                    entity.setLevel(world);
                }
                return entity;
            } catch (Exception e) {
                return presentBlockEntities.values().stream().filter(blockEntity -> blockEntity.getBlockPos().equals(pos)).findFirst().orElse(null);
            }
        };
    }
    @ForFunctionChanger(method = "deserialize")
    @Inject(method = "readNBT",at = @At("RETURN"),remap = false)
    public void clearData(Level world, CompoundTag nbt, boolean spawnData, CallbackInfo ci){
        FunctionChanger.getBlockEntity = null;
    }

    @Override
    public @Nullable Object get(String name) {
        if(Objects.equals(name, "manager"))
            return this.getStorageForSpawnPacket();
        return null;
    }
}