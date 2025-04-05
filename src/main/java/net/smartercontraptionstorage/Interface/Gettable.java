package net.smartercontraptionstorage.Interface;

import javax.annotation.Nullable;

public interface Gettable {
    @Nullable
    Object get(String name);
}
