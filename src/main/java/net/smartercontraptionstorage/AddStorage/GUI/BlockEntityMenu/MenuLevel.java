package net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu;

import net.createmod.catnip.data.Pair;
import net.createmod.catnip.levelWrappers.WrappedLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.smartercontraptionstorage.Message.MenuLevelPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class MenuLevel extends WrappedLevel {
    private static final Map<Pair<Integer,Long>,BlockEntity> blocks = new HashMap<>();

    private static BlockEntity tickingBlockEntity;

    private static Level tickingLevel;

    public static MenuLevel level = new MenuLevel();

    private MenuLevel() {
        super(Minecraft.getInstance().level);
    }

    public static Map<Pair<Integer, Long>, BlockEntity> getBlocks(){
        return blocks;
    }

    public static void addBlockEntity(Pair<Integer, Long> pair, BlockEntity blockEntity,@Nullable ServerPlayer player){
        blocks.put(pair,blockEntity);
        if(player != null)
            new MenuLevelPacket().set(blocks.keySet(),false).sendToClient(player);
    }

    public static void removeBlockEntity(Pair<Integer, Long> pair, ServerPlayer player) {
        if(pair != null) {
            blocks.computeIfPresent(pair, (p, be) -> {
                if (be == tickingBlockEntity){
                    tickingBlockEntity.setLevel(tickingLevel);
                    tickingBlockEntity = null;
                    tickingLevel = null;
                }
                return null;
            });
            new MenuLevelPacket().set(Set.of(pair), true).sendToClient(player);
        }
    }

    public static BlockEntity getBlockEntity(Pair<Integer, Long> pair){
        return blocks.get(pair);
    }

    public static MenuLevel tickingBlockEntity(Pair<Integer, Long> pair,boolean isClient) {
        if(tickingBlockEntity != null){
            tickingBlockEntity.setLevel(tickingLevel);
        }
        if(blocks.containsKey(pair)) {
            tickingBlockEntity = blocks.get(pair);
            tickingLevel = tickingBlockEntity.getLevel();
            tickingBlockEntity.setLevel(level);
        } else {
            tickingBlockEntity = null;
            tickingLevel = null;
        }
        level.isClientSide = isClient;
        return level;
    }

    public static MenuLevel tickingBlockEntity(Pair<Integer, Long> pair,Level level){
        MenuLevel value = tickingBlockEntity(pair, level.isClientSide());
        tickingLevel = level;
        return value;
    }

    private static void setTickingBlockEntity(BlockEntity blockEntity){
        tickingBlockEntity = blockEntity;
    }

    // Allow run something in MenuLevel without adding block entity to map, usually used in creating menu
    public static <T> T levelRun(@NotNull Function<Consumer<BlockEntity>,T> action){
        BlockEntity tickingBlockEntity_pre = tickingBlockEntity;
        T value = action.apply(MenuLevel::setTickingBlockEntity);
        tickingBlockEntity = tickingBlockEntity_pre;
        return value;
    }

    public static Level getTickingLevel(){
        return tickingLevel;
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos pPos) {
        return tickingBlockEntity;
    }

    @Override
    public boolean addFreshEntity(Entity entityIn) {
        entityIn.setLevel(tickingLevel);
        return tickingLevel.addFreshEntity(entityIn);
    }
}