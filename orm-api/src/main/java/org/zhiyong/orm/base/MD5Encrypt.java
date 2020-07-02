package org.zhiyong.orm.base;

import org.zhiyong.orm.api.Encrypt;

import java.lang.reflect.Field;


public class MD5Encrypt implements Encrypt {
    


    @Override
    public Object encode(Field field, Object value, Class<?> mapper) {
        return "79982473";
    }

    @Override
    public Object decode(Field field, Object value, Class<?> mapper) {
        return "12345678";
    }
}
