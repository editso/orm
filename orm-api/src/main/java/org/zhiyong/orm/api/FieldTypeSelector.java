package org.zhiyong.orm.api;

import org.zhiyong.orm.description.ColumnTypeDescription;

import java.lang.reflect.Field;

/**
 * 映射器字段类型选择
 */
public interface FieldTypeSelector {
    /**
     * 类型选择方法
     * @param field 映射的属性字段
     * @param type 类型
     * @param mapper 映射类
     * @return 返回一个字段类型描述类
     * @see ColumnTypeDescription
     */
    ColumnTypeDescription columnTypeDescription(Field field, Class<?> type, Class<?> mapper);
}
