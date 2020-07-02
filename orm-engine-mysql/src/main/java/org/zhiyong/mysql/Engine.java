package org.zhiyong.mysql;

import org.zhiyong.orm.api.ObjectMapper;
import org.zhiyong.orm.description.ConnectionDescription;
import org.zhiyong.orm.engine.EngineManager;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Engine implements org.zhiyong.orm.api.Engine {

    @Override
    public ObjectMapper getObjectMapper() {
        return new MysqlObjectMapper();
    }

    @Override
    public void close() {
        ConnectionDescription description =  getObjectMapper().getConfigure().connectionDescription();
        while (DriverManager.getDrivers().hasMoreElements()) {
            try {
                DriverManager.deregisterDriver(DriverManager.getDrivers().nextElement());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
