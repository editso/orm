package org.zhiyong.orm.api;

public interface Callback<P> {
    void success(P params);
    default void error(Object e){
        throw new Error(e.toString());
    }
}
