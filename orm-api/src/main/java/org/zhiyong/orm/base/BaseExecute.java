package org.zhiyong.orm.base;

import org.apache.log4j.Logger;
import org.zhiyong.format.builder.ParameterFactory;
import org.zhiyong.format.interfaces.Parameter;
import org.zhiyong.functional.VoidFunction;
import org.zhiyong.orm.api.Execute;
import org.zhiyong.orm.exceptions.OrmError;

import java.sql.*;


public class BaseExecute implements Execute  {
    protected final Logger logger;
    protected Connection connection;
    protected String baseSql;
    protected Parameter<PreparedStatement> parameter;
    protected Statement curStatement;

    public BaseExecute(Connection connection, String baseSql) {
        this.logger = Logger.getLogger(this.getClass());
        this.connection = connection;
        this.baseSql = baseSql;
        parameter = ParameterFactory.prepared(baseSql, connection);
    }

    @Override
    public Execute parameter(VoidFunction<Parameter<PreparedStatement>> function) {
        function.apply(parameter);
        return this;
    }

    @Override
    public boolean execute(String sql) {
        logger.debug(sql);
        try {
            this.curStatement = connection.createStatement();
            return this.curStatement.execute(sql);
        } catch (SQLException e) {
            throw new OrmError(e);
        }
    }

    @Override
    public boolean execute() {
        return execute(false);
    }

    @Override
    public ResultSet executeQuery() {
        return executeQuery(false);
    }

    @Override
    public int executeUpdate() {
        return executeUpdate(false);
    }

    @Override
    public boolean execute(boolean close) {
        logger.debug(parameter.getOrigin());
        try {
            return curStatement().execute();
        } catch (SQLException e) {
            throw new OrmError(e);
        }finally {
            if (close) close();
        }
    }

    @Override
    public ResultSet executeQuery(boolean close) {
        logger.debug(parameter.getOrigin());
        try {
            return curStatement().executeQuery();
        } catch (SQLException e) {
            throw new OrmError(e);
        }finally {
            if (close)close();
        }
    }

    @Override
    public int executeUpdate(boolean close) {
        logger.debug(parameter.getOrigin());
        try {
            return curStatement().executeUpdate();
        } catch (SQLException e) {
            throw new OrmError(e);
        }finally {
            if (close)close();
        }
    }

    @Override
    public int count() {
        if (curStatement == null)return -1;
        try {
            return curStatement.getUpdateCount();
        } catch (SQLException e) {
            throw new OrmError(e);
        }
    }

    @Override
    public void close() {
        try {
            curStatement().close();
        } catch (SQLException e) {
            throw new OrmError(e);
        }
    }

    protected PreparedStatement curStatement(){
        this.curStatement = parameter.transform();
        return (PreparedStatement) this.curStatement;
    }

}
