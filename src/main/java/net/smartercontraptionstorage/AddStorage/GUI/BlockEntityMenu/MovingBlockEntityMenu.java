package net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu;

import net.createmod.catnip.data.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

@ParametersAreNonnullByDefault
public class MovingBlockEntityMenu extends AbstractContainerMenu {
    public static DeferredHolder<MenuType<?>, MenuType<MovingBlockEntityMenu>> BlockEntityMenu;

    private final AbstractContainerMenu menu;

    private HelperMenuProvider<?> helper;

    public MovingBlockEntityMenu(AbstractContainerMenu menu, int id) {
        super(BlockEntityMenu.get(), id);
        if(menu instanceof MovingBlockEntityMenu)
            throw new IllegalArgumentException("Don't allow MovingBlockEntityMenu contains MovingBlockEntityMenu !");
        this.menu = menu;
        getMenu().slots.forEach(this::addSlot);
    }

    public MovingBlockEntityMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        super(BlockEntityMenu.get(),id);
        if(StorageHandlerHelper.findByName(buf.readUtf()) instanceof HelperMenuProvider<?> provider) {
            provider.setBlockEntity(Pair.of(buf.readInt(), buf.readLong()));
            provider.dealWithBuffer(buf);
            menu = provider.createMenu(id,inventory.player,inventory);
            setHelper(provider);
            getMenu().slots.forEach(this::addSlot);
        } else throw new IllegalStateException("The helper can not open menu ! " + StorageHandlerHelper.findByName(buf.readUtf()));
    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();
        getMenu().sendAllDataToRemote();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        Level level = setPlayerLevel(player);
        ItemStack itemStack = getMenu().quickMoveStack(player, i);
        player.setLevel(level);
        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        Level level = setPlayerLevel(player);
        boolean value = getHelper().stillValid(player, this);
        player.setLevel(level);
        return value;
    }

    public void setHelper(HelperMenuProvider<?> helper) {
        this.helper = helper;
    }

    public AbstractContainerMenu getMenu() {
        return menu;
    }

    public @NotNull HelperMenuProvider<?> getHelper() {
        Objects.requireNonNull(helper);
        return helper;
    }

    protected Level setPlayerLevel(Player player) {
        Level level = player.level();
        player.setLevel(MenuLevel.tickingBlockEntity(getHelper().getPair(),level));
        return level;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getItems() {
        return getMenu().getItems();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        Level level = setPlayerLevel(player);
        boolean value = getMenu().clickMenuButton(player, id);
        player.setLevel(level);
        return value;
    }

    @Override
    public Slot getSlot(int pSlotId) {
        return getMenu().getSlot(pSlotId);
    }

    @Override
    public void clicked(int index, int flag, ClickType type, Player player) {
        if(getHelper().shouldClickScreen(this, index, flag, type, player)) {
            // Client will be synchronized
            Level level = setPlayerLevel(player);
            getMenu().clicked(index, flag, type, player);
            player.setLevel(level);
        }
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
        return getMenu().canTakeItemForPickAll(pStack, pSlot);
    }

    @Override
    public void removed(Player player) {
        Level level = setPlayerLevel(player);
        getMenu().removed(player);
        super.removed(player);
        player.setLevel(level);
        getHelper().removed(this, player);
    }

    @Override
    public void slotsChanged(Container pContainer) {
        getMenu().slotsChanged(pContainer);
    }

    @Override
    public void initializeContents(int pStateId, List<ItemStack> pItems, ItemStack pCarried) {
        getMenu().initializeContents(pStateId, pItems, pCarried);
    }

    @Override
    public boolean canDragTo(Slot pSlot) {
        return getMenu().canDragTo(pSlot);
    }

    @Override
    public void suppressRemoteUpdates() {
        super.suppressRemoteUpdates();
        getMenu().suppressRemoteUpdates();
    }

    @Override
    public void resumeRemoteUpdates() {
        super.resumeRemoteUpdates();
        getMenu().resumeRemoteUpdates();
    }

    @Override
    public void transferState(AbstractContainerMenu pMenu) {
        getMenu().transferState(pMenu);
    }

    @Override
    public OptionalInt findSlot(Container pContainer, int pSlotIndex) {
        return getMenu().findSlot(pContainer, pSlotIndex);
    }

    @Override
    public int getStateId() {
        return getMenu().getStateId();
    }

    @Override
    public int incrementStateId() {
        super.incrementStateId();
        return getMenu().incrementStateId();
    }

    @Override
    public void setCarried(ItemStack pStack) {
        super.setCarried(pStack);
        getMenu().setCarried(pStack);
    }

    @Override
    public ItemStack getCarried() {
        return getMenu().getCarried();
    }

    @Override
    public void setData(int pId, int pData) {
        super.setData(pId, pData);
        getMenu().setData(pId, pData);
    }

    @Override
    public void setItem(int pSlotId, int pStateId, ItemStack pStack) {
        getMenu().setItem(pSlotId, pStateId, pStack);
    }

    @Override
    public void setRemoteCarried(ItemStack pRemoteCarried) {
        super.setRemoteCarried(pRemoteCarried);
        getMenu().setRemoteCarried(pRemoteCarried);
    }

    @Override
    public void setRemoteSlot(int pSlot, ItemStack pStack) {
        super.setRemoteSlot(pSlot, pStack);
        getMenu().setRemoteSlot(pSlot, pStack);
    }

    @Override
    public void setRemoteSlotNoCopy(int pSlot, ItemStack pStack) {
        super.setRemoteSlotNoCopy(pSlot, pStack);
        getMenu().setRemoteSlotNoCopy(pSlot, pStack);
    }

    @Override
    public void setSynchronizer(ContainerSynchronizer pSyncronizer) {
        super.setSynchronizer(pSyncronizer);
        getMenu().setSynchronizer(pSyncronizer);
    }
}
