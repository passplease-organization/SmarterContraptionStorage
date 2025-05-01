package net.smartercontraptionstorage.AddStorage.GUI.NormalMenu;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.smartercontraptionstorage.AddStorage.GUI.ContraptionMenuProvider;
import net.smartercontraptionstorage.SmarterContraptionStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MovingMenuProvider extends ContraptionMenuProvider<AbstractMovingMenu<?>> {
    default @NotNull Component getDisplayName() {
        return Component.translatable(SmarterContraptionStorage.MODID + ".moving_container." + getTranslationKey());
    }

    String getTranslationKey();

    MenuType<? extends AbstractMovingMenu<?>> getMenuType();

    @Override
    default void rememberPlayer(ServerPlayer player){}

    @Override
    default @Nullable ServerPlayer getPlayer(){
        return null;
    }

    @Override
    default boolean hasOpened(){
        return getContraption() != null || getLocalPos() != null;
    }
}