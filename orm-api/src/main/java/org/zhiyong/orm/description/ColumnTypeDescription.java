package org.zhiyong.orm.description;

import java.lang.reflect.Field;

/**
 * 数据库字段类型
 */
public class ColumnTypeDescription {
    private final Class<?> clazz;

    private final int type;

    private final Field field;

    /**
     * 对应数据表中全类型名
     */
    private final String name;

    /**
     * 字段类型长度
     */
    private final Integer length;


    public ColumnTypeDescription(Class<?> clazz,
                                 int type,
                                 Field field,
                                 String name,
                                 Integer length) {
        this.name = name;
        this.length = length;
        this.clazz = clazz;
        this.field = field;
        this.type = type;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public Integer getLength() {
        return length;
    }

    public int getType() {
        return type;
    }

    public Field getField() {
        return field;
    }

    /**
     * 转换为sql字段类型
     */
    public String asSql(){
        if (length != null)
            return name + "(" + length + ")";
        return name;
    }

}
