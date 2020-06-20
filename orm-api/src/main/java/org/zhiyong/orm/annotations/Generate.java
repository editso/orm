package org.zhiyong.orm.annotations;

import org.zhiyong.orm.api.GenerateFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 生成器
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Generate {
    Class<? extends GenerateFactory> value();
}
