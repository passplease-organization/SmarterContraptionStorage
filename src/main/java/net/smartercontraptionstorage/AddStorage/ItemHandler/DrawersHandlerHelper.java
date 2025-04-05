package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.UpgradeData;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.smartercontraptionstorage.AddStorage.GUI.NormalMenu.AbstractMovingMenu;
import net.smartercontraptionstorage.AddStorage.GUI.NormalMenu.MovingDrawerMenu;
import net.smartercontraptionstorage.SmarterContraptionStorage;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DrawersHandlerHelper extends StorageHandlerHelper {
    public static final String NAME = "DrawersHandlerHelper";
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof BlockEntityDrawersStandard;
    }
    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {
        assert canCreateHandler(entity) && handler instanceof NormalDrawerHandler;
        BlockEntityDrawers drawer = (BlockEntityDrawers) entity;
        IDrawerGroup group = drawer.getGroup();
        NormalDrawerHandler Handler = (NormalDrawerHandler) handler;
        ItemStack stack;
        for (int i = handler.getSlots() - 1; i >= 0; i--) {
            stack = Handler.getStackInSlot(i);
            group.getDrawer(i).setStoredItemCount(stack.getCount());
            stack.setCount(1);
            group.getDrawer(i).setStoredItem(stack);
        }
        UpgradeData upgrades = drawer.upgrades();
        for (int slot = 0; slot < upgrades.getSlotCount(); slot++) {
            upgrades.setUpgrade(slot,Handler.upgrades[slot]);
        }
    }
    @Override
    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        BlockEntityDrawers drawers = (BlockEntityDrawers) entity;
        IDrawerGroup group = drawers.getGroup();
        return new NormalDrawerHandler(group,drawers.upgrades());
    }
    @Override
    public boolean allowControl(Item comparedItem) {
        return false;
    }
    @Override
    public boolean allowControl(Block block){
        return block instanceof BlockDrawers && !(block instanceof BlockCompDrawers);
    }
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public ItemStackHandler deserialize(CompoundTag nbt, HolderLookup.Provider provider) throws IllegalAccessException {
        return new NormalDrawerHandler(nbt,provider);
    }

    public static class NormalDrawerHandler extends HandlerHelper{
        public final int[] count;
        protected final ItemStack[] upgrades;
        public NormalDrawerHandler(@NotNull IDrawerGroup group, UpgradeData upgrades) {
            super(group.getDrawerCount());
            count = new int[group.getDrawerCount()];
            this.upgrades = new ItemStack[upgrades.getSlotCount()];
            for(int slot = slotLimits.length - 1;slot >= 0;slot--){
                if(group.getDrawer(slot).getAcceptingRemainingCapacity() == Integer.MAX_VALUE)
                    slotLimits[slot] = Integer.MAX_VALUE;
                else slotLimits[slot] = group.getDrawer(slot).getMaxCapacity();
                items[slot] = group.getDrawer(slot).getStoredItemPrototype();
                count[slot] = group.getDrawer(slot).getStoredItemCount();
                // Empty and locked drawers are not supported (they will be filled with item)
            }
            for (int slot = 0; slot < this.upgrades.length; slot++) {
                this.upgrades[slot] = upgrades.getUpgrade(slot);
            }
        }
        public NormalDrawerHandler(CompoundTag nbt, HolderLookup.Provider provider) {
            super(nbt,provider);
            this.upgrades = new ItemStack[nbt.getInt("upgradesSlot")];
            ListTag list = nbt.getList("count", Tag.TAG_INT);
            if(!list.isEmpty()) {
                // old version use this method,some method will return this format
                count = new int[items.length];
                for (int slot = 0; slot < count.length; slot++) {
                    count[slot] = ((IntTag) list.get(slot)).getAsInt();
                }
            }else {
                count = nbt.getIntArray("count");
            }
            List<ItemStack> upgradesList = NBTHelper.readItemList(nbt.getList("upgrades", Tag.TAG_COMPOUND),provider);
            for (int slot = 0; slot < upgrades.length; slot++) {
                this.upgrades[slot] = upgradesList.get(slot);
            }
        }
        public boolean canInsert(int slot,ItemStack stack){
            return !stack.isEmpty() && (Utils.isSameItem(items[slot],stack) || isItemEmpty(slot));
        }
        @Override
        public int getStackLimit(int slot, @NotNull ItemStack stack) {
            if(canInsert(slot,stack))
                return slotLimits[slot];
            else return 0;
        }
        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            ItemStack back = items[slot].copy();
            back.setCount(count[slot]);
            return back;
        }
        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            if(canInsert(slot,stack)) {
                super.setStackInSlot(slot, stack);
                if (slot >= 0 && slot < items.length)
                    count[slot] = stack.getCount();
            }
        }
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if(canInsert(slot,stack)){
                // below should change markedItem in time, but I don't know how to do this right now.
                if(isItemEmpty(slot)){
                    if(stack.getCount() <= slotLimits[slot]) {
                        if(!simulate) {
                            count[slot] = stack.getCount();
                            items[slot] = stack.copy();
                        }
                        return ItemStack.EMPTY;
                    } else {
                        if(!simulate) {
                            count[slot] = slotLimits[slot];
                            items[slot] = stack.copy();
                        }
                        stack.shrink(count[slot]);
                        return stack;
                    }
                }
                if(simulate){
                    if(stack.getCount() + count[slot] <= slotLimits[slot])
                        return ItemStack.EMPTY;
                    else {
                        stack.setCount(stack.getCount() - slotLimits[slot]);
                        return stack;
                    }
                }else if(count[slot] + stack.getCount() > slotLimits[slot]) {
                    stack.shrink(slotLimits[slot] - count[slot]);
                    count[slot] = slotLimits[slot];
                    return stack;
                }else {
                    count[slot] += stack.getCount();
                    return ItemStack.EMPTY;
                }
            }else return stack;
        }
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(amount == 0)
                return ItemStack.EMPTY;
            validateSlotIndex(slot);
            ItemStack toExtract = items[slot].copy();
            if(simulate){
                toExtract.setCount(Math.min(amount, count[slot]));
            }else {
                if(amount > count[slot]){
                    toExtract.setCount(count[slot]);
                    count[slot] = 0;
                }else {
                    toExtract.setCount(amount);
                    count[slot] -= amount;
                }
            }
            return toExtract;
        }
        public ItemStack getUpgrades(int slot) {
            return upgrades[slot];
        }

        @Override
        public @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            ListTag list = new ListTag();
            for (int i : count)
                list.add(IntTag.valueOf(i));
            tag.put("count",list);
            tag.put("upgrades",NBTHelper.writeItemList(Arrays.stream(upgrades).toList(),provider));
            tag.putInt("upgradesSlot",upgrades.length);
            return tag;
        }
        @Override
        public String getName() {
            return NAME;
        }

        public static DeferredHolder<MenuType<?>, MenuType<MovingDrawerMenu>> DrawerMenu;

        @Override
        public MenuType<? extends AbstractMovingMenu<?>> getMenuType() {
            Objects.requireNonNull(DrawerMenu);
            return DrawerMenu.get();
        }

        @Override
        public @NotNull AbstractMovingMenu<?> createMenu(int i, Inventory inventory, Player player) {
            return new MovingDrawerMenu(this,i,player);
        }

        @Override
        public String getTranslationKey() {
            return "drawer";
        }

        @Override
        public @NotNull Component getDisplayName() {
            return Component.translatable(SmarterContraptionStorage.MODID + ".moving_container." + getTranslationKey(),items.length);
        }
    }
}