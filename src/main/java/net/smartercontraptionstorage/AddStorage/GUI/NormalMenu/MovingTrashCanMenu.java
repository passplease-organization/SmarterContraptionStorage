package net.smartercontraptionstorage.AddStorage.GUI.NormalMenu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.smartercontraptionstorage.AddStorage.GUI.UnchangeableSlot;
import net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper;
import net.smartercontraptionstorage.AddStorage.ItemHandler.TrashHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MovingTrashCanMenu extends AbstractMovingMenu<TrashHandlerHelper.TrashHandler> {
    public static final int height = 180;

    public static final int width = 202;

    public static final int ToolboxSlot = 8;

    public MovingTrashCanMenu(@NotNull TrashHandlerHelper.TrashHandler handler, int pContainerId, @NotNull Player player) {
        super(handler, pContainerId, player);
    }

    public MovingTrashCanMenu(int id, Inventory inventory, FriendlyByteBuf buf){
        super(id,inventory,buf,(buffer,provider) -> new TrashHandlerHelper.TrashHandler(buffer.readNbt(),provider));
    }

    public void changeWhiteOrBlack(boolean whiteOrBlack) {
        getHandler().whiteOrBlack = whiteOrBlack;
    }

    public boolean whiteOrBlack() {
        return getHandler().whiteOrBlack;
    }

    @Override
    public void addPlayerSlots(@NotNull Player player) {
        addPlayerSlots(player.getInventory(),21,height - 82);
    }

    @Override
    public void addSlots() {
        this.addSlot(new SlotItemHandler(StorageHandlerHelper.NULL_HANDLER, 0, 93, 25));

        ItemStackHandler itemStackHandler = new ItemStackHandler(9);
        for(int column = 0; column < 9; ++column) {
            itemStackHandler.setStackInSlot(column,getHandler().getStackInSlot(column));
            this.addSlot(new UnchangeableSlot(itemStackHandler, column, 8 + column * 18, 64));
        }
    }

    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        ItemStack stack = this.getCarried();
        if (slotId >= 1 && slotId < getMenuSlots()) {
            if(getToolboxNumber() == 0) {
                if (stack.isEmpty()) {
                    getHandler().setFilter(slotId - 1, ItemStack.EMPTY);
                    getSlot(slotId).set(ItemStack.EMPTY);
                } else {
                    ItemStack item = stack.copy();
                    item.setCount(1);
                    getHandler().setFilter(slotId - 1, item);
                    getSlot(slotId).set(item);
                }
            }
        } else if(slotId == 0 && getHandler().canDelete(stack)) {
            this.setCarried(ItemStack.EMPTY);
        } else super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean moveItemStackToContainerSlot(@NotNull Player player,ItemStack clickedStack, int index) {
        if (getHandler().canDelete(clickedStack)) {
            getSlot(index).set(ItemStack.EMPTY);
            return true;
        }else return false;
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int id) {
        switch(id) {
            case 0 -> {
                changeWhiteOrBlack(!whiteOrBlack());
                return true;
            }
            case 1 -> {
                nextToolbox();
                refreshFilterSlot();
                playSound(player,SoundEvents.BOOK_PAGE_TURN);
                return true;
            }
            case -1 -> {
                previousToolbox();
                refreshFilterSlot();
                playSound(player,SoundEvents.BOOK_PAGE_TURN);
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public int getMenuSlots() {
        return getHandler().getSlots() + 1;
    }

    private int toolboxNumber = 0;

    public int getToolboxNumber() {
        return toolboxNumber;
    }

    public int biggestToolboxNumber() {
        return getHandler().toolboxItem.size() / ToolboxSlot;
    }

    public void nextToolbox(){
        toolboxNumber = Math.min(toolboxNumber + 1, biggestToolboxNumber());
    }

    public void previousToolbox(){
        toolboxNumber = Math.max(toolboxNumber - 1, 0);
    }

    public void refreshFilterSlot() {
        if(getToolboxNumber() != 0) {
            List<ItemStack> toolboxItem = getHandler().toolboxItem;
            for (int slotId = 0; slotId < ToolboxSlot; slotId++)
                getSlot(slotId + 1).set(toolboxItem.get((getToolboxNumber() - 1) * ToolboxSlot + slotId));
        }else {
            for (int slotId = 0; slotId < 9; slotId++)
                getSlot(slotId + 1).set(getHandler().getStackInSlot(slotId));
        }
    }
}