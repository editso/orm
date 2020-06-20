package org.zhiyong.orm.description;

import org.zhiyong.orm.annotations.Base;
import org.zhiyong.orm.annotations.Column;
import org.zhiyong.orm.annotations.Generate;
import org.zhiyong.orm.annotations.PrimaryKey;
import org.zhiyong.orm.api.FieldTypeSelector;
import org.zhiyong.orm.api.GenerateFactory;
import org.zhiyong.orm.util.ReflectionUtil;
import org.zhiyong.orm.util.StringUtil;

import javax.sound.midi.MidiFileFormat;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FieldDescription {
    private final Class<?> clazz;
    private final FieldTypeSelector selector;
    private final Field field;
    private final Column column;
    private final Method getter;
    private final Method setter;
    private Class<? extends GenerateFactory> generateClazz;
    private GenerateFactory generateFactoryInstance;


    private FieldDescription(Class<?> clazz, FieldTypeSelector selector, Field field, Column column) {
        this.clazz = clazz;
        this.selector = selector;
        this.field = field;
        this.column = column;
        try {
            getter = clazz.getMethod(StringUtil.hump("get", field.getName()));
            setter = clazz.getMethod(StringUtil.hump("set", field.getName()), field.getType());
        } catch (NoSuchMethodException methodException) {
            throw new NoSuchMethodError(methodException.getMessage());
        }
        Generate generate = field.getDeclaredAnnotation(Generate.class);
        if (generate != null){
            generateClazz = generate.value();
            generateFactoryInstance = ReflectionUtil.newInstance(generateClazz);
        }
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public boolean eq(Class<?> type){
        return field.getType().isAssignableFrom(type);
    }

    public FieldTypeSelector getSelector() {
        return selector;
    }

    public Column getColumn() {
        return column;
    }

    public boolean isPrimaryKey(){
        return field.getAnnotation(PrimaryKey.class) != null;
    }

    public String getType(){
        return selector.getType(field, field.getType());
    }

    public String getName(){
        return "".equals(column.value()) ? field.getName() : column.value();
    }

    public Class<? extends GenerateFactory> getGenerateClazz() {
        return generateClazz;
    }

    public GenerateFactory getGenerateFactoryInstance() {
        return generateFactoryInstance;
    }

    public Object getValue(Object o){
        try {
            Object value = getter.invoke(o);
            if (value == null && generateFactoryInstance != null){
                generateFactoryInstance.generate(o, this);
                value = getter.invoke(o);
            }
            return value;
        } catch (Exception e) {
            throw new Error(e.getMessage());
        }
    }

    public void setValue(Object o, Object v){
        try {
            setter.invoke(o, v);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new Error(e.getMessage());
        }
    }

    /**
     * 检查该字段是否允许为空
     */
    public boolean nullable(){
        return !isPrimaryKey() && column.nullable();
    }

    public boolean unique(){
        return isPrimaryKey() && column.unique();
    }

    public static FieldDescription from(Class<?> clazz,
                                        FieldTypeSelector selector,
                                        Field field){
        Column column = field.getAnnotation(Column.class);
        if (column == null)return null;
        return new FieldDescription(clazz, selector, field, column);
    }

    public static FieldDescription[] descriptions(Class<?> clazz, FieldTypeSelector selector){
        List<FieldDescription> descriptions = new ArrayList<>();
        Class<?> supperClass = clazz.getSuperclass();
        List<Field> fields = new ArrayList<>();
        if (supperClass != null && supperClass.getAnnotation(Base.class) != null){
            fields.addAll(Arrays.asList(supperClass.getDeclaredFields()));
        }
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        for (Field field : fields) {
            FieldDescription description = from(clazz, selector, field);
            if (description == null)continue;
            descriptions.add(description);
        }
        fields.clear();
        return descriptions.toArray(FieldDescription[]::new);
    }

    @Override
    public String toString() {
        return getName() + ":" + getType();
    }
}
