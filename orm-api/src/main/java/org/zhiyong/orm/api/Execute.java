package org.zhiyong.orm.api;

import org.zhiyong.format.interfaces.Parameter;
import org.zhiyong.functional.VoidFunction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 * 执行器接口
 */
public interface Execute{

    /**
     * 为给定的语句提供参数
     * @param parameter 参数
     * @see Parameter
     */
    Execute parameter(VoidFunction<Parameter<PreparedStatement>> parameter);

    /**
     * 直接执行一个sql语句
     * @param sql 需要被执行的sql
     */
    boolean execute(String sql);
    
    /**
     * 执行
     */
    boolean execute();

    /**
     * 执行查询
     * @return 返回查询到的结果集
     */
    ResultSet executeQuery();

    /**
     * 执行更新
     * @return 影响行
     */
    int executeUpdate();

    /**
     * 执行
     */
    boolean execute(boolean close);

    /**
     * 执行查询
     * @return 返回查询到的结果集
     */
    ResultSet executeQuery(boolean close);

    /**
     * 执行更新
     * @return 影响行
     */
    int executeUpdate(boolean close);


    /**
     * 获取影响行
     */
    int count();

    /**
     * 关闭执行器
     */
    void close();


}
