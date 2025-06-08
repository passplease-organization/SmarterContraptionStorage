package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.IFractionalDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.UpgradeData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.RegistryObject;
import net.smartercontraptionstorage.AddStorage.GUI.NormalMenu.AbstractMovingMenu;
import net.smartercontraptionstorage.AddStorage.GUI.NormalMenu.MovingCompactingDrawerMenu;
import net.smartercontraptionstorage.AddStorage.GUI.NormalMenu.MovingDrawerMenu;
import net.smartercontraptionstorage.SmarterContraptionStorage;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CompactingHandlerHelper extends DrawersHandlerHelper{
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof BlockEntityDrawersComp;
    }
    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {
        super.addStorageToWorld(entity, handler);
        IDrawerGroup group = ((BlockEntityDrawers) entity).getGroup();
        CompactingHandler Handler = (CompactingHandler) handler;
        ItemStack item = Handler.getStackInSlot(Handler.baseSlot);
        group.getDrawer(Handler.baseSlot).setStoredItem(item,item.getCount());
    }
    @Override
    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        BlockEntityDrawersComp drawer = (BlockEntityDrawersComp) entity;
        IDrawerGroup group = drawer.getGroup();
        return new CompactingHandler(group,drawer.upgrades());
    }
    @Override
    public boolean allowControl(Block block) {
        return block instanceof BlockCompDrawers;
    }
    @Override
    public String getName() {
        return "CompactingHandlerHelper";
    }
    @Override
    public @NotNull ItemStackHandler deserialize(CompoundTag nbt) {
        return new CompactingHandler(nbt);
    }
    public static class CompactingHandler extends NormalDrawerHandler{
        public final int[] conversionRate;
        private final int baseSlot;
        public CompactingHandler(IDrawerGroup group, UpgradeData upgrades) {
            super(group,upgrades);
            conversionRate = new int[3];
            IDrawer drawer;
            for (int i = group.getDrawerCount() - 1; i >= 0; i--) {
                drawer = group.getDrawer(i);
                slotLimits[i] = 1;
                if(drawer instanceof IFractionalDrawer d)
                    conversionRate[i] = d.getConversionRate();
            }
            baseSlot = getBaseSlot();
            for (int i = 0; i <= baseSlot; i++) {
                if(conversionRate[i] <= 0)
                    conversionRate[i] = -1;
                else conversionRate[i] = conversionRate[baseSlot] / conversionRate[i];
            }
            count[baseSlot] = group.getDrawer(baseSlot).getStoredItemCount();
            slotLimits[baseSlot] = group.getDrawer(baseSlot).getStoredItemStackSize();
        }
        public CompactingHandler(CompoundTag tag){
            super(tag);
            conversionRate = tag.getIntArray("conversionRate");
            assert conversionRate.length == 3;
            baseSlot = getBaseSlot();
        }
        @Override
        public boolean canInsert(int slot, ItemStack stack) {
            return !stack.isEmpty() && conversionRate[slot] > 0 && Utils.isSameItemSameTags(items[slot],stack);
        }
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if(canInsert(slot, stack)){
                int insertCount = Math.min(stack.getCount() * conversionRate[slot],slotLimits[baseSlot] - count[baseSlot]) / conversionRate[slot];
                if(!simulate)
                    count[baseSlot] += insertCount * conversionRate[slot];
                stack.shrink(insertCount);
                if(stack.getCount() == 0)
                    return ItemStack.EMPTY;
            }
            return stack;
        }
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(conversionRate[slot] > 0){
                int realCount = Math.min(amount * conversionRate[slot],count[baseSlot]) / conversionRate[slot];
                if(realCount > 0) {
                    ItemStack itemStack = items[slot].copy();
                    itemStack.setCount(realCount);
                    if (!simulate)
                        count[baseSlot] -= realCount * conversionRate[slot];
                    return itemStack;
                }
            }
            return ItemStack.EMPTY;
        }
        public int getBaseSlot(){
            for(int i = 0; i < 3; i++)
                if(conversionRate[i] <= 0)
                    return Math.max(i - 1,0);
            return 2;
        }
        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            ItemStack back = items[slot].copy();
            if(conversionRate[slot] <= 0)
                conversionRate[slot] = -1;
            back.setCount(Math.max(count[baseSlot] / conversionRate[slot],0));
            return back;
        }
        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            assert conversionRate.length == 3;
            tag.putIntArray("conversionRate", conversionRate);
            return tag;
        }

        @Override
        public String getTranslationKey() {
            return "compacting_drawer";
        }

        public static RegistryObject<MenuType<MovingDrawerMenu>> CompactingDrawerMenu;

        @Override
        public MenuType<? extends AbstractMovingMenu<?>> getMenuType() {
            Objects.requireNonNull(CompactingDrawerMenu);
            return CompactingDrawerMenu.get();
        }

        @Override
        public @NotNull Component getDisplayName() {
            return Component.translatable(SmarterContraptionStorage.MODID + ".moving_container." + getTranslationKey());
        }

        @Override
        public @NotNull AbstractMovingMenu<?> createMenu(int i, Inventory inventory, Player player) {
            return new MovingCompactingDrawerMenu(this,i,player);
        }
    }
}