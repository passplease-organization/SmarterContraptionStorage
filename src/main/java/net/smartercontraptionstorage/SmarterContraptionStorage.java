package net.smartercontraptionstorage;

import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.smartercontraptionstorage.Message.MenuLevelPacket;
import net.smartercontraptionstorage.Message.ModMessage;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.smartercontraptionstorage.AddActor.BackpackBehaviour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.smartercontraptionstorage.AddStorage.FluidHander.FunctionalFluidHandlerHelper;
import net.smartercontraptionstorage.AddStorage.FluidHander.SBackPacksFluidHandlerHelper;
import net.smartercontraptionstorage.AddStorage.FluidHander.TrashcanFluidHelper;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.MovingBlockEntityMenu;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.MovingBlockEntityScreen;
import net.smartercontraptionstorage.AddStorage.GUI.NormalMenu.*;
import net.smartercontraptionstorage.AddStorage.ItemHandler.*;
import net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper.AEControllerBlock;
import net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper.AEEnergyBlock;
import net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper.MEStorageFilter;
import net.smartercontraptionstorage.Ponder.SCS_Ponder;
import net.smartercontraptionstorage.AddActor.ToolboxBehaviour;

import static net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper.register;
import static net.smartercontraptionstorage.AddStorage.FluidHander.FluidHandlerHelper.register;
import static net.smartercontraptionstorage.Ponder.SCS_Ponder.CONTROLLABLE_CONTAINERS;

@Mod(SmarterContraptionStorage.MODID)
public class SmarterContraptionStorage {
    public static final String MODID = "smartercontraptionstorage";
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    public SmarterContraptionStorage() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MENU_TYPES.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        ModList list = ModList.get();
        if(list.isLoaded("create")) {
            MovingBlockEntityMenu.BlockEntityMenu = MENU_TYPES.register("moving_blockentity",() -> IForgeMenuType.create(
                    MovingBlockEntityMenu::new
            ));
            if (list.isLoaded("trashcans")) {
                TrashHandlerHelper.TrashHandler.TrashCanMenu = MENU_TYPES.register("moving_trashcans", () -> IForgeMenuType.create(
                        MovingTrashCanMenu::new));
            }
            if (list.isLoaded("storagedrawers")) {
                DrawersHandlerHelper.NormalDrawerHandler.DrawerMenu = MENU_TYPES.register("moving_drawer", () -> IForgeMenuType.create(
                        MovingDrawerMenu::new
                ));
                CompactingHandlerHelper.CompactingHandler.CompactingDrawerMenu = MENU_TYPES.register("moving_compacting_drawer", () -> IForgeMenuType.create(
                        MovingCompactingDrawerMenu::new
                ));
            }
            if (list.isLoaded("functionalstorage")) {
                FunctionalDrawersHandlerHelper.FDrawersHandler.MENU_TYPE = MENU_TYPES.register("moving_functional_drawer", () -> IForgeMenuType.create(
                        MovingFunctionalDrawerMenu::new
                ));
                FunctionalCompactingHandlerHelper.FCDrawersHandler.MENU_TYPE = MENU_TYPES.register("moving_compacting_functional_drawer", () -> IForgeMenuType.create(
                        MovingFunctionalCompactingMenu::new
                ));
            }
        }
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SmarterContraptionStorageConfig.SPEC,"Smarter_Contraption_Storage.toml");
    }
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        ModList list = ModList.get();
        if(list.isLoaded("create")){
            register(ToolboxHandlerHelper.INSTANCE);
            ToolboxBehaviour behaviour = new ToolboxBehaviour();
            for (BlockEntry<ToolboxBlock> toolboxBlockBlockEntry : AllBlocks.TOOLBOXES) {
                AllMovementBehaviours.registerBehaviour(toolboxBlockBlockEntry.get(), behaviour);
            }
            if(list.isLoaded("trashcans")) {
                register(new TrashHandlerHelper());
                register(new TrashcanFluidHelper());
            }
            if(list.isLoaded("storagedrawers")) {
                register(new DrawersHandlerHelper());
                register(new CompactingHandlerHelper());
            }
            if(list.isLoaded("sophisticatedbackpacks")){
                register(SBackPacksHandlerHelper.INSTANCE);
                register(new SBackPacksFluidHandlerHelper());
                BackpackBehaviour backpackBehaviour = new BackpackBehaviour();
                AllMovementBehaviours.registerBehaviour(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.BACKPACK.get(),backpackBehaviour);
                AllMovementBehaviours.registerBehaviour(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.COPPER_BACKPACK.get(),backpackBehaviour);
                AllMovementBehaviours.registerBehaviour(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.IRON_BACKPACK.get(),backpackBehaviour);
                AllMovementBehaviours.registerBehaviour(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.GOLD_BACKPACK.get(),backpackBehaviour);
                AllMovementBehaviours.registerBehaviour(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.DIAMOND_BACKPACK.get(),backpackBehaviour);
                AllMovementBehaviours.registerBehaviour(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.NETHERITE_BACKPACK.get(),backpackBehaviour);
            }
            if(list.isLoaded("functionalstorage")){
                register(new FunctionalDrawersHandlerHelper());
                register(new FunctionalCompactingHandlerHelper());
                register(new FunctionalFluidHandlerHelper());
            }
            if(SmarterContraptionStorageConfig.AE2SUPPORT.get() && list.isLoaded("ae2")){
                register(new AE2BusBlockHelper());
                register(new MEStorageFilter());
                register(new AEControllerBlock());
                register(new AEEnergyBlock());
                register(new SpatialHandler());
            }
            if(list.isLoaded("cobblefordays"))
                register(new CobblestoneGenerator());
        }
    }
    private void commonSetup(final FMLCommonSetupEvent event) {
        ModMessage.register(new MenuLevelPacket());
        ModMessage.registerMessages();
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            ModList list = ModList.get();
            if(list.isLoaded("create")){
                MenuScreens.register(MovingBlockEntityMenu.BlockEntityMenu.get(), MovingBlockEntityScreen::new);
                register(ToolboxHandlerHelper.INSTANCE);
                SCS_Ponder.register();
                if(list.isLoaded("trashcans")) {
                    MenuScreens.register(TrashHandlerHelper.TrashHandler.TrashCanMenu.get(), MovingTrashCanScreen::new);
                    register(new TrashHandlerHelper());
                    register(new TrashcanFluidHelper());
                    SCS_Ponder.registerTrashCan();
                }
                if(list.isLoaded("storagedrawers")) {
                    MenuScreens.register(DrawersHandlerHelper.NormalDrawerHandler.DrawerMenu.get(), MovingDrawerScreen::new);
                    MenuScreens.register(CompactingHandlerHelper.CompactingHandler.CompactingDrawerMenu.get(), MovingCompactingDrawerScreen::new);
                    register(new DrawersHandlerHelper());
                    register(new CompactingHandlerHelper());
                    PonderRegistry.TAGS.forTag(CONTROLLABLE_CONTAINERS).add(ModBlocks.CONTROLLER.get());
                }
                if(SmarterContraptionStorageConfig.AE2SUPPORT.get() && list.isLoaded("ae2")) {
                    register(new AE2BusBlockHelper());
                    register(new MEStorageFilter());
                    register(new AEControllerBlock());
                    register(new AEEnergyBlock());
                    register(new SpatialHandler());
                    SCS_Ponder.registerAE();
                }
                if(list.isLoaded("cobblefordays"))
                    register(new CobblestoneGenerator());
                if(list.isLoaded("sophisticatedbackpacks")){
                    register(SBackPacksHandlerHelper.INSTANCE);
                    register(new SBackPacksFluidHandlerHelper());
                }
                if(list.isLoaded("functionalstorage")){
                    MenuScreens.register(FunctionalDrawersHandlerHelper.FDrawersHandler.MENU_TYPE.get(),MovingFunctionalDrawerScreen::new);
                    MenuScreens.register(FunctionalCompactingHandlerHelper.FCDrawersHandler.MENU_TYPE.get(),MovingFunctionalCompactingScreen::new);
                    register(new FunctionalDrawersHandlerHelper());
                    register(new FunctionalCompactingHandlerHelper());
                    register(new FunctionalFluidHandlerHelper());
                }
            }
        }
    }
}
