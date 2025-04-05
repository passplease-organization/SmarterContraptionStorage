package net.smartercontraptionstorage.AddStorage.GUI.NormalMenu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.smartercontraptionstorage.AddStorage.GUI.UnchangeableSlot;
import net.smartercontraptionstorage.AddStorage.ItemHandler.FunctionalDrawersHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class MovingFunctionalDrawerMenu extends AbstractMovingMenu<FunctionalDrawersHandlerHelper.FDrawersHandler>{
    public MovingFunctionalDrawerMenu(FunctionalDrawersHandlerHelper.@NotNull FDrawersHandler handler, int pContainerId, @NotNull Player player) {
        super(handler, pContainerId, player);
    }

    public MovingFunctionalDrawerMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        super(id, inventory, buf, (buffer,provider) -> new FunctionalDrawersHandlerHelper.FDrawersHandler(buffer.readNbt(),provider));
    }

    @Override
    public void addPlayerSlots(@NotNull Player player) {
        addPlayerSlots(player.getInventory(),8,102);
    }

    @Override
    public void addSlots() {
        for (int slot = 0; slot < getHandlerSlot(); slot++) {
            // to synchronize handler
            addSlot(new SlotItemHandler(getHandler(),slot,Integer.MAX_VALUE,Integer.MAX_VALUE));
        }
        addSlot(UnchangeableSlot.create(getHandler().upgrades.get(0),10,70));
        addSlot(UnchangeableSlot.create(getHandler().upgrades.get(1),28,70));
        addSlot(UnchangeableSlot.create(getHandler().upgrades.get(2),46,70));
        addSlot(UnchangeableSlot.create(getHandler().upgrades.get(3),64,70));
        addSlot(UnchangeableSlot.create(getHandler().upgrades.get(4),114,70));
        addSlot(UnchangeableSlot.create(getHandler().upgrades.get(5),132,70));
        addSlot(UnchangeableSlot.create(getHandler().upgrades.get(6),150,70));
    }

    @Override
    public int getMenuSlots() {
        return 7;
    }

    public Object throwSlotCountError(){
        throw new IllegalArgumentException("Invalid Drawer slots count of " + getHandlerSlot() + " !");
    }
}
