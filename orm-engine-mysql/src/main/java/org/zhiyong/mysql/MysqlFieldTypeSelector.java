package org.zhiyong.mysql;

import org.zhiyong.orm.annotations.Column;
import org.zhiyong.orm.annotations.TypeClass;
import org.zhiyong.orm.api.FieldTypeSelector;
import org.zhiyong.orm.description.TableDescription;
import org.zhiyong.orm.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Timestamp;


public class MysqlFieldTypeSelector implements FieldTypeSelector {
    @Override
    public String getType(Field field, Class<?> type) {
        Column column = field.getAnnotation(Column.class);
        TypeClass typeClass = field.getAnnotation(TypeClass.class);
        if (typeClass != null)
            return ReflectionUtil.newInstance(typeClass.value()).type(field);
        if (type.isAssignableFrom(String.class))
            return String.format("VARCHAR(%d)", column.length());
        else if(type.isAssignableFrom(Integer.class))
            return "INT";
        else if (type.isAssignableFrom(Date.class))
            return "DATE(10)";
        else if(type.isAssignableFrom(Timestamp.class))
            return "DATETIME(19)";
        TableDescription description = Global.findToInstance(type);
        if (description == null){
            throw new Error("无法解析的类型: " + type.getName());
        }
        return description.getPrimaryKey().getType();
    }
}
