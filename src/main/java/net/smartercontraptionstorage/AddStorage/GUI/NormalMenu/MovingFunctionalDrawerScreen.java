package net.smartercontraptionstorage.AddStorage.GUI.NormalMenu;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.client.gui.DrawerInfoGuiAddon;
import com.hrznstudio.titanium.Titanium;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.smartercontraptionstorage.Utils;
import org.apache.commons.lang3.tuple.Pair;

public class MovingFunctionalDrawerScreen extends AbstractMovingScreen<MovingFunctionalDrawerMenu>{
    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Titanium.MODID, "textures/gui/background.png");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FunctionalStorage.MOD_ID,"textures/block/oak_front_1.png");
    public DrawerInfoGuiAddon inventorySlot;
    public MovingFunctionalDrawerScreen(MovingFunctionalDrawerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        setTitleCenter(titleLabelY);
        inventoryLabelY = 91;
        inventorySlot = new DrawerInfoGuiAddon(64,16,TEXTURE,menu.getHandlerSlot(),(slot) -> {
            switch (menu.getHandlerSlot()) {
                case 1 -> {
                    return Pair.of(16,16);
                }
                case 2 -> {
                    return slot == 0 ? Pair.of(16,28) : Pair.of(16,4);
                }
                case 4 -> {
                    return switch (slot){
                        case 0 -> Pair.of(28,28);
                        case 1 -> Pair.of(4,28);
                        case 2 -> Pair.of(28,4);
                        case 3 -> Pair.of(4,4);
                        default -> throw new IllegalStateException("Unexpected slot " + slot + " for " + menu.getHandlerSlot());
                    };
                }
                default -> {
                    menu.throwSlotCountError();
                    Utils.addError("Invalid slot count ! This method should never be called !");
                    return Pair.of(-1,-1);
                }
            }
        },menu.getHandler()::getStackInSlot,(slot) -> menu.getHandler().slotLimits[slot]);
    }

    @Override
    protected void renderScreen(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        if (inventorySlot != null) {
            inventorySlot.drawBackgroundLayer(guiGraphics,this,null,getGuiLeft(),getGuiTop(),mouseX,mouseY,partialTicks);
        }
        bindTexture(BACKGROUND);
        for (int slot = 0; slot < 4; slot++) {
            blit(getXofScreen(8 + slot * 18),getYofScreen(68),0f,height(),20,20,256,256,guiGraphics);
        }
        for (int slot = 0; slot < 3; slot++) {
            blit(getXofScreen(112 + slot * 18),getYofScreen(68),0f,height(),20,20,256,256,guiGraphics);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        if(inventorySlot != null) {
            inventorySlot.drawForegroundLayer(guiGraphics,this,null,getGuiLeft(),getGuiTop(),mouseX,mouseY,0);
        }
        drawContent(guiGraphics,"key.categories.storage",10,59);
        drawContent(guiGraphics,"key.categories.utility",114,59);
    }

    @Override
    public ResourceLocation getBackground() {
        return BACKGROUND;
    }

    @Override
    public int height() {
        return 184;
    }

    @Override
    public int width() {
        return 176;
    }

    @Override
    public float getTextureHeight() {
        return 184f / 256f;
    }

    @Override
    public float getTextureWidth() {
        return 176f / 256f;
    }
}