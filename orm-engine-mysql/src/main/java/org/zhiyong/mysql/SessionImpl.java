package org.zhiyong.mysql;

import org.zhiyong.format.interfaces.Parameter;
import org.zhiyong.orm.api.Cache;
import org.zhiyong.orm.api.Execute;
import org.zhiyong.orm.base.BaseExecute;
import org.zhiyong.orm.base.BaseSession;


import java.sql.Connection;


public class SessionImpl extends BaseSession {


    public SessionImpl(Connection connection) {
        super(MysqlQuery.class, connection);
    }

    @Override
    public <T> T get(Object key, Class<T> mapper) {
        return query(mapper).get(key);
    }

    @Override
    public void delete(Object entity) {
        new Cascade(this, entity).delete();
    }

    @Override
    public void update(Object entity) {

    }

    @Override
    public boolean execute(String sql) {
        try {
            return builder(sql).execute();
        } catch (Exception throwables) {
            exceptions.push(throwables);
            return false;
        }
    }

    @Override
    public Execute builder(String sql) {
        return new BaseExecute(connection, sql);
    }

    @Override
    public boolean save(Object o) {
        // 级联保存
        return new Cascade(this, o).save();
    }

    @Override
    public boolean execute(Parameter formatter) {
        return false;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

}
