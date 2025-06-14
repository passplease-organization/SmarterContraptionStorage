package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.buuz135.functionalstorage.block.CompactingDrawerBlock;
import com.buuz135.functionalstorage.block.SimpleCompactingDrawerBlock;
import com.buuz135.functionalstorage.block.tile.CompactingDrawerTile;
import com.buuz135.functionalstorage.block.tile.SimpleCompactingDrawerTile;
import com.buuz135.functionalstorage.inventory.CompactingInventoryHandler;
import com.buuz135.functionalstorage.util.CompactingUtil;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import net.smartercontraptionstorage.AddStorage.GUI.NormalMenu.MovingFunctionalCompactingMenu;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.buuz135.functionalstorage.inventory.CompactingInventoryHandler.*;

public class FunctionalCompactingHandlerHelper extends StorageHandlerHelper{
    public static final String NAME = "FunctionalCompactingHandlerHelper";
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof CompactingDrawerTile || entity instanceof SimpleCompactingDrawerTile;
    }

    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {
        assert canCreateHandler(entity) && handler instanceof FCDrawersHandler;
        FCDrawersHandler h = (FCDrawersHandler) handler;
        CompoundTag tag;
        CompoundTag nbt = new CompoundTag();
        CompoundTag compoundTag = new CompoundTag();
        nbt.putInt(AMOUNT,h.amount);
        nbt.put(PARENT,h.items[h.PARENT_SLOT].serializeNBT());
        for (int slot = 0; slot < h.getSlots(); slot++) {
            tag = new CompoundTag();
            tag.putInt(AMOUNT,h.isItemEmpty(slot) ? 0 : 1);
            tag.put(STACK,h.items[slot].serializeNBT());
            compoundTag.put(Integer.toString(slot),tag);
        }
        nbt.put(BIG_ITEMS,compoundTag);
        if(entity instanceof CompactingDrawerTile)
            ((CompactingDrawerTile)entity).getHandler().deserializeNBT(nbt);
        else ((SimpleCompactingDrawerTile)entity).getHandler().deserializeNBT(nbt);
    }

    @Override
    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        if(entity instanceof CompactingDrawerTile drawer) {
            return new FCDrawersHandler(drawer.getHandler(),drawer.getUtilityUpgrades(),drawer.getStorageUpgrades());
        } else {
            SimpleCompactingDrawerTile drawer = (SimpleCompactingDrawerTile) entity;
            return new FCDrawersHandler(drawer.getHandler(),drawer.getUtilityUpgrades(),drawer.getStorageUpgrades());
        }
    }

    @Override
    public boolean allowControl(Item comparedItem) {
        try{
            return comparedItem instanceof CompactingDrawerBlock.CompactingDrawerItem;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean allowControl(Block block) {
        return block instanceof CompactingDrawerBlock || block instanceof SimpleCompactingDrawerBlock;
    }

    @Override
    public void registerBlock(Consumer<Block> register) {}

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public @NotNull ItemStackHandler deserialize(CompoundTag nbt) {
        return new FCDrawersHandler(nbt);
    }

    public static class FCDrawersHandler extends HandlerHelper {
        public final int PARENT_SLOT;
        public final int[] needed;
        public final List<ItemStack> upgrades;
        public final boolean isVoid;
        public final boolean isCreative;
        public int amount;
        public FCDrawersHandler(CompactingInventoryHandler handler, ItemStackHandler utility_upgrades, ItemStackHandler storage_upgrades) {
            super(handler.isVoid() ? handler.getSlots() - 1 : handler.getSlots());
            isVoid = handler.isVoid();
            int slots = handler.getSlots();
            needed = new int[slots];
            amount = handler.getAmount();
            this.isCreative = handler.isCreative();
            int parent_slot = -1;
            List<CompactingUtil.Result> list = handler.getResultList();
            for(int i = 0;i < slots;i++){
                slotLimits[i] = handler.getSlotLimit(i);
                items[i] = list.get(i).getResult();
                needed[i] = list.get(i).getNeeded();
                if(parent_slot == -1 && !items[i].is(Items.AIR))
                    parent_slot = i;
            }
            if(parent_slot != -1)
                PARENT_SLOT = parent_slot;
            else PARENT_SLOT = handler.getSlots() - 1;
            upgrades = new ArrayList<>();
            for (int slot = 0; slot < storage_upgrades.getSlots(); slot++) {
                upgrades.add(storage_upgrades.getStackInSlot(slot));
            }
            for (int slot = 0; slot < utility_upgrades.getSlots(); slot++) {
                upgrades.add(utility_upgrades.getStackInSlot(slot));
            }
            assert upgrades.size() == 6;
        }
        public FCDrawersHandler(CompoundTag nbt){
            super(nbt);
            PARENT_SLOT = nbt.getInt(PARENT);
            isCreative = nbt.getBoolean("isCreative");
            amount = nbt.getInt(AMOUNT);
            ListTag list = nbt.getList("needed", Tag.TAG_INT);
            needed = new int[list.size()];
            isVoid = nbt.getBoolean("isVoid");
            for (int slot = 0; slot < needed.length; slot++)
                needed[slot] = ((IntTag)list.get(slot)).getAsInt();
            upgrades = NBTHelper.readItemList(nbt.getList("upgrades", Tag.TAG_COMPOUND));
            assert upgrades.size() == 6;
        }
        public boolean canInsert(int slot, @NotNull ItemStack stack) {
            if(stack.isEmpty())
                return false;
            return Utils.isSameItemSameTags(items[slot],stack);
        }
        public void addCountInSlot(int slot, int count){
            amount += count * needed[slot];
        }
        public int getCountInSlot(int slot){
            return amount / needed[slot];
        }
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if(canInsert(slot,stack)){
                items[slot] = stack;
                if(isCreative || isVoid)
                    return ItemStack.EMPTY;
                int slotCount = getCountInSlot(slot);
                int sumCount = slotCount + stack.getCount();
                if(sumCount >= slotLimits[slot]){
                    if(!simulate)
                        addCountInSlot(slot, slotLimits[slot] - slotCount);
                    stack.setCount(slotLimits[slot] - sumCount);
                    return stack;
                }else {
                    if(!simulate)
                        addCountInSlot(slot, sumCount - slotCount);
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
                int count = getCountInSlot(slot);
                if(!simulate){
                    if(amount <= count){
                        addCountInSlot(slot,-amount);
                        toExtract.setCount(amount);
                    }else {
                        toExtract.setCount(count);
                        addCountInSlot(slot,-count);
                    }
                }else {
                    toExtract.setCount(Math.min(amount, count));
                }
            }
            return toExtract;
        }
        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            ItemStack stack = items[slot].copy();
            stack.setCount(getCountInSlot(slot));
            return stack;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            tag.putInt(PARENT,PARENT_SLOT);
            tag.putInt(AMOUNT,amount);
            tag.putBoolean("isVoid",isVoid);
            ListTag list = new ListTag();
            for(int i : needed)
                list.add(IntTag.valueOf(i));
            tag.put("needed",list);
            tag.put("upgrades", NBTHelper.writeItemList(upgrades));
            tag.putBoolean("isCreative",isCreative);
            return tag;
        }
        @Override
        public String getName() {
            return NAME;
        }

        public static RegistryObject<MenuType<MovingFunctionalCompactingMenu>> MENU_TYPE;

        @Override
        public MenuType<? extends AbstractMovingMenu<?>> getMenuType() {
            Objects.requireNonNull(MENU_TYPE);
            return MENU_TYPE.get();
        }

        @Override
        public @NotNull AbstractMovingMenu<?> createMenu(int id, Inventory inventory, Player player) {
            return new MovingFunctionalCompactingMenu(this,id,player);
        }

        @Override
        public String getTranslationKey() {
            return "compacting_drawer";
        }
    }
}