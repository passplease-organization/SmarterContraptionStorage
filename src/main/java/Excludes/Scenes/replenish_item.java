package Excludes.Scenes;

import Excludes.CreateNBTFile;
import com.simibubi.create.AllBlocks;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static Excludes.CreateNBTFile.Facing.*;

public class replenish_item extends CreateNBTFile {
    public replenish_item(@NotNull String name) {
        super(name);
    }
    public static final Random random = new Random();
    public static final int MAX_x = 8;
    public static final int MAX_y = 2;
    public static final int MAX_z = 5;
    public static final int MIN_x = -4;
    public static final int MIN_y = -2;
    public static final int MIN_z = -4;
    public final Map<BlockPos,Ores> orePoses = new HashMap<>();
    @Override
    public int setValue() {
        spawnOres();
        int id = addPalette(Blocks.STONE.getDescriptionId(),null);
        for (int y = MIN_y; y <= MAX_y; y++)
            for (int x = MIN_x; x <= MAX_x; x++)
                for (int z = MIN_z; z <= MAX_z; z++) {
                    if(addOres(x,y,z))
                        continue;
                    addBlock(x, y, z, id);
                }
        for (int x = -8; x < MIN_x; x++)
            for (int z = MIN_z; z < MAX_z; z++)
                addBlock(x,-1,z,id);
        addBlock(MAX_x / 2,-16,2,id);
        addBlock(MAX_x / 2,-16,3,id);
        addBlock(MAX_x / 2 + 1,-16,2,id);
        addBlock(MAX_x / 2 + 1,-16,3,id);
        CompoundTag properties = new CompoundTag();
        properties.putString("axis","x");
        addBlock(-9,0,-1,"create:minecart_anchor",properties);

        properties = new CompoundTag();
        CompoundTag nbt = new CompoundTag();
        east.setTag(properties);
        nbt.put("NextTick", IntTag.valueOf(0));
        nbt.put("Speed", FloatTag.valueOf(0));
        nbt.put("Progress",IntTag.valueOf(0));
        nbt.put("id", StringTag.valueOf("create:mechanical_drill"));
        id = addPalette("create:mechanical_drill",properties);
        for (int y = 0; y <= 2; y++)
            for (int z = -4; z <= 3; z++)
                addBlock(-8,y,z,id,"nbt",nbt);

        nbt = new CompoundTag();
        nbt.putString("id","create:deployer");
        nbt.putString("Mode","USE");
        nbt.putFloat("Speed",0);
        nbt.put("HeldItem", NBTHelper.writeItemList(Collections.singleton(Items.RAIL.getDefaultInstance())));
        addBlock(-10,0,-1, AllBlocks.DEPLOYER.get().getDescriptionId(),properties,"nbt",nbt);
        id = addBlock(-9,1,-1,Blocks.BARREL.getDescriptionId(),properties);
        addBlock(-10,1,-1,id,properties);
        north.setTag(properties);
        addBlock(-11,0,-1,AllBlocks.TOOLBOXES.get(DyeColor.BROWN).get().getDescriptionId(),properties);
        return 3;
    }
    public boolean addOres(int x,int y,int z){
        if(x == MAX_x / 2 && y <= -1 && z == 2)// leave SpatialPylonBlockEntityMixin hole to let player fall in
            return true;
        Ores ore = searchOres(x,y,z);
        if(ore != null) {
            addBlock(x, y, z,ore.block.getDescriptionId());
            return true;
        }
        return false;
    }
    private @Nullable Ores searchOres(int x, int y, int z){
        Ores ore;
        for(BlockPos pos : Utils.getAroundedBlockPos(new BlockPos(x,y,z))){
            ore = orePoses.getOrDefault(pos,null);
            if(ore != null)
                return ore;
        }
        return null;
    }
    public void spawnOres(){
        orePoses.clear();
        int x,y,z,times = 0;
        while (true){
            x = random.nextInt() % (1 + MAX_x - MIN_x) + MIN_x;
            y = random.nextInt() % (1 + MAX_y - MIN_y) + MIN_y;
            z = random.nextInt() % (1 + MAX_z - MIN_z) + MIN_z;
            if(x == MAX_x || x == MIN_x || y == MAX_y || y == MIN_y || z == MAX_z || z == MIN_z)
                if(++times == 4)
                    return;
            orePoses.put(new BlockPos(x,y,z),Ores.getOre(random.nextInt()));
        }
    }
    public enum Ores{
        Iron(Blocks.IRON_ORE),
        Coal(Blocks.COAL_ORE),
        Gold(Blocks.GOLD_ORE),
        Diamond(Blocks.DIAMOND_ORE);
        final Block block;
        Ores(Block block){
            this.block = block;
        }
        public static Ores getOre(int num){
            switch (num % 4 + 1){
                case 1 -> {
                    return Iron;
                }
                case 2 -> {
                    return Coal;
                }
                case 3 -> {
                    return Gold;
                }
                default -> {
                    return Diamond;
                }
            }
        }
    }
}