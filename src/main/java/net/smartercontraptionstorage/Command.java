package net.smartercontraptionstorage;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.smartercontraptionstorage.Render.Overlay;

@Mod(SmarterContraptionStorage.MODID)
public class Command {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        Overlay.registerCommand(dispatcher,event.getBuildContext());
    }
}
