package Excludes.GameTest;

import appeng.api.implementations.items.ISpatialStorageCell;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.blockentity.networking.ControllerBlockEntity;
import appeng.blockentity.spatial.SpatialIOPortBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.items.tools.powered.WirelessCraftingTerminalItem;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.parts.automation.IOBusPart;
import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.block.tile.CompactingDrawerTile;
import com.buuz135.functionalstorage.util.CompactingUtil;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.mojang.serialization.DataResult;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlock;
import com.simibubi.create.infrastructure.gametest.CreateGameTestHelper;
import com.simibubi.create.infrastructure.gametest.GameTestGroup;
import com.supermartijn642.trashcans.TrashCans;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.smartercontraptionstorage.AddStorage.ItemHandler.AE2BusBlockHelper;
import net.smartercontraptionstorage.AddStorage.ItemHandler.SpatialHandler;
import net.smartercontraptionstorage.SmarterContraptionStorage;
import net.smartercontraptionstorage.SmarterContraptionStorageConfig;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@GameTestGroup(path = "item_handler",namespace = SmarterContraptionStorage.MODID)
public class SCSItemHandlerTest {
    @GameTest(template = "trashcan",timeoutTicks = SCSGameTests.ONE_MINUTE * 2)
    public static void testTrashCan(CreateGameTestHelper helper) {
        BlockPos button = new BlockPos(1,6,2);
        BlockPos gearshift = new BlockPos(1,7,2);
        BlockPos chest = new BlockPos(1,4,3);
        BlockPos barrel = new BlockPos(3,6,4);
        BlockPos grassBlock = new BlockPos(1,2,3);
        BlockPos toolbox = new BlockPos(1,8,4);
        BlockPos trashcan = new BlockPos(1,7,4);
        AtomicInteger step = new AtomicInteger(1);

        helper.assertContainerEmpty(chest);
        helper.assertContainerEmpty(barrel);

        helper.pressButton(button);
        helper.succeedWhen(() -> {
            if(step.get() != 4) {
                contraptionStoped(helper,gearshift);
                switch(step.get()) {
                    case 1 -> {
                        helper.assertContainerContains(barrel, Items.DIRT);

                        // Prepare for next step
                        helper.destroyBlock(barrel);
                        helper.setBlock(barrel,Blocks.BARREL);
                        helper.destroyBlock(toolbox);
                        helper.setBlock(toolbox,Blocks.COBBLESTONE);
                    }
                    case 2 -> {
                        helper.assertContainerEmpty(barrel);

                        // Prepare for next step
                        helper.getBlockEntity(TrashCans.item_trash_can_tile,trashcan).itemFilterWhitelist = true;
                    }
                    case 3 -> helper.assertContainerContains(barrel, Items.DIRT);
                }
                helper.setBlock(grassBlock,Blocks.GRASS_BLOCK);
                helper.pressButton(button);
                helper.fail("Next step: " + step.incrementAndGet());
            }
        });
    }

    @GameTest(template = "drawer",timeoutTicks = CreateGameTestHelper.FIFTEEN_SECONDS)
    public static void testDrawer(CreateGameTestHelper helper) {
        defaultTest(helper);
    }

    @GameTest(template = "compacting_drawer",timeoutTicks = SCSGameTests.ONE_MINUTE)
    public static void testCompactingDrawer(CreateGameTestHelper helper) {
        BlockPos button = new BlockPos(1,6,2);
        BlockPos gearshift = new BlockPos(1,7,2);
        BlockPos barrel = new BlockPos(3,4,4);
        BlockPos drawer = new BlockPos(1,4,3);

        helper.assertContainerEmpty(barrel);
        helper.assertContainerEmpty(drawer);

        helper.pressButton(button);
        helper.succeedWhen(() -> {
            contraptionStoped(helper,gearshift);
            Object2LongMap<Item> items = helper.getItemContent(barrel);
            if(!items.containsKey(Items.IRON_INGOT) || items.getLong(Items.IRON_INGOT) != 9)
                helper.fail("Compacting drawer doesn't contain 9 iron ingots !");
            IDrawerGroup group = helper.getBlockEntity(ModBlockEntities.FRACTIONAL_DRAWERS_3.get(), drawer).getGroup();
            for(int slot = 0; slot < group.getDrawerCount(); slot++)
                if(group.getDrawer(slot).getStoredItemPrototype().isEmpty())
                    helper.fail("Didn't save compacting drawer filter !");
        });
    }

    @GameTest(template = "functional_drawer",timeoutTicks = CreateGameTestHelper.FIFTEEN_SECONDS)
    public static void testFunctionalDrawer(CreateGameTestHelper helper) {
        defaultTest(helper);
    }

    @GameTest(template = "functional_compacting_drawer",timeoutTicks = SCSGameTests.ONE_MINUTE)
    public static void testFunctionalCompactingDrawer(CreateGameTestHelper helper) {
        BlockPos button = new BlockPos(1,6,2);
        BlockPos gearshift = new BlockPos(1,7,2);
        BlockPos barrel = new BlockPos(3,4,4);
        BlockPos drawer = new BlockPos(1,4,3);

        helper.assertContainerEmpty(barrel);
        helper.assertContainerEmpty(drawer);

        helper.pressButton(button);
        helper.succeedWhen(() -> {
            contraptionStoped(helper,gearshift);
            Object2LongMap<Item> items = helper.getItemContent(barrel);
            if(!items.containsKey(Items.IRON_INGOT) || items.getLong(Items.IRON_INGOT) != 9)
                helper.fail("Compacting drawer doesn't contain 9 iron ingots !");
            BlockEntity blockEntity = helper.getBlockEntity(FunctionalStorage.COMPACTING_DRAWER.getRight().get(), drawer);
            if(blockEntity instanceof CompactingDrawerTile tile){
                for(CompactingUtil.Result filter : tile.handler.getResultList())
                    if(filter.getResult().isEmpty())
                        helper.fail("Didn't save compacting drawer filter!");
            }else helper.fail("Wrong drawer entity type !");
        });
    }

    @GameTest(template = "ae2_controller",setupTicks = CreateGameTestHelper.TEN_SECONDS,timeoutTicks = CreateGameTestHelper.FIFTEEN_SECONDS)
    public static void testAEController(CreateGameTestHelper helper) {
        BlockPos button = new BlockPos(2,6,2);
        BlockPos gearshift = new BlockPos(2,7,2);
        BlockPos grass = new BlockPos(2,2,3);
        GlobalPos wirelessAccessPoint = GlobalPos.of(helper.getLevel().dimension(),helper.absolutePos(new BlockPos(3, 10, 3)));
        ControllerBlockEntity controller = helper.getBlockEntity(AEBlocks.CONTROLLER.block().getBlockEntityType(), new BlockPos(4, 10, 4));
        IGridNode gridNode = controller.getGridNode();
        if(gridNode == null || !gridNode.isActive() || gridNode.getGrid() == null)
            helper.fail("Controller block entity is not active !");
        MEStorage storage = gridNode.getGrid().getStorageService().getInventory();
        AEItemKey linerChassis = AEItemKey.of(AllBlocks.LINEAR_CHASSIS);
        AEItemKey dirt = AEItemKey.of(Items.DIRT);
        AEItemKey controllerItem = AEItemKey.of(AEBlocks.CONTROLLER);
        Set<AEKey> aeKeys = storage.getAvailableStacks().keySet();
        if(aeKeys.contains(dirt) || !aeKeys.contains(linerChassis) || !aeKeys.contains(controllerItem))
            helper.fail("Wrong AE net storage !");
        BlockPos[] cables = {new BlockPos(2,7,5),new BlockPos(2,7,4)};
        cable:
        for(BlockPos cable : cables){
            CableBusBlockEntity bus = helper.getBlockEntity(AEBlocks.CABLE_BUS.block().getBlockEntityType(), cable);
            for(IPart part : AE2BusBlockHelper.getAllPart(bus)){
                if (part instanceof IOBusPart){
                    AEKey k = ((IOBusPart) part).getConfig().getKey(0);
                    if (k instanceof AEItemKey key) {
                        CompoundTag nbt = key.getTag();
                        if(nbt != null) {
                            GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE,wirelessAccessPoint).result()
                                    .ifPresent(tag -> nbt.put("accessPoint",tag));
                            continue cable;
                        }
                    }
                    helper.fail("Wrong cable settings");
                }
            }
        }

        helper.pressButton(button);
        helper.succeedWhen(() -> {
            contraptionStoped(helper,gearshift);
            if(SmarterContraptionStorageConfig.AE2Loaded()) {
                if (!storage.getAvailableStacks().keySet().contains(dirt))
                    helper.fail("Dirt was not stored !");
                if(storage.getAvailableStacks().get(linerChassis) == 64)
                    helper.fail("Chassis was placed ! Filter may not work !");
            }else{
                helper.assertItemEntityPresent(Items.DIRT,grass,1);
                if(storage.getAvailableStacks().get(linerChassis) != 64)
                    helper.fail("Chassis was been placed !");
            }
            if(storage.getAvailableStacks().get(controllerItem) != 64)
                helper.fail("Controller was been placed !");
        });
    }

    @GameTest(template = "spatial",timeoutTicks = CreateGameTestHelper.FIFTEEN_SECONDS)
    public static void testSpatial(CreateGameTestHelper helper) {
        BlockPos button = new BlockPos(0,5,1);
        BlockPos gearshift = new BlockPos(0,6,1);
        BlockPos lever = new BlockPos(0,6,3);
        helper.pullLever(lever);
        helper.whenSecondsPassed(1,() -> {
            SpatialIOPortBlockEntity ioPort = helper.getBlockEntity(AEBlocks.SPATIAL_IO_PORT.block().getBlockEntityType(), new BlockPos(0, 6, 4));
            ItemStack stack = ioPort.getInternalInventory().getStackInSlot(1);
            if(stack.getItem() instanceof ISpatialStorageCell cell){
                SpatialHandler.SpatialHelper handlerHelper = SpatialHandler.SpatialHelper.create(cell.getAllocatedPlotId(stack));
                handlerHelper.setWork();
                for (int slot = 0; slot < handlerHelper.getSlots(); slot++){
                    if(handlerHelper.getStackInSlot(slot).is(Blocks.COBBLESTONE.asItem()) || handlerHelper.getStackInSlot(slot).isEmpty())
                        continue;
                    helper.fail("Wrong inventory slot: " + slot + " !");
                }
                helper.pressButton(button);
                helper.succeedWhen(() -> {
                    contraptionStoped(helper,gearshift);
                    boolean hasDirt = false;
                    for (int slot = 0; slot < handlerHelper.getSlots(); slot++) {
                        if(handlerHelper.getStackInSlot(slot).is(Items.DIRT)) {
                            hasDirt = true;
                            break;
                        }
                    }
                    if(SmarterContraptionStorageConfig.AE2Loaded()) {
                        if (!hasDirt) {
                            helper.fail("Dirt is not stored !");
                        }
                        helper.assertBlockPresent(Blocks.COBBLESTONE, 1, 6, 7);
                    }else {
                        if(hasDirt) {
                            helper.fail("Dirt is stored !");
                        }
                        helper.assertBlockNotPresent(Blocks.COBBLESTONE, 1, 6, 7);
                    }
                });
            }else helper.fail("Spatial is not stored !");
        });
    }

    public static void defaultTest(CreateGameTestHelper helper){
        BlockPos button = new BlockPos(1,6,2);
        BlockPos gearshift = new BlockPos(1,7,2);
        BlockPos barrel = new BlockPos(3,4,4);
        BlockPos container = new BlockPos(1,4,3);

        helper.assertContainerEmpty(barrel);
        helper.assertContainerEmpty(container);

        helper.pressButton(button);
        helper.succeedWhen(() -> {
            contraptionStoped(helper,gearshift);
            helper.assertContainerContains(barrel, Items.DIRT);
        });
    }

    public static void contraptionStoped(CreateGameTestHelper helper,BlockPos gearshift){
        helper.assertBlockProperty(gearshift, SequencedGearshiftBlock.STATE, 0);
    }
}
