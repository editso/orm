package org.zhiyong.orm.base;

import org.zhiyong.orm.api.Cache;
import org.zhiyong.orm.api.Cascade;
import org.zhiyong.orm.api.Session;
import org.zhiyong.orm.description.TableDescription;
import org.zhiyong.orm.engine.EngineManager;
import org.zhiyong.orm.exceptions.MapperError;
import org.zhiyong.orm.exceptions.NoMapperFoundException;
import org.zhiyong.orm.exceptions.OrmError;

public abstract class BaseCascade implements Cascade {
    protected final Session session;
    protected final TableDescription tableDescription;
    protected Object entity;
    /*
    *  是否可以被操作
    * */
    protected boolean actionable = false;

    private BaseCascade(Session session, Class<?> clazz){
        this.session = session;
        this.tableDescription = findMapper(clazz);
    }

    public BaseCascade(Session session, TableDescription tableDescription) {
        this.session = session;
        this.tableDescription = tableDescription;
    }

    public BaseCascade(Session session, Object entity) {
        this(session, entity.getClass());
        this.entity = entity;
        this.actionable = true;
    }


    protected TableDescription findMapper(Class<?> clazz){
        try {
            return EngineManager.currentObjectMapper().findMapper(clazz);
        } catch (NoMapperFoundException e) {
            throw new MapperError(clazz);
        }
    }

    protected boolean isActionable(){
        return actionable;
    }

    protected void checkActionable(){
        if (!isActionable())
            throw new OrmError("不是实体模型不可以被操作");
    }


    @Override
    public Session getSession() {
        return session;
    }

}
