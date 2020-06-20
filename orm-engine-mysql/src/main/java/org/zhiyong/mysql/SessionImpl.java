package org.zhiyong.mysql;

import org.zhiyong.format.interfaces.ParameterFormatter;
import org.zhiyong.orm.api.Execute;
import org.zhiyong.orm.base.BaseSession;
import org.zhiyong.orm.description.ConstraintDescription;
import org.zhiyong.orm.description.FieldDescription;
import org.zhiyong.orm.description.TableDescription;
import org.zhiyong.orm.factory.ExecuteFactory;
import org.zhiyong.orm.util.ArrayUtil;

import java.sql.Connection;
import java.util.Map;


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
        return ExecuteFactory.builder(connection, sql);
    }

    @Override
    public boolean save(Object o) {
        TableDescription description = Global.findToInstance(o.getClass());
        if (description == null)return false;
        String INSERT = "INSERT INTO :table(:columns) VALUES (:values);";
        builder(INSERT).mat(o1 -> {
            o1.set("table", description.getName());
            o1.set("columns", ArrayUtil.toString(description.columns()));
            o1.set("values", ArrayUtil.fillToString("?", description.columns().length));
            System.out.println(o1.transform());
        }).parameter(o12 -> {
            Map<String, Object> values = description.values(o);
            TableDescription tableDescription = null;
            for (ConstraintDescription constraintDescription : description.getConstraintDescriptions()) {
                System.out.println(constraintDescription.getJoinColumn());
                if (constraintDescription.isCollection())continue;
                tableDescription = Global.findToInstance(constraintDescription.getTypeClass());
                if (tableDescription == null) throw new Error("不兼容的类型");
                Object t = constraintDescription.getValue(o);
                FieldDescription f = description.findFieldDescription(constraintDescription.getJoinColumn());
                if (f != null && !f.nullable() && t == null)
                    throw new Error("缺少值: " + f.getName() + " 不能为空/类型 " + constraintDescription.getTypeClass().getName());
                if (values.containsKey(constraintDescription.getJoinColumn()))
                    values.put(constraintDescription.getJoinColumn(), tableDescription.getPrimaryKey().getValue(t));
            }
            o12.fromTo(values.values().toArray(Object[]::new));
        }).execute();
        return true;
    }

    @Override
    public boolean execute(ParameterFormatter formatter) {
        return execute(formatter.transform());
    }


}
