package net.smartercontraptionstorage.AddStorage.GUI;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ContraptionMenuProvider<T extends AbstractContainerMenu> extends MenuProvider {
    @OnlyIn(Dist.CLIENT)
    @Override
    @NotNull T createMenu(int i, Inventory inventory, Player player);

    /**
     * Can not use to create menu
     */
    void setContraption(AbstractContraptionEntity contraption);

    AbstractContraptionEntity getContraption();

    /**
     * Can not use to create menu
     */
    void setLocalPos(BlockPos localPos);

    BlockPos getLocalPos();

    /**
     * Only in server
     * */
    void rememberPlayer(ServerPlayer player);

    /**
     * Only in server
     * */
    @Nullable ServerPlayer getPlayer();

    void writeToBuffer(@NotNull FriendlyByteBuf buffer);

    default void playSound(Level level){
        level.playSound(null, BlockPos.containing(getSoundPos()), SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 0.75f, 1.0f);
    }

    default Vec3 getSoundPos(){
        return getContraption().toGlobalVector(Vec3.atCenterOf(getLocalPos()), 0.0f);
    }

    default boolean stillValid(Player player, T menu){
        return getContraption().isAlive() && player.distanceToSqr(getContraption().toGlobalVector(Vec3.atCenterOf(getLocalPos()), 0.0F)) < (double)64.0F;
    }

    default boolean check(){
        return getContraption() != null && getContraption().isAlive() && getLocalPos() != null;
    }

    default void removed(T menu, Player player){
        setContraption(null);
        setLocalPos(null);
    }

    default void error(){
        setContraption(null);
        setLocalPos(null);
        Utils.addError(getDisplayName().getString() + " can not open menu !");
    }

    /**Only In Server !*/
    boolean hasOpened();

    default void dealWithBuffer(FriendlyByteBuf buffer){}
}
