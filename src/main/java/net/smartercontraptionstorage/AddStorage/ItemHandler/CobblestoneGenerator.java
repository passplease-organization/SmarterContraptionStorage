//package net.smartercontraptionstorage.AddStorage.ItemHandler;
//
//import net.minecraft.core.HolderLookup;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraftforge.lex.cfd.CobbleForDays;
//import net.minecraftforge.lex.cfd.CobbleGenTile;
//import net.neoforged.neoforge.items.ItemStackHandler;
//import org.jetbrains.annotations.NotNull;
//
//@Deprecated
//public class CobblestoneGenerator extends StorageHandlerHelper{
//    public static final ItemStackHandler cobblestoneGenerator = new ItemStackHandler(){
//        @Override
//        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
//            if(stack.getItem() == Items.COBBLESTONE)
//                return ItemStack.EMPTY;
//            else return stack;
//        }
//
//        @Override
//        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
//            ItemStack stack = Items.COBBLESTONE.getDefaultInstance();
//            stack.setCount(Math.min(stack.getMaxStackSize(),amount));
//            return stack;
//        }
//    };
//    @Override
//    public boolean canCreateHandler(BlockEntity entity) {
//        return entity instanceof CobbleGenTile && allowControl(entity.getBlockState().getBlock());
//    }
//
//    @Override
//    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {}
//
//    @Override
//    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
//        return cobblestoneGenerator;
//    }
//
//    @Override
//    public boolean allowControl(Item comparedItem) {
//        //return comparedItem == CobbleForDays.TIER5_ITEM.get();
//        return false;
//    }
//
//    @Override
//    public boolean allowControl(Block block) {
//        //return block == CobbleForDays.TIER5_BLOCK.get();
//        return false;
//    }
//
//    @Override
//    public String getName() {
//        return "CobblestoneGenerator";
//    }
//
//    @Deprecated
//    @Override
//    public @NotNull ItemStackHandler deserialize(CompoundTag nbt){
//        return cobblestoneGenerator;
//    }
//
//    @Override
//    public ItemStackHandler deserialize(CompoundTag nbt, HolderLookup.Provider provider) throws IllegalAccessException {
//        return  cobblestoneGenerator;
//    }
//}
