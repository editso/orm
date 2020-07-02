package org.zhiyong.orm.description;

import org.zhiyong.orm.annotations.*;
import org.zhiyong.orm.api.Encrypt;
import org.zhiyong.orm.api.FieldType;
import org.zhiyong.orm.api.FieldTypeSelector;
import org.zhiyong.orm.api.GenerateFactory;
import org.zhiyong.orm.engine.EngineManager;
import org.zhiyong.orm.exceptions.MapperColumnTypeError;
import org.zhiyong.orm.exceptions.NoMapperFoundException;
import org.zhiyong.orm.exceptions.OrmError;
import org.zhiyong.orm.util.ReflectionUtil;
import org.zhiyong.orm.util.StringUtil;

import javax.sound.midi.MidiFileFormat;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FieldDescription {
    private final Class<?> clazz;
    private final FieldTypeSelector selector;
    private final Field field;
    private final Column column;
    private final ManyToOne manyToOne;
    private final Method getter;
    private final Method setter;
    private Class<? extends GenerateFactory> generateClazz;
    private GenerateFactory generateFactoryInstance;
    private Encrypt encrypt;
    private ColumnTypeDescription columnTypeDescription;
    private final FieldType fieldType;

    private FieldDescription(Class<?> clazz, FieldTypeSelector selector, Field field, Column column) {
        this.clazz = clazz;
        this.selector = selector;
        this.field = field;
        this.column = column;
        this.manyToOne = field.getDeclaredAnnotation(ManyToOne.class);
        Generate generate = field.getDeclaredAnnotation(Generate.class);
        EncryptClass encryptClass = field.getDeclaredAnnotation(EncryptClass.class);
        TypeClass typeClass = field.getDeclaredAnnotation(TypeClass.class);
        Join join = field.getDeclaredAnnotation(Join.class);
        try {
            getter = clazz.getMethod(StringUtil.hump("get", field.getName()));
            setter = clazz.getMethod(StringUtil.hump("set", field.getName()), field.getType());
        } catch (NoSuchMethodException methodException) {
            throw new NoSuchMethodError(methodException.getMessage());
        }

        if (! Objects.isNull(generate)){
            generateClazz = generate.value();
            generateFactoryInstance = ReflectionUtil.newInstance(generateClazz);
        }

        if (! Objects.isNull(typeClass)){
            fieldType = ReflectionUtil.newInstance(typeClass.value());
            columnTypeDescription = fieldType.columnTypeDescription(field, field.getType(), clazz);
        }else if(manyToOne !=  null && join == null){
            fieldType = null;
            columnTypeDescription = null;
        }else {
            fieldType = null;
            columnTypeDescription = selector.columnTypeDescription(field, field.getType(), clazz);
        }
        if (Objects.isNull(columnTypeDescription) && Objects.isNull(manyToOne))
            throw new MapperColumnTypeError(clazz, field);

        if (encryptClass != null)
            encrypt = ReflectionUtil.newInstance(encryptClass.value());

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

    public FieldType geColumnType() {
        return fieldType;
    }

    public Class<?> getFieldType(){
        return field.getType();
    }

    public Column getColumn() {
        return column;
    }

    public boolean isPrimaryKey(){
        return field.getAnnotation(PrimaryKey.class) != null;
    }

    public String getType(){
        return getColumnTypeDescription().asSql();
    }

    public String getName(){
        return "".equals(column.value()) ? StringUtil.under(field.getName()) : column.value();
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
            if (encrypt != null)
                value = encrypt.encode(field, value, clazz);
            return value;
        } catch (Exception e) {
            throw new Error(e.getMessage());
        }
    }

    public void setValue(Object o, Object v){
        try {
            if (! Objects.isNull(fieldType)){
                setter.invoke(o, fieldType.convert(v));
                return;
            }
            if (getFieldType().isAssignableFrom(Date.class) && v != null){
                setter.invoke(o, Date.valueOf(v.toString()));
            }else {
                setter.invoke(o, encrypt != null ?encrypt.decode(field, v, clazz) : v);
            }
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
        return isPrimaryKey() || column.unique();
    }

    public boolean isAutoIncrement(){
        return column.autoIncreasing();
    }

    public ColumnTypeDescription getColumnTypeDescription() {
        if (columnTypeDescription == null && manyToOne != null) {
            try {
                columnTypeDescription = EngineManager.currentObjectMapper()
                        .findMapper(getFieldType())
                        .getPrimaryKey()
                        .getColumnTypeDescription();
            } catch (NoMapperFoundException e) {
                throw new OrmError("找不到依赖: "+ getFieldType());
            }
        }
        return columnTypeDescription;
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
