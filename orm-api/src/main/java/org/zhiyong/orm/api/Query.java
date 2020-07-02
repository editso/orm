package org.zhiyong.orm.api;

import org.zhiyong.format.interfaces.Parameter;
import org.zhiyong.functional.VoidFunction;

import java.sql.PreparedStatement;
import java.util.List;

/**
 * 查询器
 * @param <T> 被查询的映射类
 */
public interface Query<T> {
    /**
     * 查询所有或者满足条件的数据
     * @return 返回该映射类的集合对象
     */
    List<T> all();

    /**
     * 获取指定主键的数据实体
     * @param o 主键数据
     * @return 返回一个映射实体, 该实体可能为空
     */
    T get(Object o);

    /**
     * 过滤
     * @param str 过滤条件
     * @return 返回查询器
     */
    Query<T> filter(String str);

    Result<T> filter(VoidFunction<Filter<T>> filter);

    Execute filter(String sql, VoidFunction<Parameter<PreparedStatement>> param);

}
