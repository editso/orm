package org.zhiyong.functional;

public interface ReturnFunction<R, E> {
    R apply(E element);
}
