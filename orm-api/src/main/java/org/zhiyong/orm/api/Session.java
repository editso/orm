package org.zhiyong.orm.api;


import org.zhiyong.format.interfaces.ParameterFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * 会话
 */
public interface Session {

    /**
     * 开启一个新的查询
     * @param clazz 查询表的映射类
     * @param <T> 查询表的映射类
     * @return 返回一个查询器
     * @see Query
     */
    <T>Query<T> query(Class<T> clazz, String ...select);
    <T>Query<T> query(Class<T> clazz);

    <T>T get(Object key, Class<T> mapper);

    /**
     * 删除一个实体对应的数据库中的行
     * @param entity 被删除的实体
     */
    void delete(Object entity);

    /**
     *
     * 更新一行中指定列或多个列
     * @param entity 被更新的实体
     */
    void update(Object entity);

    /**
     * 执行sql
     * @param sql 需要被预编译的sql语句
     * @return 成功 true 否则 false
     */
    boolean execute(String sql);

    /**
     * 构建一个预编译sql执行器
     * @param sql 被处理的语句
     * @return 返回一个执行器
     */
    Execute builder(String sql);

    /**
     * 开启一个事务
     * @return 返回该事务对象
     * @see Transaction
     */
    Transaction beginTransaction();

    /**
     * 向数据库插入一行新的数据
     * @param o 被插入的实体
     * @return 成功true 否则 false
     */
    boolean save(Object o);

    /**
     * 提交
     */
    void commit() throws SQLException;

    /**
     * 关闭会话
     */
    void close() throws SQLException;

    boolean execute(ParameterFormatter formatter);

    Exception getException();

}
