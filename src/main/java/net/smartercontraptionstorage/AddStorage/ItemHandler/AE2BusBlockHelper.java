package net.smartercontraptionstorage.AddStorage.ItemHandler;

import appeng.api.config.Actionable;
import appeng.api.implementations.parts.ICablePart;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.storage.IStorageService;
import appeng.api.parts.IPart;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import appeng.api.util.AECableType;
import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.blockentity.networking.*;
import appeng.blockentity.spatial.SpatialIOPortBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.items.tools.powered.WirelessCraftingTerminalItem;
import appeng.me.GridNode;
import appeng.me.service.EnergyService;
import appeng.parts.automation.ExportBusPart;
import appeng.parts.automation.IOBusPart;
import appeng.util.ConfigInventory;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.smartercontraptionstorage.AddStorage.AE2ContraptionSource;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;

public class AE2BusBlockHelper extends StorageHandlerHelper{
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof CableBusBlockEntity;
    }

    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {}

    @Override
    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
        assert this.canCreateHandler(entity);

        CableBusBlockEntity bus = (CableBusBlockEntity)entity;
        ICablePart center = (ICablePart)bus.getPart(null);
        if (center != null && center.getCableConnectionType() == AECableType.COVERED) {
            IGridNode exportHost = null;
            IGridNode importHost = null;

            for(IPart part : getAllPart(bus)) {
                if (part instanceof IOBusPart) {
                    if (!checkUpgrade(((IOBusPart)part).getUpgrades(), AEItems.SPEED_CARD.asItem(), 3)) {
                        continue;
                    }

                    ConfigInventory config = ((IOBusPart)part).getConfig();
                    AEKey key = config.getKey(0);
                    if (key != null) {
                        ItemStack item = key.wrapForDisplayOrFilter();
                        if (item.getItem() instanceof WirelessCraftingTerminalItem terminal) {
                            if (!checkUpgrade(terminal.getUpgrades(item), AEItems.ENERGY_CARD.asItem())) {
                                continue;
                            }

                            IGrid id = terminal.getLinkedGrid(item, entity.getLevel(), null);
                            if (id != null) {
                                IGridNode host = id.getPivot();
                                if (host == null) {
                                    continue;
                                }

                                if (part instanceof ExportBusPart) {
                                    exportHost = host;
                                } else {
                                    importHost = host;
                                }
                            }
                        }
                    }
                }

                if (exportHost != null && importHost != null) {
                    return AE2HandlerHelper.create(exportHost, importHost);
                }
            }

            return exportHost == null && importHost == null ? NULL_HANDLER : AE2HandlerHelper.create(exportHost, importHost);
        } else {
            return NULL_HANDLER;
        }
    }

    private static IPart[] getAllPart(CableBusBlockEntity bus){
        IPart[] iParts = new IPart[6];
        iParts[0] = bus.getPart(Direction.NORTH);
        iParts[1] = bus.getPart(Direction.SOUTH);
        iParts[2] = bus.getPart(Direction.EAST);
        iParts[3] = bus.getPart(Direction.WEST);
        iParts[4] = bus.getPart(Direction.UP);
        iParts[5] = bus.getPart(Direction.DOWN);
        return iParts;
    }

    public static boolean checkUpgrade(InternalInventory upgrade, Item targetUpgrade, int size) {
        for(int i = size; i >= 0; --i) {
            ItemStack item = upgrade.getStackInSlot(i);
            if (item.isEmpty() || !item.is(targetUpgrade)) {
                return false;
            }
        }

        return true;
    }

    public static boolean checkUpgrade(InternalInventory upgrade, Item targetUpgrade) {
        return checkUpgrade(upgrade, targetUpgrade, upgrade.size() - 1);
    }

    @Override
    public boolean allowControl(Item comparedItem) {
        return false;
    }

    @Override
    public boolean allowControl(Block block) {
        return false;
    }

    @Override
    public void registerBlock(Consumer<Block> register) {
        register.accept(AEBlocks.CABLE_BUS.block());
    }

    @Override
    public String getName() {
        return "AE2BusBlockHelper";
    }

    @Override
    public boolean canDeserialize() {
        return false;
    }

    @Override
    public @NotNull ItemStackHandler deserialize(CompoundTag nbt) throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static class AE2HandlerHelper extends ItemStackHandler implements NeedDealWith {
        public final @Nullable IGridNode extractNode;
        public final @Nullable IGridNode importNode;
        private final ArrayList<AEKey> extractKeys = new ArrayList<>();
        private boolean hasFilter = false;
        private boolean canWork = false;
        private final AE2ContraptionSource extractSource;
        private final AE2ContraptionSource importSource;
        private AE2HandlerHelper(int size, @Nullable IGridNode extractNode, @Nullable IGridNode importNode) {
            super(size);
            this.extractNode = extractNode;
            this.importNode = importNode;
            this.extractSource = AE2ContraptionSource.create(extractNode);
            this.importSource = AE2ContraptionSource.create(importNode);
        }

        public static AE2HandlerHelper create(@Nullable IGridNode extractNode, @Nullable IGridNode importNode) {
            IStorageService extractNet = null;
            if (extractNode != null) {
                extractNet = extractNode.getGrid().getStorageService();
            }

            int size = extractNet == null ? 1 : extractNet.getInventory().getAvailableStacks().size();
            return new AE2HandlerHelper(size, extractNode, importNode);
        }

        public boolean canWork(boolean extractOrImport,boolean simulate){
            if(!canWork)
                return false;
            if(extractOrImport){
                if(extractNode == null)
                    return false;
                return extractNode.isActive() && useEnergy(true,simulate);
            }else {
                if(importNode == null)
                    return false;
                return importNode.isActive() && useEnergy(false,simulate);
            }
        }

        public @Nullable IGrid getGrid(boolean extractOrImport){
            if(extractOrImport)
                return extractNode == null ? null : extractNode.getGrid();
            else
                return importNode == null ? null : importNode.getGrid();
        }

        public @Nullable MEStorage getStorage(boolean extractOrImport){
            IGrid grid = getGrid(extractOrImport);
            if(grid == null)
                return null;
            return grid.getStorageService().getInventory();
        }

        public @Nullable EnergyService getEnergy(boolean extractOrImport){
            IGrid grid = getGrid(extractOrImport);
            if(grid == null)
                return null;
            if(grid.getEnergyService() instanceof EnergyService service)
                return service;
            else return null;
        }

        public boolean useEnergy(boolean extractOrImport,boolean simulate){
            EnergyService provider = getEnergy(extractOrImport);
            boolean back = false;
            if(provider != null) {
                double use = provider.getChannelPowerUsage() * 5;
                back = Double.compare(provider.extractProviderPower(use, Actionable.ofSimulate(simulate)),use) == 0;
            }
            return back;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if(slot == 0)
                if(canWork(false,simulate)) {
                    MEStorage importStorage = getStorage(false);
                    if (importStorage == null)
                        return stack;
                    AEKey key = AEItemKey.of(stack);
                    int input = (int) importStorage.insert(key, stack.getCount(), Actionable.ofSimulate(simulate), importSource);
                    stack.shrink(input);
                }
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(canWork(true,simulate) && slot < extractKeys.size()) {
                MEStorage extractStorage = getStorage(true);
                if (extractStorage == null)
                    return ItemStack.EMPTY;
                ItemStack stack = extractKeys.get(slot).wrapForDisplayOrFilter();
                int extract = (int) extractStorage.extract(extractKeys.get(slot), amount, Actionable.ofSimulate(simulate), extractSource);
                stack.setCount(extract);
                return stack;
            }
            return ItemStack.EMPTY;
        }

        public void refreshStack(@Nullable MEStorage extractStorage){// synchronize stored items in AE Net
            if(hasFilter)
                return;
            extractKeys.clear();
            if(extractStorage != null)
                extractKeys.addAll(extractStorage.getAvailableStacks().keySet());
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            if(canWork(true,true)) {
                MEStorage extractStorage = getStorage(true);
                if (extractStorage == null)
                    return ItemStack.EMPTY;
                refreshStack(extractStorage);
                ItemStack stack = extractKeys.get(slot).wrapForDisplayOrFilter();
                int size = (int) extractStorage.extract(extractKeys.get(slot), Integer.MAX_VALUE, Actionable.SIMULATE, extractSource);
                stack.setCount(size);
                return stack;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlots() {
            refreshStack(getStorage(true));
            return Math.max(extractKeys.size(),1);
        }

        @Override
        public void doSomething(BlockEntity entity) {
            BlockEntityList.add(entity);
        }

        @Override
        public void finallyDo() {
            boolean controller = false,energy = false;
            for(BlockEntity entity : StorageHandlerHelper.BlockEntityList) {
                if (entity instanceof InterfaceBlockEntity MEInterface) {
                    extractKeys.addAll(MEInterface.getInterfaceLogic().getConfig().getAvailableStacks().keySet());
                } else if (controller || entity instanceof ControllerBlockEntity)
                    controller = true;
                else if(energy || entity instanceof EnergyCellBlockEntity || entity instanceof CreativeEnergyCellBlockEntity)
                    energy = true;
                else if(entity instanceof SpatialIOPortBlockEntity){
                    canWork = false;
                    return;
                }
            }
            canWork = controller && energy;
            hasFilter = !extractKeys.isEmpty();
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            if(canWork) {
                if (importNode instanceof GridNode node) {
                    node.saveToNBT("importNode", tag);
                }
                if (extractNode instanceof GridNode node) {
                    node.saveToNBT("extractNode", tag);
                }
                ListTag keys = new ListTag();
                extractKeys.forEach((key) -> keys.add(key.toTagGeneric()));
                tag.put("extractKeys", keys);
                tag.putBoolean("hasFilter", hasFilter);
                tag.putInt("size",super.getSlots());
            }
            return tag;
        }

        public boolean canWork() {
            return canWork;
        }
    }
}