package org.zhiyong.orm.base;

import org.zhiyong.orm.api.Query;
import org.zhiyong.orm.api.Session;

public abstract class  BaseQuery<T> implements Query<T> {
    protected Class<T> mapperClazz;
    protected Session session;
    protected String[] select;

    public BaseQuery(Class<T> mapperClazz, Session session) {
        this.mapperClazz = mapperClazz;
        this.session = session;
    }

    public BaseQuery(Class<T> mapperClazz, Session session, String ...select) {
        this(mapperClazz, session);
        this.select = select;
    }
}
