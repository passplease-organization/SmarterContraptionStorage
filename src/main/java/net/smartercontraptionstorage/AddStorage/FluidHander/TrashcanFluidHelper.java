package net.smartercontraptionstorage.AddStorage.FluidHander;

import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.filter.ItemFilter;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.smartercontraptionstorage.Utils.getFluidByItem;

public class TrashcanFluidHelper extends FluidHandlerHelper {
    @Override
    public void addStorageToWorld(BlockEntity entity, SmartFluidTank helper) {
        assert canCreateHandler(entity);
    }

    @Override
    public boolean canCreateHandler(Item comparedItem) {
        String name = comparedItem.getDescriptionId();
        return name.startsWith("trashcans.block.item") || name.startsWith("trashcans.block.ultimate");
    }
    @Override
    public boolean canCreateHandler(Block block) {
        return false;
    }
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof TrashCanBlockEntity && ((TrashCanBlockEntity)entity).liquids;
    }
    @Override
    public @NotNull SmartFluidTank createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        return new TrashcanHelper((TrashCanBlockEntity) entity);
    }

    @Override
    public String getName() {
        return "TrashcanFluidHelper";
    }

    @Override
    public @NotNull SmartFluidTank deserialize(CompoundTag nbt) {
        return new TrashcanHelper(nbt);
    }

    public static class TrashcanHelper extends FluidHelper implements NeedDealWith {
        public final boolean whiteOrBlack;
        public final ArrayList<FluidStack> filter;
        public List<FluidStack> toolboxFluid;
        public TrashcanHelper(TrashCanBlockEntity entity) {
            super(entity.FLUID_HANDLER.getTankCapacity(0));
            whiteOrBlack = entity.liquidFilterWhitelist;
            filter = new ArrayList<>();
            ArrayList<FluidStack> list;
            for(ItemFilter item : entity.liquidFilter)
                if(item != null) {
                    list = getFluidByItem(item.getRepresentingItem());
                    if (list != null)
                        filter.addAll(list);
                }
            fluid = FluidStack.EMPTY;
        }

        public TrashcanHelper(CompoundTag nbt) {
            super(nbt);
            whiteOrBlack = nbt.getBoolean("whiteOrBlack");
            filter = new ArrayList<>();
            nbt.getList("filter",Tag.TAG_COMPOUND).forEach(tag -> filter.add(FluidStack.loadFluidStackFromNBT((CompoundTag) tag)));
            toolboxFluid = new ArrayList<>();
            nbt.getList("toolboxFluid",Tag.TAG_COMPOUND).forEach(tag -> toolboxFluid.add(FluidStack.loadFluidStackFromNBT((CompoundTag) tag)));
        }

        @Override
        public boolean canFill(FluidStack stack){
            for(FluidStack toolboxFluid : this.toolboxFluid)
                if(stack.isFluidEqual(toolboxFluid))
                    return false;
            for(FluidStack filterFluid : filter)
                if(stack.isFluidEqual(filterFluid))
                    return whiteOrBlack;
            return !whiteOrBlack;
        }
        @Override
        public void setFluid(int amount,FluidStack stack) {}
        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if(canFill(resource))
                return resource.getAmount();
            else return 0;
        }

        @Override
        public int getAmount() {
            return 0;
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
            return FluidStack.EMPTY;
        }

        @Override
        public CompoundTag serialize(CompoundTag nbt) {
            nbt.putBoolean("whiteOrBlack",whiteOrBlack);
            ListTag filterTag = new ListTag();
            filter.forEach((stack) -> filterTag.add(stack.writeToNBT(new CompoundTag())));
            nbt.put("filter",filterTag);
            ListTag toolboxFluidTag = new ListTag();
            toolboxFluid.forEach((stack) -> toolboxFluidTag.add(stack.writeToNBT(new CompoundTag())));
            nbt.put("toolboxFluid",toolboxFluidTag);
            return nbt;
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            return FluidStack.EMPTY;
        }

        @Override
        public void doSomething(BlockEntity entity) {}

        @Override
        public void finallyDo() {
            List<ItemStack> toolboxItem = new ArrayList<>();
            for(BlockEntity entity : BlockEntityList)
                if(entity instanceof ToolboxBlockEntity){
                    toolboxItem.addAll(NBTHelper.readItemList(entity.serializeNBT().getCompound("Inventory").getList("Compartments", Tag.TAG_COMPOUND)));
                }
            this.toolboxFluid = toolboxItem.stream().filter((item)->!item.isEmpty()).collect(ArrayList::new,(fluid,item)-> fluid.addAll(getFluidByItem(item)), ArrayList::addAll);
        }
    }
}