package org.zhiyong.orm.util;

import org.zhiyong.orm.description.FieldDescription;
import org.zhiyong.orm.description.TableDescription;
import org.zhiyong.orm.engine.EngineManager;
import org.zhiyong.orm.exceptions.NoMapperFoundException;
import org.zhiyong.orm.interfaces.ReturnFunction;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcUtils {
    public static Map<String, ColumnInfo[]> tableInfo(String dbName,
                                                      Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(dbName, null, null, new String[]{"TABLE"});
        Map<String, ColumnInfo[]> tableInfo = new HashMap<>();
        List<ColumnInfo> column = new ArrayList<>();
        ResultSet columnResult;
        String tableName;
        while (resultSet.next()){
            tableName = resultSet.getString("TABLE_NAME");
            columnResult = metaData.getColumns(dbName, null,tableName, null);
            while (columnResult.next()){
                column.add(new ColumnInfo(
                        columnResult.getString("COLUMN_NAME"),
                        columnResult.getInt("DATA_TYPE"),
                        columnResult.getString("SCOPE_TABLE"),
                        columnResult.getInt("SOURCE_DATA_TYPE"),
                        columnResult.getInt("NULLABLE"),
                        columnResult.getInt("COLUMN_SIZE")
                ));
            }
            tableInfo.put(tableName, column.toArray(ColumnInfo[]::new));
            column.clear();
        }
        return tableInfo;
    }

    public static <T>T injection(Class<T> clazz, ReturnFunction<Object, String> function)
            throws NoMapperFoundException {
        TableDescription description = EngineManager.currentObjectMapper().findMapper(clazz);
        T t = ReflectionUtil.newInstance(clazz);
        for (FieldDescription fieldDescription : description.getFieldDescription()) {
            fieldDescription.setValue(t, function.apply(fieldDescription.getName()));
        }
        return t;
    }


    public static <E> List<E> mapper(ResultSet set, TableDescription description){
        List<E> elements = new ArrayList<>();
        if (set == null)return elements;
        try {
            E element = null;
            while (set.next()){
                element = description.getMapperInstance();
                for (FieldDescription fieldDescription : description.getFieldDescription()) {
                    fieldDescription.setValue(element, set.getObject(fieldDescription.getName()));
                }
                elements.add(element);
            }
        }catch (SQLException e){
            return elements;
        }
        return elements;
    }

    public static <E>E mapperKey(ResultSet set, TableDescription description){
        List<E> elements =  mapper(set, description);
        if (elements.isEmpty())return null;
        return elements.get(0);
    }

    public final static class ColumnInfo{
        public final String name;
        public final int type;
        public final String ref;
        public final Integer refType;
        public final Integer nullable;
        public final Integer length;

        public ColumnInfo(String name, int type, String ref, Integer refType, int nullable, Integer length) {
            this.name = name;
            this.type = type;
            this.ref = ref;
            this.refType = refType;
            this.nullable = nullable;
            this.length = length;
        }

        @Override
        public String toString() {
            return "ColumnInfo{" +
                    "name='" + name + '\'' +
                    ", type=" + type +
                    ", ref='" + ref + '\'' +
                    ", refType=" + refType +
                    ", nullable=" + nullable +
                    ", length=" + length +
                    '}';
        }
    }
}
