package net.smartercontraptionstorage.Render;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

import static net.minecraft.world.item.Items.*;
import static net.smartercontraptionstorage.Utils.asResources;
import static net.smartercontraptionstorage.Utils.sendMessage;

public enum Overlay {
    WHITE((DyeItem) WHITE_DYE),
    RED((DyeItem) RED_DYE),
    ORANGE((DyeItem) ORANGE_DYE),
    YELLOW((DyeItem) YELLOW_DYE),
    GREEN((DyeItem) GREEN_DYE),
    BLUE((DyeItem) BLUE_DYE),
    PURPLE((DyeItem) PURPLE_DYE),
    PINK((DyeItem) PINK_DYE),
    LIME((DyeItem) LIME_DYE),
    MAGENTA((DyeItem) MAGENTA_DYE),
    LIGHT_BLUE((DyeItem) LIGHT_BLUE_DYE),
    CYAN((DyeItem) CYAN_DYE),
    BROWN((DyeItem) BROWN_DYE),
    BLACK((DyeItem) BLACK_DYE),
    GRAY((DyeItem) GRAY_DYE),
    LIGHT_GRAY((DyeItem) LIGHT_GRAY_DYE);
    Overlay(@NotNull DyeItem item) {
        this.item = item;
        this.location = asResources("overlays/" + this.getName());
    }
    TextureAtlasSprite uv;
    @NotNull public final ResourceLocation location;
    @NotNull public final DyeItem item;
    // register by json file
//    static void register(TextureStitchEvent.Pre event){
//        for(Overlay overlay : Overlay.values()){
//            event.addSprite(overlay.getResourceLocation());
//        }
//    }
    static void setUV(TextureAtlasStitchedEvent event){
        TextureAtlas atlas = event.getAtlas();
        for(Overlay overlay : Overlay.values()){
            overlay.uv = atlas.getSprite(overlay.getResourceLocation());
        }
    }
    public @NotNull TextureAtlasSprite getUV(){
        Objects.requireNonNull(uv);
        return uv;
    }
    public static @Nullable Overlay get(String name) {
        for (Overlay overlay : Overlay.values()) {
            if (overlay.getName().equals(name))
                return overlay;
        }
        return null;
    }
    public @NotNull DyeColor getColor(){
        return this.item.getDyeColor();
    }
    public static @NotNull Overlay get(DyeItem item) {
        for (Overlay overlay : Overlay.values()) {
            if(overlay.item.getDyeColor().equals(item.getDyeColor()))
                return overlay;
        }
        throw new Error("Cannot find the overlay color of this dye:" + item.getDyeColor());
    }

    public @NotNull ResourceLocation getResourceLocation() {
        return this.location;
    }
    public @NotNull String getName(boolean translate) {
        if(translate){
            return this.item.getName(new ItemStack(item)).getString();
        } else return this.item.getDyeColor().getName();
    }
    public @NotNull String getName() {
        return getName(false);
    }

    @Override
    public String toString() {
        return "Overlay: " + getName();
    }

    public static @NotNull Overlay getByOrdinal(int ordinal) {
        if(ordinal < 0 || ordinal >= Overlay.values().length)
            throw new IllegalArgumentException("Invalid ordinal: " + ordinal);
        return Overlay.values()[ordinal];
    }

    public static @NotNull Overlay getFirst() {
        return getByOrdinal(0);
    }

    public static @NotNull Overlay getLast() {
        return getByOrdinal(Overlay.values().length - 1);
    }

    public static void forEachSequentially(@NotNull Consumer<Overlay> consumer) {
        Overlay overlay = getFirst();
        for(;overlay.hasNext();overlay = overlay.next())
            consumer.accept(overlay);
    }

    public static void forEachInversely(@NotNull Consumer<Overlay> consumer) {
        Overlay overlay = getLast();
        for(; overlay.hasPrev();overlay = overlay.prev())
            consumer.accept(overlay);
    }

    public @NotNull Overlay next(){
        return getByOrdinal((this.ordinal() + 1) % length());
    }

    public boolean hasNext(){
        return this.next() != getFirst();
    }

    public @NotNull Overlay prev(){
        return getByOrdinal((this.ordinal() - 1) % length());
    }

    public boolean hasPrev(){
        return this != getFirst();
    }

    public static int length(){
        return Overlay.values().length;
    }
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext){
        dispatcher.register(Commands.literal("scs").requires(CommandSourceStack::isPlayer).then(
                Commands.literal("get")
                        .then(
                                Commands.literal("ordinal").executes((context)->{
                                    ServerPlayer player = context.getSource().getPlayer();
                                    if(player != null) {
                                        ItemStack item = player.getMainHandItem();
                                        DyeItem dye = null;
                                        if(item.getItem() instanceof DyeItem)
                                            dye = (DyeItem) item.getItem();
                                        else {
                                            item = player.getOffhandItem();
                                            if(item.getItem() instanceof DyeItem)
                                                dye = (DyeItem) item.getItem();
                                        }
                                        if(dye != null)
                                            sendMessage("ordinary.color.success",player,dye.getName(item), Overlay.get(dye).ordinal() + 1);
                                        else sendMessage("ordinary.color.dye.failure",player);
                                        return 1;
                                    }
                                    return 0;
                                }).then(
                                        Commands.argument("item", ItemArgument.item(buildContext)).executes((context -> {
                                            Item item = ItemArgument.getItem(context, "item").getItem();
                                            ServerPlayer player = context.getSource().getPlayer();
                                            if(player != null) {
                                                if (item instanceof DyeItem dye) {
                                                    sendMessage("ordinary.color.success",player,dye.getName(new ItemStack(dye)),Overlay.get(dye).ordinal() + 1);
                                                    return 1;
                                                } else sendMessage("ordinary.color.item.failure",player);
                                            }
                                            return 0;
                                        }))
                                )
                        ).then(
                                Commands.literal("by_ordinary").then(
                                        Commands.argument("ordinary", IntegerArgumentType.integer(1,Overlay.length()))
                                                .executes(context -> {
                                                    ServerPlayer player = context.getSource().getPlayer();
                                                    if(player != null) {
                                                        int ordinal = IntegerArgumentType.getInteger(context, "ordinary");
                                                        Overlay overlay = Overlay.values()[ordinal - 1];
                                                        sendMessage("ordinary.color.success",player,overlay.getName(true),ordinal);
                                                    }
                                                    return 1;
                                                })
                                )
                        )
        ));
    }
}