package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.buuz135.functionalstorage.block.DrawerBlock;
import com.buuz135.functionalstorage.block.tile.DrawerTile;
import com.buuz135.functionalstorage.inventory.BigInventoryHandler;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.RegistryObject;
import net.smartercontraptionstorage.AddStorage.GUI.NormalMenu.AbstractMovingMenu;
import net.smartercontraptionstorage.AddStorage.GUI.NormalMenu.MovingFunctionalDrawerMenu;
import net.smartercontraptionstorage.SmarterContraptionStorage;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.buuz135.functionalstorage.inventory.BigInventoryHandler.*;

public class FunctionalDrawersHandlerHelper extends StorageHandlerHelper{
    public static final String NAME = "FunctionalDrawersHandlerHelper";
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof DrawerTile;
    }

    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {
        assert canCreateHandler(entity) && handler instanceof FDrawersHandler;
        CompoundTag nbt = new CompoundTag();
        CompoundTag tag;
        FDrawersHandler h = (FDrawersHandler)handler;
        for (int slot = 0; slot < h.getSlots(); slot++) {
            tag = new CompoundTag();
            tag.putInt(AMOUNT,h.count[slot]);
            tag.put(STACK,h.items[slot].serializeNBT());
            nbt.put(Integer.toString(slot),tag);
        }
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put(BIG_ITEMS,nbt);
        ((DrawerTile)entity).getHandler().deserializeNBT(compoundTag);
    }

    @Override
    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        DrawerTile drawer = (DrawerTile) entity;
        return new FDrawersHandler(drawer.getHandler(),drawer.getUtilityUpgrades(),drawer.getStorageUpgrades());
    }

    @Override
    public boolean allowControl(Item comparedItem) {
        try{
            return comparedItem instanceof DrawerBlock.DrawerItem;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean allowControl(Block block) {
        return block instanceof DrawerBlock;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public @NotNull ItemStackHandler deserialize(CompoundTag nbt) {
        return new FDrawersHandler(nbt);
    }

    public static class FDrawersHandler extends HandlerHelper{
        public final int[] count;
        public final List<ItemStack> upgrades;
        public final boolean isCreative;
        public final boolean isVoid;
        public FDrawersHandler(BigInventoryHandler handler, ItemStackHandler utility_upgrades, ItemStackHandler storage_upgrades){
            super(handler.isVoid() ? handler.getSlots() - 1 : handler.getSlots());
            isVoid = handler.isVoid();
            count = new int[items.length];
            this.isCreative = handler.isCreative();
            for (int i = slotLimits.length - 1; i >= 0; i--) {
                slotLimits[i] = handler.getSlotLimit(i);
                items[i] = handler.getStackInSlot(i);
                count[i] = items[i].getCount();
                items[i].setCount(1);
            }
            upgrades = new ArrayList<>();
            for (int slot = 0; slot < storage_upgrades.getSlots(); slot++) {
                upgrades.add(storage_upgrades.getStackInSlot(slot));
            }
            for (int slot = 0; slot < utility_upgrades.getSlots(); slot++) {
                upgrades.add(utility_upgrades.getStackInSlot(slot));
            }
            assert upgrades.size() == 7;
        }
        public FDrawersHandler(CompoundTag nbt) {
            super(nbt);
            isCreative = nbt.getBoolean("isCreative");
            isVoid = nbt.getBoolean("isVoid");
            ListTag list = nbt.getList("count", Tag.TAG_INT);
            if(!list.isEmpty()) {
                count = new int[list.size()];
                for (int slot = 0; slot < count.length; slot++)
                    count[slot] = ((IntTag) list.get(slot)).getAsInt();
            }else {
                count = nbt.getIntArray("count");
            }
            upgrades = NBTHelper.readItemList(nbt.getList("upgrades", Tag.TAG_COMPOUND));
            assert upgrades.size() == 7;
        }
        public boolean canInsert(int slot,@NotNull ItemStack stack){
            return !stack.isEmpty() && (Utils.isSameItemSameTags(items[slot],stack) || items[slot].is(Items.AIR));
        }
        public void setCountInSlot(int slot, @NotNull ItemStack stack, int count){
            if(isItemEmpty(slot))
                items[slot] = stack.copy();
            this.count[slot] = count;
        }
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if(canInsert(slot,stack)){
                if(isCreative || isVoid)
                    return ItemStack.EMPTY;
                int sumCount = count[slot] + stack.getCount();
                if(sumCount >= slotLimits[slot]){
                    if(!simulate)
                        setCountInSlot(slot,stack,slotLimits[slot]);
                    stack.setCount(slotLimits[slot] - sumCount);
                    return stack;
                }else {
                    if(!simulate)
                        setCountInSlot(slot,stack,sumCount);
                    return ItemStack.EMPTY;
                }
            }else return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack toExtract = items[slot].copy();
            if(isCreative)
                toExtract.setCount(amount);
            else {
                if(!simulate){
                    if(amount <= count[slot]){
                        count[slot] -= amount;
                        toExtract.setCount(amount);
                    }else {
                        toExtract.setCount(count[slot]);
                        count[slot] = 0;
                    }
                }else {
                    toExtract.setCount(Math.min(amount, count[slot]));
                }
            }
            return toExtract;
        }
        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            ItemStack stack = items[slot].copy();
            stack.setCount(count[slot]);
            return stack;
        }
        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            tag.putBoolean("isCreative",isCreative);
            tag.putBoolean("isVoid",isVoid);
            ListTag list = new ListTag();
            for(int i : count)
                list.add(IntTag.valueOf(i));
            tag.put("count",list);
            tag.put("upgrades", NBTHelper.writeItemList(upgrades));
            return tag;
        }
        @Override
        public String getName() {
            return NAME;
        }

        public static RegistryObject<MenuType<MovingFunctionalDrawerMenu>> MENU_TYPE;

        @Override
        public MenuType<? extends AbstractMovingMenu<?>> getMenuType() {
            Objects.requireNonNull(MENU_TYPE);
            return MENU_TYPE.get();
        }

        @Override
        public @NotNull AbstractMovingMenu<?> createMenu(int id, Inventory inventory, Player player) {
            return new MovingFunctionalDrawerMenu(this,id,player);
        }

        @Override
        public String getTranslationKey() {
            return "drawer";
        }

        @Override
        public @NotNull Component getDisplayName() {
            return Component.translatable(SmarterContraptionStorage.MODID + ".moving_container." + getTranslationKey(),items.length).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY));
        }
    }
}