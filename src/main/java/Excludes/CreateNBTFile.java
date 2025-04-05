package Excludes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.smartercontraptionstorage.Interface.TriFunction;
import net.minecraft.nbt.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class CreateNBTFile implements TriFunction<CompoundTag,String, Integer,CompoundTag> {
    @NotNull String Name;
    private static final int WRONG_BLOCK_ID = -1;
    public CreateNBTFile(@NotNull String name){
        Name = name;
    }
    public enum Facing{
        north("north"),
        south("south"),
        west("west"),
        east("east"),
        up("up"),
        down("down");
        final StringTag value;
        Facing(String facing){
            this.value = StringTag.valueOf(facing);
        }
        public void setTag(CompoundTag tag){
            tag.put("facing",value);
        }
    }

    @Override
    public final CompoundTag function(CompoundTag tag, String string, Integer integer) {
        int DataVersion = setValue();
        addSize();
        addStressBlock();
        tag.put("size",s);
        tag.put("entities",e);
        tag.put("blocks",b);
        tag.put("palette",p);
        tag.put("DataVersion",IntTag.valueOf(DataVersion));
        return tag;
    }
    public abstract int setValue();// return DataVersion

    private final ListTag s = new ListTag();
    private void addSize(){
        int x = 1000;
        int y = 1000;
        int z = 1000;
        int X = -1000;
        int Y = -1000;
        int Z = -1000;
        for(Tag block : b)
            if(block instanceof CompoundTag)
                if(((CompoundTag) block).get("pos") instanceof ListTag pos){
                    x = Math.min(pos.getInt(0),x);
                    X = Math.max(pos.getInt(0),X);
                    y = Math.min(pos.getInt(1),y);
                    Y = Math.max(pos.getInt(1),Y);
                    z = Math.min(pos.getInt(2),z);
                    Z = Math.max(pos.getInt(2),Z);
                }
        if(x <= X && y <= Y && z <= Z){
            s.add(IntTag.valueOf(X - x + 1));
            s.add(IntTag.valueOf(Y - y + 1));
            s.add(IntTag.valueOf(Z - z + 1));
        }else throw new RuntimeException("The size of ponder is wrong");
    }
    private final ListTag e = new ListTag();
    public void addEntity(EntityHelper helper){
        CompoundTag tag = new CompoundTag();
        ListTag pos = new ListTag();
        ListTag blocPos = new ListTag();
        pos.add(DoubleTag.valueOf(helper.pos_x));
        pos.add(DoubleTag.valueOf(helper.pos_y));
        pos.add(DoubleTag.valueOf(helper.pos_z));
        blocPos.add(IntTag.valueOf(helper.blocPos_x));
        blocPos.add(IntTag.valueOf(helper.blocPos_y));
        blocPos.add(IntTag.valueOf(helper.blocPos_z));
        tag.put("nbt",helper.generateNBT());
        tag.put("blockPos",blocPos);
        tag.put("pos",pos);
        e.add(tag);
    }
    private final ListTag b = new ListTag();
    public int addBlock(int x,int y,int z,@NotNull String blockId){
        return addBlock(x,y,z,blockId,null,null,null);
    }
    public int addBlock(int x,int y,int z,@NotNull String blockId,@Nullable CompoundTag properties){
        return addBlock(x,y,z,blockId,properties,null,null);
    }
    public int addBlock(int x,int y,int z,@NotNull String blockId,@Nullable CompoundTag properties,@Nullable Tag nbt){
        return addBlock(x,y,z,blockId,properties,"nbt",nbt);
    }
    public int addBlock(int x,int y,int z,@NotNull String blockId,@Nullable CompoundTag properties,String name,@Nullable Tag tag){
        CompoundTag Tag = new CompoundTag();
        int id;
        if(properties != null)
            id = addPalette(blockId,properties);
        else id = findBlockId(blockId);
        if(id == WRONG_BLOCK_ID)
            id = addPalette(blockId,properties);
        if(tag != null)
            Tag.put(name, tag.copy());
        ListTag pos = new ListTag();
        pos.add(IntTag.valueOf(x));
        pos.add(IntTag.valueOf(y));
        pos.add(IntTag.valueOf(z));
        Tag.put("pos",pos);
        Tag.put("state",IntTag.valueOf(id));
        b.add(Tag);
        return id;
    }
    public void addBlock(int x,int y,int z,int id){
        addBlock(x,y,z,id,null,null);
    }
    public void addBlock(int x,int y,int z,int id,@NotNull CompoundTag tag){
        addBlock(x,y,z,id,"nbt",tag);
    }
    public void addBlock(int x,int y,int z,int id,@Nullable String name,@Nullable CompoundTag tag){
        CompoundTag Tag = new CompoundTag();
        ListTag pos = new ListTag();
        pos.add(IntTag.valueOf(x));
        pos.add(IntTag.valueOf(y));
        pos.add(IntTag.valueOf(z));
        Tag.put("pos",pos);
        Tag.put("state",IntTag.valueOf(id));
        if(name != null && tag != null)
            Tag.put(name,tag.copy());
        b.add(Tag);
    }
    public boolean containsBlock(int x,int y,int z){
        return b.stream().anyMatch(nbt -> {
            assert nbt instanceof CompoundTag;
            ListTag pos = (ListTag) ((CompoundTag) nbt).get("pos");
            assert pos != null;
            return pos.getInt(0) == x &&
                    pos.getInt(1) == y &&
                    pos.getInt(2) == z;
        });
    }
    public int findBlockId(String blockId){
        for (int i = 0; i < p.size(); i++){
            if(p.get(i) instanceof CompoundTag){
                if(((CompoundTag) p.get(i)).getString("Name").endsWith(blockId))
                    return i;
            }
        }
        return WRONG_BLOCK_ID;
    }
    private final ListTag p = new ListTag();
    public int addPalette(String blockId,@Nullable CompoundTag properties){
        blockId = blockId.replace("block.","").replace(".",":");
        int i =findBlockId(blockId);
        if(i != WRONG_BLOCK_ID)
            return i;
        CompoundTag tag = new CompoundTag();
        if(properties != null)
            tag.put("Properties",properties.copy());
        tag.put("Name",StringTag.valueOf(blockId));
        p.add(tag);
        return p.size() - 1;
    }
    public void setBasePlate(int x, int z){
        setBasePlate(0,0,x,z,null,"minecraft:white_concrete","minecraft:snow_block");
    }
    public void setBasePlate(int x, int z,@Nullable Runnable runner){
        setBasePlate(0,0,x,z,runner,"minecraft:white_concrete","minecraft:snow_block");
    }
    public void setBasePlate(int x,int z,@Nullable Runnable runner,String... blockId){
        setBasePlate(0,0,x,z,runner,blockId);
    }
    public void setBasePlate(int x,int z,int X,int Z,@Nullable Runnable runner,String... blockId){
        int size = blockId.length;
        int i = 0;
        int[] id = new int[size];
        if(runner != null)
            runner.run();
        for (int j = 0; j < size; j++) {
            id[j] = findBlockId(blockId[j]);
            if(id[j] == WRONG_BLOCK_ID)
                id[j] = addPalette(blockId[j],null);
        }
        for(int a = x;a <= X;a++,i = (a - x) % size)
            for (int c = z; c <= Z; c++) {
                if(runner == null || !containsBlock(a,0,c))
                    addBlock(a,0,c,id[i]);
                i++;
                i = i >= size ? i - size : i;
            }
    }
    private final Map<StressBlock,CompoundTag> stressBlock = new HashMap<>();
    public void addStressBlock(int x, int y, int z, String blockId,@Nullable CompoundTag properties, float speed, float addedStress){
        addStressBlock(x,y,z,blockId,properties,speed, (byte) 0,addedStress,null);
    }
    public void addStressBlock(int x, int y, int z, String blockId,@Nullable CompoundTag properties, float speed, byte update, float addedStress,@Nullable Function<CompoundTag,CompoundTag> other){
        if(properties != null)
            addPalette(blockId,properties);
        CompoundTag tag = new CompoundTag();
        tag.put("Speed",FloatTag.valueOf(speed));
        tag.put("NeedSpeedUpdate",ByteTag.valueOf(update));
        tag.put("id",StringTag.valueOf(blockId));
        if(other != null)
            other.apply(tag);
        stressBlock.put(new StressBlock(x, y, z, blockId, speed, addedStress),tag);
    }
    private void addStressBlock(){
        CompoundTag netWork = new CompoundTag();
        netWork.put("Capacity",FloatTag.valueOf(262164F));
        netWork.put("size",IntTag.valueOf(stressBlock.size()));
        netWork.put("Id",LongTag.valueOf(-8246336864199L));
        stressBlock.forEach((block,tag)->{
            CompoundTag Network = netWork.copy();
            if(block.addedStress != 0) {
                Network.put("AddedStress", FloatTag.valueOf(block.addedStress));
                Network.put("Stress", FloatTag.valueOf(block.addedStress * block.speed));
            }
            tag.put("Network",Network);
            addBlock(block.x,block.y,block.z,block.blockId,null,tag);
        });
    }
    public static String getBlockId(@NotNull Block block){
        return block.getDescriptionId().replace("blocks.", "").replace("block.","");
    }
    public void addTree(int x,int y,int z){
        String log = getBlockId(Blocks.OAK_LOG);
        String leaf = getBlockId(Blocks.OAK_LEAVES);
        for(int height = 0;height < 5;height++)
            addBlock(x,y + height,z,log);
        for (int x_offset = -2; x_offset <= 2; x_offset++)
            for (int z_offset = -2; z_offset <= 2; z_offset++)
                if(x_offset != 0 || z_offset != 0) {
                    if ((x_offset != -2 || z_offset != -2)) {// for beautiful
                        addBlock(x + x_offset, y + 2, z + z_offset, leaf);
                        if(x_offset != 2 || z_offset != 2)
                            addBlock(x + x_offset, y + 3, z + z_offset, leaf);
                    }
                }
        addBlock(x + 1,y + 4,z,leaf);
        addBlock(x - 1,y + 4,z,leaf);
        addBlock(x,y + 4,z + 1,leaf);
        addBlock(x,y + 4,z - 1,leaf);
        addBlock(x - 1,y + 4,z - 1,leaf);// for beautiful

        addBlock(x + 1,y + 5,z,leaf);
        addBlock(x - 1,y + 5,z,leaf);
        addBlock(x,y + 5,z + 1,leaf);
        addBlock(x,y + 5,z - 1,leaf);
        addBlock(x,y + 5,z,leaf);
    }
    private static class StressBlock {
        int x;
        int y;
        int z;
        float speed;
        float addedStress;
        String blockId;
        private StressBlock(int x, int y, int z, String blockId, float speed, float addedStress){
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockId = blockId;
            this.speed = speed;
            this.addedStress = addedStress;
        }
    }
    public static class EntityHelper{
        public String id;
        public double pos_x;
        public double pos_y;
        public double pos_z;
        public int blocPos_x;
        public int blocPos_y;
        public int blocPos_z;
        public Facing facing;
        public EntityHelper(String id,double x,double y,double z){
            this(id,x,y,z, (int) x, (int) y, (int) z);
        }
        public EntityHelper(String id, double pos_x,double pos_y,double pos_z,int blocPos_x,int blocPos_y,int blocPos_z){
            this.id = id;
            this.pos_x = pos_x;
            this.pos_y = pos_y;
            this.pos_z = pos_z;
            this.blocPos_x = blocPos_x;
            this.blocPos_y = blocPos_y;
            this.blocPos_z = blocPos_z;
        }
        public EntityHelper(EntityHelper helper){
            id = helper.id;
            pos_x = helper.pos_x;
            pos_y = helper.pos_y;
            pos_z = helper.pos_z;
            blocPos_x = helper.blocPos_x;
            blocPos_y = helper.blocPos_y;
            blocPos_z = helper.blocPos_z;
        }
        public CompoundTag generateNBT(){
            CompoundTag nbt = new CompoundTag();
            nbt.putString("id",id);
            return nbt;
        }
    }
}