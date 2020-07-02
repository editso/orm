package org.zhiyong.mysql;

import org.zhiyong.format.builder.ParameterFactory;
import org.zhiyong.orm.api.Execute;
import org.zhiyong.orm.api.Session;
import org.zhiyong.orm.base.BaseCascade;
import org.zhiyong.orm.description.ConstraintDescription;
import org.zhiyong.orm.description.TableDescription;
import org.zhiyong.orm.exceptions.NoMapperFoundException;
import org.zhiyong.orm.exceptions.OrmError;
import org.zhiyong.orm.util.ArrayUtil;
import org.zhiyong.orm.util.JdbcUtils;


import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class Cascade extends BaseCascade {
    public Cascade(Session session, TableDescription tableDescription) {
        super(session, tableDescription);
    }

    public Cascade(Session session, Object entity) {
        super(session, entity);
    }

    public Cascade(Session session){
        this(session, null);
    }

    @Override
    public boolean delete() {
        checkActionable();
        return delete(
                tableDescription.getPrimaryValue(entity),
                tableDescription.getPrimaryKeyName(),
                tableDescription,
                true);
    }


    private boolean delete(Object value, String joinColumn, TableDescription description, boolean foreign){
        if (foreign){
            ArrayUtil.each(description.getConstraintDescriptions(), e->{
                if (e.isCollection()){
                    try {
                        delete(value, e.getSqlJoinColumn(), e.getReferenceDescription(), true);
                    } catch (NoMapperFoundException noMapperFoundException) {
                        throw new OrmError(noMapperFoundException);
                    }
                }else {
                    delete(value, e.getSqlJoinColumn(), description, false);
                }
            });
        }
        session.builder("DELETE FROM :table WHERE :column=:value").parameter(m->{
            m.set(description.getName());
            m.set(joinColumn);
            m.setByValue(value);
        }).executeUpdate(true);
        return true;
    }

    @Override
    public boolean save() {
        checkActionable();
        session.builder("INSERT INTO :table(:fields) VALUES (:values);").parameter(m->{
            m.set(tableDescription.getName())
            .set(ArrayUtil.toString(tableDescription.columns()))
            .set(ArrayUtil.fillSequence(":value", tableDescription.columns().length))
            .replace();
            ArrayUtil.each(tableDescription.getConstraintDescriptions(), c->{
                if (!c.isCollection()){
                    c.getJoinFieldDescription().setValue(entity,
                            findMapper(c.getTypeClass()).getPrimaryValue(c.getValue(entity)));
                }
            });
            ArrayUtil.each(tableDescription.values(entity), v-> m.setByValue(v.value));
        }).executeUpdate(true);
        return true;
    }


    @Override
    public int update() {
        checkActionable();

        return 0;
    }

    @Override
    public <E> E get(Object value) {
        return get(value,   tableDescription, 1);
    }

    @Override
    public <E> List<E> query(String query) {
        List<E> elements= new ArrayList<>(JdbcUtils.mapper(session.builder(query).executeQuery(true),
                tableDescription));
        ArrayUtil.each(elements, e->{
            String value = tableDescription.getPrimaryValue(e).toString();
            ArrayUtil.each(tableDescription.getConstraintDescriptions(), c->{
                if (c.isCollection()){
                    ArrayUtil.each(query(value, c.getCollectionTableDescription(), c.getSqlJoinColumn(), 1, 0),
                            i->{
                        c.setValue(e, i);
                    });
                }else {
                    try {
                        c.setValue(e, get(c.getJoinFieldDescription().getValue(e), c.getReferenceDescription(), 2));
                    } catch (NoMapperFoundException noMapperFoundException) {
                        throw new OrmError(noMapperFoundException);
                    }
                }
            });
        });
        return elements;
    }


    public <E> List<E> query(Object value, TableDescription mapper, String joinColumn, int maxDep, int curDep){
        List<E> elements = new ArrayList<>();
        if (maxDep < curDep) return elements;
        ResultSet result = session.builder("SELECT * FROM :table WHERE :column=:value").parameter(m->{
            m.set(mapper.getName());
            m.set(joinColumn);
            m.setByValue(value);
        }).executeQuery(true);
        elements.addAll(JdbcUtils.mapper(result, mapper));
        ArrayUtil.each(elements, e->{
            ArrayUtil.each(mapper.getConstraintDescriptions(), c->{
                if (!c.isCollection()){
                    try {
                        TableDescription description =  c.getReferenceDescription();
                        c.setValue(e, get(c.getJoinFieldDescription().getValue(e), description, maxDep, curDep + 1));
                    } catch (NoMapperFoundException noMapperFoundException) {
                        throw new OrmError(noMapperFoundException);
                    }
                }
            });
        });
        return elements;
    }

    public <E>E get(Object value, TableDescription mapper, int maxDep){
        return get(value, mapper, maxDep, 0);
    }

    private  <E>E get(Object value, TableDescription mapper, int maxDep, int curDep){
        if (maxDep == curDep) return null;
        Execute execute = session.builder("SELECT * FROM :table WHERE :column=:value").parameter(m->{
            m.set(mapper.getName());
            m.set(mapper.getPrimaryKeyName());
            m.setByValue(value);
        });
        E element = JdbcUtils.mapperKey(execute.executeQuery(true), mapper);
        execute.close();
        if (element == null)return null;
        TableDescription refTable = null;
        Object columnValue = null;
        Object primaryKeyValue = mapper.getPrimaryValue(element);
        for (ConstraintDescription c : mapper.getConstraintDescriptions()) {
            if (c.isCollection()){
                refTable = c.getCollectionTableDescription();
                // 映射一组数据
                for (Object o : query(primaryKeyValue, refTable, c.getSqlJoinColumn(), maxDep, curDep + 1)) {
                    c.setValue(element, o);
                }
            }else {
                // 映射单条
                try {
                    refTable = c.getReferenceDescription();
                    if (refTable == null)continue;
                    columnValue = get(c.getJoinFieldDescription().getValue(element), refTable, maxDep, curDep);
                    c.setValue(element, columnValue);
                } catch (NoMapperFoundException e) {
                    throw new OrmError(e);
                }
            }

        }
        return element;
    }



}
