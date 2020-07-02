package org.zhiyong.orm.exceptions;

public class OrmError extends Error{
    public OrmError() {
        super();
    }

    public OrmError(String message) {
        super(message);
    }

    public OrmError(String message, Throwable cause) {
        super(message, cause);
    }

    public OrmError(Throwable cause) {
        super(cause);
    }

    protected OrmError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
