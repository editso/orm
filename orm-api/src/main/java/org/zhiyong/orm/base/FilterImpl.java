package org.zhiyong.orm.base;

import org.zhiyong.format.builder.ParameterFactory;
import org.zhiyong.format.interfaces.Parameter;
import org.zhiyong.functional.VoidFunction;
import org.zhiyong.orm.api.*;
import org.zhiyong.orm.util.ArrayUtil;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;


public class FilterImpl<T> implements Filter<T> {
    private Class<T> clazz;
    private boolean setValues = false;
    private final ArrayList<String> blocks = new ArrayList<>();;
    private Execute execute;
    private Session session;
    private Parameter<String> parameter;

    public FilterImpl(Class<T> clazz, Session session, String defaultSQL) {
        this.clazz = clazz;
        this.session = session;
        if (defaultSQL != null && defaultSQL.length() > 0){
            blocks.add(defaultSQL);
        }
    }

    public FilterImpl(Class<T> clazz, Session session){
        this(clazz, session, null);
    }

    @Override
    public Filter<T> join(Class<?> clazz) {
        check();


        return this;
    }

    @Override
    public Filter<T> group(String... column) {
        check();


        return this;
    }

    @Override
    public Filter<T> where(String... where) {
        check();
        blocks.add("WHERE");
        blocks.addAll(Arrays.asList(where));
        return this;
    }

    @Override
    public Filter<T> values(VoidFunction<Parameter<?>> values) {
        parameter = ParameterFactory.stringParameter(asSql());
        values.apply(parameter);
        return this;
    }

    @Override
    public String asSql() {
        if (parameter != null)
            return parameter.transform();
        return ArrayUtil.toString(blocks, " ");
    }

    private void check(){
        if (setValues)
            throw new Error("重复调用");
    }

}
