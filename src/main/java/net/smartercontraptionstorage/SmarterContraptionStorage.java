package net.smartercontraptionstorage;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.smartercontraptionstorage.AddStorage.FluidHander.MovingFluidStorageType;
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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.smartercontraptionstorage.AddStorage.FluidHander.FunctionalFluidHandlerHelper;
import net.smartercontraptionstorage.AddStorage.FluidHander.TrashcanFluidHelper;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.MovingBlockEntityMenu;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.MovingBlockEntityScreen;
import net.smartercontraptionstorage.AddStorage.GUI.NormalMenu.*;
import net.smartercontraptionstorage.AddStorage.ItemHandler.*;
import net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper.AEControllerBlock;
import net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper.AEEnergyBlock;
import net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper.MEStorageFilter;
import net.smartercontraptionstorage.AddActor.ToolboxBehaviour;
import net.smartercontraptionstorage.Ponder.SCS_Ponder;

import static net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper.register;
import static net.smartercontraptionstorage.AddStorage.FluidHander.FluidHandlerHelper.register;

@Mod(SmarterContraptionStorage.MODID)
public class SmarterContraptionStorage {
    public static final String MODID = "smartercontraptionstorage";
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);
    public static final String TrashCans = "trashcans";
    public static final String StorageDrawers = "storagedrawers";
    public static final String FunctionalStorage = "functionalstorage";
    public static final String CobbleForDays = "cobblefordays";
    public static final String SBackPack = "sophisticatedbackpacks";
    public static final String AE2 = "ae2";
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    /**
     * For Forge
     * */
    public SmarterContraptionStorage(FMLJavaModLoadingContext context) {
        this(context.getContainer());
    }
    /**
     * For NeoForge
     * */
    public SmarterContraptionStorage(FMLModContainer context) {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus modEventBus = context.getEventBus();
        MENU_TYPES.register(modEventBus);
        REGISTRATE.registerEventListeners(modEventBus);
        MovingItemStorageType.load();
        MovingFluidStorageType.load();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerType);
        ModList list = ModList.get();
        if(list.isLoaded("create")) {
            MovingBlockEntityMenu.BlockEntityMenu = MENU_TYPES.register("moving_blockentity",() -> IForgeMenuType.create(
                    MovingBlockEntityMenu::new
            ));
            if (list.isLoaded(TrashCans)) {
                TrashHandlerHelper.TrashHandler.TrashCanMenu = MENU_TYPES.register("moving_trashcans", () -> IForgeMenuType.create(
                        MovingTrashCanMenu::new));
            }
            if (list.isLoaded(StorageDrawers)) {
                DrawersHandlerHelper.NormalDrawerHandler.DrawerMenu = MENU_TYPES.register("moving_drawer", () -> IForgeMenuType.create(
                        MovingDrawerMenu::new
                ));
                CompactingHandlerHelper.CompactingHandler.CompactingDrawerMenu = MENU_TYPES.register("moving_compacting_drawer", () -> IForgeMenuType.create(
                        MovingCompactingDrawerMenu::new
                ));
            }
            if (list.isLoaded(FunctionalStorage)) {
                FunctionalDrawersHandlerHelper.FDrawersHandler.MENU_TYPE = MENU_TYPES.register("moving_functional_drawer", () -> IForgeMenuType.create(
                        MovingFunctionalDrawerMenu::new
                ));
                FunctionalCompactingHandlerHelper.FCDrawersHandler.MENU_TYPE = MENU_TYPES.register("moving_compacting_functional_drawer", () -> IForgeMenuType.create(
                        MovingFunctionalCompactingMenu::new
                ));
            }
        }
        context.addConfig(new ModConfig(ModConfig.Type.COMMON, SmarterContraptionStorageConfig.SPEC,context,"Smarter_Contraption_Storage.toml"));
    }
    private void commonSetup(final FMLCommonSetupEvent event) {
        ModMessage.register(new MenuLevelPacket());
        ModMessage.registerMessages();
        ModList list = ModList.get();
        if(list.isLoaded("create")){
            ToolboxBehaviour behaviour = new ToolboxBehaviour();
            for (BlockEntry<ToolboxBlock> toolboxBlockBlockEntry : AllBlocks.TOOLBOXES) {
                MovementBehaviour.REGISTRY.register(toolboxBlockBlockEntry.get(), behaviour);
            }
            if(list.isLoaded(SBackPack)){
                BackpackBehaviour backpackBehaviour = new BackpackBehaviour();
                MovementBehaviour.REGISTRY.register(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.BACKPACK.get(),backpackBehaviour);
                MovementBehaviour.REGISTRY.register(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.COPPER_BACKPACK.get(),backpackBehaviour);
                MovementBehaviour.REGISTRY.register(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.IRON_BACKPACK.get(),backpackBehaviour);
                MovementBehaviour.REGISTRY.register(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.GOLD_BACKPACK.get(),backpackBehaviour);
                MovementBehaviour.REGISTRY.register(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.DIAMOND_BACKPACK.get(),backpackBehaviour);
                MovementBehaviour.REGISTRY.register(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.NETHERITE_BACKPACK.get(),backpackBehaviour);
            }
        }
    }

    private void registerType(FMLLoadCompleteEvent event){
        ModList list = ModList.get();
        if(list.isLoaded("create")){
            if(list.isLoaded(TrashCans)) {
                register(new TrashHandlerHelper());
                register(new TrashcanFluidHelper());
            }
            if(list.isLoaded(StorageDrawers)) {
                register(new DrawersHandlerHelper());
                register(new CompactingHandlerHelper());
            }
            if(list.isLoaded(FunctionalStorage)){
                register(new FunctionalDrawersHandlerHelper());
                register(new FunctionalCompactingHandlerHelper());
                register(new FunctionalFluidHandlerHelper());
            }
            if(SmarterContraptionStorageConfig.AE2Loaded()){
                register(new AE2BusBlockHelper());
                register(new MEStorageFilter());
                register(new AEControllerBlock());
                register(new AEEnergyBlock());
                register(new SpatialHandler());
            }
            if(list.isLoaded(CobbleForDays))
                register(new CobblestoneGenerator());
        }
        if(list.isLoaded(TrashCans)) {
            MovingFluidStorageType.registerTrashCan();
        }
        MovingItemStorageType.register();
        MovingFluidStorageType.register();
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            ModList list = ModList.get();
            if(list.isLoaded("create")){
                PonderIndex.addPlugin(new SCS_Ponder());
                MenuScreens.register(MovingBlockEntityMenu.BlockEntityMenu.get(), MovingBlockEntityScreen::new);
                if(list.isLoaded(TrashCans)) {
                    MenuScreens.register(TrashHandlerHelper.TrashHandler.TrashCanMenu.get(), MovingTrashCanScreen::new);
                }
                if(list.isLoaded(StorageDrawers)) {
                    MenuScreens.register(DrawersHandlerHelper.NormalDrawerHandler.DrawerMenu.get(), MovingDrawerScreen::new);
                    MenuScreens.register(CompactingHandlerHelper.CompactingHandler.CompactingDrawerMenu.get(), MovingCompactingDrawerScreen::new);
                }
                if(list.isLoaded(FunctionalStorage)){
                    MenuScreens.register(FunctionalDrawersHandlerHelper.FDrawersHandler.MENU_TYPE.get(),MovingFunctionalDrawerScreen::new);
                    MenuScreens.register(FunctionalCompactingHandlerHelper.FCDrawersHandler.MENU_TYPE.get(),MovingFunctionalCompactingScreen::new);
                }
            }
        }
    }
}
