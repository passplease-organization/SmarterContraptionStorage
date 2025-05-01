package net.smartercontraptionstorage.Message;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.MenuLevel;
import net.smartercontraptionstorage.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class MenuLevelPacket extends ModMessage {
    private Set<Pair<Integer, Long>> pairs;

    private boolean removeOrAdd = false;

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        int[] contraptionIds = new int[pairs.size()];
        long[] blockPosIds = new long[pairs.size()];
        List<Pair<Integer, Long>> list = pairs.stream().toList();
        Pair<Integer, Long> pair;
        for(int i = 0; i < pairs.size(); i++) {
            pair = list.get(i);
            contraptionIds[i] = pair.getFirst();
            blockPosIds[i] = pair.getSecond();
        }
        buffer.writeVarIntArray(contraptionIds);
        buffer.writeLongArray(blockPosIds);
    }

    @Override
    public MenuLevelPacket fromBytes(FriendlyByteBuf buffer) {
        MenuLevelPacket packet = new MenuLevelPacket();
        Set<Pair<Integer, Long>> pair = new HashSet<>();
        int[] contraptionIds = buffer.readVarIntArray();
        long[] blockPosIds = buffer.readLongArray();
        if(contraptionIds.length != blockPosIds.length) {
            Utils.addError("The length of contraption IDs and block positions don't match");
            packet.set(Set.of(), false);
            return packet;
        }
        for(int i = 0; i < contraptionIds.length; i++) {
            pair.add(Pair.of(contraptionIds[i], blockPosIds[i]));
        }
        packet.set(pair, false);
        return packet;
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        if(removeOrAdd) {
            pairs.forEach(pair -> MenuLevel.getBlocks().remove(pair));
        } else {
            pairs.forEach(pair -> {
                if (!MenuLevel.getBlocks().containsKey(pair)) {
                    if (Minecraft.getInstance().level.getEntity(pair.getFirst()) instanceof AbstractContraptionEntity contraption) {
                        MenuLevel.addBlockEntity(pair, contraption.getContraption().presentBlockEntities.get(BlockPos.of(pair.getSecond())),null);
                    }
                }
            });
        }
    }

    public MenuLevelPacket set(Set<Pair<Integer, Long>> pairs, boolean removeOrAdd){
        this.pairs = pairs;
        this.removeOrAdd = removeOrAdd;
        return this;
    }
}
