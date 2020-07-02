package org.zhiyong.mysql;

import org.zhiyong.orm.annotations.Column;
import org.zhiyong.orm.annotations.TypeClass;
import org.zhiyong.orm.api.FieldTypeSelector;
import org.zhiyong.orm.description.ColumnTypeDescription;
import org.zhiyong.orm.description.TableDescription;
import org.zhiyong.orm.exceptions.FieldSelectError;
import org.zhiyong.orm.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;


public class MysqlFieldTypeSelector implements FieldTypeSelector {

    @Override
    public ColumnTypeDescription columnTypeDescription(Field field, Class<?> type, Class<?> mapper) {
        Column column = field.getAnnotation(Column.class);
        if (type.isAssignableFrom(String.class))
            return new ColumnTypeDescription(mapper, Types.VARCHAR, field, "VARCHAR", column.length());
        else if(type.isAssignableFrom(Integer.class))
            return new ColumnTypeDescription(mapper, Types.INTEGER, field, "INT", null);
        else if (type.isAssignableFrom(Date.class))
            return new ColumnTypeDescription(mapper, Types.DATE, field, "DATE", null);
        else if(type.isAssignableFrom(Timestamp.class))
            return new ColumnTypeDescription(mapper, Types.TIMESTAMP, field, "DATETIME", 19);
        return null;
    }
}
