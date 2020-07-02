package org.zhiyong.mysql;

import org.zhiyong.format.interfaces.Parameter;
import org.zhiyong.functional.VoidFunction;
import org.zhiyong.orm.annotations.Constraint;
import org.zhiyong.orm.api.*;
import org.zhiyong.orm.base.BaseQuery;
import org.zhiyong.orm.base.BaseResult;
import org.zhiyong.orm.base.FilterImpl;
import org.zhiyong.orm.description.ConstraintDescription;
import org.zhiyong.orm.description.TableDescription;
import org.zhiyong.orm.exceptions.NoMapperFoundException;
import org.zhiyong.orm.exceptions.OrmError;
import org.zhiyong.orm.util.StringUtil;

import java.sql.PreparedStatement;
import java.util.List;

public class MysqlQuery<T> extends BaseQuery<T> {
    private final static String QUERY = "SELECT";
    private final static String QUERY_GET = QUERY + " * FROM :table WHERE :key=?";

    public MysqlQuery(Class<T> mapperClazz, Session session) {
       this(mapperClazz, session, new String[]{});

    }

    public MysqlQuery(Class<T> mapperClazz, Session session, String... select) {
        super(mapperClazz, session);
        setDefaultSQL(QUERY +
                " * FROM " +
                description.getName() +
                " ");
    }

    @Override
    public Cascade getCascade() {
        return new Cascade(session, description);
    }

    @Override
    public Query<T> filter(String str) {
        return this;
    }

    @Override
    public Result<T> filter(VoidFunction<Filter<T>> filter) {
        Filter<T> f = new FilterImpl<>(mapperClazz, session, getDefaultSQL());
        filter.apply(f);
        return new BaseResult<>(cascade.query(f.asSql()));
    }

    @Override
    public Execute filter(String sql, VoidFunction<Parameter<PreparedStatement>> param) {
        setDefaultSQL(sql);
        return session.builder(getDefaultSQL()).parameter(param);
    }

}
