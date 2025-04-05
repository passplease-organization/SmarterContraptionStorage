package net.smartercontraptionstorage.AddStorage.ItemHandler;

import appeng.api.implementations.items.ISpatialStorageCell;
import appeng.api.networking.IGrid;
import appeng.api.networking.spatial.ISpatialService;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.blockentity.networking.CreativeEnergyCellBlockEntity;
import appeng.blockentity.networking.EnergyCellBlockEntity;
import appeng.blockentity.spatial.SpatialIOPortBlockEntity;
import appeng.spatial.SpatialStoragePlot;
import appeng.spatial.SpatialStoragePlotManager;
import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import net.smartercontraptionstorage.SmarterContraptionStorageConfig;
import net.smartercontraptionstorage.Utils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SpatialHandler extends StorageHandlerHelper{
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof SpatialIOPortBlockEntity;
    }

    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {
        assert canCreateHandler(entity);
        ((SpatialHelper)handler).unloadChunks();
    }

    @Override
    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        SpatialIOPortBlockEntity io = (SpatialIOPortBlockEntity) entity;
        ItemStack stack = io.getInternalInventory().getStackInSlot(1);
        if(stack.getItem() instanceof ISpatialStorageCell cell) {
            IGrid grid = io.getMainNode().getGrid();
            if(grid != null){
                ISpatialService service = grid.getSpatialService();
                if(service.isValidRegion())
                    return SpatialHelper.create(cell.getAllocatedPlotId(stack));
            }
        }
        return NULL_HANDLER;
    }

    @Override
    public boolean allowControl(Item comparedItem) {
        return false;
    }

    @Override
    public boolean allowControl(Block block) {
        return false;
    }

    @Override
    public String getName() {
        return "SpatialHandler";
    }

    @Deprecated
    @Override
    public @NotNull ItemStackHandler deserialize(CompoundTag nbt){
        try {
            SpatialHelper handler = SpatialHelper.create(nbt.getInt("plotId"));
            handler.canWork = nbt.getBoolean("canWork");
            return handler;
        }catch (RuntimeException e){
            Utils.addError(e.getMessage());
            return NULL_HANDLER;
        }
    }

    @Override
    public ItemStackHandler deserialize(CompoundTag nbt, HolderLookup.Provider provider){
        try {
            SpatialHelper handler = SpatialHelper.create(nbt.getInt("plotId"));
            handler.canWork = nbt.getBoolean("canWork");
            return handler;
        }catch (RuntimeException e){
            Utils.addError(e.getMessage());
            return NULL_HANDLER;
        }
    }

    public static class SpatialHelper extends ItemStackHandler implements NeedDealWith {
        public final ArrayList<IItemHandler> insertHandlers;
        public final ArrayList<IItemHandler> exportHandlers;
        public final int edge;
        public final int plotId;
        private boolean canWork = false;
        private SpatialHelper(int size, int edge, @NotNull ArrayList<IItemHandler> insertHandlers, @NotNull ArrayList<IItemHandler> exportHandlers, int plotId) {
            super(size);
            this.insertHandlers = insertHandlers;
            this.exportHandlers = exportHandlers;
            this.edge = edge;
            this.plotId = plotId;
        }

        public static @NotNull SpatialStoragePlot findPlot(int plotId){
            SpatialStoragePlot plot = SpatialStoragePlotManager.INSTANCE.getPlot(plotId);
            if(plot == null)
                throw new RuntimeException("Id isn't exist");
            return plot;
        }

        public static @NotNull SpatialHelper create(int plotId){
            int size = 0,edge = 0;
            SpatialStoragePlot plot = findPlot(plotId);
            BlockPos startPos = plot.getOrigin();
            BlockPos endPos = plot.getSize();
            int min_x = startPos.getX(),
                    min_y = startPos.getY(),
                    min_z = startPos.getZ(),
                    max_x = min_x + endPos.getX(),
                    max_y = min_y + endPos.getY(),
                    max_z = min_z + endPos.getZ();
            BlockPos pos;
            BlockEntity entity;
            IItemHandler handler;
            ArrayList<IItemHandler> insertHandlers = new ArrayList<>();
            ArrayList<IItemHandler> exportHandlers = new ArrayList<>();
            ServerLevel level = SpatialStoragePlotManager.INSTANCE.getLevel();
            for (int x = min_x; x <= max_x; x++)
                for (int y = min_y; y <= max_y; y++)
                    for (int z = min_z; z <= max_z; z++) {
                        pos = new BlockPos(x,y,z);
                        entity = level.getBlockEntity(pos);
                        if(entity != null && Utils.canUseCreateInventory(entity.getBlockState().getBlock())) {
                            handler = level.getCapability(Capabilities.ItemHandler.BLOCK,pos,entity.getBlockState(),entity,null);
                            if (entity instanceof ItemVaultBlockEntity)
                                insertHandlers.add(handler);
                            else {
                                exportHandlers.add(handler);
                                edge += handler.getSlots() - 1;
                            }
                            size += handler.getSlots();
                        }
                    }
            loadChunks(level,min_x,min_z,max_x,max_z);
            return new SpatialHelper(size,edge,insertHandlers,exportHandlers,plotId);
        }

        public static ChunkPos createChunkPos(int x,int z){
            return new ChunkPos(SectionPos.blockToSectionCoord(x),SectionPos.blockToSectionCoord(z));
        }

        private static void loadChunks(ServerLevel level, int minX, int minZ, int maxX, int maxZ) {
            if(SmarterContraptionStorageConfig.loadChunkAuto()) {
                ChunkPos pos;
                for (int x = minX; x < maxX; x += 16)
                    for (int z = minZ; z < maxZ; z += 16) {
                        pos = createChunkPos(x,z);
                        level.setChunkForced(pos.x, pos.z, true);
                    }
            }
        }

        public void unloadChunks(){
            SpatialStoragePlot plot = findPlot(plotId);
            BlockPos startPos = plot.getOrigin();
            BlockPos endPos = plot.getSize();
            int min_x = startPos.getX(),
                    min_z = startPos.getZ(),
                    max_x = min_x + endPos.getX(),
                    max_z = min_z + endPos.getZ();
            ChunkPos pos;
            ServerLevel level = SpatialStoragePlotManager.INSTANCE.getLevel();
            for (int x = min_x; x < max_x; x += 16)
                for (int z = min_z; z < max_z; z += 16) {
                    pos = createChunkPos(x,z);
                    level.setChunkForced(pos.x, pos.z, false);
                }
        }

        public @NotNull Pair<IItemHandler,Integer> searchHandler(int slot){
            if(canWork) {
                if (slot <= edge) {
                    for (IItemHandler handler : exportHandlers) {
                        if (slot >= handler.getSlots())
                            slot -= handler.getSlots();
                        else return Pair.of(handler, slot);
                    }
                } else {
                    slot -= edge + 1;
                    for (IItemHandler handler : insertHandlers) {
                        if (slot >= handler.getSlots())
                            slot -= handler.getSlots();
                        else return Pair.of(handler, slot);
                    }
                }
                Utils.addWarning("Slot is too big !");
            }
            return Pair.of(NULL_HANDLER,0);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if(slot <= edge)
                return stack;
            var pair = searchHandler(slot);
            return pair.getKey().insertItem(pair.getRight(),stack,simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(slot > edge)
                return ItemStack.EMPTY;
            var pair = searchHandler(slot);
            return pair.getKey().extractItem(pair.getRight(),amount,simulate);
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            var pair = searchHandler(slot);
            return pair.getKey().getStackInSlot(pair.getRight());
        }

        @Override
        public int getSlotLimit(int slot) {
            var pair = searchHandler(slot);
            return pair.getKey().getSlotLimit(pair.getRight());
        }

        @Override
        public void doSomething(BlockEntity entity) {
            BlockEntityList.add(entity);
        }

        @Override
        public void finallyDo() {
            for(BlockEntity entity : BlockEntityList){
                if (entity instanceof CableBusBlockEntity){
                    canWork = false;
                    unloadChunks();
                    return;
                }else if(entity instanceof EnergyCellBlockEntity || entity instanceof CreativeEnergyCellBlockEntity)
                    canWork = true;
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putBoolean("canWork",canWork);
            tag.putInt("plotId",plotId);
            return tag;
        }

        public boolean canWork() {
            return canWork;
        }
    }
}