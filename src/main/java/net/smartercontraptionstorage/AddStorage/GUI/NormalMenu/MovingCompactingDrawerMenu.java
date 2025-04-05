package net.smartercontraptionstorage.AddStorage.GUI.NormalMenu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.smartercontraptionstorage.AddStorage.ItemHandler.CompactingHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class MovingCompactingDrawerMenu extends MovingDrawerMenu {
    public MovingCompactingDrawerMenu(CompactingHandlerHelper.@NotNull CompactingHandler handler, int pContainerId, @NotNull Player player) {
        super(handler, pContainerId, player);
    }

    public MovingCompactingDrawerMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        super(id, inventory, buf,(buffer,provider) -> new CompactingHandlerHelper.CompactingHandler(buf.readNbt(),provider));
    }

    @Override
    public void addSlots() {
        addUpgradesSlots();
        if(getHandlerSlot() == 3) {
            addSlot(new DrawerSlot(getHandler(), 0, 80, 23));
            addSlot(new DrawerSlot(getHandler(), 1, 67, 49));
            addSlot(new DrawerSlot(getHandler(), 2, 93, 49));
        }else throwSlotCountError();
    }

    @Override
    public @NotNull CompactingHandlerHelper.CompactingHandler getHandler() {
        return (CompactingHandlerHelper.CompactingHandler)handler;
    }
}