package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import net.p3pp3rf1y.sophisticatedcore.util.BlockItemBase;
import net.p3pp3rf1y.sophisticatedstorage.block.LimitedBarrelBlock;
import net.p3pp3rf1y.sophisticatedstorage.block.StorageBlockBase;
import net.p3pp3rf1y.sophisticatedstorage.block.StorageBlockEntity;
import net.p3pp3rf1y.sophisticatedstorage.client.gui.LimitedBarrelScreen;
import net.p3pp3rf1y.sophisticatedstorage.client.gui.StorageScreen;
import net.p3pp3rf1y.sophisticatedstorage.common.gui.LimitedBarrelContainerMenu;
import net.p3pp3rf1y.sophisticatedstorage.common.gui.StorageContainerMenu;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.HelperMenuProvider;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.MenuLevel;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.MovingBlockEntityMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Deprecated
public class SStorageBlockHelper extends StorageHandlerHelper implements HelperMenuProvider<SStorageBlockHelper> {
    public static final SStorageBlockHelper INSTANCE = new SStorageBlockHelper();
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof StorageBlockEntity;
    }

    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {
        assert canCreateHandler(entity);
        ((InventoryHandler)handler).changeSlots(0);
        ((StorageBlockEntity)entity).getStorageWrapper().getInventoryHandler().deserializeNBT(handler.serializeNBT());
    }

    @Override
    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        return ((StorageBlockEntity)entity).getStorageWrapper().getInventoryHandler();
    }

    @Override
    public boolean allowControl(Item comparedItem) {
        return comparedItem instanceof BlockItemBase;
    }

    @Override
    public boolean allowControl(Block block) {
        return block instanceof StorageBlockBase;
    }

    @Override
    public String getName() {
        return "SStorageBlockHelper";
    }

    @Override
    public @NotNull ItemStackHandler deserialize(CompoundTag nbt) throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    @Override
    public boolean canDeserialize() {
        return false;
    }

    @Override
    public HelperMenuProvider<SStorageBlockHelper> get() {
        return new SStorageBlockHelper();
    }

    @Override
    public SStorageBlockHelper getHelper() {
        return INSTANCE;
    }

    private StorageBlockEntity blockEntity;

    @Override
    public void setBlockEntity(@Nullable BlockEntity blockEntity) {
        if(blockEntity instanceof StorageBlockEntity)
            this.blockEntity = (StorageBlockEntity)blockEntity;
        else this.blockEntity = null;
    }

    @Override
    public StorageBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public boolean isLimitedBarrelBlock() {
        return getBlockEntity().getBlockState().getBlock() instanceof LimitedBarrelBlock;
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Player player, Inventory inventory) {
        StorageContainerMenu menu = null;
        if(getPair() != null) {
            MenuLevel.tickingBlockEntity(getPair(),player.level);
            Level level = player.level;
            player.level = MenuLevel.level(level);
            menu = isLimitedBarrelBlock() ? new LimitedBarrelContainerMenu(i, player, getBlockEntity().getBlockPos()) : new StorageContainerMenu(i,player,getBlockEntity().getBlockPos());
            player.level = level;
        }
        return menu;
    }

    @Override
    public boolean checkMenu(MovingBlockEntityMenu menu) {
        return isLimitedBarrelBlock() ? menu.getMenu() instanceof LimitedBarrelContainerMenu : menu.getMenu() instanceof StorageContainerMenu;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public StorageScreen createScreen(MovingBlockEntityMenu menu, Inventory inventory, Component component) {
        if(isLimitedBarrelBlock()) {
            return new LimitedBarrelScreen((StorageContainerMenu)menu.getMenu(),inventory,component);
        }else return StorageScreen.constructScreen((StorageContainerMenu)menu.getMenu(),inventory,component);
    }

    private Pair<Integer, Long> pair;

    @Override
    public void rememberPair(Pair<Integer, Long> pair) {
        this.pair = pair;
    }

    @Override
    public Pair<Integer, Long> getPair() {
        return pair;
    }

    private AbstractContraptionEntity contraption;

    private BlockPos localPos;

    @Override
    public void setContraption(AbstractContraptionEntity contraption) {
        this.contraption = contraption;
    }

    @Override
    public AbstractContraptionEntity getContraption() {
        return contraption;
    }

    @Override
    public void setLocalPos(BlockPos localPos) {
        this.localPos = localPos;
    }

    @Override
    public BlockPos getLocalPos() {
        return localPos;
    }

    private ServerPlayer player;

    @Override
    public void rememberPlayer(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public @NotNull ServerPlayer getPlayer() {
        return Objects.requireNonNull(player);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return getBlockEntity().getDisplayName();
    }
}
