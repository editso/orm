package org.zhiyong.orm.api;



import org.zhiyong.orm.description.TableDescription;

import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * 数据库所有创建操作
 */
public interface Create {
    void createTable(Session session, TableDescription description) throws SQLException;
}
