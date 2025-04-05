package net.smartercontraptionstorage.Interface;

import java.util.Arrays;

public interface Settable {
    void set(Object object);
    default void set(String parameterName, Object object){
        set(object);
    }
    default void set(Object... objects){
        Arrays.stream(objects).forEach(this::set);
    }
}
