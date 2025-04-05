package net.smartercontraptionstorage.AddStorage.GUI.NormalMenu;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MovingCompactingDrawerScreen extends MovingDrawerScreen {
    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(StorageDrawers.MOD_ID,"textures/gui/drawers_comp.png");

    public MovingCompactingDrawerScreen(MovingDrawerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public ResourceLocation getBackground() {
        if(menu.getHandlerSlot() == 3)
            return BACKGROUND;
        else return (ResourceLocation) menu.throwSlotCountError();
    }
}
