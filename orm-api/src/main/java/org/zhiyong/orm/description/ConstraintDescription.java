package org.zhiyong.orm.description;

import org.zhiyong.orm.annotations.Constraint;
import org.zhiyong.orm.annotations.Join;
import org.zhiyong.orm.annotations.OneToMany;
import org.zhiyong.orm.util.StringUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ConstraintDescription {
    private Class<?> clazz;
    private final Field field;
    private final Constraint.Type constraintType;
    private Class<?> collectionTypeClass;
    private Class<?> typeClass;
    private Method setter;
    private Method getter;
    private String joinColumn;


    public ConstraintDescription(Class<?> clazz,
                                 Field field,
                                 Constraint.Type type) {
        this.clazz = clazz;
        this.field = field;
        this.constraintType = type;
        if (Collection.class.isAssignableFrom(field.getType())){
            if (!(type.equals(Constraint.Type.ManyToMany) || type.equals(Constraint.Type.OneToMany))){
                throw new Error("约束错误: " + type.name());
            }
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            collectionTypeClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }
        Join join = field.getDeclaredAnnotation(Join.class);
        joinColumn = join != null? join.value() : field.getName();
        if (!isCollection())
            typeClass = field.getType();
        try {
            getter = clazz.getMethod(StringUtil.hump("get", field.getName()));
            setter = clazz.getMethod(StringUtil.hump("set", field.getName()), field.getType());
        } catch (NoSuchMethodException methodException) {
            throw new NoSuchMethodError(methodException.getMessage());
        }

    }

    public Field getField() {
        return field;
    }

    public Class<?> getCollectionTypeClass() {
        return collectionTypeClass;
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

    public void setValue(Object o, Object value){
        if (isCollection()){
            if (!collectionTypeClass.isAssignableFrom(value.getClass())){
                throw new Error("类型错误: 预期 "+ collectionTypeClass.getName() + " 实际 "+ value.getClass().getName());
            }
            Collection<Object> collection = (Collection<Object>) getValue(o);
            collection.add(value);
        }
    }

    public Constraint.Type getConstraintType() {
        return constraintType;
    }

    public static ConstraintDescription[] from(Class<?> clazz){
        List<ConstraintDescription> descriptions = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                Constraint constraint = annotation.annotationType().getDeclaredAnnotation(Constraint.class);
                if (constraint == null)
                    continue;
                descriptions.add(new ConstraintDescription(
                        clazz,
                        field,
                        constraint.value()));
            }
        }
        return descriptions.toArray(ConstraintDescription[]::new);
    }
}
