package net.smartercontraptionstorage.AddStorage.GUI.NormalMenu;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.client.gui.StorageGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class MovingDrawerScreen extends AbstractMovingScreen<MovingDrawerMenu>{
    public static final ResourceLocation BACKGROUND_1 = ResourceLocation.fromNamespaceAndPath(StorageDrawers.MOD_ID,"textures/gui/drawers_1.png");

    public static final ResourceLocation BACKGROUND_2 = ResourceLocation.fromNamespaceAndPath(StorageDrawers.MOD_ID,"textures/gui/drawers_2.png");

    public static final ResourceLocation BACKGROUND_4 = ResourceLocation.fromNamespaceAndPath(StorageDrawers.MOD_ID,"textures/gui/drawers_4.png");

    protected static StorageGuiGraphics storageGuiGraphics;
    
    public MovingDrawerScreen(MovingDrawerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = 8;
        titleLabelY = 6;
        inventoryLabelX = 8;
        inventoryLabelY = 105;
        if(storageGuiGraphics == null && minecraft != null)
            storageGuiGraphics = new StorageGuiGraphics(minecraft,minecraft.renderBuffers().bufferSource());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        menu.storageGuiGraphics = storageGuiGraphics;
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        menu.storageGuiGraphics = null;
        storageGuiGraphics.overrideStack = ItemStack.EMPTY;
    }

    @Override
    protected void renderScreen(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {}


    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        drawContent(guiGraphics,"container.storagedrawers.upgrades",8f,75f);
    }

    @Override
    public void drawContent(GuiGraphics guiGraphics, String key, float x, float y, Object... objects) {
        guiGraphics.drawString(font,I18n.get("container.storagedrawers.upgrades", objects),x,y,4210752,false);
    }

    @Override
    public ResourceLocation getBackground() {
        return switch (menu.getHandlerSlot()) {
            case 1 -> BACKGROUND_1;
            case 2 -> BACKGROUND_2;
            case 4 -> BACKGROUND_4;
            default -> (ResourceLocation) menu.throwSlotCountError();
        };
    }

    @Override
    public int height() {
        return 199;
    }

    @Override
    public int width() {
        return 176;
    }

    @Override
    public float getTextureHeight() {
        return 199f / 256f;
    }

    @Override
    public float getTextureWidth() {
        return 176f / 256f;
    }
}
