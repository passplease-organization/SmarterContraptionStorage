package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.WrapperMountedItemStorage;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.HelperMenuProvider;
import net.smartercontraptionstorage.AddStorage.GUI.ContraptionMenuProvider;
import net.smartercontraptionstorage.AddStorage.GUI.NormalMenu.MovingMenuProvider;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MovingItemStorage extends WrapperMountedItemStorage<ItemStackHandler> {
    public final @NonNull StorageHandlerHelper helper;

    public BlockEntity blockEntity;

    public MovingItemStorage(ItemStackHandler handler, @NotNull StorageHandlerHelper helper) {
        super(MovingItemStorageType.HELPER_STORAGE.get(), handler);
        this.helper = helper;
    }

    @Override
    public void unmount(Level level, BlockState blockState, BlockPos blockPos, @Nullable BlockEntity blockEntity) {
        helper.addStorageToWorld(helper.canCreateHandler(blockEntity) ? blockEntity : this.blockEntity,getHandler());
    }

    public ItemStackHandler getHandler() {
        return this.wrapped;
    }

    @Override
    public boolean handleInteraction(ServerPlayer player, Contraption contraption, StructureTemplate.StructureBlockInfo info) {
        ContraptionMenuProvider<?> provider = null;
        if(getHandler() instanceof MovingMenuProvider h && !h.hasOpened())
            provider = h;
        else {
            if(helper instanceof HelperMenuProvider<?>) {
                HelperMenuProvider<?> h = ((HelperMenuProvider<?>) helper).get();
                if(h.canOpenMenu(blockEntity) && !h.hasOpened()) {
                    h.setBlockEntity(blockEntity);
                    provider = h;
                }
            }
        }
        if(provider != null){
            provider.setContraption(contraption.entity);
            provider.setLocalPos(info.pos());
            if(provider.check()){
                provider.rememberPlayer(player);
                NetworkHooks.openScreen(player,provider,provider::writeToBuffer);
                provider.playSound(player.level());
                return true;
            }else provider.error();
        }
        return false;
    }

    protected @Nullable NeedDealWith getDeal(){
        if(helper instanceof NeedDealWith)
            return  ((NeedDealWith) helper);
        else if(getHandler() instanceof NeedDealWith)
            return  ((NeedDealWith) getHandler());
        return null;
    }

    public void doSomething(Map<BlockPos, MountedItemStorage> itemsBuilder) {
        NeedDealWith deal = getDeal();
        if(deal != null) {
            deal.doSomething(blockEntity,itemsBuilder);
        }
    }

    public void finallyDo(Map<BlockPos, MountedItemStorage> itemsBuilder) {
        NeedDealWith deal = getDeal();
        if(deal != null) {
            deal.finallyDo(itemsBuilder);
        }
    }
}
