package net.smartercontraptionstorage.Mixin.Render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlock;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.smartercontraptionstorage.Render.Overlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static net.smartercontraptionstorage.Utils.*;

@Mixin(ContraptionControlsRenderer.class)
public class RenderMixin extends SmartBlockEntityRenderer<ContraptionControlsBlockEntity> {
    public RenderMixin(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(remap = false,method = "renderSafe(Lcom/simibubi/create/content/contraptions/actors/contraptionControls/ContraptionControlsBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",at = @At("RETURN"))
    protected void renderSafe(ContraptionControlsBlockEntity entity, float pt, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
        if(entity != null && entity.getLevel() != null) {
            poseStack.pushPose();
            RenderSystem.enableBlend();
            VertexConsumer builder = buffer.getBuffer(RenderType.solid());
            Color color = new Color(255, 255, 255, 128);
            final float offset = 0.0001f;
//            final float y = 14f / 16 + offset;
            Overlay texture = Overlay.get(entity.getUpdateTag(entity.getLevel().registryAccess()).getString("overlay"));
            if(texture != null) {
                TextureAtlasSprite uv = texture.getUV();
                /*
                 * The sequences of adding dots matters.
                 * It must be anti-clockwise, or it would be rendered to a triangle even not be rendered.
                 */
                rotate(poseStack, Direction.UP, calcHorizonDegrees(Direction.NORTH, entity.getBlockState().getValue(ContraptionControlsBlock.FACING).getOpposite()));
//                renderRectangle(Direction.UP, y, 0, 0, 2f / 16, 10f / 16, uv, 16, builder, poseStack, color, overlay, light);
//                renderRectangle(Direction.UP, y, 14f / 16, 0, 1f, 10f / 16, uv, 16, builder, poseStack, color, overlay, light);
//                renderRectangle(Direction.WEST, -offset, 9f / 16, 0, 10f / 16, 14f / 16, uv, 16, builder, poseStack, color, overlay, light);
//                renderRectangle(Direction.EAST, 1f + offset, 9f / 16, 0, 10f / 16, 14f / 16, uv, 16, builder, poseStack, color, overlay, light);
//                renderRectangle(Direction.SOUTH, 10f / 16 + offset, 0, 0, 1f / 16, 14f / 16, uv, 16, builder, poseStack, color, overlay, light);
//                renderRectangle(Direction.SOUTH, 10f / 16 + offset, 15f / 16, 0, 1f, 14f / 16, uv, 16, builder, poseStack, color, overlay, light);
                renderRectangle(Direction.NORTH, -offset, 0, 13f / 16, 1f, 14f / 16, uv, 16, builder, poseStack, color, overlay, light);
            }
            RenderSystem.disableBlend();
            poseStack.popPose();
        }
    }
}