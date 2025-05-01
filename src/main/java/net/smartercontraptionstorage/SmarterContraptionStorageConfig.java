package net.smartercontraptionstorage;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;

import static net.smartercontraptionstorage.SmarterContraptionStorage.AE2;

public class SmarterContraptionStorageConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue DEFAULT_OPEN;
    public static final ForgeConfigSpec.BooleanValue AUTO_DUMPING;
    public static final ForgeConfigSpec.ConfigValue<Integer> SEARCH_RANGE;
    public static final ForgeConfigSpec.BooleanValue AE2SUPPORT;
    public static final ForgeConfigSpec.BooleanValue LOAD_CHUNK_AUTO;
    static {
        BUILDER.push("Config for More legal Storage on Contraption !");// push will create an indent
        DEFAULT_OPEN = BUILDER.comment("""
                Weather default uses all storage blocks
                Instructions:
                If you set it true,the mod will open all of you contraption's storage by default
                If you set it false, your block will close by default,also,as the result you can't open it until they become block.
                And in any circumstances you can use Contraption Control Block to change the action it performed
                """).define("DefaultOpen",true);
        SEARCH_RANGE = BUILDER.comment("Search range for neighboring block (best bigger than 9, because Vault maxsize is 9)").define("SearchRange",10);
        AUTO_DUMPING = BUILDER.comment("""
                When contraption want to store any item which can save fluid (such as water bucket),
                we will automatically dump fluid into tanks and return an empty tankItem and
                automatically fill bucket with fluid when contraption extract it (such as extracting water bucket, and make water bucket right now)
                """).define("AutoDumping",true);
        AE2SUPPORT = BUILDER.comment("Allow to use AE2 on contraption").define("AE2_Support",false);
        LOAD_CHUNK_AUTO = BUILDER.comment("Load Spatial chunks those are used on contraptions").define("load_chunk_auto",true);
        BUILDER.pop();// pop will unindent
        SPEC = BUILDER.build();
    }
    public static boolean getDefaultOpen(boolean open){
        return open == DEFAULT_OPEN.get();
    }
    public static boolean AE2Loaded(){
        return AE2SUPPORT.get() && ModList.get().isLoaded(AE2);
    }
}