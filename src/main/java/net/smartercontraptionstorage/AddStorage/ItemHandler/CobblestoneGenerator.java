package net.smartercontraptionstorage.AddStorage.ItemHandler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.lex.cfd.CobbleForDays;
import net.minecraftforge.lex.cfd.CobbleGenTile;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CobblestoneGenerator extends StorageHandlerHelper{
    public static final ItemStackHandler cobblestoneGenerator = new ItemStackHandler(){
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if(stack.getItem() == Items.COBBLESTONE)
                return ItemStack.EMPTY;
            else return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack stack = Items.COBBLESTONE.getDefaultInstance();
            stack.setCount(Math.min(stack.getMaxStackSize(),amount));
            return stack;
        }
    };
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof CobbleGenTile && allowControl(entity.getBlockState().getBlock());
    }

    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {}

    @Override
    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
        return cobblestoneGenerator;
    }

    @Override
    public boolean allowControl(Item comparedItem) {
        return comparedItem == CobbleForDays.TIER5_ITEM.get();
    }

    @Override
    public boolean allowControl(Block block) {
        return block == CobbleForDays.TIER5_BLOCK.get();
    }

    @Override
    public void registerBlock(Consumer<Block> register) {
        register.accept(CobbleForDays.TIER5_BLOCK.get());
    }

    @Override
    public String getName() {
        return "CobblestoneGenerator";
    }

    @Override
    public @NotNull ItemStackHandler deserialize(CompoundTag nbt){
        return cobblestoneGenerator;
    }
}
