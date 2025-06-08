package net.smartercontraptionstorage.AddActor;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandler;
import com.simibubi.create.foundation.item.ItemHelper;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.smartercontraptionstorage.SmarterContraptionStorage;
import net.smartercontraptionstorage.Utils;

import java.util.List;

public class ToolboxBehaviour implements MovementBehaviour {
    public static double getMaxDistance(){
        return ToolboxHandler.getMaxRange(null);
    }
    @Override
    public void tick(MovementContext context) {
        if(context.blockEntityData.get("Inventory") instanceof CompoundTag tag) {
            List<ItemStack> toolboxItems = NBTHelper.readItemList(tag.getList("Compartments", Tag.TAG_COMPOUND));
            for (Player player : context.world.players()) {
                if (!player.isCreative() && Utils.calcDistance(getEntityPos(context), player.getOnPos()) <= getMaxDistance()) {
                    if(context.world.isClientSide)
                        sendMessage("contraption.toolbox.behaviour.open",player);
                    replenishPlayer(context,getPlayerItems(player),toolboxItems);
                }
            }
        }
    }
    public static BlockPos getEntityPos(MovementContext context){
        return  new BlockPos(
                context.blockEntityData.getInt("x") + context.contraption.anchor.getX(),
                context.blockEntityData.getInt("y") + context.contraption.anchor.getY(),
                context.blockEntityData.getInt("z") + context.contraption.anchor.getZ()
        );
    }
    public static NonNullList<ItemStack> getPlayerItems(Player player){
        int i;
        NonNullList<ItemStack> playerItems = NonNullList.create();
        playerItems.add(player.getOffhandItem());
        for(i = 0;i <= 8;i++)// only check hotbar
            playerItems.add(player.getInventory().getItem(i));
        return playerItems;
    }
    public static void replenishPlayer(MovementContext context,NonNullList<ItemStack> playerItems,List<ItemStack> filterItems){
        int count,halfMaxSize;
        ItemStack item;
        for(ItemStack playerItem : playerItems){
            for(ItemStack filterItem : filterItems){
                if(Utils.isSameItemSameTags(playerItem,filterItem)){
                    halfMaxSize = playerItem.getMaxStackSize() / 2;
                    if(halfMaxSize == 0)
                        continue;
                    count = playerItem.getCount();
                    if(count > halfMaxSize){
                        item = playerItem.copy();
                        item.setCount(count - halfMaxSize);
                        halfMaxSize += ItemHandlerHelper.insertItem(context.contraption.getStorage().getAllItems(),item,false).getCount();
                        playerItem.setCount(halfMaxSize);
                    }else if(count < halfMaxSize){
                        count += ItemHelper.extract(context.contraption.getStorage().getAllItems(), (stack) -> Utils.isSameItemSameTags(playerItem,stack),halfMaxSize - count,false).getCount();
                        playerItem.setCount(count);
                    }
                }
            }
        }
    }
    public static void sendMessage(String key,Player player){
        Lang.builder(SmarterContraptionStorage.MODID).translate(key).style(ChatFormatting.GOLD).sendStatus(player);
    }
}