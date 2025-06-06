package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlockEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContext;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.HelperMenuProvider;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.MenuLevel;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.MovingBlockEntityMenu;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.MovingBlockEntityScreen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SBackPacksHandlerHelper extends StorageHandlerHelper implements HelperMenuProvider<SBackPacksHandlerHelper> {
    public static final SBackPacksHandlerHelper INSTANCE = new SBackPacksHandlerHelper();

    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof BackpackBlockEntity;
    }

    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {
        assert canCreateHandler(entity);
        ((InventoryHandler)handler).copyStacksTo(((BackpackBlockEntity)entity).getStorageWrapper().getInventoryHandler());
    }

    @Override
    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        return ((BackpackBlockEntity)entity).getStorageWrapper().getInventoryHandler();
    }

    @Override
    public boolean allowControl(Item comparedItem) {
        return comparedItem instanceof BackpackItem;
    }

    @Override
    public boolean allowControl(Block block) {
        return block instanceof BackpackBlock;
    }

    @Override
    public String getName() {
        return "SBackPacksHandlerHelper";
    }

    @Override
    public boolean canDeserialize() {
        return false;
    }

    @Override
    public @NotNull ItemStackHandler deserialize(CompoundTag nbt) throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    @Override
    public HelperMenuProvider<SBackPacksHandlerHelper> get() {
        return new SBackPacksHandlerHelper();
    }

    @Override
    public SBackPacksHandlerHelper getHelper() {
        return INSTANCE;
    }

    private BackpackBlockEntity blockEntity;

    @Override
    public void setBlockEntity(@Nullable BlockEntity blockEntity) {
        if(blockEntity instanceof BackpackBlockEntity)
            this.blockEntity = (BackpackBlockEntity) blockEntity;
        else this.blockEntity = null;
    }

    @Override
    public BackpackBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Player player, Inventory inventory) {
        return MenuLevel.levelRun((setter) -> {
            setter.accept(getBlockEntity());
            Level level = player.level;
            player.level = MenuLevel.level(level);
            BackpackContainer container = new BackpackContainer(i, player, new BackpackContext.Block(getBlockEntity().getBlockPos()));
            player.level = level;
            return container;
        });
    }

    @Override
    public boolean checkMenu(MovingBlockEntityMenu menu) {
        return menu.getMenu() instanceof BackpackContainer;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public BackpackScreen createScreen(MovingBlockEntityMenu menu, Inventory inventory, Component component) {
        return new BackpackScreen((BackpackContainer) menu.getMenu(), inventory, component);
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
        return getBlockEntity().getBackpackWrapper().getDisplayName();
    }

    @Override
    public boolean shouldClickScreen(MovingBlockEntityScreen screen, double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean shouldClickScreen(MovingBlockEntityMenu menu, int index, int flag, ClickType type, Player player) {
        return true;
    }

    @Override
    public void render(MovingBlockEntityScreen screen, PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        screen.renderTooltip(poseStack,Component.translatable("smartercontraptionstorage.moving_container.backpack.reminder"), 0, screen.getScreen().getGuiTop() - 10);
    }
}
