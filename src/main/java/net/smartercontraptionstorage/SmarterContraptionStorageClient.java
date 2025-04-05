package net.smartercontraptionstorage;

import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.smartercontraptionstorage.AddStorage.FluidHander.FunctionalFluidHandlerHelper;
import net.smartercontraptionstorage.AddStorage.FluidHander.TrashcanFluidHelper;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.MovingBlockEntityMenu;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.MovingBlockEntityScreen;
import net.smartercontraptionstorage.AddStorage.GUI.NormalMenu.*;
import net.smartercontraptionstorage.AddStorage.ItemHandler.*;
import net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper.AEControllerBlock;
import net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper.AEEnergyBlock;
import net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper.MEStorageFilter;
import net.smartercontraptionstorage.Ponder.SCS_Ponder;

import static net.smartercontraptionstorage.AddStorage.FluidHander.FluidHandlerHelper.register;
import static net.smartercontraptionstorage.SmarterContraptionStorage.*;

@Mod(value = SmarterContraptionStorage.MODID, dist = Dist.CLIENT)
public class SmarterContraptionStorageClient {
    public SmarterContraptionStorageClient(FMLModContainer container, IEventBus modEventBus, Dist dist) {
        SmarterContraptionStorageConfig.registerInClient(container);
        modEventBus.addListener(this::registerScreens);
        modEventBus.addListener(this::registerHelper);
        PonderIndex.addPlugin(new SCS_Ponder());
    }

    private void registerScreens(RegisterMenuScreensEvent event){
        ModList list = ModList.get();
        if(list.isLoaded("create")){
            event.register(MovingBlockEntityMenu.BlockEntityMenu.get(), MovingBlockEntityScreen::new);
            if(list.isLoaded(TrashCans)) {
                event.register(TrashHandlerHelper.TrashHandler.TrashCanMenu.get(), MovingTrashCanScreen::new);
            }
            if(list.isLoaded(StorageDrawers)){
                event.register(DrawersHandlerHelper.NormalDrawerHandler.DrawerMenu.get(), MovingDrawerScreen::new);
                event.register(CompactingHandlerHelper.CompactingHandler.CompactingDrawerMenu.get(), MovingCompactingDrawerScreen::new);
            }
            if(list.isLoaded(FunctionalStorage)){
                event.register(FunctionalDrawersHandlerHelper.FDrawersHandler.MENU_TYPE.get(), MovingFunctionalDrawerScreen::new);
                event.register(FunctionalCompactingHandlerHelper.FCDrawersHandler.MENU_TYPE.get(), MovingFunctionalCompactingScreen::new);
            }
        }
    }

    private void registerHelper(FMLClientSetupEvent event){
        ModList list = ModList.get();
        if(list.isLoaded("create")){
//            StorageHandlerHelper.register(ToolboxHandlerHelper.INSTANCE);
            if(list.isLoaded(TrashCans)) {
                StorageHandlerHelper.register(new TrashHandlerHelper());
                register(new TrashcanFluidHelper());
            }
            if(list.isLoaded(StorageDrawers)) {
                StorageHandlerHelper.register(new DrawersHandlerHelper());
                StorageHandlerHelper.register(new CompactingHandlerHelper());
            }
            if(SmarterContraptionStorageConfig.AE2Loaded()) {
                StorageHandlerHelper.register(new AE2BusBlockHelper());
                StorageHandlerHelper.register(new MEStorageFilter());
                StorageHandlerHelper.register(new AEControllerBlock());
                StorageHandlerHelper.register(new AEEnergyBlock());
                StorageHandlerHelper.register(new SpatialHandler());
            }
//            if(list.isLoaded(CobbleForDays))
//                StorageHandlerHelper.register(new CobblestoneGenerator());
//            if(list.isLoaded(SBackPack)){
//                StorageHandlerHelper.register(SBackPacksHandlerHelper.INSTANCE);
//                register(new SBackPacksFluidHandlerHelper());
//            }
            if(list.isLoaded(FunctionalStorage)){
                StorageHandlerHelper.register(new FunctionalDrawersHandlerHelper());
                StorageHandlerHelper.register(new FunctionalCompactingHandlerHelper());
                register(new FunctionalFluidHandlerHelper());
            }
        }
    }
}
