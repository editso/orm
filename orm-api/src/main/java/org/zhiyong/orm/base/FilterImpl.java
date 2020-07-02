package org.zhiyong.orm.base;

import org.zhiyong.format.interfaces.Parameter;
import org.zhiyong.functional.VoidFunction;
import org.zhiyong.orm.api.*;
import org.zhiyong.orm.util.ArrayUtil;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;


public class FilterImpl<T> implements Filter<T> {
    private Class<T> clazz;
    private boolean setValues = false;
    private final ArrayList<String> blocks = new ArrayList<>();;
    private Execute execute;
    private Session session;

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


        return null;
    }

    @Override
    public Filter<T> where(String... where) {
        check();
        return this;
    }

    @Override
    public Filter<T> values(VoidFunction<Parameter<?>> values) {
        return null;
    }

    @Override
    public String asSql() {
        return ArrayUtil.toString(blocks, "");
    }

    private void check(){
        if (setValues)
            throw new Error("重复调用");
    }

}
