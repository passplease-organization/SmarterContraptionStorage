package net.smartercontraptionstorage.AddStorage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawersComp;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class DrawersHandlerHelper extends StorageHandlerHelper {
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof BlockEntityDrawers && !(entity instanceof BlockEntityDrawersComp);
    }
    @Override
    public void addStorageToWorld(BlockEntity entity,ItemStackHandler handler) {
        assert canCreateHandler(entity) && handler instanceof NormalDrawerHandler;
        if(handler instanceof NormalDrawerHandler Handler) {
            IDrawerGroup group = ((BlockEntityDrawers) entity).getGroup();
            for (int i = handler.getSlots() - 1; i >= 0; i--) {
                group.getDrawer(i).setStoredItem(Handler.items[i]);
                group.getDrawer(i).setStoredItemCount(Handler.count[i]);
            }
        }
    }
    @Override
    public ItemStackHandler createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        IDrawerGroup group = ((BlockEntityDrawers) entity).getGroup();
        return new NormalDrawerHandler(group);
    }
    @Override
    public boolean allowControl(Item comparedItem) {
        return false;
    }
    @Override
    public boolean allowControl(Block block){
        return block instanceof BlockDrawers && !(block instanceof BlockCompDrawers);
    }
    protected static class NormalDrawerHandler extends HandlerHelper{
        public final int[] count;
        public NormalDrawerHandler(@NotNull IDrawerGroup group) {
            super(group.getDrawerCount());
            count = new int[group.getDrawerCount()];
            for(int i = slotLimits.length - 1;i >= 0;i--){
                if(group.getDrawer(i).getAcceptingRemainingCapacity() == Integer.MAX_VALUE)
                    slotLimits[i] = Integer.MAX_VALUE;
                else slotLimits[i] = group.getDrawer(i).getMaxCapacity();
                items[i] = group.getDrawer(i).getStoredItemPrototype();
                count[i] = group.getDrawer(i).getStoredItemCount();
                // Empty and locked drawers are not supported (they will be filled with item)
            }
        }
        public boolean canInsert(int slot,ItemStack stack){
            return !stack.isEmpty() && (ItemStack.isSameItem(items[slot],stack) || items[slot].is(Items.AIR));
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
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if(canInsert(slot,stack)){
                // below should change markedItem in time, but I don't know how to do this right now.
                if(items[slot].is(Items.AIR)){
                    if(stack.getCount() <= slotLimits[slot]) {
                        if(!simulate) {
                            count[slot] = stack.getCount();
                            items[slot] = stack.copy();
                        }
                        return ItemStack.EMPTY;
                    }
                    else {
                        if(!simulate) {
                            count[slot] = slotLimits[slot];
                            items[slot] = stack.copy();
                        }
                        stack.setCount(stack.getCount() - count[slot]);
                        return stack;
                    }
                }
                if(simulate){
                    if(stack.getCount() <= slotLimits[slot])
                        return ItemStack.EMPTY;
                    else {
                        stack.setCount(stack.getCount() - slotLimits[slot]);
                        return stack;
                    }
                }
                if(count[slot] + stack.getCount() > slotLimits[slot]) {
                    stack.grow(slotLimits[slot] - count[slot]);
                    count[slot] += slotLimits[slot];
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
    }
}