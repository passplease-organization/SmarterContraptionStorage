package net.smartercontraptionstorage.Mixin.Contraption;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlock;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.smartercontraptionstorage.Interface.Gettable;
import net.smartercontraptionstorage.Render.Overlay;
import net.smartercontraptionstorage.Interface.Settable;
import net.smartercontraptionstorage.Utils;
import net.smartercontraptionstorage.SmarterContraptionStorageConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ContraptionControlsBlock.class)
public abstract class ContraptionControlBlockMixin extends ControlsBlock implements IBE<ContraptionControlsBlockEntity> {
    public ContraptionControlBlockMixin(Properties pProperties) {
        super(pProperties);
    }

    /**
     * @author passplease
     * @reason Add function of changing ordinary and show proper info of container state
     */
    @Overwrite(remap = false)
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return this.onBlockEntityUse(level, pos, (cte) -> {
            ItemStack stack = player.getMainHandItem();
            if(stack.getItem() instanceof DyeItem item){
                Overlay overlay = (Overlay)((Gettable)cte).get("overlay");
                if(overlay == null || !overlay.getColor().equals(item.getDyeColor())) {
                    ((Settable) cte).set("overlay", item);
                    if (!player.isCreative())
                        stack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }else if(stack.getItem() == Items.WATER_BUCKET) {
                ((Settable) cte).set("overlay", null);
                return InteractionResult.SUCCESS;
            }

            cte.pressButton();
            if (!level.isClientSide()) {
                boolean open;
                if(Utils.canBeControlledItem(cte.filtering.getFilter().getItem()))
                    open = !SmarterContraptionStorageConfig.getDefaultOpen(cte.disabled);
                else open = !cte.disabled;
                cte.notifyUpdate();
                cte.disabled = !cte.disabled;
                ContraptionControlsBlockEntity.sendStatus(player, cte.filtering.getFilter(), !open);
                AllSoundEvents.CONTROLLER_CLICK.play(cte.getLevel(), null, cte.getBlockPos(), 1.0F, open ? 0.8F : 1.5F);
            }
            return InteractionResult.SUCCESS;
        });
    }
}