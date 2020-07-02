package org.zhiyong.orm.api;

import java.util.List;

/**
 * 数据库级联查询接口
 * 该接口下的方法都会级联操作的
 */
public interface Cascade {

    /**
     * 删除
     */
    boolean delete();

    /**
     * 保存
     */
    boolean save();

    /**
     * 更新
     * @return 影响行
     */
    int update();

    /**
     * 获取指定键的值
     * @param value 键
     * @param <E> 值
     * @return 值
     */
    <E>E get(Object value);

    /**
     * 使用指定sql查询
     * @param query 查询语句
     * @param <E> 查询到的实体
     * @return 返回查询到的集合
     */
    <E> List<E> query(String query);

    /**
     * 获取当前级联查询的会话
     * @return 放会当前会话
     */
    Session getSession();
}
