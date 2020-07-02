package org.zhiyong.orm.exceptions;

import java.lang.reflect.Field;

public class MapperColumnTypeError extends Error{
    public MapperColumnTypeError(Class<?> mapper, Field field) {
        super("字段类型错误: " + mapper.getName()+"."+field.getName());

    }
}
