package org.zhiyong.orm.factory;

import org.zhiyong.format.builder.FormatterFactory;
import org.zhiyong.format.interfaces.ParameterFormatter;
import org.zhiyong.orm.api.Callback;
import org.zhiyong.orm.api.Execute;
import org.zhiyong.orm.api.Parameter;
import org.zhiyong.orm.api.VoidFunction;
import org.zhiyong.orm.util.ReflectionUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExecuteFactory {
    public static Execute builder(Connection connection , String sql){
        return new ExecuteImpl(connection, sql);
    }

    private static class ExecuteImpl implements Execute{
        private final Connection connection;
        private PreparedStatement statement;
        private final String sql;
        private String formatSql = null;

        public  class Invoke implements InvocationHandler {
            private final Parameter parameter;
            private final Parameter entity;
            public Invoke(Parameter entity){
                this.entity = entity;
                parameter = ReflectionUtil.proxy(Parameter.class, (o, method, objects) -> {
                    try {
                        Method m = PreparedStatement.class.getMethod(method.getName(), method.getParameterTypes());
                        return m.invoke(getStatement(), objects);
                    }catch (Exception e){
                        return method.invoke(entity, objects);
                    }
                });
            }

            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                return method.invoke(parameter, objects);
            }
        }

        public ExecuteImpl(Connection connection, String sql) {
            this.connection = connection;
            this.sql = sql;
        }

        public PreparedStatement getStatement() {
            if (statement != null)return statement;
            try {
                statement = connection.prepareStatement(formatSql != null ? formatSql : sql);
                return statement;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Execute parameter(VoidFunction<Parameter> parameter) {
            parameter.apply(ReflectionUtil.proxy(Parameter.class,
                    new Invoke(new Parameter() {
                        @Override
                        public Parameter setObject(int index, Object o) {
                            try {
                                getStatement().setObject(index, o);
                            } catch (SQLException e) {
                                throw new Error(e.getMessage());
                            }
                            return this;
                        }
                    })));
            return this;
        }

        @Override
        public Execute mat(VoidFunction<ParameterFormatter> mat) {
            ParameterFormatter formatter = FormatterFactory.builder(sql);
            mat.apply(formatter);
            formatSql = formatter.transform();
            return this;
        }

        @Override
        public boolean execute(String sql) {
            try {
                return getStatement().execute(sql);
            } catch (SQLException e) {
                throw new Error(e.getMessage());
            }
        }

        @Override
        public boolean execute(Callback<ResultSet> callback) {
            try {
                System.out.println(getStatement());
                if (getStatement().execute()){
                    callback.success(getStatement().getResultSet());
                }
                return true;
            } catch (SQLException e) {
                callback.error(e);
            }
            return false;
        }

        @Override
        public boolean execute() {
            try {
                System.out.println(getStatement());
                return getStatement().execute();
            } catch (SQLException e) {
                throw new Error(e.getMessage());
            }
        }

        @Override
        public void close() {
            try {
                getStatement().close();
            }catch (Exception e){
                throw new RuntimeException(e);
            }

        }
    }
}
