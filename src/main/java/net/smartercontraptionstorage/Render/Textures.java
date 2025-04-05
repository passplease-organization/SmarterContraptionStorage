package net.smartercontraptionstorage.Render;

import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.smartercontraptionstorage.SmarterContraptionStorage;

@Mod(value = SmarterContraptionStorage.MODID,dist = Dist.CLIENT)
public class Textures {
    // register by json file
//    @SubscribeEvent
//    public static void register(TextureStitchEvent.Pre event){
//        if(event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS)){
//            Overlay.register(event);
//        }
//    }
    @SubscribeEvent
    public static void setValue(TextureAtlasStitchedEvent event){
        if(event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS)){
            Overlay.setUV(event);
        }
    }
}
