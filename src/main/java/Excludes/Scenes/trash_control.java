package Excludes.Scenes;

import Excludes.CreateNBTFile;
import net.minecraft.nbt.*;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import static Excludes.CreateNBTFile.Facing.*;

public class trash_control extends CreateNBTFile {
    public trash_control(@NotNull String name) {
        super(name);
    }
    @Override
    public int setValue() {
        setBasePlate(4,4);
        CompoundTag properties = new CompoundTag();
        CompoundTag nbt = new CompoundTag();
        int id;

        id = addBlock(1,1,2,getBlockId(Blocks.COBBLESTONE));
        addBlock(0,1,2,id);
        addBlock(2,1,0,"trashcans:item_trash_can",null,"itemFilterWhitelist",ByteTag.valueOf((byte) 0));

        properties.put("waterlogged", StringTag.valueOf("false"));
        properties.put("axis",StringTag.valueOf("z"));
        addStressBlock(3,1,5,"create:cogwheel",properties,32,0);
        addStressBlock(4,0,5,"create:large_cogwheel",properties,-16,0);
        addStressBlock(3,1,3,"create:shaft",properties,-32,0);
        properties.put("powered",StringTag.valueOf("true"));
        addStressBlock(3,1,4,"create:gearshift",properties,-32,0);
        properties.put("face",StringTag.valueOf("floor"));
        east.setTag(properties);
        addBlock(3,2,4,"minecraft:lever",properties);

        properties = new CompoundTag();
        north.setTag(properties);
        addBlock(3,1,0,"create:brown_toolbox");
        west.setTag(properties);
        properties.put("axis_along_first",StringTag.valueOf("false"));
        properties.put("state",StringTag.valueOf("moving"));
        addStressBlock(3,1,2,"create:sticky_mechanical_piston",properties,-32,4);

        properties = new CompoundTag();
        west.setTag(properties);
        nbt.put("NextTick", IntTag.valueOf(0));
        nbt.put("Speed", FloatTag.valueOf(0));
        nbt.put("Progress",IntTag.valueOf(0));
        nbt.put("id",StringTag.valueOf("create:mechanical_drill"));
        addBlock(2,1,1,"create:mechanical_drill",properties,nbt);
        id = addBlock(4,1,1,"create:piston_extension_pole",properties);
        addBlock(5,1,1,id);
        properties.put("waterlogged",StringTag.valueOf("false"));
        properties.put("type",StringTag.valueOf("sticky"));
        addBlock(3,1,1,"create:mechanical_piston_head",properties);

        return 2;
    }
}