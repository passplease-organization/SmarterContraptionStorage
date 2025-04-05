package net.smartercontraptionstorage.Message;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.MenuLevel;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MenuLevelPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MenuLevelPacket> TYPE = new Type<>(Utils.asResources("menu_level_packet"));

    public static final StreamCodec<ByteBuf, MenuLevelPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            MenuLevelPacket::name,
            ByteBufCodecs.VAR_INT,
            MenuLevelPacket::age,
            MenuLevelPacket::new
    );

    private final String name;

    private final int age;

    public MenuLevelPacket(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public MenuLevelPacket(){
        this("menu_level_packet",1);
    }

    private Set<Pair<Integer, Long>> pairs;

    private boolean removeOrAdd = false;

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

    public void handle(IPayloadContext context) {
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

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public String name(){
        return name;
    }

    public int age(){
        return age;
    }

    public void sendToClient(@Nullable ServerPlayer player) {
        if(player != null)
            PacketDistributor.sendToPlayer(player,this);
    }
}
