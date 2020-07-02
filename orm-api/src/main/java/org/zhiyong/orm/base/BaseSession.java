package org.zhiyong.orm.base;

import org.apache.log4j.Logger;
import org.zhiyong.orm.api.*;
import org.zhiyong.orm.engine.EngineManager;
import org.zhiyong.orm.exceptions.OrmError;
import org.zhiyong.orm.util.ReflectionUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;


public abstract class BaseSession implements Session {
    protected final Logger logger;
    protected final Connection connection;
    protected Savepoint savepoint;
    protected Class<? extends BaseQuery> queryClazz;
    protected Deque<Exception> exceptions;
    protected ObjectMapper currentObjectMapper;


    public BaseSession(Class<? extends BaseQuery> queryClazz, Connection connection) {
        logger = Logger.getLogger(this.getClass());
        this.currentObjectMapper = Objects.requireNonNull(EngineManager.currentObjectMapper(), "orm引擎对象错误");
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
                public void close(){
                    try {
                        connection.releaseSavepoint(savepoint);
                    } catch (SQLException e) {
                        throw new OrmError(e);
                    }
                }
            };
        }catch (Exception e){
            throw new OrmError(e);
        }

    }


    @Override
    public void close(){
        try {
            if (!connection.getAutoCommit())
                commit();
            connection.close();
            logger.debug("关闭会话");
        } catch (SQLException e) {
            throw new OrmError(e.getMessage());
        }

    }

    @Override
    public void commit() {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new OrmError(e.getMessage());
        }
    }

    @Override
    public Exception getException(){
        return exceptions.size() <= 0 ? null : exceptions.pop();
    }
}
