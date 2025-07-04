package Excludes.GameTest;

import com.simibubi.create.infrastructure.gametest.CreateGameTestHelper;
import com.simibubi.create.infrastructure.gametest.GameTestGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.smartercontraptionstorage.SmarterContraptionStorage;

import static Excludes.GameTest.SCSItemHandlerTest.contraptionStoped;

@GameTestGroup(path = "fluid_handler",namespace = SmarterContraptionStorage.MODID)
public class SCSFluidHandlerTest {
    @GameTest(template = "fluid_trashcan")// Test DumpHandler too
    public static void testTrashCan(CreateGameTestHelper helper) {
        BlockPos water = new BlockPos(3,5,1);
        BlockPos gearshift =  new BlockPos(1,7,3);
        BlockPos button = new BlockPos(1,6,3);
        BlockPos drawer = new BlockPos(1,4,1);

        helper.pressButton(button);
        helper.succeedWhen(() -> {
            contraptionStoped(helper,gearshift);
            helper.assertBlockNotPresent(Blocks.WATER,water);
            helper.assertBlockNotPresent(Blocks.WATER,water.east());
            helper.assertContainerContains(drawer,Items.BUCKET);
        });
    }

    @GameTest(template = "fluid_functional_drawer")
    public static void testFunctionalDrawer(CreateGameTestHelper helper) {
        defaultTest(helper);
    }

    public static void defaultTest(CreateGameTestHelper helper) {
        BlockPos button = new BlockPos(3,2,3);
        BlockPos gearshift = new BlockPos(3,3,3);
        BlockPos tank = new BlockPos(3,3,1);
        helper.assertTankEmpty(tank);

        helper.pressButton(button);
        helper.succeedWhen(() -> {
            contraptionStoped(helper,gearshift);
            if(helper.getFluidInTanks(tank) == 0)
                helper.fail("No water stored !");
        });
    }
}
