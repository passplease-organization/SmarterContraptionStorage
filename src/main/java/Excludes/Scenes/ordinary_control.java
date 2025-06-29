package Excludes.Scenes;

import com.simibubi.create.AllBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import static Excludes.Scenes.CreateNBTFile.Facing.*;

public class ordinary_control extends CreateNBTFile {
    public ordinary_control(@NotNull String name) {
        super(name);
    }

    @Override
    public int setValue() {
        CompoundTag properties = new CompoundTag();

        setBasePlate(8,8,() -> {
            addBlock(0,0,4,getBlockId(Blocks.GRASS_BLOCK));
            addBlock(4,0,8,getBlockId(Blocks.GRASS_BLOCK));
            addBlock(8,0,4,getBlockId(Blocks.GRASS_BLOCK));
            addBlock(4,0,0,getBlockId(Blocks.GRASS_BLOCK));
        });
        up.setTag(properties);
        addStressBlock(4,1,4,getBlockId(AllBlocks.MECHANICAL_BEARING.get()),properties,16,4);
        properties.putString("axis","x");
        properties.putString("axis_along_first","true");
        addBlock(4,2,4,getBlockId(AllBlocks.LINEAR_CHASSIS.get()),properties);
        addBlock(3,2,4,getBlockId(AllBlocks.LINEAR_CHASSIS.get()),properties);
        properties.putString("sticky_top","true");
        addBlock(3,1,4,getBlockId(AllBlocks.LINEAR_CHASSIS.get()),properties);
        addBlock(3,1,5,getBlockId(AllBlocks.LINEAR_CHASSIS.get()),properties);
        properties.remove("sticky_top");
        west.setTag(properties);
        addBlock(2,1,4,getBlockId(AllBlocks.DEPLOYER.get()),properties);
        north.setTag(properties);
        addBlock(0,1,5,getBlockId(AllBlocks.MECHANICAL_SAW.get()),properties);

        properties = new CompoundTag();
        west.setTag(properties);
        addBlock(2,1,5,getBlockId(Blocks.BARREL),properties);
        addBlock(1,1,5,getBlockId(Blocks.BARREL),properties);
        south.setTag(properties);
        properties.put("open", StringTag.valueOf("false"));
        addBlock(0,2,5,getBlockId(AllBlocks.CONTRAPTION_CONTROLS.get()),properties);

        addTree(0,1,4);
        addTree(4,1,8);
        return 1;
    }
}