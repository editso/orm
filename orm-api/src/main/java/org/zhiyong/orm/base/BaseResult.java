package org.zhiyong.orm.base;


import org.zhiyong.functional.ReturnFunction;
import org.zhiyong.orm.api.Result;
import org.zhiyong.orm.util.ReflectionUtil;

import java.util.ArrayList;
import java.util.List;


public class BaseResult<T> implements Result<T> {
    private final List<T> entityList = new ArrayList<>();

    public BaseResult(List<T> entityList) {
        this.entityList.addAll(entityList);
    }

    @Override
    public int size() {
        return entityList.size();
    }

    @Override
    public T first() {
        return size() > 0 ? entityList.get(0) : null;
    }


    @Override
    public Result<T> filter(ReturnFunction<Boolean, T> filter) {
        List<T> result = new ArrayList<>();
        for (T t : entityList) {
            if (filter.apply(t))
                result.add(t);
        }
        return ReflectionUtil.newInstance(this.getClass(), result);
    }

    @Override
    public List<T> all() {
        return entityList;
    }
}
