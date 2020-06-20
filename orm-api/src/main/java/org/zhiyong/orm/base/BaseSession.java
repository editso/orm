package org.zhiyong.orm.base;

import org.zhiyong.orm.api.Query;
import org.zhiyong.orm.api.Session;
import org.zhiyong.orm.api.Transaction;
import org.zhiyong.orm.util.ReflectionUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class BaseSession implements Session {
    protected final Connection connection;
    protected Savepoint savepoint;
    protected Class<? extends BaseQuery> queryClazz;
    protected Deque<Exception> exceptions;

    public BaseSession(Class<? extends BaseQuery> queryClazz, Connection connection) {
        this.connection = connection;
        this.queryClazz = queryClazz;
        exceptions = new ConcurrentLinkedDeque<>();
    }

    @Override
    public <T> Query<T> query(Class<T> clazz) {
        return (Query<T>) ReflectionUtil.newInstance(queryClazz, clazz, this);
    }

    @Override
    public <T> Query<T> query(Class<T> clazz, String ...select) {
        return (Query<T>) ReflectionUtil.newInstance(queryClazz, clazz, this, select);
    }


    @Override
    public Transaction beginTransaction() {
        try {
            savepoint = connection.setSavepoint();
            return new Transaction() {
                @Override
                public void rollback(){
                    try {
                        connection.rollback(savepoint);
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void close() throws SQLException {
                    connection.releaseSavepoint(savepoint);
                }
            };
        }catch (Exception ignore){
            return null;
        }

    }


    @Override
    public void close() throws SQLException {
        connection.commit();
    }

    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    @Override
    public Exception getException(){
        return exceptions.size() <= 0 ? null : exceptions.pop();
    }
}
