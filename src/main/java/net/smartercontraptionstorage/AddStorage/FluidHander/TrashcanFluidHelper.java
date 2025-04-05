package net.smartercontraptionstorage.AddStorage.FluidHander;

import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.filter.ItemFilter;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
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
    public SmartFluidTank deserialize(CompoundTag nbt, HolderLookup.Provider provider) throws IllegalAccessException {
        return new TrashcanHelper(nbt,provider);
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

        public TrashcanHelper(CompoundTag nbt, HolderLookup.Provider provider) {
            super(nbt,provider);
            whiteOrBlack = nbt.getBoolean("whiteOrBlack");
            filter = new ArrayList<>();
            nbt.getList("filter",Tag.TAG_COMPOUND).forEach(tag -> filter.add(FluidStack.parseOptional(provider,(CompoundTag) tag)));
            toolboxFluid = new ArrayList<>();
            nbt.getList("toolboxFluid",Tag.TAG_COMPOUND).forEach(tag -> toolboxFluid.add(FluidStack.parseOptional(provider,(CompoundTag) tag)));
        }

        @Override
        public boolean canFill(FluidStack stack){
            for(FluidStack toolboxFluid : this.toolboxFluid)
                if(FluidStack.isSameFluidSameComponents(toolboxFluid,stack))
                    return false;
            for(FluidStack filterFluid : filter)
                if(FluidStack.isSameFluidSameComponents(filterFluid,stack))
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
        public CompoundTag serialize(CompoundTag nbt, HolderLookup.Provider provider) {
            nbt.putBoolean("whiteOrBlack",whiteOrBlack);
            ListTag filterTag = new ListTag();
            filter.forEach((stack) -> filterTag.add(stack.save(provider)));
            nbt.put("filter",filterTag);
            ListTag toolboxFluidTag = new ListTag();
            toolboxFluid.forEach((stack) -> toolboxFluidTag.add(stack.save(provider)));
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
            RegistryAccess registryAccess;
            for(BlockEntity entity : BlockEntityList)
                if(entity instanceof ToolboxBlockEntity && entity.getLevel() != null){
                    registryAccess = entity.getLevel().registryAccess();
                    toolboxItem.addAll(NBTHelper.readItemList(entity.saveCustomOnly(registryAccess).getCompound("Inventory").getList("Compartments", Tag.TAG_COMPOUND),registryAccess));
                }
            this.toolboxFluid = toolboxItem.stream().filter((item)->!item.isEmpty()).collect(ArrayList::new,(fluid,item)-> fluid.addAll(getFluidByItem(item)), ArrayList::addAll);
        }
    }
}