package org.zhiyong.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示数据库中的外键约束
 * @see Column
 * @see Table
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignKey {
    /**
     * 外键对应的字段,默认选择主键
     */
    String column() default "";
}
