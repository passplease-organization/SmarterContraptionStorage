package net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu;

import net.createmod.catnip.data.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.smartercontraptionstorage.AddStorage.GUI.ContraptionMenuProvider;
import net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HelperMenuProvider<T extends StorageHandlerHelper> extends ContraptionMenuProvider<MovingBlockEntityMenu> {
    /**
     * Create a new Provider, usually called by static-like handler helper (registered in helper class)
     * */
    HelperMenuProvider<T> get();

    /**
     * Get a static-like handler helper (registered in helper class)
     * */
    T getHelper();

    void setBlockEntity(@Nullable BlockEntity blockEntity);

    default void setBlockEntity(Pair<Integer, Long> pair) {
        setLocalPos(BlockPos.of(pair.getSecond()));
        setBlockEntity(MenuLevel.getBlockEntity(pair));
        rememberPair(pair);
    }

    /**
     * Can use to create menu
     */
    BlockEntity getBlockEntity();

    @Override
    default @NotNull MovingBlockEntityMenu createMenu(int i, Inventory inventory, Player player){
        MovingBlockEntityMenu menu = new MovingBlockEntityMenu(createMenu(i, player,inventory),i);
        menu.setHelper(this);
        return menu;
    }

    AbstractContainerMenu createMenu(int i, Player player, Inventory inventory);

    default boolean canOpenMenu(BlockEntity blockEntity){
        return getHelper().canCreateHandler(blockEntity);
    }

    @Override
    default boolean check() {
        return ContraptionMenuProvider.super.check() && canOpenMenu(getBlockEntity());
    }

    boolean checkMenu(MovingBlockEntityMenu menu);

    @Override
    default void error() {
        setBlockEntity((BlockEntity) null);
        ContraptionMenuProvider.super.error();
    }

    <U extends Screen & MenuAccess<?>> U createScreen(MovingBlockEntityMenu menu, Inventory inventory, Component component);

    default void writeToBuffer(@NotNull FriendlyByteBuf buffer) {
        buffer.writeUtf(getHelper().getName());
        rememberPair(asPair());
        buffer.writeInt(getPair().getFirst());
        buffer.writeLong(getPair().getSecond());
        MenuLevel.addBlockEntity(getPair(),getBlockEntity(),getPlayer());
    }

    void rememberPair(Pair<Integer,Long> pair);

    // Get Generated Pair
    Pair<Integer,Long> getPair();

    // Generate Pair
    default Pair<Integer,Long> asPair(){
        return Pair.of(getContraption().getId(), getLocalPos().asLong());
    }

    @Override
    default void removed(MovingBlockEntityMenu menu, Player player) {
        if(check()) {
            MenuLevel.removeBlockEntity(getPair(), (ServerPlayer) player);
        }
        ContraptionMenuProvider.super.removed(menu, player);
        rememberPair(null);
    }

    @Override
    @NotNull ServerPlayer getPlayer();

    @Override
    default boolean hasOpened(){
        return MenuLevel.getBlocks().containsValue(getBlockEntity());
    }

    default boolean shouldClickScreen(MovingBlockEntityMenu menu, int index, int flag, ClickType type, Player player){
        return !player.level().isClientSide();
    }

    default boolean shouldClickScreen(MovingBlockEntityScreen screen, double mouseX, double mouseY, int button){
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    default void render(MovingBlockEntityScreen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick){}

    @OnlyIn(Dist.CLIENT)
    default void renderWithTooltip(MovingBlockEntityScreen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick){
        render(screen, guiGraphics, mouseX, mouseY, partialTick);
    }

    default boolean slotClicked(MovingBlockEntityScreen screen, Slot slot, int slotId, int button, ClickType type){
        return true;
    }
}