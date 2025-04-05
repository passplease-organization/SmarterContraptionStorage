package net.smartercontraptionstorage.Interface;
@FunctionalInterface
public interface TriFunction<T,R,U,W> {
    W function(T t,R r,U u);
    default W function(T t,R r){
        return function(t,r,null);
    }
}
