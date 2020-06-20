package org.zhiyong.orm.annotations;

import org.zhiyong.orm.api.FieldType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示一个数据库表字段类型,使用该注解后,可以自定义数据类型
 * @see Table
 * @see Column
 * @see FieldType
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeClass {
    /**
     * 自定义类型类
     */
    Class<? extends FieldType> value();
}
