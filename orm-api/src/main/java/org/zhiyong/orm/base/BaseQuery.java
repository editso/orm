package org.zhiyong.orm.base;

import org.zhiyong.orm.api.*;

import org.zhiyong.orm.description.TableDescription;
import org.zhiyong.orm.engine.EngineManager;
import org.zhiyong.orm.exceptions.MapperError;
import org.zhiyong.orm.exceptions.NoMapperFoundException;
import org.zhiyong.orm.util.ReflectionUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public abstract class  BaseQuery<T> implements Query<T> {
    protected Class<T> mapperClazz;
    protected Class<BaseResult> resultClass = BaseResult.class;
    protected Session session;
    protected String[] select;
    protected TableDescription description;
    private final StringBuffer defaultSQL = new StringBuffer();
    protected ObjectMapper currentObjectMapper;
    protected Cascade cascade;

    public BaseQuery(Class<T> mapper, Session session) {
        currentObjectMapper = Objects.requireNonNull(EngineManager.currentObjectMapper(),  "orm引擎错误");
        try {
            this.description = currentObjectMapper.findMapper(mapper);
        } catch (NoMapperFoundException e) {
            throw new MapperError(mapper);
        }
        this.mapperClazz = mapper;
        this.session = session;
        this.cascade = getCascade();
    }

    public abstract Cascade getCascade();

    public BaseQuery(Class<T> mapper,
                     Session session,
                     String ...select) {
        this(mapper, session);
        this.select = select;
    }

    protected void setDefaultSQL(String sql){
        this.defaultSQL.append(sql);
    }

    protected void clearDefaultSQL(){
        this.defaultSQL.delete(0, defaultSQL.length());
    }

    protected String getDefaultSQL(){
        return defaultSQL.toString();
    }

    @Override
    public T get(Object o) {
        return cascade.get(o);
    }

    @Override
    public List<T> all() {
        return filter(m->{}).all();
    }

}
