package net.smartercontraptionstorage.AddStorage.GUI.NormalMenu;

import com.jaquadro.minecraft.storagedrawers.client.gui.StorageGuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.smartercontraptionstorage.AddStorage.GUI.UnchangeableSlot;
import net.smartercontraptionstorage.AddStorage.ItemHandler.DrawersHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public class MovingDrawerMenu extends AbstractMovingMenu<DrawersHandlerHelper.NormalDrawerHandler>{
    @OnlyIn(Dist.CLIENT)
    public StorageGuiGraphics storageGuiGraphics;
    private final boolean isClient;
    public MovingDrawerMenu(@NotNull DrawersHandlerHelper.NormalDrawerHandler handler, int pContainerId, @NotNull Player player) {
        super(handler, pContainerId, player);
        isClient = player.getCommandSenderWorld().isClientSide();
    }

    public MovingDrawerMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory, buf, (buffer,provider) -> new DrawersHandlerHelper.NormalDrawerHandler(buffer.readNbt(),provider));
    }

    public MovingDrawerMenu(int id, Inventory inventory, FriendlyByteBuf buf, BiFunction<FriendlyByteBuf, HolderLookup.Provider,DrawersHandlerHelper.NormalDrawerHandler> getHandler){
        super(id,inventory,buf,getHandler);
        isClient = inventory.player.getCommandSenderWorld().isClientSide();
    }

    @Override
    public void addPlayerSlots(@NotNull Player player) {
        addPlayerSlots(player.getInventory(),8,117);
    }

    @Override
    public void addSlots() {
        addUpgradesSlots();
        switch (getHandlerSlot()){
            case 1 -> addSlot(new DrawerSlot(getHandler(),0,80,36));
            case 2 -> {
                addSlot(new DrawerSlot(getHandler(),0,80,23));
                addSlot(new DrawerSlot(getHandler(),1,80,49));
            }
            case 4 -> {
                addSlot(new DrawerSlot(getHandler(),0,67,23));
                addSlot(new DrawerSlot(getHandler(),1,93,23));
                addSlot(new DrawerSlot(getHandler(),2,67,49));
                addSlot(new DrawerSlot(getHandler(),3,93,49));
            }
            default -> throwSlotCountError();
        }
    }

    protected void addUpgradesSlots(){
        ItemStackHandler h;
        for (int column = 0; column < 7; column++) {
            h = new ItemStackHandler();
            h.setStackInSlot(0,getHandler().getUpgrades(column));
            addSlot(new UnchangeableSlot(h,0,26 + column * 18, 86));
        }
    }

    @Override
    public int getMenuSlots() {
        return getHandlerSlot() + 7;
    }

    public Object throwSlotCountError() throws IllegalStateException {
        throw new IllegalArgumentException("Invalid Drawer slots count of " + getHandlerSlot() + " !");
    }

    public void setLastAccessedItem(ItemStack stack) {
        if (this.isClient() && this.storageGuiGraphics != null) {
            this.storageGuiGraphics.overrideStack = stack;
        }
    }

    public boolean isClient() {
        return isClient;
    }

    protected class DrawerSlot extends UnchangeableSlot {

        public DrawerSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public @NotNull ItemStack getItem() {
            ItemStack item = super.getItem();
            MovingDrawerMenu.this.setLastAccessedItem(item);
            return item;
        }
    }
}
