package org.zhiyong.orm.api;

import org.zhiyong.functional.ReturnFunction;

import java.util.List;


public interface Result<T> {
    int size();
    List<T> all();
    T first();
    Result<T> filter(ReturnFunction<Boolean, T> filter);
}
