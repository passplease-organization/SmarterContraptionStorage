//package net.smartercontraptionstorage.AddStorage.ItemHandler;
//
//import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
//import com.simibubi.create.content.equipment.toolbox.*;
//import net.createmod.catnip.data.Pair;
//import net.minecraft.core.BlockPos;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.chat.Component;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.sounds.SoundSource;
//import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.inventory.AbstractContainerMenu;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.neoforged.neoforge.items.ItemStackHandler;
//import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.HelperMenuProvider;
//import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.MovingBlockEntityMenu;
//import net.smartercontraptionstorage.AddStorage.NeedDealWith;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.Objects;
//
//@Deprecated
//public class ToolboxHandlerHelper extends StorageHandlerHelper implements NeedDealWith, HelperMenuProvider<ToolboxHandlerHelper> {
//    public static final ToolboxHandlerHelper INSTANCE = new ToolboxHandlerHelper();
//
//    @Override
//    public boolean canCreateHandler(BlockEntity entity) {
//        return entity instanceof ToolboxBlockEntity;
//    }
//
//    @Override
//    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {
//        assert canCreateHandler(entity);
//        ((ToolboxBlockEntity)entity).readInventory(handler.serializeNBT());
//    }
//
//    @Override
//    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
//        assert canCreateHandler(entity);
//        return (ItemStackHandler) entity.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(NULL_HANDLER);
//    }
//    @Override
//    public boolean allowControl(Item comparedItem) {
//        return false;
//    }
//
//    @Override
//    public boolean allowControl(Block block) {
//        return block instanceof ToolboxBlock;
//    }
//
//    @Override
//    public String getName() {
//        return "ToolboxHandlerHelper";
//    }
//
//    @Override
//    public boolean canDeserialize() {
//        return false;
//    }
//
//    @Override
//    public @NotNull ItemStackHandler deserialize(CompoundTag nbt) throws IllegalAccessException {
//        throw new IllegalAccessException();
//    }
//
//    @Override
//    public void doSomething(BlockEntity entity) {
//        StorageHandlerHelper.BlockEntityList.add(entity);
//    }
//
//    @Override
//    public void finallyDo() {}
//
//    @Override
//    public ToolboxHandlerHelper get() {
//        return new ToolboxHandlerHelper();
//    }
//
//    @Override
//    public ToolboxHandlerHelper getHelper() {
//        return INSTANCE;
//    }
//
//    private ToolboxBlockEntity toolbox = null;
//
//    @Override
//    public void setBlockEntity(@Nullable BlockEntity blockEntity) {
//        if(blockEntity instanceof ToolboxBlockEntity)
//            toolbox = (ToolboxBlockEntity) blockEntity;
//        else toolbox = null;
//    }
//
//    @Override
//    public ToolboxBlockEntity getBlockEntity() {
//        return toolbox;
//    }
//
//    @Override
//    public AbstractContainerMenu createMenu(int i, Player player, Inventory inventory) {
//        return Objects.requireNonNull(getBlockEntity().createMenu(i, inventory, player));
//    }
//
//    @Override
//    public boolean checkMenu(MovingBlockEntityMenu menu) {
//        return menu.getMenu() instanceof ToolboxMenu;
//    }
//
//    private Pair<Integer,Long> pair;
//
//    @Override
//    public void rememberPair(Pair<Integer,Long> pair) {
//        this.pair = pair;
//    }
//
//    @Override
//    public Pair<Integer, Long> getPair() {
//        return pair;
//    }
//
//    @Override
//    public ToolboxScreen createScreen(MovingBlockEntityMenu menu, Inventory inventory, Component component) {
//        return new ToolboxScreen((ToolboxMenu) menu.getMenu(),inventory,component);
//    }
//
//    private AbstractContraptionEntity contraption = null;
//
//    private BlockPos localPos = null;
//
//    @Override
//    public void setContraption(AbstractContraptionEntity contraption) {
//        this.contraption = contraption;
//    }
//
//    @Override
//    public AbstractContraptionEntity getContraption() {
//        return contraption;
//    }
//
//    @Override
//    public void setLocalPos(BlockPos localPos) {
//        this.localPos = localPos;
//    }
//
//    @Override
//    public BlockPos getLocalPos() {
//        return localPos;
//    }
//
//    private ServerPlayer player;
//
//    @Override
//    public void rememberPlayer(ServerPlayer player) {
//        this.player = player;
//    }
//
//    @Override
//    public @NotNull ServerPlayer getPlayer() {
//        return Objects.requireNonNull(player);
//    }
//    @Override
//    public @NotNull Component getDisplayName() {
//        return getBlockEntity().getDisplayName();
//    }
//
//    @Override
//    public void playSound(Level level) {
//        BlockPos soundPos = BlockPos.containing(getSoundPos());
//        level.playSound(null,soundPos, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 0.25f, level.random.nextFloat() * 0.1F + 1.2F);
//        level.playSound(null,soundPos,SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.1F, level.random.nextFloat() * 0.1F + 1.1F);
//    }
//
//    @Override
//    public void removed(MovingBlockEntityMenu menu, Player player) {
//        if(check()) {
//            BlockPos soundPos = BlockPos.containing(getSoundPos());
//            player.level().playSound(null,soundPos, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.1F, player.level().random.nextFloat() * 0.1F + 1.1F);
//            player.level().playSound(null,soundPos, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 0.25F, player.level().random.nextFloat() * 0.1F + 1.2F);
//        }
//        HelperMenuProvider.super.removed(menu, player);
//    }
//}