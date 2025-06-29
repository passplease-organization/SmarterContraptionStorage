package Excludes.GameTest;

import com.simibubi.create.infrastructure.gametest.CreateTestFunction;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SCSGameTests {
    public static final int ONE_MINUTE = 20 * 60;

    private static final Class<?>[] TESTS = {
            SCSItemHandlerTest.class,
            SCSFluidHandlerTest.class,
    };

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        event.register(SCSGameTests.class);
    }

    @GameTestGenerator
    public static Collection<TestFunction> generateTests(){
        return CreateTestFunction.getTestsFrom(TESTS);
    }
}
