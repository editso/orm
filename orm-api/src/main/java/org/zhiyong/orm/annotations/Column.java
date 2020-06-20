package org.zhiyong.orm.annotations;

import org.zhiyong.orm.api.NULL;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示数据库中一个字段信息注解
 * @see Table
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    /**
     * 字段名
     */
    String value() default "";

    /**
     * 长度
     */
    int length() default 255;

    /**
     * 唯一
     */
    boolean unique() default false;


    /**
     * 外键
     */
    ForeignKey foreignKey() default @ForeignKey();

    /**
     * 空值约束
     */
    boolean nullable() default true;

    /**
     * 自增长
     */
    boolean autoIncreasing() default false;

}
