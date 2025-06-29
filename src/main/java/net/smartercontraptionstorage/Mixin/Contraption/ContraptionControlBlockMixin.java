package net.smartercontraptionstorage.Mixin.Contraption;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlock;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.smartercontraptionstorage.Interface.Gettable;
import net.smartercontraptionstorage.Render.Overlay;
import net.smartercontraptionstorage.Interface.Settable;
import net.smartercontraptionstorage.Utils;
import net.smartercontraptionstorage.SmarterContraptionStorageConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContraptionControlsBlock.class)
public abstract class ContraptionControlBlockMixin extends ControlsBlock implements IBE<ContraptionControlsBlockEntity> {
    public ContraptionControlBlockMixin(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    @Inject(method = "lambda$use$0",at = @At("HEAD"),remap = false, cancellable = true)
    private static void use(Level level, Player player, ContraptionControlsBlockEntity cte, CallbackInfoReturnable<InteractionResult> cir){
        ItemStack stack = player.getMainHandItem();
        if(stack.getItem() instanceof DyeItem item){
            Overlay overlay = (Overlay)((Gettable)cte).get("overlay");
            if(overlay == null || !overlay.getColor().equals(item.getDyeColor())) {
                ((Settable) cte).set("overlay", item);
                if (!player.isCreative())
                    stack.shrink(1);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }else if(stack.getItem() == Items.WATER_BUCKET) {
            ((Settable) cte).set("overlay", null);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
    // My @Inject method, can not work in HMCL, but can work in IDEA.
    // I think I need to fill refmap file, but I do not know how to fill it.

//    @Inject(method = "use",at = @At("HEAD"),cancellable = true)
//    public void use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir){
//        cir.setReturnValue(onBlockEntityUse(level, pos, (cte) -> {
//            cte.pressButton();
//            if (!level.isClientSide()) {
//                ContraptionControlsBlockEntity entity = GetBlockEntity(level,pos);
//                boolean open;
//                if(Utils.canBeControlledBlock(entity.filtering.getFilter().getItem()))
//                    open = !MoreContraptionStorageConfig.getDefaultOpen(entity.disabled);
//                else open = !entity.disabled;
//                cte.notifyUpdate();
//                cte.disabled = !cte.disabled;
//                ContraptionControlsBlockEntity.sendStatus(player, cte.filtering.getFilter(), !open);
//                AllSoundEvents.CONTROLLER_CLICK.play(cte.getLevel(), (Player) null, cte.getBlockPos(), 1.0F, open ? 0.8F : 1.5F);
//            }
//            return InteractionResult.SUCCESS;
//        }));
//    }
}