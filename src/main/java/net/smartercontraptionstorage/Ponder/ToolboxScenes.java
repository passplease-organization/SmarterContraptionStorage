package net.smartercontraptionstorage.Ponder;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.drill.DrillBlockEntity;
import com.simibubi.create.foundation.ponder.*;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.element.MinecartElement;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

public class ToolboxScenes {
    public static void trashcansControl(SceneBuilder builder, SceneBuildingUtil util){
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("trash_control","Help Control TrashCans");
        scene.configureBasePlate(0,0,5);
        scene.scaleSceneView(0.9F);
        scene.showBasePlate();
        scene.idle(5);
        BlockPos shaft = util.grid().at(3,1,3);
        BlockPos gearshift = util.grid().at(3,1,4);
        Selection kinetics = util.select().fromTo(3,1,2,3,1,4);
        BlockPos lever = util.grid().at(3,2,4);
        Selection cogwheel = util.select().fromTo(3,1,5,4,0,5);
        BlockPos[] cobblestone = {util.grid().at(1,1,2),util.grid().at(0,1,2)};
        BlockPos toolbox = util.grid().at(3,1,1);
        BlockPos trashcan = util.grid().at(2,1,1);
        BlockPos drill = util.grid().at(2,1,1);
        scene.world().modifyKineticSpeed(util.select().position(gearshift).add(cogwheel),(speed) -> speed);
        scene.idle(5);
        scene.world().showSection(cogwheel, Direction.DOWN);
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
        scene.idle(5);
        scene.world().showSectionAndMerge(util.select().fromTo(trashcan.north(),toolbox.north()).add(util.select().position(drill)),Direction.DOWN,contraption);
        scene.world().showSection(util.select().position(cobblestone[0]),Direction.UP);
        scene.idle(22);
        scene.addKeyframe();
        scene.overlay().showOutline(PonderPalette.GREEN, AllItems.SUPER_GLUE,util.select().fromTo(trashcan,toolbox).add(util.select().position(drill.south())),25);
        scene.overlay().showControls(util.vector().centerOf(trashcan), Pointing.DOWN,30).withItem(AllItems.SUPER_GLUE.asStack());
        scene.idle(40);
        scene.overlay().showText(50).placeNearTarget().pointAt(util.vector().topOf(trashcan).add(0,-0.25,0)).text("Normally every item will be deleted if there is no any other containers except trashcan");
        scene.idle(60);
        scene.effects().indicateRedstone(lever);
        scene.world().toggleRedstonePower(util.select().fromTo(lever,gearshift));
        scene.world().modifyKineticSpeed(kinetics,(speed) -> -speed);
        scene.world().moveSection(contraption,util.vector().of(-0.1,0,0),2);
        scene.world().modifyBlockEntity(drill, DrillBlockEntity.class,(block) -> block.setSpeed(32));
        scene.idle(2);
        for (int i = 0; i < 10; i++) {
            scene.idle(3);
            scene.world().incrementBlockBreakingProgress(cobblestone[0]);
        }
        scene.world().moveSection(contraption,util.vector().of(-1.9,0,0),38);
        scene.idle(10);
        scene.overlay().showText(30).independent(70).text("no drops are spawned ...");
        scene.idle(28);
        scene.world().modifyBlockEntity(drill, DrillBlockEntity.class,(block) -> block.setSpeed(0));
        scene.idle(5);
        scene.effects().indicateRedstone(lever);
        scene.world().toggleRedstonePower(util.select().fromTo(lever,gearshift));
        scene.world().modifyKineticSpeed(kinetics,(speed) -> -speed);
        scene.world().moveSection(contraption,util.vector().of(2,0,0),40);
        scene.idle(42);
        scene.addKeyframe();
        scene.overlay().showText(150).placeNearTarget().pointAt(util.vector().topOf(trashcan).add(0,-0.25,0)).text("Although we can use the filter trashcan provided to us, it may not enough to use. And also, any items matches contents in toolbox we wouldn't want them be deleted, so we can use toolbox as an extended black filter. Let's see how to use it");
        scene.idle(160);
        scene.overlay().showText(70).placeNearTarget().pointAt(util.vector().centerOf(toolbox)).text("If we add cobblestone to our toolbox (actually, only need to set it's filter)");
        scene.idle(30);
        scene.overlay().showControls(util.vector().centerOf(toolbox),Pointing.UP,40).withItem(Items.COBBLESTONE.getDefaultInstance());
        scene.idle(45);
        scene.overlay().showText(90).placeNearTarget().pointAt(util.vector().topOf(trashcan).add(0,-0.25,0)).text("Then this cobblestone will drop on the ground rather than deleted by trashcan (toolbox can be anywhere on contraption, don't need to place near trashcan)");
        scene.idle(5);
        scene.world().showSection(util.select().position(cobblestone[1]),Direction.UP);
        scene.idle(90);
        scene.effects().indicateRedstone(lever);
        scene.world().toggleRedstonePower(util.select().fromTo(lever,gearshift));
        scene.world().modifyKineticSpeed(kinetics,(speed) -> -speed);
        scene.world().moveSection(contraption,util.vector().of(-1.1,0,0),22);
        scene.world().modifyBlockEntity(drill, DrillBlockEntity.class,(block) -> block.setSpeed(32));
        scene.idle(22);
        for (int i = 0; i < 10; i++) {
            scene.idle(3);
            scene.world().incrementBlockBreakingProgress(cobblestone[1]);
        }
        scene.world().createItemEntity(util.vector().centerOf(cobblestone[1]),util.vector().of(-0.1,0,0),Items.COBBLESTONE.getDefaultInstance());
        scene.world().moveSection(contraption,util.vector().of(-0.9,0,0),18);
        scene.idle(18);
        scene.world().modifyBlockEntity(drill,DrillBlockEntity.class,(block) -> block.setSpeed(0));
        scene.idle(5);
    }
    public static void replenishItem(SceneBuilder builder, SceneBuildingUtil util){
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("replenish_item","Use toolbox to replenish item to your hotbar");
        scene.configureBasePlate(0,0,9);
        scene.setSceneOffsetY(2F);
        scene.scaleSceneView(0.7F);
        scene.idle(5);
        ElementLink<WorldSectionElement> stones = scene.world().showIndependentSection(util.select().fromTo(-4,-2,-4,8,2,6).add(util.select().fromTo(-7,-1,-4,-4,0,5).add(util.select().fromTo(4,-16,2,5,-16,3))),Direction.DOWN);
        BlockState rail = Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.EAST_WEST);
        scene.idle(20);
        scene.world().setBlock(util.grid().at(-7,0,0),rail,false);
        scene.world().setBlock(util.grid().at(-6,0,0),rail,false);
        scene.world().setBlock(util.grid().at(-5,0,0),rail,false);
        scene.idle(10);
        ElementLink<EntityElement> player = scene.world().createEntity((world)->{
            ArmorStand armorStand = new ArmorStand(world,-6.5,0,2.5);
            armorStand.setItemSlot(EquipmentSlot.MAINHAND,Items.DIAMOND_PICKAXE.getDefaultInstance());
            armorStand.setItemSlot(EquipmentSlot.OFFHAND,Items.TORCH.getDefaultInstance());
            armorStand.setItemSlot(EquipmentSlot.HEAD,Items.IRON_HELMET.getDefaultInstance());
            armorStand.setItemSlot(EquipmentSlot.CHEST,Items.DIAMOND_CHESTPLATE.getDefaultInstance());
            armorStand.setItemSlot(EquipmentSlot.LEGS,Items.IRON_LEGGINGS.getDefaultInstance());
            armorStand.setItemSlot(EquipmentSlot.FEET,Items.IRON_BOOTS.getDefaultInstance());
            armorStand.lookAt(EntityAnchorArgument.Anchor.EYES,new Vec3(0,0,0));
            return armorStand;
        });
        ElementLink<MinecartElement> cart = scene.special().createCart(util.vector().topOf(-7,-1,0),180F, MinecartFurnace::new);
        ElementLink<WorldSectionElement> contraption = scene.world().showIndependentSection(util.select().fromTo(-8,0,-4,-8,2,3).add(util.select().fromTo(-9,0,-1,-10,1,-1)), Direction.DOWN);
        scene.world().moveSection(contraption,util.vector().of(2,0,1),0);
        BlockPos deployer = util.grid().at(-10,0,-1);
        scene.world().setFilterData(util.select().position(deployer), DeployerBlockEntity.class,Items.RAIL.getDefaultInstance());
        scene.overlay().showText(30).placeNearTarget().independent(70).text("You may usually use minecart to help mining ores");
        scene.idle(30);
        setSpeed(scene,util,-64);
        int j,y,z;
        Vec3 vec = new Vec3(1,0,0);
        Vec3 finalVec1 = vec;
        for (int i = 0; i <= 10; i++) {
            scene.special().moveCart(cart,vec,2);
            scene.world().moveSection(contraption,vec,2);
            scene.world().modifyEntity(player,(entity) -> entity.move(MoverType.PLAYER, finalVec1));
            for (j = 0; j < 10; j++) {
                scene.idle(2);
                for (y = 0; y <= 2; y++)
                    for (z = -3; z <= 4; z++)
                        scene.world().incrementBlockBreakingProgress(util.grid().at(i - 4, y, z));
            }
            scene.world().setBlock(util.grid().at(i - 5,0,0),rail,false);
        }
        setSpeed(scene,util,0);
        vec = util.vector().of(0,15,0);
        scene.world().moveSection(contraption,vec,20);
        scene.world().moveSection(stones,vec,20);
        scene.special().moveCart(cart,vec,20);
        scene.idle(20);
        scene.overlay().showText(70).placeNearTarget().pointAt(util.vector().topOf(4,0,2)).text("But you may encounter some embarrassing scenes like this: fall into SpatialPylonBlockEntityMixin hole and have not enough blocks to get out");
        scene.idle(75);
        scene.world().hideIndependentSection(contraption,Direction.UP);
        scene.world().hideIndependentSection(stones,Direction.UP);
        scene.special().hideElement(cart,Direction.UP);
        scene.world().modifyEntity(player, Entity::kill);
        scene.addKeyframe();
        scene.idle(20);
        scene.world().showIndependentSection(util.select().fromTo(-4,-2,-4,8,2,6).add(util.select().fromTo(-7,-1,-4,-4,0,5).add(util.select().fromTo(4,-16,2,5,-16,3))),Direction.DOWN);
        scene.world().setBlock(util.grid().at(-7,0,0),rail,false);
        scene.idle(10);
        player = scene.world().createEntity((world)->{
            ArmorStand armorStand = new ArmorStand(world,-6.5,0,2.5);
            armorStand.setItemSlot(EquipmentSlot.MAINHAND,Items.DIAMOND_PICKAXE.getDefaultInstance());
            armorStand.setItemSlot(EquipmentSlot.OFFHAND,Items.COBBLESTONE.getDefaultInstance());
            armorStand.setItemSlot(EquipmentSlot.HEAD,Items.IRON_HELMET.getDefaultInstance());
            armorStand.setItemSlot(EquipmentSlot.CHEST,Items.DIAMOND_CHESTPLATE.getDefaultInstance());
            armorStand.setItemSlot(EquipmentSlot.LEGS,Items.IRON_LEGGINGS.getDefaultInstance());
            armorStand.setItemSlot(EquipmentSlot.FEET,Items.IRON_BOOTS.getDefaultInstance());
            armorStand.lookAt(EntityAnchorArgument.Anchor.EYES,new Vec3(0,0,0));
            return armorStand;
        });
        cart = scene.special().createCart(util.vector().topOf(-7,-1,0),180F, MinecartFurnace::new);
        contraption = scene.world().showIndependentSection(util.select().fromTo(-8,0,-4,-8,2,3).add(util.select().fromTo(-9,0,-1,-10,1,-1)), Direction.DOWN);
        scene.world().moveSection(contraption,util.vector().of(2,0,1),0);
        scene.idle(20);
        scene.overlay().showText(120).independent(70).text("So you can use toolbox to replenish item helping you get out of there, and it can also help you mining. It can store the cobblestones and extract items from minecart to your hotbar");
        scene.idle(80);
        scene.world().showSectionAndMerge(util.select().position(-11,0,-1),Direction.DOWN,contraption);
        scene.effects().superGlue(util.grid().at(-8,0,0),Direction.WEST,false);
        scene.idle(40);
        setSpeed(scene,util,-64);
        vec = util.vector().of(11,0,0);
        scene.special().moveCart(cart,vec,44);
        scene.world().moveSection(contraption,vec,44);
        scene.idle(44);
        vec = util.vector().of(1,0,0);
        for (int i = 11; i < 13; i++) {
            scene.special().moveCart(cart,vec,2);
            scene.world().moveSection(contraption,vec,2);
            for (j = 0; j < 10; j++) {
                scene.idle(2);
                for (y = 0; y <= 2; y++)
                    for (z = -3; z <= 4; z++)
                        scene.world().incrementBlockBreakingProgress(util.grid().at(i - 4, y, z));
            }
            scene.world().setBlock(util.grid().at(i - 5,0,0),rail,false);
        }
        setSpeed(scene,util,0);
        scene.idle(10);
        vec = util.vector().topOf(5,0,0);
        scene.overlay().showText(150).pointAt(vec).placeNearTarget().attachKeyFrame().text("Toolbox works like an actor, it will search all containers (including itself) to replenish items to all players nearby and store items");
        Vec3 finalVec3 = util.vector().of(0.5,0,0);
        for(j = 0;j < 18;j++) {
            scene.idle(4);
            scene.world().modifyEntity(player, (entity) -> entity.move(MoverType.PLAYER, finalVec3));
        }
        scene.idle(10);
        scene.world().setBlock(util.grid().at(4,-1,2),Blocks.COBBLESTONE.defaultBlockState(),false);
        scene.idle(5);
        for(j = 0;j < 6;j++) {
            scene.idle(4);
            scene.world().modifyEntity(player, (entity) -> entity.move(MoverType.PLAYER, finalVec3));
        }
        scene.idle(40);
        scene.overlay().showText(150).pointAt(vec).placeNearTarget().text("Must point out that, though you can close toolbox by controls block, the controls block will distinguish nbt labels, so you best use SpatialPylonBlockEntityMixin new box to set the filter and make sure the color of filter and block are the same (color also matters)");
        scene.idle(150);
    }
    private static void setSpeed(SceneBuilder scene, SceneBuildingUtil util,float speed){
        int y,z;
        for (y = 0; y <= 2; y++)
            for (z = -4; z <= 3; z++)
                scene.world().modifyBlockEntity(util.grid().at(-8,y,z), DrillBlockEntity.class,(entity) -> entity.setSpeed(speed));
    }
}