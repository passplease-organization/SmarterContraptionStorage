package net.smartercontraptionstorage.AddStorage.GUI.NormalMenu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public abstract class AbstractMovingScreen<T extends AbstractMovingMenu<?>> extends AbstractContainerScreen<T>{
    @Nullable private ResourceLocation bindTexture;

    public AbstractMovingScreen(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        imageWidth = width();
        imageHeight = height();
        super.init();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, getBackground());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(getBackground(),guiGraphics,leftPos,topPos,width(),height(),getTextureLeft(),getTextureTop(),getTextureWidth(),getTextureHeight());
        bindTexture(bindTexture);
        renderScreen(guiGraphics, partialTick, mouseX, mouseY);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    protected abstract void renderScreen(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY);

    public void drawTexture(ResourceLocation texture,GuiGraphics guiGraphics, int left, int top, int width, int height, float textureLeft, float textureTop, float textureWidth, float textureHeight) {
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        bindTexture(texture);
//        Matrix4f matrix = guiGraphics.pose().last().pose();
//        Tesselator tesselator = Tesselator.getInstance();
//        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
//        buffer.addVertex(matrix, left, bottom, 0.0F).setUv(textureLeft, textureBottom);
//        buffer.addVertex(matrix, right, bottom, 0.0F).setUv(textureRight, textureBottom);
//        buffer.addVertex(matrix, right, top, 0.0F).setUv(textureRight, textureTop);
//        buffer.addVertex(matrix, left, top, 0.0F).setUv(textureLeft, textureTop);
//        buffer.buildOrThrow();
        guiGraphics.blit(texture,left,top,width,height,textureLeft,textureTop,(int)(textureWidth * 256),(int)(textureHeight * 256),256,256);
    }

    public void bindTexture(@Nullable ResourceLocation location) {
        if (location != null)
            RenderSystem.setShaderTexture(0, location);
        bindTexture = location;
    }

    public void blit(int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight,GuiGraphics guiGraphics){
        if(bindTexture != null)
            guiGraphics.blit(bindTexture,x,y,width,height,u,v,width,height,textureWidth,textureHeight);
    }

    public abstract ResourceLocation getBackground();

    public void setTitleCenter(int y) {
        titleLabelX = getCenterX() - font.width(getTitle()) / 2;
        titleLabelY = y;
    }

    public void drawContent(GuiGraphics guiGraphics,String key,float x,float y,Object... objects) {
        guiGraphics.drawString(font,Component.translatable(key,objects).getString(),x,y,4210752,false);
    }

    public int getCenterX(){
        return width() / 2;
    }

    public abstract int height();

    public abstract int width();

    public float getTextureLeft(){
        return 0f;
    }

    public float getTextureTop(){
        return 0f;
    }

    public float getTextureWidth(){
        return 1f;
    }

    public float getTextureHeight(){
        return 1f;
    }

    public int getXofScreen(int x){
        return x + leftPos;
    }

    public int getYofScreen(int y){
        return y + topPos;
    }

    public float getXofScreen(float x){
        return x + leftPos;
    }

    public float getYofScreen(float y){
        return y + topPos;
    }

    public void handleButtonClick(int id){
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId,id);
    }

    public Player getPlayer(){
        return this.minecraft.player;
    }
}