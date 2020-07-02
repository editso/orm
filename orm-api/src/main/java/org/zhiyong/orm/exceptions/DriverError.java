package org.zhiyong.orm.exceptions;

public class DriverError extends OrmError{
    public DriverError() {
        super("找不到数据库驱动");
    }
}
