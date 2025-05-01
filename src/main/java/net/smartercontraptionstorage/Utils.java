package net.smartercontraptionstorage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.logistics.vault.ItemVaultBlock;
import com.simibubi.create.content.logistics.vault.ItemVaultItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.smartercontraptionstorage.AddStorage.FluidHander.FluidHandlerHelper;
import net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.smartercontraptionstorage.Interface.FourFunction;
import net.smartercontraptionstorage.Interface.TriFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.awt.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class Utils {
    public static boolean canBeControlledItem(Item comparedItem){
        return canUseModInventory(comparedItem) ||
                canUseAsStorage(comparedItem);
    }
    public static boolean canUseAsStorage(Item comparedItem){
        return FluidHandlerHelper.canUseAsStorage(comparedItem);
    }
    public static boolean canUseModInventory(Item comparedItem){
        return canUseCreateInventory(comparedItem) ||
                StorageHandlerHelper.canControl(comparedItem);
    }
    public static boolean canUseCreateInventory(Item comparedItem){
        return comparedItem instanceof ItemVaultItem ||
                comparedItem == Items.CHEST ||
                comparedItem == Items.TRAPPED_CHEST ||
                comparedItem == Items.BARREL;
    }
    public static boolean canBeControlledBlock(Block comparedBlock){
        return canUseModInventory(comparedBlock) ||
                canUseAsStorage(comparedBlock);
    }
    public static boolean canUseAsStorage(Block comparedBlock){
        return FluidHandlerHelper.canUseAsStorage(comparedBlock);
    }
    public static boolean canUseModInventory(Block comparedBlock){
        return canUseCreateInventory(comparedBlock) ||
                StorageHandlerHelper.canControl(comparedBlock);
    }
    public static boolean canUseCreateInventory(Block comparedBlock){
        return comparedBlock instanceof ItemVaultBlock ||
                comparedBlock == Blocks.CHEST ||
                comparedBlock == Blocks.TRAPPED_CHEST ||
                comparedBlock == Blocks.BARREL;
    }
    public static BlockPos[] getAroundedBlockPos(BlockPos pos){
        BlockPos[] block = new BlockPos[6];
        block[0] = pos.above();
        block[1] = pos.south();
        block[2] = pos.east();
        block[3] = pos.north();
        block[4] = pos.west();
        block[5] = pos.below();
        return block;
    }
    /**
     * @param search_or_stop is used to check whether it should stop search Blocks or it should hang on searching other Block
     * @param setReturnValue is used to set what it should return the last time
     * @param finallyDo is used to do anything you want to do last
     * */
    public static <T> @NotNull T searchBlockPos(@NotNull BlockPos initialBlock, @NotNull BiFunction<BlockPos,BlockPos,Boolean> search_or_stop, @NotNull FourFunction<BlockPos,BlockPos,@Nullable T,@Nullable T,@Nullable T> setReturnValue, @Nullable TriFunction<HashMap<BlockPos,Boolean>,BlockPos,@Nullable T,@NotNull T> finallyDo, @Nullable T startValue){
        HashMap<BlockPos,Boolean> checkedPos = new HashMap<>();
        T returnValue = searchBlockPos(checkedPos,startValue,initialBlock,initialBlock,search_or_stop,setReturnValue);
        if(finallyDo != null)
            returnValue = finallyDo.function(checkedPos,initialBlock,returnValue);

        Objects.requireNonNull(returnValue);
        return returnValue;
    }
    private static <T> T searchBlockPos(@NotNull HashMap<BlockPos,Boolean> checkedPos,@Nullable T t, @NotNull BlockPos pos, @NotNull BlockPos initialBlock, @NotNull BiFunction<BlockPos,BlockPos,Boolean> search_or_stop, @NotNull FourFunction<BlockPos,BlockPos,@Nullable T,@Nullable T,@Nullable T> setReturnValue){
        if(checkedPos.containsKey(pos) || calcDistance(pos,initialBlock) >= SmarterContraptionStorageConfig.SEARCH_RANGE.get())
            return null;
        if(checkedPos.size() >= 300){
            addWarning("To much block pos searched !");
            return null;
        }
        if(search_or_stop.apply(pos,initialBlock) || pos == initialBlock){
            checkedPos.put(pos,true);
            for (BlockPos Pos : getAroundedBlockPos(pos))
                t = setReturnValue.function(pos,initialBlock,t,searchBlockPos(checkedPos,t,Pos,initialBlock,search_or_stop,setReturnValue));
        }else checkedPos.put(pos,false);
        return t;
    }
    public static double calcDistance(BlockPos pos1,BlockPos pos2){
        double X = Math.abs(pos1.getX() - pos2.getX());
        double Y = Math.abs(pos1.getY() - pos2.getY());
        double Z = Math.abs(pos1.getZ() - pos2.getZ());
        return Math.max(X,Math.max(Y,Z));
    }
    public static @Nullable ArrayList<FluidStack> getFluidByItem(ItemStack can){
        IFluidHandler fluidHandler = can.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if(fluidHandler == null)
            return null;
        ArrayList<FluidStack> list = new ArrayList<>();
        for(int i = fluidHandler.getTanks() - 1;i >= 0;i--)
            list.add(fluidHandler.getFluidInTank(i));
        return list;
    }
    public static void forEachTankDo(ICapabilityProvider can, Consumer<IFluidHandler> consumer){
        can.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve().ifPresent(consumer);
    }
    public static final Logger LOGGER = LogManager.getLogger();
    public static void addWarning(String text){
        LOGGER.warn(text);
    }
    public static void addError(String text){
        LOGGER.error(text);
    }
    public static boolean isSameItem(ItemStack stack1,ItemStack stack2){
        return ItemStack.isSameItemSameTags(stack1, stack2);
    }
    public static boolean isItemEmpty(ItemStack stack){
        return stack.isEmpty() || stack.getItem() == Items.AIR;
    }
    public static ResourceLocation asResources(String name){
        return ResourceLocation.fromNamespaceAndPath(SmarterContraptionStorage.MODID,name);
    }
    public static void renderInto(VertexConsumer builder, Matrix4f matrix, float x, float y, float z,Color color, TextureAtlasSprite uv, double u, double v, int overlay, int light, float normal_1, float normal_2, float normal_3){
        builder.vertex(matrix,x,y,z)
                .color(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha())
                .uv(uv.getU(u),uv.getV(v))
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal_1, normal_2, normal_3)
                .endVertex();
    }
    /**
     * @param u (based on 16 pixels) to locate the texture in UV map
     * @param v (based on 16 pixels) to locate the texture in UV map
     * @param UVSize the size of UV map (must be the power of 2)
     */
    public static void renderInto(VertexConsumer builder, Matrix4f matrix4f,float x,float y,float z,Color color,TextureAtlasSprite uv,float u, float v,int UVSize, int overlay, int light, float normal_1, float normal_2,float normal_3){
        if(UVSize == 16)
            renderInto(builder,matrix4f,x,y,z,color,uv,u,v,overlay,light,normal_1,normal_2,normal_3);
        else {
            assert UVSize >= 32 && (UVSize & (UVSize - 1)) == 0;// ensure UVSize is powers of 2
            renderInto(builder, matrix4f, x, y, z, color, uv, (double) u * 16 / UVSize, (double) v * 16 / UVSize, overlay, light, normal_1, normal_2, normal_3);
        }
    }
    /**
     * @param x1 to locate the pixels those needed be rendered
     * @param z1 to locate the pixels those needed be rendered
     * @param x2 to locate the pixels those needed be rendered
     * @param z2 to locate the pixels those needed be rendered<br>
     *  The rendered area is determined by parameters above, from (x1,z1) to (x2,z2)<br>
     *  x1,x2 is the pixel's horizontal coordinate<br>
     *  z1,z2 is the pixel's vertical coordinate<br>
     *  lower left corner is the origin of coordinate system, positive to the right and upwards.<br>
     */
    public static void renderRectangle(Direction facing, float axis, float x1, float z1, float x2, float z2, TextureAtlasSprite uv, int UVSize, VertexConsumer builder, PoseStack poseStack, Color color, int overlay, int light){
        assert x1 <= x2 && z1 <= z2;
        Matrix4f matrix4f = poseStack.last().pose();
        switch (facing){
            case NORTH->{
                renderInto(builder,matrix4f,x1,z1,axis,color,uv,x1,z1,UVSize,overlay,light,0,0,1);
                renderInto(builder,matrix4f,x1,z2,axis,color,uv,x1,z2,UVSize,overlay,light,0,0,1);
                renderInto(builder,matrix4f,x2,z2,axis,color,uv,x2,z2,UVSize,overlay,light,0,0,1);
                renderInto(builder,matrix4f,x2,z1,axis,color,uv,x2,z1,UVSize,overlay,light,0,0,1);
            }
            case SOUTH->{
                renderInto(builder,matrix4f,x1,z1,axis,color,uv,x1,z1,UVSize,overlay,light,0,0,-1);
                renderInto(builder,matrix4f,x2,z1,axis,color,uv,x2,z1,UVSize,overlay,light,0,0,-1);
                renderInto(builder,matrix4f,x2,z2,axis,color,uv,x2,z2,UVSize,overlay,light,0,0,-1);
                renderInto(builder,matrix4f,x1,z2,axis,color,uv,x1,z2,UVSize,overlay,light,0,0,-1);
            }
            case EAST->{
                renderInto(builder,matrix4f,axis,z1,x1,color,uv,z1,x1,UVSize,overlay,light,-1,0,0);
                renderInto(builder,matrix4f,axis,z2,x1,color,uv,z2,x1,UVSize,overlay,light,-1,0,0);
                renderInto(builder,matrix4f,axis,z2,x2,color,uv,z2,x2,UVSize,overlay,light,-1,0,0);
                renderInto(builder,matrix4f,axis,z1,x2,color,uv,z1,x2,UVSize,overlay,light,-1,0,0);
            }
            case WEST->{
                renderInto(builder,matrix4f,axis,z1,x1,color,uv,z1,x1,UVSize,overlay,light,1,0,0);
                renderInto(builder,matrix4f,axis,z1,x2,color,uv,z1,x2,UVSize,overlay,light,1,0,0);
                renderInto(builder,matrix4f,axis,z2,x2,color,uv,z2,x2,UVSize,overlay,light,1,0,0);
                renderInto(builder,matrix4f,axis,z2,x1,color,uv,z2,x1,UVSize,overlay,light,1,0,0);
            }
            case UP->{
                renderInto(builder,matrix4f,x1,axis,z1,color,uv,x1,z1,UVSize,overlay,light,0,1,0);
                renderInto(builder,matrix4f,x1,axis,z2,color,uv,x1,z2,UVSize,overlay,light,0,1,0);
                renderInto(builder,matrix4f,x2,axis,z2,color,uv,x2,z2,UVSize,overlay,light,0,1,0);
                renderInto(builder,matrix4f,x2,axis,z1,color,uv,x2,z1,UVSize,overlay,light,0,1,0);
            }
            case DOWN->{
                renderInto(builder,matrix4f,x1,axis,z1,color,uv,x1,z1,UVSize,overlay,light,0,-1,0);
                renderInto(builder,matrix4f,x1,axis,z2,color,uv,x1,z2,UVSize,overlay,light,0,-1,0);
                renderInto(builder,matrix4f,x2,axis,z2,color,uv,x2,z2,UVSize,overlay,light,0,-1,0);
                renderInto(builder,matrix4f,x2,axis,z1,color,uv,x2,z1,UVSize,overlay,light,0,-1,0);
            }
            default -> {
                addError("Unable to get CCW facing of " + facing);
                throw new IllegalStateException("Unable to get CCW facing of " + facing);
            }
        }
    }
    public static void rotate(PoseStack poseStack, Direction direction,float degrees){
        poseStack.translate(0.5f,0.5f,0.5f);
        poseStack.mulPose(new Quaternionf().fromAxisAngleDeg(direction.step(),degrees));
        poseStack.translate(-0.5f,-0.5f,-0.5f);
    }
    public static int calcHorizonDegrees(Direction tragetDirection,Direction facing){
        int degrees = 0;
        while(!facing.equals(tragetDirection)){
            facing = facing.getCounterClockWise().getOpposite();
            degrees += 90;
        }
        return degrees;
    }
    public static void sendMessage(ServerPlayer player, String text){
        player.sendSystemMessage(MutableComponent.create(new LiteralContents(text)));
    }
    public static void sendMessage(ServerPlayer player,MutableComponent component){
        player.sendSystemMessage(component);
    }
    public static void sendMessage(String pKey,ServerPlayer player, Object... pArgs){
        player.sendSystemMessage(Component.translatable(SmarterContraptionStorage.MODID + '.' + pKey,pArgs));
    }
}