package net.smartercontraptionstorage.AddStorage.GUI.NormalMenu;

import com.supermartijn642.core.gui.widget.MutableWidgetRenderContext;
import com.supermartijn642.trashcans.screen.WhitelistButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.smartercontraptionstorage.SmarterContraptionStorage;

public class MovingTrashCanScreen extends AbstractMovingScreen<MovingTrashCanMenu> {
    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(SmarterContraptionStorage.TrashCans,"textures/item_screen.png");

    public static final int BUTTON_X_IN_SCREEN = 175;

    public static final int BUTTON_Y_IN_SCREEN = MovingTrashCanMenu.height - 118;

    private static final MutableWidgetRenderContext widgetRender = MutableWidgetRenderContext.create();

    public WhitelistButton BUTTON;

    public MovingTrashCanScreen(MovingTrashCanMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        setTitleCenter(titleLabelY);
        inventoryLabelX = 21;
        inventoryLabelY = 86;
        BUTTON = new WhitelistButton(BUTTON_X_IN_SCREEN + leftPos,BUTTON_Y_IN_SCREEN + topPos,this::buttonClicked);
        BUTTON.update(menu.getToolboxNumber() == 0 && menu.whiteOrBlack());
        BUTTON.setActive(true);
    }

    @Override
    protected void renderScreen(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        BUTTON.setFocused(checkButtonFocused(mouseX,mouseY));
        BUTTON.render(getWidgetRender(guiGraphics, partialTicks), mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        if(menu.getToolboxNumber() == 0) {
            MutableComponent text = Component.translatable("trashcans.gui.liquid_trash_can.filter");
            if(!menu.getHandler().toolboxItem.isEmpty())
                text.append(Component.translatable("smartercontraptionstorage.moving_container.trashcans.scrolling"));
            guiGraphics.drawString(font,text.getString(),8f,52f,4210752,false);
        } else drawContent(guiGraphics,"smartercontraptionstorage.moving_container.trashcans.toolbox",8f,52f,menu.getToolboxNumber());
    }

    protected boolean checkButtonFocused(double mouseX, double mouseY) {
        return mouseX >= BUTTON.left() && mouseX <= BUTTON.left() + BUTTON.width() && mouseY >= BUTTON.top() && mouseY <= BUTTON.top() + BUTTON.height();
    }

    @Override
    public ResourceLocation getBackground() {
        return BACKGROUND;
    }

    @Override
    public int height() {
        return MovingTrashCanMenu.height;
    }

    @Override
    public int width() {
        return MovingTrashCanMenu.width;
    }

    public void buttonClicked(){
        if(this.menu.clickMenuButton(getPlayer(),0))
            handleButtonClick(0);
        BUTTON.update(menu.whiteOrBlack());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(menu.getToolboxNumber() == 0 && checkButtonFocused(mouseX, mouseY)) {
            BUTTON.mousePressed((int) mouseX, (int) mouseY, button, false);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(!super.mouseScrolled(mouseX, mouseY, scrollX,scrollY)){
            if(scrollY < 0.0 && menu.clickMenuButton(getPlayer(),1)) {
                handleButtonClick(1);
                BUTTON.update(false);
            } else if(menu.clickMenuButton(getPlayer(),-1)) {
                handleButtonClick(-1);
                if(menu.getToolboxNumber() == 0)
                    BUTTON.update(menu.whiteOrBlack());
                else BUTTON.update(false);
            }
        }
        return true;
    }

    public static MutableWidgetRenderContext getWidgetRender(GuiGraphics guiGraphics, float partialTicks){
        widgetRender.update(guiGraphics, partialTicks);
        return widgetRender;
    }
}