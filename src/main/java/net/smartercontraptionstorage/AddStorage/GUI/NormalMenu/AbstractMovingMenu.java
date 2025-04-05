package net.smartercontraptionstorage.AddStorage.GUI.NormalMenu;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public abstract class AbstractMovingMenu<T extends ItemStackHandler & MovingMenuProvider> extends AbstractContainerMenu {
    protected final @NotNull T handler;

    public AbstractMovingMenu(@NotNull T handler, int pContainerId,@NotNull Player player) {
        super(handler.getMenuType(), pContainerId);
        this.handler = handler;
        addSlots();
        addPlayerSlots(player);
    }

    public AbstractMovingMenu(int id, Inventory inventory, FriendlyByteBuf buf, BiFunction<FriendlyByteBuf, HolderLookup.Provider,T> getHandler) {
        this(getHandler.apply(buf,inventory.player.registryAccess()),id,inventory.player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot clickedSlot = this.getSlot(index);
        if (!clickedSlot.hasItem()) {
            return ItemStack.EMPTY;
        } else {
            ItemStack stack = clickedSlot.getItem();
            int size = getMenuSlots();
            if (index < size) {
                if(!this.moveItemStackTo(stack, size, this.slots.size(), false))
                    return ItemStack.EMPTY;
            } else if(index >= slots.size() - 9) {
                if(!moveItemStackToContainerSlot(player,stack,index) && !this.moveItemStackTo(stack, size, slots.size() - 9, false))
                    return ItemStack.EMPTY;
            } else {
                if(!moveItemStackToContainerSlot(player,stack,index) && !this.moveItemStackTo(stack, slots.size() - 9, slots.size(), false) && !this.moveItemStackTo(stack, index + 1, slots.size() - 9, false))
                    return ItemStack.EMPTY;
            }
            return stack;
        }
    }

    public boolean moveItemStackToContainerSlot(@NotNull Player player,ItemStack clickedStack, int index){
        return false;
    }

    public abstract void addPlayerSlots(@NotNull Player player);

    public abstract void addSlots();

    public abstract int getMenuSlots();

    public @NotNull T getHandler() {
        return handler;
    }

    public int getHandlerSlot() {
        return getHandler().getSlots();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return getHandler().stillValid(player,this);
    }

    protected void addPlayerSlots(Inventory inventory, int x, int y) {
        for(int row = 0; row < 3; ++row) {
            for(int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(inventory, row * 9 + column + 9, x + 18 * column, y + 18 * row));
            }
        }

        for(int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(inventory, column, x + 18 * column, y + 58));
        }
    }

    public void playSound(@NotNull Player player, SoundEvent soundEvent) {
        player.getCommandSenderWorld().playSound(player,player,soundEvent, SoundSource.BLOCKS,0.75f,1f);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        getHandler().removed(this, player);
    }
}