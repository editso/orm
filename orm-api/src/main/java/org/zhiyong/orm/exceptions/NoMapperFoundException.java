package org.zhiyong.orm.exceptions;

public class NoMapperFoundException extends MapperException{
    public NoMapperFoundException(){
        super("没有那个映射器");
    }
}
