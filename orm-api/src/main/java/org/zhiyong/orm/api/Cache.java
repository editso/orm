package org.zhiyong.orm.api;


/**
 * 数据库查询缓存接口
 */
public interface Cache<K, V> {
    /**
     * 获取指定key的value
     * @param key key
     * @param <E> value
     * @return value
     */
    <E>E get(K key);

    /**
     * 添加一个k,v到缓存
     * @param key key
     * @param value value
     */
    void put(K key, V value);
}
