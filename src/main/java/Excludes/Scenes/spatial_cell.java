package Excludes.Scenes;

import Excludes.CreateNBTFile;
import appeng.core.definitions.AEBlocks;
import com.simibubi.create.AllBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import static Excludes.CreateNBTFile.Facing.north;
import static Excludes.CreateNBTFile.Facing.west;

public class spatial_cell extends CreateNBTFile {
    public static final int CELL_SIZE = 4;
    public spatial_cell(@NotNull String name) {
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

        setBasePlate(7,7);
        tag.put("gn",gn);
        addBlock(CELL_SIZE,1,CELL_SIZE,getBlockId(AEBlocks.SPATIAL_IO_PORT.block()),null,nbt);
        tag.remove("gn");

        tag.put("proxy",gn);
        id = addBlock(0,7,CELL_SIZE,getBlockId(AEBlocks.SPATIAL_PYLON.block()),null,nbt);
        for (int i = 1; i < CELL_SIZE; i++) {
            addBlock(i, 1, CELL_SIZE, id, nbt);
            addBlock(CELL_SIZE,7,i - 1,id,nbt);
        }
        addBlock(CELL_SIZE,7,CELL_SIZE - 1,id,nbt);
        addBlock(CELL_SIZE,7,CELL_SIZE,id,nbt);
        tag.putString("id","ae2:fluix_covered_cable");
        tag.put("gn",gn);
        nbt.put("cable",tag);
        nbt.putString("id",getBlockId(AEBlocks.CABLE_BUS.block()));
        for (int y = 2; y < 7; y++)
            addBlock(CELL_SIZE,y,CELL_SIZE,getBlockId(AEBlocks.CABLE_BUS.block()),null,nbt);

        north.setTag(properties);
        addBlock(CELL_SIZE - 1,3,CELL_SIZE - 1,getBlockId(Blocks.CHEST),properties);
        addBlock(CELL_SIZE - 1,3,CELL_SIZE - 2,getBlockId(AllBlocks.ITEM_VAULT.get()),properties);
        west.setTag(properties);
        addBlock(CELL_SIZE,0,CELL_SIZE,getBlockId(Blocks.RAIL),properties);
        addBlock(CELL_SIZE,-1,CELL_SIZE,getBlockId(Blocks.SNOW_BLOCK));
        addStressBlock(0,1,4,getBlockId(AllBlocks.MECHANICAL_DRILL.get()),properties,0,0);
        return 4;
    }
}
