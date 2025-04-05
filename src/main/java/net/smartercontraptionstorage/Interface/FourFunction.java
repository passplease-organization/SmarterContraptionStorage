package net.smartercontraptionstorage.Interface;
@FunctionalInterface
public interface FourFunction<T,U,R,W,V> {
    V function(T t,U u,R r,W w);
    default V function(T t,U u,R r){
        return function(t,u,r,null);
    }
}
