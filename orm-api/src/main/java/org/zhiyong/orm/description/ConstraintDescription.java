package org.zhiyong.orm.description;

import org.zhiyong.orm.annotations.Column;
import org.zhiyong.orm.annotations.Constraint;
import org.zhiyong.orm.annotations.Join;
import org.zhiyong.orm.annotations.OneToMany;
import org.zhiyong.orm.engine.EngineManager;
import org.zhiyong.orm.exceptions.MapperError;
import org.zhiyong.orm.exceptions.NoMapperFoundException;
import org.zhiyong.orm.exceptions.OrmError;
import org.zhiyong.orm.util.ReflectionUtil;
import org.zhiyong.orm.util.StringUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


public class ConstraintDescription {
    private TableDescription tableDescription;
    private Class<?> clazz;
    private final Field field;
    private final Constraint.Type constraintType;
    private Class<?> collectionTypeClass;
    private Class<?> typeClass;
    private final Method setter;
    private final Method getter;
    private final String joinColumn;
    private final Join join;
    private FieldDescription joinFieldDescription;

    public ConstraintDescription(TableDescription description,
                                 Field field,
                                 Constraint.Type type) {
        this.clazz = description.getClazz();
        this.tableDescription = description;
        this.field = field;
        this.constraintType = type;
        join = field.getDeclaredAnnotation(Join.class);
        if (Collection.class.isAssignableFrom(field.getType())){
            if (!(type.equals(Constraint.Type.ManyToMany) || type.equals(Constraint.Type.OneToMany))){
                throw new Error("约束错误: " + type.name());
            }
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            collectionTypeClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }
        if (!isCollection())
            typeClass = field.getType();
        try {
            getter = clazz.getMethod(StringUtil.hump("get", field.getName()));
            setter = clazz.getMethod(StringUtil.hump("set", field.getName()), field.getType());
        } catch (NoSuchMethodException methodException) {
            throw new NoSuchMethodError(methodException.getMessage());
        }
        if (join != null && type == Constraint.Type.ManyToOne){
            joinFieldDescription = description.findFieldDescription(join.value());
            if (joinFieldDescription == null){
                joinFieldDescription = description.findFieldDescription(StringUtil.under(join.value()));
            }
            if (joinFieldDescription == null)
                throw new MapperError(clazz, "找不到指定连接到的字段: " + join.value());
        }
        joinColumn = getSqlJoinColumn();
    }

    public FieldDescription getJoinFieldDescription() {
        return joinFieldDescription;
    }

    public Field getField() {
        return field;
    }

    public Class<?> getCollectionTypeClass() {
        return collectionTypeClass;
    }

    public TableDescription getCollectionTableDescription(){
        if (getCollectionTypeClass() == null)return null;
        try {
            return EngineManager.currentObjectMapper().findMapper(getCollectionTypeClass());
        } catch (NoMapperFoundException e) {
            throw new OrmError(e);
        }
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public boolean isCollection(){
        return collectionTypeClass != null;
    }

    public String getJoinColumn() {
        return joinColumn;
    }

    public Object getValue(Object o){
        try {
            return getter.invoke(o);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new Error(e.getMessage());
        }
    }

    public String getJoinFieldName(){
        return joinFieldDescription.getName();
    }

    public String getSqlJoinColumn(){
        if (getConstraintType() == Constraint.Type.OneToMany)
            return StringUtil.under(join.value());
        return joinFieldDescription != null ? getJoinFieldName() : StringUtil.under(field.getName());
    }

    public void setValue(Object o, Object value){
        if (isCollection()){
            if (!collectionTypeClass.isAssignableFrom(value.getClass()))
                throw new Error("类型错误: 预期 "+ collectionTypeClass.getName() + " 实际 "+ value.getClass().getName());
            Collection<Object> collection = (Collection<Object>) getValue(o);
            collection.add(value);
        }else {
            try {
                setter.invoke(o, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new MapperError(clazz);
            }
        }
    }

    public Constraint.Type getConstraintType() {
        return constraintType;
    }

    // 是否有外键依赖
    public boolean isForeign(){
        return getConstraintType() == Constraint.Type.OneToMany;
    }

    public TableDescription getReferenceDescription() throws NoMapperFoundException {
        if (Objects.isNull(EngineManager.currentObjectMapper()))throw new NoMapperFoundException();
        return EngineManager.currentObjectMapper().findMapper(collectionTypeClass == null? typeClass : collectionTypeClass);
    }

    public static ConstraintDescription[] from(TableDescription description){
        List<ConstraintDescription> descriptions = new ArrayList<>();
        Class<?> clazz = description.getClazz();
        for (Field field : clazz.getDeclaredFields()) {
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                Constraint constraint = annotation.annotationType().getDeclaredAnnotation(Constraint.class);
                if (constraint == null)
                    continue;
                descriptions.add(new ConstraintDescription(
                        description,
                        field,
                        constraint.value()));
            }
        }
        return descriptions.toArray(ConstraintDescription[]::new);
    }
}
