package org.zhiyong.orm.api;

/**
 * 映射器字段类型选择
 * @see org.zhiyong.orm.annotations.TypeClass
 */
public interface FieldType extends FieldTypeSelector{

    /**
     * 将数据库中的value映射的实体
     * @param o 数据库中的值
     * @return 对应实体的值
     */
    Object convert(Object o);
}
