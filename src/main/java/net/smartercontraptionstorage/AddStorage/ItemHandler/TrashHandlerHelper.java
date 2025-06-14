package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import com.simibubi.create.content.equipment.toolbox.ToolboxMountedStorage;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.TrashCans;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.RegistryObject;
import net.smartercontraptionstorage.AddStorage.GUI.NormalMenu.MovingTrashCanMenu;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class TrashHandlerHelper extends StorageHandlerHelper{
    public static final String NAME = "TrashHandlerHelper";
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof TrashCanBlockEntity && ((TrashCanBlockEntity)entity).items;
    }
    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {
        assert canCreateHandler(entity) && handler instanceof TrashHandler;
        if(entity instanceof TrashCanBlockEntity Entity) {
            TrashHandler Handler = (TrashHandler) handler;
            Entity.itemFilterWhitelist = Handler.whiteOrBlack;
            for (int i = Entity.itemFilter.size() - 1; i >= 0; i--)
                Entity.itemFilter.set(i, Handler.items[i]);
            Entity.setChanged();
            Entity.update();
        }
    }
    @Override
    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        TrashCanBlockEntity Entity = (TrashCanBlockEntity)entity;
        return new TrashHandler(Entity.itemFilterWhitelist,Entity.itemFilter);
    }
    @Override
    public boolean allowControl(Item comparedItem) {
        String name = comparedItem.getDescriptionId();
        return name.startsWith("trashcans.block.item") || name.startsWith("trashcans.block.ultimate");
    }
    @Override
    public boolean allowControl(Block block) {
        return false;
    }
    @Override
    public void registerBlock(Consumer<Block> register) {
        register.accept(TrashCans.item_trash_can);
        register.accept(TrashCans.ultimate_trash_can);
    }
    @Override
    public String getName() {
        return NAME;
    }
    @Override
    public @NotNull ItemStackHandler deserialize(CompoundTag nbt) {
        return new TrashHandler(nbt);
    }

    public static class TrashHandler extends HandlerHelper implements NeedDealWith{
        public boolean whiteOrBlack;
        public List<ItemStack> toolboxItem;
        // false : black
        // true : white
        public TrashHandler(boolean whiteOrBlack, List<ItemStack> itemFilter) {
            super(itemFilter.size());
            this.whiteOrBlack = whiteOrBlack;
            for (int i = items.length - 1; i >= 0; i--) {
                items[i] = itemFilter.get(i);
                items[i].setCount(1);
                slotLimits[i] = 1;
            }
            toolboxItem = new ArrayList<>();
        }
        public TrashHandler(CompoundTag nbt){
            super(nbt);
            whiteOrBlack = nbt.getBoolean("whiteOrBlack");
            toolboxItem = NBTHelper.readItemList(nbt.getList("toolboxItem", Tag.TAG_COMPOUND));
        }
        public boolean canDelete(ItemStack stack){
            if(Utils.isItemEmpty(stack))
                return false;
            for (ItemStack item : toolboxItem) {
                if(Utils.isSameItem(item,stack))
                    return false;
            }
            for (ItemStack item : items){
                if(Utils.isSameItemSameTags(item,stack))
                    return whiteOrBlack;
            }
            return !whiteOrBlack;
        }
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return canDelete(stack) ? ItemStack.EMPTY : stack;
        }
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
        @Override
        public String getName() {
            return NAME;
        }
        @Override
        public void doSomething(BlockEntity entity) {}
        @Override
        public void finallyDo() {}

        @Override
        public void finallyDo(Map<BlockPos, MountedItemStorage> itemsBuilder) {
            ArrayList<ItemStack> toolboxItem = new ArrayList<>();
            for(MountedItemStorage storage : itemsBuilder.values())
                if(storage instanceof ToolboxMountedStorage toolboxMountedStorage){
                    for (int slot = 0; slot < toolboxMountedStorage.getSlots(); slot += ToolboxInventory.STACKS_PER_COMPARTMENT) {
                        if(!toolboxItem.contains(toolboxMountedStorage.getStackInSlot(slot)))
                            toolboxItem.add(toolboxMountedStorage.getStackInSlot(slot));
                    }
                }
            this.toolboxItem = toolboxItem.stream().filter((item)->!item.isEmpty()).toList();
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            tag.putBoolean("whiteOrBlack",whiteOrBlack);
            tag.put("toolboxItem",NBTHelper.writeItemList(toolboxItem));
            return tag;
        }

        @Override
        public String getTranslationKey() {
            return "trashcans";
        }

        @Override
        public @NotNull MovingTrashCanMenu createMenu(int i, Inventory inventory, Player player) {
            return new MovingTrashCanMenu(this,i,player);
        }
        @Override
        public void writeToBuffer(@NotNull FriendlyByteBuf buffer) {
            buffer.writeNbt(serializeNBT());
        }

        public static RegistryObject<MenuType<MovingTrashCanMenu>> TrashCanMenu;

        @Override
        public MenuType<MovingTrashCanMenu> getMenuType() {
            Objects.requireNonNull(TrashCanMenu);
            return TrashCanMenu.get();
        }

        public void setFilter(int slot,ItemStack stack){
            if(slot >= 0 && slot < items.length)
                items[slot] = stack;
        }
    }
}