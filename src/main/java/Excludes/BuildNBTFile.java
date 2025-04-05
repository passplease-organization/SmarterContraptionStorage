package Excludes;

import Excludes.Scenes.*;
import net.smartercontraptionstorage.SmarterContraptionStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class BuildNBTFile {
    private static int num = 1;
    private static final String Path = Paths.get("").toAbsolutePath().toString()
            .replace("run","src/main/resources/assets/" + SmarterContraptionStorage.MODID + "/ponder/")
            .replace('\\','/');
    private static final List<CreateNBTFile> list = new ArrayList<>();
    static {
        list.add(new storage_control("storage_control"));
        list.add(new trash_control("trash_control"));
        list.add(new replenish_item("replenish_item"));
        list.add(new use_ae("use_ae"));
        list.add(new spatial_cell("spatial_cell"));
        list.add(new ordinary_control("ordinary_control"));
    }
    public static void createNBTFile() {
        String path;
        for(CreateNBTFile ponder : list) {
            path = ponder.Name + ".nbt";
            createNBTFile(Path + path, ponder);
        }
    }
    private static void createNBTFile(String path, CreateNBTFile writeToCompoundTag){
        CompoundTag nbt = writeToCompoundTag.function(new CompoundTag(),path,num);
        File file = new File(path);
        try(OutputStream stream = new FileOutputStream(file)){
            NbtIo.writeCompressed(nbt,stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        num++;
    }
}