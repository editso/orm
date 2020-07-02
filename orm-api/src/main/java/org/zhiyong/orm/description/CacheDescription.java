package org.zhiyong.orm.description;

import org.zhiyong.orm.engine.EngineManager;
import org.zhiyong.orm.exceptions.NoMapperFoundException;
import org.zhiyong.orm.exceptions.OrmError;

public class CacheDescription {
    public final Class<?> mapper;
    public final Object entity;
    public final Object value;

    public CacheDescription(Class<?> mapper, Object entity, Object value) {
        this.mapper = mapper;
        this.entity = entity;
        this.value = value;
    }

    public CacheDescription from(Object entity){
        try {
            TableDescription description = EngineManager.currentObjectMapper().findMapper(entity.getClass());
            return new CacheDescription(description.getClazz(), entity, description.getPrimaryValue(entity));
        } catch (NoMapperFoundException e) {
            throw new OrmError(e);
        }
    }

}
