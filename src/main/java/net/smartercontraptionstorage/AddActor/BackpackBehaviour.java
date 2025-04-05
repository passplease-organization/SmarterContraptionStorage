package net.smartercontraptionstorage.AddActor;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.MagnetUpgradeItem;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BackpackBehaviour extends ToolboxBehaviour{
    public static @Nullable List<ItemStack> getUpgradeItems(MovementContext context){
        if(context.blockEntityData.get("backpackData") instanceof CompoundTag tag){
            tag = (CompoundTag) tag.get("tag");
            if(tag != null) {
                tag = (CompoundTag) tag.get("renderInfo");
                if(tag != null)
                    return List.of();//NBTHelper.readItemList(tag.getList("upgradeItems", Tag.TAG_COMPOUND));
            }
        }
        return null;
    }

    @Override
    public void tick(MovementContext context) {
        List<ItemStack> items = getUpgradeItems(context);
        if(items != null)
            for(ItemStack item : items){
                if(item.getItem() instanceof MagnetUpgradeItem)
                    magnetTick(context, ((MagnetUpgradeItem) item.getItem()).getRadius());
                else if(item.is(ModItems.ADVANCED_REFILL_UPGRADE.get()))
                    refillTick(context,getMaxDistance());
            }
    }
    public void magnetTick(MovementContext context,int radius){
        BlockPos entityPos = getEntityPos(context);
        AABB aabb = new AABB(entityPos.getX() - radius,entityPos.getY() - radius,entityPos.getZ() - radius,
                entityPos.getX() + radius,entityPos.getY() + radius,entityPos.getZ() + radius);
        List<ItemEntity> items = context.world.getEntitiesOfClass(ItemEntity.class,aabb);
        ItemStack stack;
        for(ItemEntity item : items) {
            stack = ItemHandlerHelper.insertItem(context.contraption.getStorage().getAllItems(), item.getItem(), false);
            if(stack.isEmpty())
                item.kill();
            else Utils.isSameItem(item.getItem(),stack);
        }
    }
    public void refillTick(MovementContext context,double range){
        NonNullList<ItemStack> playerItems;
        for(Player player : context.world.players()){
            if(Utils.calcDistance(getEntityPos(context),player.getOnPos()) <= range){
                playerItems = getPlayerItems(player);
                replenishPlayer(context,playerItems,playerItems);
            }
        }
    }
}