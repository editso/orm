package org.zhiyong.orm.api;


import java.sql.SQLException;

/**
 * 事物接口
 */
public interface Transaction {
    /**
     * 回滚当前事物
     */
    void rollback();

    /**
     * 关闭事物
     */
    void close();
}
