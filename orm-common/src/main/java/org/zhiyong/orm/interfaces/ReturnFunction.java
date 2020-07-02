package org.zhiyong.orm.interfaces;

public interface ReturnFunction<R, E> {
    R apply(E e);
}
