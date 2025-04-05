package net.smartercontraptionstorage.AddStorage;

import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public interface SerializableHandler<T> {
    default boolean canDeserialize(){
        return true;
    }

    String getName();

    @Deprecated
    @NotNull
    default T deserialize(CompoundTag nbt) throws Exception{
        return deserialize(nbt, Minecraft.getInstance().level.registryAccess());
    }

    T deserialize(CompoundTag nbt, HolderLookup.Provider provider) throws IllegalAccessException;
}
