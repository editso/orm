package org.zhiyong.orm.api;

import java.lang.reflect.Field;

public interface Encrypt {
    /**
     * 加密
     * @param field 被加密对应的字段
     * @param value 值
     * @param mapper 映射器
     */
    Object encode(Field field, Object value, Class<?> mapper);

    /**
     * 解密
     * @param field 被解密对应的字段
     * @param value 值
     * @param mapper 映射器
     */
    Object decode(Field field, Object value, Class<?> mapper);
}
