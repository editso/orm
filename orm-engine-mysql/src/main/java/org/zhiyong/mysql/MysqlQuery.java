package org.zhiyong.mysql;

import org.zhiyong.format.interfaces.ParameterFormatter;
import org.zhiyong.orm.annotations.Join;
import org.zhiyong.orm.api.Execute;
import org.zhiyong.orm.api.Query;
import org.zhiyong.orm.api.Session;
import org.zhiyong.orm.api.VoidFunction;
import org.zhiyong.orm.base.BaseQuery;
import org.zhiyong.orm.description.ConstraintDescription;
import org.zhiyong.orm.description.FieldDescription;
import org.zhiyong.orm.description.TableDescription;
import org.zhiyong.orm.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MysqlQuery<T> extends BaseQuery<T> {
    private final StringBuilder builder = new StringBuilder();
    private final static String QUERY = "SELECT";
    private final static String QUERY_GET = QUERY + " * FROM :table WHERE :key=?";
    private TableDescription description;

    public MysqlQuery(Class<T> mapperClazz, Session session) {
        super(mapperClazz, session);
        this.description = Global.findToInstance(mapperClazz);
        if (description == null)
            throw new Error("找不到映射表: "+ mapperClazz.getName());
        builder.append(QUERY)
                .append(" * FROM ")
                .append(description.getName())
                .append(" ");
    }

    public MysqlQuery(Class<T> mapperClazz, Session session, String... select) {
        super(mapperClazz, session, select);
    }


    @Override
    public List<T> all() {
        return null;
    }

    @Override
    public T get(Object o) {
        final T[] object = (T[]) new Object[1];
        session.builder(QUERY_GET).mat(o1 -> {
            System.out.println(description);
            o1.set("table", description.getName());
            o1.set("key", description.getPrimaryKey().getName());
        }).parameter(o1 -> {
            o1.setObject(1, o);
        }).execute(params -> {
            try {
                if (!params.absolute(1))
                    return;
                object[0] = ReflectionUtil.newInstance(mapperClazz);
                for (FieldDescription fieldDescription : description.getFieldDescription()) {
                    fieldDescription.setValue(object[0], params.getObject(fieldDescription.getName()));
                }
                for (ConstraintDescription c : description.getConstraintDescriptions()) {
                    Class<?> clazz = c.isCollection()?c.getCollectionTypeClass() : c.getTypeClass();
                    TableDescription target = Global.findToInstance(clazz);
                    if (target == null){
                        throw new Error("找不到数据库实体类: "+ clazz.getName());
                    }
                    session.query(target.getClazz())
                            .filter("WHERE :column=?", o12 -> {
                                String joinColumn = c.getJoinColumn();
                                if (joinColumn == null){
                                    Field[] fields =  target.findField(mapperClazz);
                                    if (fields.length != 1){
                                        throw new Error("映射不明确 " + clazz.getName() + " 包含多个 " + mapperClazz.getName() + "请指明字段");
                                    }
                                    Field field = fields[0];
                                    Join join = field.getDeclaredAnnotation(Join.class);
                                    joinColumn = join != null ? join.value() : field.getName();
                                }
                                o12.set("column", joinColumn);
                            }).parameter(o13 -> {
                                o13.setObject(1, description.getPrimaryKey().getValue(object[0]));
                            }).execute(resultSet -> {
                                List<?> list = mapper(resultSet, target);
                                for (Object o1 : list) {
                                    c.setValue(object[0], o1);
                                }
                            });
                }
            } catch (SQLException e) {
                object[0] = null;
            }
        });
        return object[0];
    }

    @Override
    public Query<T> filter(String str) {
        return null;
    }

    @Override
    public Execute filter(String sql, VoidFunction<ParameterFormatter> param) {
        builder.append(sql);
        return session.builder(builder.toString()).mat(param);
    }

    public List<?> mapper(ResultSet resultSet, TableDescription target){
        List<Object> list = new ArrayList<>();
        try {
            Object o = null;
            while (resultSet.next()) {
                o = ReflectionUtil.newInstance(target.getClazz());
                for (FieldDescription fieldDescription : target.getFieldDescription()) {
                    fieldDescription.setValue(o, resultSet.getObject(fieldDescription.getName()));
                }
                list.add(o);
            }
        }catch (Exception e){
            throw new Error(e);
        }
        return list;
    }


}
