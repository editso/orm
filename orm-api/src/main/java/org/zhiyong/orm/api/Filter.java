package org.zhiyong.orm.api;


import org.zhiyong.format.interfaces.Parameter;
import org.zhiyong.functional.VoidFunction;

/**
 * 过滤器
 * @param <T> 被过滤的实体
 */
public interface Filter<T> {

    /**
     * 对应数据库 join 操作
     * @param clazz 被连接的映射
     */
    Filter<T> join(Class<?> clazz);

    /**
     * 分组查询
     * @param column 被连接的 column
     * @see org.zhiyong.orm.annotations.Column
     */
    Filter<T> group(String ...column);

    /**
     * 过滤条件
     * @param where 条件
     */
    Filter<T> where(String ...where);

    /**
     * 参数化
     * @param values 参数
     * @see Parameter
     */
    Filter<T> values(VoidFunction<Parameter<?>> values);

    String asSql();

}
