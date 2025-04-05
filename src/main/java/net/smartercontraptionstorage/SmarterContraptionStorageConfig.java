package net.smartercontraptionstorage;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Objects;

public class SmarterContraptionStorageConfig {
    private static SmarterContraptionStorageConfig INSTANCE;
    public final ModConfigSpec.BooleanValue DEFAULT_OPEN;
    public final ModConfigSpec.BooleanValue AUTO_DUMPING;
    public final ModConfigSpec.ConfigValue<Integer> SEARCH_RANGE;
    public final ModConfigSpec.BooleanValue AE2SUPPORT;
    public final ModConfigSpec.BooleanValue LOAD_CHUNK_AUTO;
    public static boolean getDefaultOpen(boolean open){
        return open == INSTANCE.DEFAULT_OPEN.get();
    }

    public static boolean AE2Loaded(){
        return INSTANCE.AE2SUPPORT.get() && ModList.get().isLoaded(SmarterContraptionStorage.AE2);
    }

    private SmarterContraptionStorageConfig(ModConfigSpec.Builder builder){
        DEFAULT_OPEN = builder.comment("""
                Weather default uses all storage blocks
                Instructions:
                If you set it true,the mod will open all of you contraption's storage by default
                If you set it false, your block will close by default,also,as the result you can't open it until they become block.
                And in any circumstances you can use Contraption Control Block to change the action it performed
                """)
                .translation(getKey("default_open"))
                .define("DefaultOpen", true);
        SEARCH_RANGE = builder.comment("Search range for neighboring block (best bigger than 9, because Vault maxsize is 9)")
                .translation(getKey("search_range"))
                .define("SearchRange",10);
        AUTO_DUMPING = builder.comment("""
                When contraption want to store any item which can save fluid (such as water bucket),
                we will automatically dump fluid into tanks and return an empty tankItem and
                automatically fill bucket with fluid when contraption extract it (such as extracting water bucket, and make water bucket right now)
                """)
                .translation(getKey("auto_dumping"))
                .define("AutoDumping",true);
        AE2SUPPORT = builder.comment("Allow to use AE2 on contraption")
                .translation(getKey("ae2support"))
                .define("AE2Loaded", true);
        LOAD_CHUNK_AUTO = builder.comment("Load Spatial chunks those are used on contraptions")
                .translation(getKey("load_chunk_auto"))
                .define("LoadChunkAuto", true);
    }

    public static String getKey(String key){
        return "smartercontraptionstorage.config." + key;
    }

    public static void register(ModContainer modContainer){
        Pair<SmarterContraptionStorageConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(SmarterContraptionStorageConfig::new);
        INSTANCE = pair.getLeft();
        ModConfigSpec CONFIG_SPEC = pair.getRight();
        Objects.requireNonNull(INSTANCE);
        Objects.requireNonNull(CONFIG_SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, CONFIG_SPEC);
    }

    public static void registerInClient(ModContainer modContainer){
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    public static boolean defaultOpen(){
        return INSTANCE.DEFAULT_OPEN.get();
    }

    public static int maxSearchRange(){
        return INSTANCE.SEARCH_RANGE.get();
    }

    public static boolean autoDumping(){
        return INSTANCE.AUTO_DUMPING.get();
    }

    public static boolean loadChunkAuto(){
        return INSTANCE.LOAD_CHUNK_AUTO.get();
    }

    public static boolean ae2Support(){
        return INSTANCE.AE2SUPPORT.get();
    }
}