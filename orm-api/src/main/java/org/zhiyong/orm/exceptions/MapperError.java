package org.zhiyong.orm.exceptions;

public class MapperError extends OrmError{
    public MapperError(Class<?> clazz) {
        this(clazz, "");
    }
    
    public MapperError(Class<?> clazz, String message){
        super("映射器错误: "+ clazz.getName() + "/" + message);
    }
}
