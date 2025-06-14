package net.smartercontraptionstorage.Ponder;

import appeng.block.networking.ControllerBlock;
import appeng.blockentity.networking.ControllerBlockEntity;
import appeng.core.definitions.AEItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.drill.DrillBlockEntity;
import com.simibubi.create.foundation.ponder.*;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.block.Blocks;

import java.util.Objects;

import static com.simibubi.create.content.contraptions.mounted.CartAssemblerBlock.POWERED;
import static net.minecraft.core.Direction.*;

public class AEScenes {
    public static void useAE(SceneBuilder builder, SceneBuildingUtil util){
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("use_ae","How to use AE on contraption");
        scene.world().showSection(util.select().fromTo(0,0,0,5,0,5),UP);
        scene.overlay().showText(50).independent(70).text("First, let's build SpatialPylonBlockEntityMixin small AE net");
        BlockPos importBus = util.grid().at(2,1,3);
        BlockPos exportBus = util.grid().at(3,1,3);
        BlockPos drill = util.grid().at(1,1,3);
        Selection shaft = util.select().fromTo(2,3,3,5,3,3).add(util.select().fromTo(6,-2,3,6,3,3));
        BlockPos stone = util.grid().at(0,1,3);
        scene.idle(15);
        ElementLink<WorldSectionElement> contraption = scene.world().showIndependentSection(util.select().position(2,1,2),DOWN);
        scene.idle(5);
        scene.world().showSectionAndMerge(util.select().position(2,1,1),DOWN,contraption);
        scene.idle(5);
        scene.world().showSectionAndMerge(util.select().position(3,1,2),DOWN,contraption);
        scene.idle(5);
        scene.world().showSectionAndMerge(util.select().fromTo(exportBus,importBus),UP,contraption);
        scene.idle(25);
        scene.overlay().showText(40).placeNearTarget().pointAt(util.vector().topOf(exportBus)).text("Export Bus, with covered_cable");
        scene.overlay().showText(40).placeNearTarget().pointAt(util.vector().topOf(importBus)).text("Import Bus, with covered_cable");
        scene.idle(5);
        scene.world().modifyBlockEntity(util.grid().at(2,1,2), ControllerBlockEntity.class,entity -> Objects.requireNonNull(entity.getLevel()).setBlockAndUpdate(entity.getBlockPos(), entity.getBlockState().setValue(ControllerBlock.CONTROLLER_STATE, ControllerBlock.ControllerBlockState.online)));
        scene.idle(45);
        scene.overlay().showText(100).pointAt(util.vector().topOf(importBus)).placeNearTarget().attachKeyFrame().text("These bus is important for contraptions to locate the proper AE Net of the world. And due to the input and output AE Net can be different, so you should set them all");
        scene.idle(110);
        scene.overlay().showControls(util.vector().topOf(importBus), Pointing.DOWN,50).withItem(AEItems.WIRELESS_CRAFTING_TERMINAL.stack());
        scene.overlay().showControls(util.vector().topOf(exportBus), Pointing.DOWN,50).withItem(AEItems.WIRELESS_CRAFTING_TERMINAL.stack());
        scene.overlay().showText(100).pointAt(util.vector().topOf(importBus)).placeNearTarget().text("Buses also need setup, they should be all filled with Speed Card Upgrade and Wireless Crafting Terminal (have connected to AE Net)");
        scene.idle(110);
        scene.overlay().showOutline(PonderPalette.GREEN,AllItems.SUPER_GLUE,util.select().fromTo(2,1,1,3,1,3),50);
        scene.overlay().showText(100).placeNearTarget().pointAt(util.vector().centerOf(3,1,2)).text("Adding energy, controller (due to Access Point cannot be moved, so it may be wired) so that your contraption can connect to AE Net");
        scene.idle(110);
        scene.addKeyframe();
        scene.world().showSectionAndMerge(util.select().position(drill),UP,contraption);
        scene.world().showSection(util.select().position(stone),UP);
        scene.idle(5);
        scene.world().showSection(shaft,DOWN);
        scene.world().showSectionAndMerge(util.select().position(3,2,3),DOWN,contraption);
        scene.idle(10);
        scene.world().modifyBlockEntity(drill, DrillBlockEntity.class,entity -> entity.setSpeed(32F));
        scene.world().moveSection(contraption,util.vector().of(-0.2,0,0),4);
        scene.idle(4);
        for (int i = 0; i < 10; i++) {
            scene.idle(3);
            scene.world().incrementBlockBreakingProgress(stone);
        }
        scene.world().moveSection(contraption,util.vector().of(-0.8,0,0),16);
        scene.overlay().showText(136).placeNearTarget().pointAt(util.vector().topOf(stone)).text("Items will be stored in the AE Net which is connecting to Wireless Crafting Terminal in in Export Bus. And I have to point out, each action that contraption try to insert or extract items consumes energy of AE net");
        scene.idle(16);
        scene.world().modifyBlockEntity(drill, DrillBlockEntity.class,entity -> entity.setSpeed(0F));
        scene.idle(120);
        scene.overlay().showText(80).pointAt(util.vector().topOf(2,1,2)).text("One more thing, ME Interface can set the white filter for extracting AE Net (filled with Fuzzy Card), so that it costs less computer source (I really recommend to set that)");
        scene.idle(80);
    }
    public static void spatialCell(SceneBuilder builder, SceneBuildingUtil util){
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("spatial_cell","Let your machines \"work\" on contraptions");
        scene.configureBasePlate(0,0,7);
        scene.scaleSceneView(0.5F);
        scene.showBasePlate();
        BlockPos IO_Port = util.grid().at(4,1,4);
        var contraption = scene.world().showIndependentSection(util.select().fromTo(4,1,4,1,1,4).add(util.select().fromTo(4,2,4,4,7,4)).add(util.select().fromTo(4,7,4,4,7,0)),UP);
        var textPlace = util.vector().topOf(IO_Port);
        scene.idle(5);
        Selection machines = util.select().fromTo(3,2,3,1,6,1);
        scene.world().showSection(machines,DOWN);
        scene.idle(15);
        scene.overlay().showText(40).placeNearTarget().pointAt(textPlace).attachKeyFrame().text("First, let's build a spatial cell");
        scene.idle(20);
        scene.world().setBlocks(machines, Blocks.AIR.defaultBlockState(),false);
        scene.idle(25);
        scene.overlay().showText(60).placeNearTarget().pointAt(textPlace).text("Most importantly, don't take the Storage Cell out, because contraption only check the output slot of IO Port. You should move IO Port and other structure to contraption by block.");
        scene.idle(35);
        scene.world().moveSection(contraption,util.vector().of(0,1,0),25);
        scene.idle(10);
        scene.world().showSectionAndMerge(util.select().fromTo(IO_Port.below(),IO_Port.below(2)),UP,contraption);
        scene.idle(20);
        scene.special().createCart(util.vector().topOf(IO_Port.below()),0F, Minecart::new);
        scene.world().setBlock(IO_Port.below(), AllBlocks.CART_ASSEMBLER.getDefaultState(), false);
        scene.idle(10);
        scene.addKeyframe();
        scene.overlay().showControls(util.vector().centerOf(1,2,4),Pointing.LEFT,40).withItem(AllItems.SUPER_GLUE.asStack());
        scene.overlay().showOutline(PonderPalette.GREEN,AllItems.SUPER_GLUE,util.select().fromTo(4,8,0,1,2,4),40);
        scene.idle(25);
        scene.world().showSectionAndMerge(util.select().position(0,1,4),DOWN,contraption);
        scene.effects().superGlue(util.grid().at(0,1,4),EAST,false);
        scene.idle(20);
        scene.world().setBlock(IO_Port.below(),AllBlocks.CART_ASSEMBLER.getDefaultState().cycle(POWERED),false);
        scene.overlay().showText(90).pointAt(textPlace).placeNearTarget().attachKeyFrame().text("Your contraption must have the whole structure of Spatial IO Port and ensure it's powered (when contraption assembles), or else it won't work.Also, you cannot use AE Net at the same contraption with IO Port");
        scene.idle(95);
        scene.overlay().showText(120).pointAt(textPlace).placeNearTarget().text("You can put some machines in the Spatial Cell, and the Chunks will be loaded until you destroy this contraption. But, I highly recommend you check block status in the cell and ensure they could work normally, because unloaded packaged block most likely have a wrong block status and looks like unticked");
        scene.idle(125);
        scene.overlay().showText(70).pointAt(textPlace).placeNearTarget().text("You can use Chest, Trapped Chest and Barrel as the output interface of cell and Vault as the input interface of cell");
        scene.idle(75);
        scene.overlay().showText(90).pointAt(textPlace).placeNearTarget().text("By this way, you can add some product line on your contraption for replenishing stuffs to your contraption or use other mods' container indirectly");
        scene.idle(95);
    }
}