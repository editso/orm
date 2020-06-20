package org.zhiyong.orm.api;

import java.lang.reflect.Field;

public interface FieldTypeSelector {
    String getType(Field field, Class<?> type);
}
