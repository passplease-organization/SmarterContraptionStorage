package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.smartercontraptionstorage.AddStorage.GUI.NormalMenu.MovingMenuProvider;
import net.smartercontraptionstorage.AddStorage.SerializableHandler;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class StorageHandlerHelper implements SerializableHandler<ItemStackHandler>{
    public static final String DESERIALIZE_MARKER = "OtherHandlers";
    public static final ItemStackHandler NULL_HANDLER = new ItemStackHandler(){
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return false;
        }
        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }
    };
    private static final Set<StorageHandlerHelper> HandlerHelpers = new HashSet<>();
    protected static final ArrayList<BlockEntity> BlockEntityList = new ArrayList<>();
    public static void clearData(){
        BlockEntityList.clear();
    }
    public static void register(@NotNull StorageHandlerHelper helper){
        if(HandlerHelpers.stream().noneMatch(h -> h.getClass() == helper.getClass()))
            HandlerHelpers.add(helper);
    }
    public static boolean canControl(@Nullable Item comparedItem){
        return canControl(comparedItem,Block.byItem(comparedItem));
    }
    public static boolean canControl(@NotNull Block comparedBlock){
        return canControl(comparedBlock.asItem(),comparedBlock);
    }
    public static boolean canControl(@Nullable Item comparedItem,@Nullable Block comparedBlock){
        if(comparedItem != null) {
            for (StorageHandlerHelper handlerHelper : HandlerHelpers) {
                if (handlerHelper.allowControl(comparedItem))
                    return true;
            }
        }
        if(comparedBlock == Blocks.AIR || comparedBlock == null)
            return false;
        for (StorageHandlerHelper handlerHelper : HandlerHelpers){
            if( handlerHelper.allowControl(comparedBlock))
                return true;
        }
        return false;
    }
    public static boolean canUseModdedInventory(BlockEntity entity){
        for(StorageHandlerHelper handlerHelper : HandlerHelpers){
            if(handlerHelper.canCreateHandler(entity))
                return true;
        }
        return false;
    }
    public static @Nullable StorageHandlerHelper findSuitableHelper(BlockEntity entity){
        for(StorageHandlerHelper handlerHelper : HandlerHelpers)
            if(handlerHelper.canCreateHandler(entity))
                return handlerHelper;
        return null;
    }
    public static @Nonnull StorageHandlerHelper findByName(String name){
        StorageHandlerHelper h = HandlerHelpers.stream().filter((helper) -> Objects.equals(helper.getName(), name)).findFirst().orElse(null);
        if(h == null)
            Utils.addWarning("Invalid storage handler name: " + name);
        return Objects.requireNonNull(h);
    }
    public abstract boolean canCreateHandler(BlockEntity entity);
    public abstract void addStorageToWorld(BlockEntity entity,ItemStackHandler handler);
    public abstract @NotNull ItemStackHandler createHandler(BlockEntity entity);
    public abstract boolean allowControl(Item comparedItem);
    public abstract boolean allowControl(Block block);
    // two allowDumping only need to achieve one, another can return false
    public static Set<StorageHandlerHelper> getHandlerHelpers() {
        return HandlerHelpers;
    }

    public abstract static class HandlerHelper extends ItemStackHandler implements MovingMenuProvider {
        public final int[] slotLimits;
        protected final ItemStack[] items;
        public HandlerHelper(int size) {
            super(size);
            slotLimits = new int[size];
            items = new ItemStack[size];
        }
        protected HandlerHelper(CompoundTag nbt){
            super(nbt.getInt("size"));
            List<ItemStack> list_items = NBTHelper.readItemList(nbt.getList("items", Tag.TAG_COMPOUND));
            int size = list_items.size();
            items = new ItemStack[size];
            ListTag list_slotLimits = nbt.getList("slotLimits", Tag.TAG_INT);
            if(!list_slotLimits.isEmpty()) {
                slotLimits = new int[size];
                for (int slot = 0; slot < size; slot++) {
                    slotLimits[slot] = list_slotLimits.getInt(slot);
                    items[slot] = list_items.get(slot);
                }
            }else {
                slotLimits = nbt.getIntArray("slotLimits");
                for (int slot = 0; slot < size; slot++) {
                    items[slot] = list_items.get(slot);
                }
            }
        }
        @Override
        public int getSlots() {
            return items.length;
        }
        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return items[slot];
        }
        @Override
        public int getStackLimit(int slot, @NotNull ItemStack stack){
            if(Utils.isSameItem(items[slot],stack))
                return slotLimits[slot];
            else return 0;
        }
        @Override
        public int getSlotLimit(int slot) {
            return slotLimits[slot];
        }
        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            if(slot >= 0 && slot < items.length)
                items[slot] = stack.copy();
        }
        @Override
        public abstract @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate);
        @Override
        public abstract @NotNull ItemStack extractItem(int slot, int amount, boolean simulate);
        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            ListTag list = new ListTag(),itemList = new ListTag();
            for (int slot = 0; slot < slotLimits.length; slot++) {
                list.add(IntTag.valueOf(slotLimits[slot]));
                itemList.add(items[slot].serializeNBT());
            }
            tag.put("slotLimits",list);
            tag.put("items",itemList);
            tag.putInt("size",slotLimits.length);
            return tag;
        }

        public abstract String getName();

        @Override
        public void writeToBuffer(@NotNull FriendlyByteBuf buffer) {
            buffer.writeNbt(serializeNBT());
        }

        protected boolean isItemEmpty(int slot){
            return Utils.isItemEmpty(getStackInSlot(slot));
        }

        private AbstractContraptionEntity contraption;

        private BlockPos localPos;

        @Override
        public void setContraption(AbstractContraptionEntity contraption) {
            this.contraption = contraption;
        }

        @Override
        public AbstractContraptionEntity getContraption() {
            return contraption;
        }

        @Override
        public void setLocalPos(BlockPos localPos) {
            this.localPos = localPos;
        }

        @Override
        public BlockPos getLocalPos() {
            return localPos;
        }
    }
}