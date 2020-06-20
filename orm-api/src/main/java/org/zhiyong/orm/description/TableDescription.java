package org.zhiyong.orm.description;

import org.zhiyong.orm.annotations.Table;
import org.zhiyong.orm.api.FieldTypeSelector;
import org.zhiyong.orm.util.StringUtil;

import java.lang.reflect.Field;
import java.util.*;

public class TableDescription {
    private final Class<?>  clazz;
    private final Table table;
    private final FieldDescription[] fieldDescription;
    private final ConstraintDescription[] constraintDescriptions;
    private FieldDescription primaryKey;

    private TableDescription(Class<?> clazz,
                             Table table,
                             FieldDescription[] fieldDescription,
                             ConstraintDescription[] constraintDescriptions) {
        this.clazz = clazz;
        this.table = table;
        this.fieldDescription = fieldDescription;
        this.constraintDescriptions = constraintDescriptions;
        for (FieldDescription description : fieldDescription) {
            if (primaryKey != null && description.isPrimaryKey()){
                throw new Error("重复的主键: " + clazz.getName());
            }
            if (description.isPrimaryKey()){
                primaryKey = description;
            }
        }
        if (primaryKey == null)
            throw new Error("找不到主键: " + clazz.getName());
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


    public static TableDescription from(Class<?> clazz, FieldTypeSelector fieldTypeSelector){
        Table table = clazz.getAnnotation(Table.class);
        if (table == null)return null;
        return new TableDescription(
                clazz,
                table,
                FieldDescription.descriptions(clazz, fieldTypeSelector),
                ConstraintDescription.from(clazz));
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
