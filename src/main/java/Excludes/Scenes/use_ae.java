package Excludes.Scenes;

import Excludes.CreateNBTFile;
import appeng.core.definitions.AEBlocks;
import com.simibubi.create.AllBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import static Excludes.CreateNBTFile.Facing.*;

public class use_ae extends CreateNBTFile {
    public use_ae(@NotNull String name) {
        super(name);
    }

    @Override
    public int setValue() {
        CompoundTag properties = new CompoundTag();
        CompoundTag nbt = new CompoundTag();
        CompoundTag tag = new CompoundTag();
        CompoundTag gn = new CompoundTag();
        int id;
        gn.putInt("p",0);
        gn.putLong("g",604L);
        gn.putLong("k",-1L);

        setBasePlate(5,5);
        addBlock(2,1,2,getBlockId(AEBlocks.CONTROLLER.block()));
        addBlock(2,1,1,getBlockId(AEBlocks.CREATIVE_ENERGY_CELL.block()));
        addBlock(3,1,2,getBlockId(AEBlocks.INTERFACE.block()));
        tag.putString("id","ae2:export_bus");
        tag.put("gn",gn);
        nbt.put("up",tag);
        tag = new CompoundTag();
        tag.putString("id","ae2:fluix_covered_cable");
        tag.put("gn",gn);
        nbt.put("cable",tag);
        nbt.putString("id",getBlockId(AEBlocks.CABLE_BUS.block()));
        id = addBlock(3,1,3,getBlockId(AEBlocks.CABLE_BUS.block()),null,nbt);
        tag = new CompoundTag();
        tag.putString("id","ae2:import_bus");
        tag.put("gn",gn);
        nbt.put("up",tag);
        addBlock(2,1,3,id,"nbt",nbt);

        east.setTag(properties);
        for (int x = 2; x < 6; x++)
            addStressBlock(x,3,3,getBlockId(AllBlocks.GANTRY_SHAFT.get()),properties, 32F,0);
        properties = new CompoundTag();
        properties.putString("axis","x");
        for (int y = -2; y <= 3; y++)
            addStressBlock(6,y,3,getBlockId(AllBlocks.ENCASED_CHAIN_DRIVE.get()),properties,32F,0);
        down.setTag(properties);
        addBlock(3,2,3,getBlockId(AllBlocks.GANTRY_CARRIAGE.get()),properties,properties);
        west.setTag(properties);
        addStressBlock(1,1,3,getBlockId(AllBlocks.MECHANICAL_DRILL.get()),properties,0F,0);
        addBlock(0,1,3,getBlockId(Blocks.STONE));

        return 3;
    }
}
