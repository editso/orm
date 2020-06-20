package org.zhiyong.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Constraint {
    Type value();
    enum Type{
        OneToMany(),
        ManyToOne(),
        ManyToMany();
    }
}
