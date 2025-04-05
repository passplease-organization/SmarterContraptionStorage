package net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.smartercontraptionstorage.SmarterContraptionStorage;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Path;
import java.util.List;

@ParametersAreNonnullByDefault
public class MovingBlockEntityScreen extends AbstractContainerScreen<MovingBlockEntityMenu> {
    private final AbstractContainerScreen<?> screen;

    public static final String TITLE_KEY = SmarterContraptionStorage.MODID + ".moving_blockentity_container";

    public MovingBlockEntityScreen(MovingBlockEntityMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, generateTitle(title));
        if(menu.getHelper().checkMenu(menu))
            screen = menu.getHelper().createScreen(menu, inventory, this.title);
        else throw new IllegalStateException("Invalid MovingBlockEntityMenu !");
    }

    public void init(Minecraft pMinecraft, int pWidth, int pHeight) {
        getScreen().init(pMinecraft, pWidth, pHeight);
        super.init(pMinecraft, pWidth, pHeight);
    }

    public static Component generateTitle(Component title) {
        if(Minecraft.getInstance().options.languageCode.equals(Language.DEFAULT))
            return Component.translatable(TITLE_KEY).append(title);
        else {
            MutableComponent moving = Component.translatable(TITLE_KEY);
            if (moving.getString().equals("Moving "))
                return title;
            else return moving.append(title);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        getScreen().render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        getMenu().getHelper().render(this,pGuiGraphics,pMouseX,pMouseY,pPartialTick);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        throw new IllegalCallerException("This method should not be called !");
    }

    public AbstractContainerScreen<?> getScreen() {
        return screen;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return getScreen().keyPressed(pKeyCode,pScanCode,pModifiers);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        return getScreen().keyReleased(pKeyCode,pScanCode,pModifiers);
    }

    @Override
    public void onClose() {
        super.onClose();
        getScreen().onClose();
    }

    @Override
    public void removed() {
        getScreen().removed();
        super.removed();
    }

    @Override
    public void renderWithTooltip(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        getScreen().renderWithTooltip(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        getMenu().getHelper().renderWithTooltip(this,pGuiGraphics,pMouseX,pMouseY,pPartialTick);
    }

    public void drawContent(GuiGraphics guiGraphics,String key,int x,int y,Object... objects) {
        guiGraphics.renderTooltip(font,Component.translatable(key,objects),x,y);
    }

    public Font getFont() {
        return font;
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        getScreen().resize(pMinecraft,pWidth,pHeight);
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return getScreen().isMouseOver(pMouseX,pMouseY);
    }

    @Override
    public void onFilesDrop(List<Path> pPacks) {
        getScreen().onFilesDrop(pPacks);
    }

    @Override
    public void afterMouseMove() {
        getScreen().afterMouseMove();
    }

    @Override
    public void afterKeyboardAction() {
        getScreen().afterKeyboardAction();
    }

    @Override
    public void afterMouseAction() {
        getScreen().afterMouseAction();
    }

    @Override
    public void handleDelayedNarration() {
        getScreen().handleDelayedNarration();
    }

    @Override
    public void triggerImmediateNarration(boolean p_169408_) {
        getScreen().triggerImmediateNarration(p_169408_);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return getMenu().getHelper().shouldClickScreen(this, pMouseX, pMouseY, pButton) && getScreen().mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return getScreen().mouseDragged(pMouseX,pMouseY,pButton,pDragX,pDragY);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        return getScreen().mouseReleased(pMouseX,pMouseY,pButton);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return getScreen().mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        getScreen().mouseMoved(pMouseX,pMouseY);
    }

    @Override
    public void clearDraggingState() {
        super.clearDraggingState();
        getScreen().clearDraggingState();
    }

    @Override
    public void tick() {
        getScreen().tick();
    }

    @Override
    public void slotClicked(Slot pSlot, int pSlotId, int pMouseButton, ClickType pType) {
        if(getMenu().getHelper().slotClicked(this,pSlot,pSlotId,pMouseButton,pType))
            super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
    }
}
