package org.zhiyong.orm.api;

/**
 * 数据库引擎
 */
public interface Engine {
    /**
     * 获取对象映射器
     */
    ObjectMapper getObjectMapper();

    /**
     * 关闭引擎
     */
    void close();
}
