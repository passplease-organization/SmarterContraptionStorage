package net.smartercontraptionstorage.Ponder;

//import Excludes.BuildNBTFile;
import appeng.api.ids.AEBlockIds;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.simibubi.create.AllBlocks;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.ModList;
import net.smartercontraptionstorage.SmarterContraptionStorage;
import net.smartercontraptionstorage.SmarterContraptionStorageConfig;

import static net.smartercontraptionstorage.Utils.asResources;

public class SCS_Ponder implements PonderPlugin {
    public static final ResourceLocation CONTROLLABLE_CONTAINERS = asResources("controllable_containers");
    @Override
    public String getModId() {
        return SmarterContraptionStorage.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        //BuildNBTFile.createNBTFile();
        PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);
        HELPER.forComponents(AllBlocks.CONTRAPTION_CONTROLS)
                .addStoryBoard("ordinary_control", MovementActorScenes::changeOrdinary,CONTROLLABLE_CONTAINERS)
                .addStoryBoard("storage_control",MovementActorScenes::controlStorageBlock,CONTROLLABLE_CONTAINERS);
        HELPER.forComponents(AllBlocks.TOOLBOXES)
                .addStoryBoard("replenish_item",ToolboxScenes::replenishItem,CONTROLLABLE_CONTAINERS)
                .addStoryBoard("trash_control", ToolboxScenes::trashcansControl, CONTROLLABLE_CONTAINERS);
        ModList list = ModList.get();
        if(list.isLoaded("create")){
            if(list.isLoaded(SmarterContraptionStorage.TrashCans))
                HELPER.forComponents(AllBlocks.TOOLBOXES).addStoryBoard("trash_control", ToolboxScenes::trashcansControl, CONTROLLABLE_CONTAINERS);
            if(SmarterContraptionStorageConfig.AE2Loaded()){
                helper.addStoryBoard(AEBlockIds.CONTROLLER, "use_ae",AEScenes::useAE);
                helper.addStoryBoard(AEBlockIds.SPATIAL_PYLON,"spatial_cell",AEScenes::spatialCell);
            }
        }
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<Block> HELPER = helper.withKeyFunction(Block::getLootTable);
        helper.registerTag(CONTROLLABLE_CONTAINERS)
                .addToIndex()
                .item(AllBlocks.CONTRAPTION_CONTROLS)
                .title("Controllable Containers")
                .description("Containers can be controlled by Contraption Controls")
                .register();
        helper.addToTag(CONTROLLABLE_CONTAINERS)
                .add(AllBlocks.ITEM_VAULT.getId())
                .add(AllBlocks.TOOLBOXES.get(DyeColor.BROWN).getId());
        HELPER.addToTag(CONTROLLABLE_CONTAINERS)
                .add(Blocks.CHEST)
                .add(Blocks.TRAPPED_CHEST)
                .add(Blocks.BARREL);
        ModList list = ModList.get();
        if(list.isLoaded("create")){
            if(list.isLoaded(SmarterContraptionStorage.StorageDrawers))
                HELPER.addToTag(CONTROLLABLE_CONTAINERS)
                        .add(ModBlocks.DARK_OAK_FULL_DRAWERS_1.get())
                        .add(ModBlocks.DARK_OAK_FULL_DRAWERS_2.get())
                        .add(ModBlocks.DARK_OAK_FULL_DRAWERS_4.get())
                        .add(ModBlocks.COMPACTING_DRAWERS_3.get());
        }
    }
}