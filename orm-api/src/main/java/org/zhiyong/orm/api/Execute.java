package org.zhiyong.orm.api;

import org.zhiyong.format.interfaces.ParameterFormatter;

import java.sql.ResultSet;

public interface Execute{

    Execute parameter(VoidFunction<Parameter> parameter);
    
    Execute mat(VoidFunction<ParameterFormatter> mat);

    /**
     * 直接执行一个sql语句
     * @param sql 需要被执行的sql
     */
    boolean execute(String sql);

    boolean execute(Callback<ResultSet> callback);


    /**
     * 执行
     */
    boolean execute();

    /**
     * 关闭执行器
     */
    void close();



}
