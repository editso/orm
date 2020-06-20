package org.zhiyong.mysql;

import org.zhiyong.format.builder.FormatterFactory;
import org.zhiyong.format.interfaces.ParameterFormatter;
import org.zhiyong.orm.api.Create;
import org.zhiyong.orm.api.Session;
import org.zhiyong.orm.description.FieldDescription;
import org.zhiyong.orm.description.TableDescription;

import java.sql.SQLException;

public class MysqlCreate implements Create {
    public static String CREATE_TABLE_FORMAT = "CREATE TABLE IF NOT EXISTS :table(:fields, CONSTRAINT PK PRIMARY KEY(:key));";

    @Override
    public void createTable(Session session, TableDescription description) throws SQLException {
        FieldDescription[] fieldDescription = description.getFieldDescription();
        StringBuilder builder = new StringBuilder();
        ParameterFormatter formatter;
        for (FieldDescription field : fieldDescription) {
            formatter = FormatterFactory.builder(":name :type ${primary key,unique,not null},");
            formatter.set("name", field.getName())
                    .set("type", field.getType())
                    .setOption("unique", field.unique())
                    .setOption("notNull", !field.nullable());
            builder.append(formatter.transform());
        }
        builder.delete(builder.length() - 1, builder.length());
        session.builder(CREATE_TABLE_FORMAT).mat(o -> {
            o.set("table", description.getName());
            o.set("fields", builder.toString());
            o.set("key", description.getPrimaryKey().getName());
        }).execute();
    }
}
