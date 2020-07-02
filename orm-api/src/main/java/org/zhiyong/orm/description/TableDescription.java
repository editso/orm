package org.zhiyong.orm.description;

import org.apache.log4j.Logger;
import org.zhiyong.orm.annotations.Table;
import org.zhiyong.orm.api.FieldTypeSelector;
import org.zhiyong.orm.util.ReflectionUtil;
import org.zhiyong.orm.util.StringUtil;

import java.lang.reflect.Field;
import java.util.*;

public class TableDescription {
    private final Logger logger = Logger.getLogger(TableDescription.class);
    private final Class<?>  clazz;
    private final Table table;
    private final FieldDescription[] fieldDescription;
    private final ConstraintDescription[] constraintDescriptions;
    private FieldDescription primaryKey;
    private FieldDescription autoIncrement;

    private TableDescription(Class<?> clazz,
                             Table table,
                             FieldDescription[] fieldDescription) {
        this.clazz = clazz;
        this.table = table;
        this.fieldDescription = fieldDescription;
        for (FieldDescription description : fieldDescription) {
            if (primaryKey != null && description.isPrimaryKey()){
                throw new Error("重复的主键: " + clazz.getName());
            }
            if (description.isPrimaryKey()){
                primaryKey = description;
            }
        }

        for (FieldDescription description : fieldDescription) {
            if (autoIncrement != null && description.isAutoIncrement()){
                throw new Error("重复的自增列: "+ clazz.getName());
            }
            if (description.isAutoIncrement()){
                autoIncrement = description;
                if (!description.getFieldType().isAssignableFrom(Integer.class)){
                    logger.warn(clazz.getName() + "/请确保您的自增列对象数据库类型为整型, " +
                            "如果不是可能会出现:(Incorrect column specifier for column)错误!");
                }
            }
        }

        if (primaryKey == null)
            throw new Error("找不到主键: " + clazz.getName());

        if (autoIncrement != null && autoIncrement != primaryKey){
            throw new Error("自增列必须设为主键: " + clazz.getName());
        }
        constraintDescriptions = ConstraintDescription.from(this);
    }

    public Field[] findField(Class<?> type){
        ArrayList<Field> list = new ArrayList<>();
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (!declaredField.getType().isAssignableFrom(type))continue;
            list.add(declaredField);
        }
        return list.toArray(Field[]::new);
    }

    public FieldDescription findFieldDescription(String columnName){
        for (FieldDescription description : fieldDescription) {
            if (description.getName().equals(columnName)){
                return description;
            }
        }
        return null;
    }

    public String getPrimaryKeyName(){
        return primaryKey.getName();
    }

    public Object getPrimaryValue(Object o){
        return primaryKey.getValue(o);
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public FieldDescription[] getFieldDescription() {
        return fieldDescription;
    }

    public ConstraintDescription[] getConstraintDescriptions() {
        return constraintDescriptions;
    }

    public String getName(){
        return table.value().equals("") ? StringUtil.under(clazz.getSimpleName()) : table.value();
    }

    public FieldDescription getPrimaryKey() {
        return primaryKey;
    }

    public FieldDescription getAutoIncrement() {
        return autoIncrement;
    }

    public Map<String, Object> values(Object o){
        Map<String, Object> values = new LinkedHashMap<>();
        for (FieldDescription value : fieldDescription) {
            values.put(value.getName(), value.getValue(o));
        }
        return values;
    }

    public String[] columns(){
        String[] columns = new String[fieldDescription.length];
        for (int i = 0; i < fieldDescription.length; i++) {
            columns[i] = fieldDescription[i].getName();
        }
        return columns;
    }

    public <E>E getMapperInstance(){
        return (E)ReflectionUtil.newInstance(clazz);
    }


    public static TableDescription from(Class<?> clazz, FieldTypeSelector fieldTypeSelector){
        Table table = clazz.getAnnotation(Table.class);
        if (table == null)return null;
        return new TableDescription(
                clazz,
                table,
                FieldDescription.descriptions(clazz, fieldTypeSelector));
    }

    public static TableDescription[] descriptions(Class<?>[] classes, FieldTypeSelector selector){
        List<TableDescription> descriptions = new ArrayList<>();
        for (Class<?> aClass : classes) {
            TableDescription description = from(aClass, selector);
            if (description == null) continue;
            descriptions.add(description);
        }
        return descriptions.toArray(TableDescription[]::new);
    }




    @Override
    public String toString() {
        return getName() + "{" + Arrays.toString(fieldDescription).replaceAll("[\\[|\\]]", "") + "}";
    }
}
