package net.smartercontraptionstorage.AddStorage.GUI;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class UnchangeableSlot extends SlotItemHandler {
    public UnchangeableSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public static UnchangeableSlot create(ItemStack stack, int xPosition, int yPosition) {
        ItemStackHandler handler = new ItemStackHandler();
        handler.setStackInSlot(0, stack);
        return new UnchangeableSlot(handler, 0, xPosition, yPosition);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return false;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }
}
