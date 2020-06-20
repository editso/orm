package org.zhiyong.orm.api;


import java.sql.SQLException;
import java.sql.Savepoint;

public interface Transaction {

    void rollback();
    void close() throws SQLException;
}
