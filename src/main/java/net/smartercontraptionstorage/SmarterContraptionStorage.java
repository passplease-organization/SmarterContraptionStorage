package net.smartercontraptionstorage;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.smartercontraptionstorage.AddActor.BackpackBehaviour;
import net.smartercontraptionstorage.AddStorage.FluidHander.FunctionalFluidHandlerHelper;
import net.smartercontraptionstorage.AddStorage.FluidHander.TrashcanFluidHelper;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.MovingBlockEntityMenu;
import net.smartercontraptionstorage.AddStorage.GUI.NormalMenu.*;
import net.smartercontraptionstorage.AddStorage.ItemHandler.*;
import net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper.AEControllerBlock;
import net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper.AEEnergyBlock;
import net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper.MEStorageFilter;
import net.smartercontraptionstorage.Message.MenuLevelPacket;

import static net.smartercontraptionstorage.AddStorage.FluidHander.FluidHandlerHelper.register;

@Mod(SmarterContraptionStorage.MODID)
public class SmarterContraptionStorage {
    public static final String MODID = "smartercontraptionstorage";
    public static final String TrashCans = "trashcans";
    public static final String StorageDrawers = "storagedrawers";
    public static final String FunctionalStorage = "functionalstorage";
    public static final String CobbleForDays = "cobblefordays";
    public static final String SBackPack = "sophisticatedbackpacks";
    public static final String AE2 = "ae2";
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, MODID);

    public SmarterContraptionStorage(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(SmarterContraptionStorage::registerPacket);
        modEventBus.addListener(SmarterContraptionStorage::onServerStarting);
        MENU_TYPES.register(modEventBus);
        MovingItemStorageType.REGISTRATE.registerEventListeners(modEventBus);
        ModList list = ModList.get();
        if(list.isLoaded("create")) {
            MovingBlockEntityMenu.BlockEntityMenu = MENU_TYPES.register("moving_blockentity",() -> IMenuTypeExtension.create(
                    MovingBlockEntityMenu::new
            ));
            if (list.isLoaded(TrashCans)) {
                TrashHandlerHelper.TrashHandler.TrashCanMenu = MENU_TYPES.register("moving_trashcans", () -> IMenuTypeExtension.create(
                        MovingTrashCanMenu::new));
            }
            if (list.isLoaded(StorageDrawers)) {
                DrawersHandlerHelper.NormalDrawerHandler.DrawerMenu = MENU_TYPES.register("moving_drawer", () -> IMenuTypeExtension.create(
                        MovingDrawerMenu::new
                ));
                CompactingHandlerHelper.CompactingHandler.CompactingDrawerMenu = MENU_TYPES.register("moving_compacting_drawer", () -> IMenuTypeExtension.create(
                        MovingCompactingDrawerMenu::new
                ));
            }
            if (list.isLoaded(FunctionalStorage)) {
                FunctionalDrawersHandlerHelper.FDrawersHandler.MENU_TYPE = MENU_TYPES.register("moving_functional_drawer", () -> IMenuTypeExtension.create(
                        MovingFunctionalDrawerMenu::new
                ));
                FunctionalCompactingHandlerHelper.FCDrawersHandler.MENU_TYPE = MENU_TYPES.register("moving_compacting_functional_drawer", () -> IMenuTypeExtension.create(
                        MovingFunctionalCompactingMenu::new
                ));
            }
        }
        SmarterContraptionStorageConfig.register(modContainer);
    }

    public static void onServerStarting(FMLCommonSetupEvent event) {
        // Do something when the server starts (entering worlds)
        ModList list = ModList.get();
        if(list.isLoaded("create")){
//            StorageHandlerHelper.register(ToolboxHandlerHelper.INSTANCE);
//            ToolboxBehaviour behaviour = new ToolboxBehaviour();
//            for (BlockEntry<ToolboxBlock> toolboxBlockBlockEntry : AllBlocks.TOOLBOXES) {
//                MovementBehaviour.REGISTRY.register(toolboxBlockBlockEntry.get(), behaviour);
//            }
            if(list.isLoaded(TrashCans)) {
                StorageHandlerHelper.register(new TrashHandlerHelper());
                register(new TrashcanFluidHelper());
            }
            if(list.isLoaded(StorageDrawers)) {
                StorageHandlerHelper.register(new DrawersHandlerHelper());
                StorageHandlerHelper.register(new CompactingHandlerHelper());
            }
            if(list.isLoaded(SBackPack)){
//                StorageHandlerHelper.register(SBackPacksHandlerHelper.INSTANCE);
//                register(new SBackPacksFluidHandlerHelper());
                BackpackBehaviour backpackBehaviour = new BackpackBehaviour();
                MovementBehaviour.REGISTRY.register(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.BACKPACK.get(),backpackBehaviour);
                MovementBehaviour.REGISTRY.register(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.COPPER_BACKPACK.get(),backpackBehaviour);
                MovementBehaviour.REGISTRY.register(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.IRON_BACKPACK.get(),backpackBehaviour);
                MovementBehaviour.REGISTRY.register(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.GOLD_BACKPACK.get(),backpackBehaviour);
                MovementBehaviour.REGISTRY.register(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.DIAMOND_BACKPACK.get(),backpackBehaviour);
                MovementBehaviour.REGISTRY.register(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.NETHERITE_BACKPACK.get(),backpackBehaviour);
            }
            if(list.isLoaded(FunctionalStorage)){
                StorageHandlerHelper.register(new FunctionalDrawersHandlerHelper());
                StorageHandlerHelper.register(new FunctionalCompactingHandlerHelper());
                register(new FunctionalFluidHandlerHelper());
            }
            if(SmarterContraptionStorageConfig.AE2Loaded()){
                StorageHandlerHelper.register(new AE2BusBlockHelper());
                StorageHandlerHelper.register(new MEStorageFilter());
                StorageHandlerHelper.register(new AEControllerBlock());
                StorageHandlerHelper.register(new AEEnergyBlock());
                StorageHandlerHelper.register(new SpatialHandler());
            }
//            if(list.isLoaded(CobbleForDays))
//                StorageHandlerHelper.register(new CobblestoneGenerator());
        }
        MovingItemStorageType.register();
    }

    public static void registerPacket(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar("1.0");
        registrar.playToServer(
                MenuLevelPacket.TYPE,
                MenuLevelPacket.STREAM_CODEC,
                MenuLevelPacket::handle
        );
    }
}
