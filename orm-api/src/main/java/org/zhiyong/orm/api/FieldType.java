package org.zhiyong.orm.api;

import java.lang.reflect.Field;

public interface FieldType {
    String type(Field field);
    Object convert(Object o);
}
