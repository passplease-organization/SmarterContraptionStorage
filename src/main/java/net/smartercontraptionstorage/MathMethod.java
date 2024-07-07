package net.smartercontraptionstorage;

import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.logistics.vault.ItemVaultBlock;
import com.simibubi.create.content.logistics.vault.ItemVaultItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.smartercontraptionstorage.AddStorage.StorageHandlerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.smartercontraptionstorage.FunctionInterface.FiveFunction;
import net.smartercontraptionstorage.FunctionInterface.FourFunction;
import net.smartercontraptionstorage.FunctionInterface.TriFunction;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MathMethod {
    public static final HashMap<Integer, Pair<StructureTemplate.StructureBlockInfo, BlockEntity>> pair = new HashMap<>();
    public static final HashMap<Integer, BlockPos> pos = new HashMap<>();
    public static boolean playerInteracting = false;
    public static boolean smarterContraptionStorage$canStoreItem = false;
    // A foolish variant to avoid adding disabled storage blocks to our contraptions
    // The reason I use this way: after @Unique comments it cannot be called outside the Mixin class
    // Wish some guys teach me how to deal with this problem, thanks ahead!
    public static void resetStatus(){
        pair.clear();
        pos.clear();
        smarterContraptionStorage$canStoreItem = false;
    }
    public static <T> void addData(HashMap<Integer,T> map, T t){
        map.put(map.size(), t);
    }
    public static boolean canBeControlledItem(Item comparedItem){
        return comparedItem instanceof ItemVaultItem ||
                comparedItem == Items.CHEST ||
                comparedItem == Items.TRAPPED_CHEST ||
                comparedItem == Items.BARREL ||
                StorageHandlerHelper.canControl(comparedItem);
    }
    public static boolean canBeControlledBlock(Block comparedBlock){
        return comparedBlock instanceof ItemVaultBlock ||
                comparedBlock == Blocks.CHEST ||
                comparedBlock == Blocks.TRAPPED_CHEST ||
                comparedBlock == Blocks.BARREL ||
                StorageHandlerHelper.canControl(comparedBlock);
    }
    public static BlockPos[] getAroundedBlockPos(BlockPos pos){
        BlockPos[] block = new BlockPos[6];
        block[0] = pos.north();
        block[1] = pos.south();
        block[2] = pos.west();
        block[3] = pos.east();
        block[4] = pos.above();
        block[5] = pos.below();
        return block;
    }
    /**<pre>
     * <code>search_or_stop</code> is used to check whether we should stop search Block or we should hang on searching other Block
     * <code>setReturnValue</code> is used to set what we should return the last time
     * <code>finallyDo</code> is used to do anything you want to do before</pre>
     * */
    public static <T> @NotNull T searchBlockPos(@NotNull Level level, @NotNull BlockPos initialBlock, @NotNull TriFunction<Level,BlockPos,BlockPos,Boolean> search_or_stop, @NotNull FiveFunction<Level,BlockPos,BlockPos,@Nullable T,@Nullable T,@Nullable T> setReturnValue, @Nullable FourFunction<Level,BlockPos,BlockPos,@Nullable T,@Nullable T> finallyDo,@Nullable T t){
        Set<BlockPos> checkedPos = new HashSet<>();
        T returnValue = t;
        returnValue = searchBlockPos(checkedPos,returnValue,level,initialBlock,initialBlock,search_or_stop,setReturnValue);
        Iterator<BlockPos> CheckedPos = checkedPos.iterator();
        if (finallyDo != null)
            while (CheckedPos.hasNext()){
                finallyDo.function(level,CheckedPos.next(),initialBlock,returnValue);
            }
        if(returnValue == null)
            throw new RuntimeException("The return value is null!");
        return returnValue;
    }
    private static <T> T searchBlockPos(@NotNull Set<BlockPos> checkedPos,@Nullable T t, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockPos initialBlock, @NotNull TriFunction<Level,BlockPos,BlockPos,Boolean> search_or_stop, @NotNull FiveFunction<Level,BlockPos,BlockPos,@Nullable T,@Nullable T,@Nullable T> setReturnValue){
        if(checkedPos.contains(pos) || calcDistance(pos,initialBlock) >= SmarterContraptionStorageConfig.SEARCH_RANGE.get())
            return setReturnValue.function(level,pos,initialBlock,t,null);
        checkedPos.add(pos);
        if(search_or_stop.function(level,pos,initialBlock)){
            for (BlockPos Pos : getAroundedBlockPos(pos))
                t = setReturnValue.function(level,pos,initialBlock,t,searchBlockPos(checkedPos,t,level,Pos,initialBlock,search_or_stop,setReturnValue));
        }
        t = setReturnValue.function(level,pos,initialBlock,t);
        return t;
    }
        public static double calcDistance(BlockPos pos1,BlockPos pos2){
        double X = Math.abs(pos1.getX() - pos2.getX());
        double Y = Math.abs(pos1.getY() - pos2.getY());
        double Z = Math.abs(pos1.getZ() - pos2.getZ());
        return Math.max(X,Math.max(Y,Z));
    }
    private static CompoundTag toolboxInventory = new CompoundTag();
    public static void addInventory(ToolboxBlockEntity entity){
        toolboxInventory.put(String.valueOf(toolboxInventory.size()),entity.serializeNBT().getCompound("Inventory"));
    }
    public static CompoundTag getInventory(){
        CompoundTag tag = new CompoundTag();
        ListTag inventory = new ListTag();
        for (int i = toolboxInventory.size() - 1; i >= 0; i--) {
            if(toolboxInventory.get(String.valueOf(i)) instanceof CompoundTag list){
                ListTag List = list.getList("Compartments", Tag.TAG_COMPOUND);
                for (int j = List.size() - 1; j >= 0; j--)
                    if(!((CompoundTag) List.get(j)).getString("id").endsWith("air"))
                        inventory.add(List.get(j));
            }
        }
        toolboxInventory = new CompoundTag();
        tag.put("Compartments",inventory);
        return tag;
    }
}