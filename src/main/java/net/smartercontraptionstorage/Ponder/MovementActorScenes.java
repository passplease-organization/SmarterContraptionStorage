package net.smartercontraptionstorage.Ponder;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.foundation.ponder.*;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.smartercontraptionstorage.Render.Overlay;

public class MovementActorScenes {
    public static void controlStorageBlock(SceneBuilder builder, SceneBuildingUtil util){
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("storage_control","Control storage on contraptions");
        scene.configureBasePlate(0,0,5);
        scene.scaleSceneView(0.9F);
        scene.showBasePlate();
        scene.idle(5);
        BlockPos shaft = util.grid().at(3,1,3);
        BlockPos gearshift = util.grid().at(3,1,4);
        Selection kinetics = util.select().fromTo(3,1,2,3,1,4);
        BlockPos[] log = {util.grid().at(1,1,2),util.grid().at(1,1,3)};
        BlockPos lever = util.grid().at(3,2,4);
        Selection cogwheel = util.select().fromTo(3,1,5,4,0,5);
        BlockPos[] saw = {util.grid().at(2,1,1),util.grid().at(2,1,2)};
        BlockPos chest = util.grid().at(2,1,0);
        BlockPos control = util.grid().at(3,1,0);
        scene.world().modifyKineticSpeed(util.select().position(gearshift).add(cogwheel),(speed) -> speed);
        scene.idle(5);
        scene.world().showSection(cogwheel,Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().position(gearshift),Direction.SOUTH);
        scene.world().showSection(util.select().position(lever),Direction.SOUTH);
        scene.idle(5);
        scene.world().showSection(util.select().position(shaft),Direction.SOUTH);
        scene.idle(5);
        ElementLink<WorldSectionElement> contraption = scene.world().showIndependentSection(util.select().position(3,1,1),Direction.DOWN);
        scene.world().moveSection(contraption,util.vector().of(0,0,1),0);
        scene.world().showSection(util.select().position(3,1,2),Direction.DOWN);
        scene.idle(5);
        scene.world().showSectionAndMerge(util.select().position(4,1,1),Direction.DOWN,contraption);
        scene.idle(5);
        scene.world().showSectionAndMerge(util.select().position(5,1,1),Direction.DOWN,contraption);
        scene.world().showSectionAndMerge(util.select().fromTo(chest,util.grid().at(2,1,2)).add(util.select().position(control)),Direction.DOWN,contraption);
        scene.world().showSection(util.select().fromTo(log[0],log[1]),Direction.UP);
        scene.idle(20);
        scene.addKeyframe();
        control = control.south();
        chest = chest.south();
        scene.overlay().showOutline(PonderPalette.GREEN,"glue",util.select().fromTo(2,1,1,2,1,3).add(util.select().position(control)),25);
        scene.overlay().showControls(util.vector().centerOf(control), Pointing.RIGHT,30).withItem(AllItems.SUPER_GLUE.asStack());
        scene.idle(40);
        scene.effects().indicateRedstone(lever);
        scene.world().toggleRedstonePower(util.select().fromTo(lever,gearshift));
        scene.world().modifyKineticSpeed(kinetics,(speed) -> -speed);
        scene.world().moveSection(contraption,util.vector().of(-0.1,0,0),2);
        scene.world().modifyBlockEntity(saw[0], SawBlockEntity.class,(block) -> block.setSpeed(32));
        scene.world().modifyBlockEntity(saw[1], SawBlockEntity.class,(block) -> block.setSpeed(32));
        scene.effects().rotationDirectionIndicator(shaft);
        scene.idle(2);
        for (int i = 0; i < 10; i++) {
            scene.idle(3);
            scene.world().incrementBlockBreakingProgress(log[0]);
            scene.world().incrementBlockBreakingProgress(log[1]);
        }
        scene.world().moveSection(contraption,util.vector().of(-1.9,0,0),38);
        scene.idle(38);
        scene.world().modifyBlockEntity(saw[0], SawBlockEntity.class,(block) -> block.setSpeed(0));
        scene.world().modifyBlockEntity(saw[1], SawBlockEntity.class,(block) -> block.setSpeed(0));
        scene.overlay().showText(70).pointAt(util.vector().topOf(chest.west(2)).add(0,-0.15,0)).placeNearTarget().text("Items will be stored in contraption's container automatically");
        scene.idle(5);
        scene.overlay().showControls(util.vector().topOf(chest.west(2)),Pointing.DOWN,45).withItem(Items.DARK_OAK_LOG.getDefaultInstance());
        scene.idle(80);
        scene.effects().indicateRedstone(lever);
        scene.world().toggleRedstonePower(util.select().fromTo(lever,gearshift));
        scene.world().modifyKineticSpeed(kinetics,(speed) -> -speed);
        scene.effects().rotationDirectionIndicator(shaft);
        scene.world().moveSection(contraption,util.vector().of(2,0,0),40);
        scene.idle(50);
        scene.overlay().showText(70).pointAt(util.vector().topOf(control).add(0,-0.25,0)).placeNearTarget().text("Using this mod, you could control every container behaviour on contraption. Here let's use drawer right click controls block to set its value");
        scene.addKeyframe();
        scene.idle(70);
        scene.overlay().showControls(util.vector().topOf(control),Pointing.DOWN,40).rightClick().withItem(Items.CHEST.getDefaultInstance());
        scene.idle(30);
        scene.world().setFilterData(util.select().position(control.north()), ContraptionControlsBlockEntity.class,Items.CHEST.getDefaultInstance());
        scene.overlay().showText(80).pointAt(util.vector().topOf(control)).placeNearTarget().text("And now, this block will control our chest");
        scene.idle(40);
        scene.overlay().showControls(util.vector().centerOf(control),Pointing.UP,20).rightClick();
        scene.idle(25);
        scene.world().modifyBlockEntity(control.north(),ContraptionControlsBlockEntity.class,(entity) -> entity.disabled = true);
        for (int i = log.length - 1; i >= 0; i--) {
            log[i] = log[i].west();
            scene.world().showSection(util.select().position(log[i]),Direction.DOWN);
        }
        scene.idle(35);
        scene.overlay().showText(70).placeNearTarget().pointAt(util.vector().topOf(control).add(0,-0.25,0)).independent(60).text("If we release piston now,the chest won't be added to the contraption. So these logs will drop on the ground");
        scene.idle(80);
        scene.effects().indicateRedstone(lever);
        scene.world().toggleRedstonePower(util.select().fromTo(lever,gearshift));
        scene.world().modifyKineticSpeed(kinetics,(speed) -> -speed);
        scene.world().moveSection(contraption,util.vector().of(-1.1,0,0),22);
        scene.world().modifyBlockEntity(saw[0], SawBlockEntity.class,(block) -> block.setSpeed(32));
        scene.world().modifyBlockEntity(saw[1], SawBlockEntity.class,(block) -> block.setSpeed(32));
        scene.effects().rotationDirectionIndicator(shaft);
        scene.idle(22);
        for (int i = 0; i < 10; i++) {
            scene.idle(3);
            scene.world().incrementBlockBreakingProgress(log[0]);
            scene.world().incrementBlockBreakingProgress(log[1]);
        }
        scene.world().createItemEntity(util.vector().centerOf(log[0]),util.vector().of(-0.1,0,0),Items.DARK_OAK_LOG.getDefaultInstance());
        scene.world().createItemEntity(util.vector().centerOf(log[1]),util.vector().of(-0.1,0,0),Items.DARK_OAK_LOG.getDefaultInstance());
        scene.overlay().showText(25).placeNearTarget().independent(50).text("It works as we predicate...");
        scene.world().moveSection(contraption,util.vector().of(-0.9,0,0),18);
        scene.idle(25);
        scene.world().modifyBlockEntity(saw[0], SawBlockEntity.class,(block) -> block.setSpeed(0));
        scene.world().modifyBlockEntity(saw[1], SawBlockEntity.class,(block) -> block.setSpeed(0));
        scene.addKeyframe();
        scene.idle(15);
        scene.overlay().showText(120).placeNearTarget().independent(50).text("Some other things, any actors on contraption can use these containers. You can change weather close them until controls overlays opened by using config file");
    }
    public static void changeOrdinary(SceneBuilder builder, SceneBuildingUtil util){
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("ordinary_control","Change the ordinary of containers and actors");
        scene.idle(5);
        scene.configureBasePlate(0,0,9);
        scene.scaleSceneView(0.9F);
        scene.showBasePlate();
        scene.idle(10);
        final BlockPos bearing = util.grid().at(4,1,4);
        final BlockPos deployer = util.grid().at(2,1,4);
        final BlockPos[] saplings = new BlockPos[]{util.grid().at(0,1,4),util.grid().at(4,1,0),util.grid().at(8,1,4),util.grid().at(4,1,8)};
        final BlockState oakSapling = Blocks.OAK_SAPLING.defaultBlockState();
        final Selection speedMatters = util.select().position(deployer).add(util.select().position(0,1,5));
        Selection tree = util.select().fromTo(0,1,4,0,2,4).add(util.select().fromTo(-2,3,2,2,6,6));
        final Selection contraption = util.select().fromTo(0,1,5,3,1,5).add(util.select().fromTo(3,1,4,2,1,4)).add(util.select().fromTo(4,2,4,3,2,4));
        final BlockPos controller = util.grid().at(0,2,5);
        scene.world().showSection(util.select().position(bearing),Direction.DOWN);
        scene.idle(5);
        scene.world().modifyBlockEntityNBT(util.select().position(deployer), DeployerBlockEntity.class,tag -> tag.put("HeldItem",Items.OAK_SAPLING.getDefaultInstance().serializeNBT()));
        ElementLink<WorldSectionElement> contraptionLink = scene.world().showIndependentSection(contraption,Direction.DOWN);
        scene.overlay().showText(60).placeNearTarget().independent(60).text("When you use contraption to plant trees, you may want to change the working order of deployer and saw");
        scene.idle(65);
        scene.world().configureCenterOfRotation(contraptionLink,util.vector().centerOf(bearing));
        scene.world().rotateBearing(bearing,-360f,120);
        scene.world().rotateSection(contraptionLink,0,-360f,0,120);
        scene.world().setFilterData(util.select().position(deployer), DeployerBlockEntity.class,Items.OAK_SAPLING.getDefaultInstance());
        scene.world().setKineticSpeed(speedMatters,32f);
        scene.world().moveDeployer(deployer,1f,0);
        scene.world().setBlock(saplings[0],oakSapling,false);
        scene.world().showIndependentSectionImmediately(util.select().position(saplings[0]));
        scene.world().moveDeployer(deployer,-1f,10);
        scene.idle(20);
        for (int i = 1; i < 4; i++){
            scene.world().moveDeployer(deployer, 1f, 10);
            scene.idle(10);
            scene.world().setBlock(saplings[i], oakSapling, false);
            scene.world().showIndependentSectionImmediately(util.select().position(saplings[i]));
            scene.world().moveDeployer(deployer, -1f, 10);
            if(i == 3) {
                scene.world().modifyBlockEntityNBT(util.select().position(deployer), DeployerBlockEntity.class, tag -> tag.put("HeldItem", ItemStack.EMPTY.serializeNBT()), true);
                scene.addLazyKeyframe();
            }
            scene.idle(20);
        }
        scene.idle(10);
        scene.world().setKineticSpeed(speedMatters,0f);
        scene.idle(5);
        scene.world().setBlock(saplings[0],Blocks.OAK_LOG.defaultBlockState(),false);
        scene.world().showIndependentSectionImmediately(tree);
        scene.idle(5);
        scene.world().setKineticSpeed(speedMatters,32f);
        for (int i = 0; i < 10; i++) {
            scene.idle(3);
            scene.world().incrementBlockBreakingProgress(saplings[0]);
        }
        scene.world().modifyBlockEntityNBT(util.select().position(deployer), DeployerBlockEntity.class,tag -> tag.put("HeldItem",Items.OAK_SAPLING.getDefaultInstance().serializeNBT()));
        scene.world().setBlocks(tree,Blocks.AIR.defaultBlockState(),true);
        scene.world().rotateBearing(bearing,-90f,120);
        scene.world().rotateSection(contraptionLink,0,-90f,0,120);
        scene.world().setKineticSpeed(speedMatters,8f);
        scene.overlay().showText(120).placeNearTarget().pointAt(util.vector().centerOf(saplings[0])).text("Like this situation, due to saw works behind of deployer, sapling is not placed down. At this time, you can use controller to adjust the order");
        scene.idle(120);
        scene.world().setKineticSpeed(speedMatters,0f);
        scene.world().hideIndependentSection(contraptionLink,Direction.UP);
        scene.idle(10);
        scene.world().showSection(util.select().position(saplings[0]),Direction.DOWN);
        scene.idle(5);
        contraptionLink = scene.world().showIndependentSection(contraption,Direction.DOWN);
        scene.idle(10);
        scene.world().showSectionAndMerge(util.select().position(controller),Direction.DOWN,contraptionLink);
        scene.effects().superGlue(controller,Direction.DOWN,true);
        scene.idle(5);
        scene.overlay().showText(120).attachKeyFrame().pointAt(util.vector().centerOf(controller)).text("Place the controller nearby the deployer, and use dye to set controller state. Different dye means different working order. You can use /scs command to check working ordinal");
        scene.idle(45);
        scene.overlay().showControls(util.vector().centerOf(controller),Pointing.DOWN,30).rightClick().withItem(Items.WHITE_DYE.getDefaultInstance());
        scene.idle(30);
        scene.world().modifyBlockEntityNBT(util.select().position(controller),ContraptionControlsBlockEntity.class,nbt -> nbt.putString("overlay", Overlay.WHITE.getName()));
        scene.idle(55);
        scene.overlay().showText(120).pointAt(util.vector().centerOf(controller)).text("After set controller, you can just let contraption work. There is no need to set controller active. If you do so, the saw will be closed instead, because controller changes order independently and won't conflict with other function");
        scene.idle(120);
        scene.world().configureCenterOfRotation(contraptionLink,util.vector().centerOf(bearing));
        scene.world().rotateBearing(bearing,-270f,90);
        scene.world().rotateSection(contraptionLink,0,-270f,0,90);
        scene.world().setKineticSpeed(speedMatters,32f);
        scene.world().moveDeployer(deployer,1f,0);
        scene.world().setBlock(saplings[0],oakSapling,false);
        scene.world().showIndependentSectionImmediately(util.select().position(saplings[0]));
        scene.world().moveDeployer(deployer,-1f,10);
        tree = util.select().fromTo(4,1,8,4,2,8).add(util.select().fromTo(2,3,6,6,6,10));
        for (int i = 1; i < 4; i++){
            scene.idle(20);
            scene.world().moveDeployer(deployer, 1f, 10);
            scene.idle(10);
            scene.world().moveDeployer(deployer, -1f, 10);
            if(i == 2) {
                scene.world().setBlock(saplings[3], Blocks.OAK_LOG.defaultBlockState(), false);
                scene.world().showIndependentSectionImmediately(tree);
            }
        }
        for (int j = 0; j < 10; j++) {
            scene.idle(3);
            scene.world().incrementBlockBreakingProgress(saplings[3]);
        }
        scene.world().setBlocks(tree,Blocks.AIR.defaultBlockState(),true);
        scene.world().setBlock(saplings[3],oakSapling,false);
        scene.world().showIndependentSectionImmediately(util.select().position(saplings[3]));
        scene.world().rotateBearing(bearing,-90f,30);
        scene.world().rotateSection(contraptionLink,0,-90f,0,30);
        scene.world().moveDeployer(deployer,1f,0);
        scene.world().moveDeployer(deployer,-1f,10);
        scene.idle(30);
        scene.world().setKineticSpeed(speedMatters,0f);
        scene.idle(10);
        scene.overlay().showText(90).attachKeyFrame().pointAt(util.vector().topOf(deployer.south())).placeNearTarget().text("Not only active block could be ordered, but also containers such as barrels and chests ( even trashcan ) do");
        scene.idle(100);
        scene.overlay().showText(120).pointAt(util.vector().centerOf(controller)).placeNearTarget().text("Must be noticed is the filter of controller doesn't matter, the controller will change the order of any block around it, unless it has been given higher priority");
        scene.idle(130);
        scene.overlay().showText(90).pointAt(util.vector().centerOf(controller)).placeNearTarget().text("And if you want to control many blocks together. You don't need to put many controllers, one is enough");
        scene.idle(100);
        scene.overlay().showText(60).pointAt(util.vector().centerOf(controller)).placeNearTarget().text("Last, water bucket to clear color, and different color dye change color");
        scene.idle(30);
        scene.overlay().showControls(util.vector().centerOf(controller),Pointing.DOWN,30).rightClick().withItem(Items.WATER_BUCKET.getDefaultInstance());
        scene.idle(10);
        scene.world().modifyBlockEntityNBT(util.select().position(controller), ContraptionControlsBlockEntity.class,nbt -> nbt.remove("overlay"),true);
        scene.idle(20);
    }
}