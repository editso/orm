package org.zhiyong.orm.api;

import org.zhiyong.orm.description.TableDescription;
import org.zhiyong.orm.exceptions.NoMapperFoundException;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * 对象映射器接口
 *
 * @see org.zhiyong.orm.base.BaseObjectMapper
 */
public interface ObjectMapper {

    /**
     * 初始化方法
     * @param configure 配置信息
     */
    ObjectMapper init(Configure configure);


    Configure getConfigure();

    boolean reCreate(Class<?> mapper);

    /**
     * 删除所有映射器对应到数据库的表
     */
    void dropAll();

    /**
     * 创建所有映射器对应到数据库的表
     */
    void createAll();

    /**
     * 删除单个映射器,该操作的级联的,被关联的表也将会被删除
     * @param mapper 映射器类
     * @return  成功 true 失败 false
     */
    boolean drop(Class<?> mapper);

    /**
     * 创建单个映射器,该操作的级联的,被关联的表也将会被删除
     * @param mapper 映射器类
     * @return  成功 true 失败 false
     * @see org.zhiyong.orm.annotations.Join
     * @see org.zhiyong.orm.annotations.ManyToOne
     */
    boolean create(Class<?> mapper);

    /**
     * 在程序运行时自动创建表
     * @param autoCreateTable true则创建false相反
     */
    ObjectMapper setAutoCreateTable(boolean autoCreateTable);

    /**
     * 是否是在程序运行时自动创建表
     */
    boolean isAutoCreateTable();

    /**
     * 打开一个会话
     * @return 会话
     * @see Session
     */
    Session openSession();

    /**
     * 获取原生数据库连接
     */
    Connection connection() throws SQLException;
    /**
     * 查找映射器
     * @param mapper 被查找的映射器
     * @return 成功返回
     * @throws NoMapperFoundException 找不到映射器时出现的异常
     */
    TableDescription findMapper(Class<?> mapper) throws NoMapperFoundException;

    /**
     * 获取所有映射信息
     */
    TableDescription[] getMappers();

    /**
     * 获取映射信息
     */
    String mapperInfo();


}
