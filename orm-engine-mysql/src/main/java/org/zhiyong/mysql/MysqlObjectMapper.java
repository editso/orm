package org.zhiyong.mysql;

import org.zhiyong.format.builder.ParameterFactory;
import org.zhiyong.orm.api.*;
import org.zhiyong.orm.base.BaseObjectMapper;
import org.zhiyong.orm.description.ColumnTypeDescription;
import org.zhiyong.orm.description.ConstraintDescription;
import org.zhiyong.orm.description.FieldDescription;
import org.zhiyong.orm.description.TableDescription;
import org.zhiyong.orm.exceptions.MapperError;
import org.zhiyong.orm.exceptions.NoMapperFoundException;
import org.zhiyong.orm.util.ArrayUtil;
import org.zhiyong.orm.util.JdbcUtils;
import org.zhiyong.orm.util.StringUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;


public class MysqlObjectMapper extends BaseObjectMapper {

    @Override
    public boolean autoCommit() {
        return false;
    }

    @Override
    public Class<? extends FieldTypeSelector> getFieldTypeSelector() {
        return MysqlFieldTypeSelector.class;
    }

    @Override
    protected boolean dropTable(Session session, TableDescription mapper) throws SQLException{
        session.builder("DROP TABLE IF EXISTS :table").parameter(m->{
            m.set("table", mapper.getName());
        }).execute();
        session.commit();
        return true;
    }

    private void updateTable(Session session,
                             TableDescription table,
                             FieldDescription[] field,
                             JdbcUtils.ColumnInfo[] columnInfo){
        logger.debug("更新表: " + table.getName());
        ColumnTypeDescription type;
        JdbcUtils.ColumnInfo column;
        int stat = 1;
        for (int i = 0, c = 0; i < field.length; i++, c++) {
            FieldDescription description  = field[i];
            type = description.getColumnTypeDescription();
            if (columnInfo.length <= c){
                stat = 2;
            }
            if (stat == 2){
                //  添加字段
                session.builder("ALTER TABLE :table ADD :field :type ${NOT NULL}").parameter(m->{
                    m.set("table", table.getName());
                    m.set("field", description.getName());
                    m.set("type", description.getType());
                }).execute();
                stat = 1;
            }else {
                column = columnInfo[i];
                if (!column.name.equals(description.getName())){
                    stat = 2; // 更新字段
                    i--;
                    continue;
                }
                if (column.type != type.getType() || (type.getLength() != null && !type.getLength().equals(column.length))){
                    session.builder("ALTER TABLE :table MODIFY :field :type").parameter(m->{
                        m.set("table", table.getName());
                        m.set("field", description.getName());
                        m.set("type", description.getType());
                    }).execute();
                }
            }
        }

    }

    protected Session openSession(Connection connection){
        return new SessionImpl(connection);
    }

    @Override
    public void dropConstraint(Session session, JdbcUtils.Constraint constraint) {
        session.builder("ALTER TABLE :table DROP FOREIGN KEY :fk").parameter(p->{
            p.set(constraint.tableName);
            p.set(constraint.fkName);
        }).execute();
    }

    @Override
    protected boolean create(Session session,
                             TableDescription mapper,
                             Map<String, JdbcUtils.ColumnInfo[]> tableInfos,
                             Set<String> tables) {
        if (tables.contains(mapper.getName())) return true;
        tables.add(mapper.getName()); // 避免循环依赖导致多次创建
        for (ConstraintDescription constraintDescription : mapper.getConstraintDescriptions()) {
            if (!constraintDescription.isCollection()){
                try {
                    create(session, findMapper(constraintDescription.getTypeClass()), tableInfos, tables);
                } catch (NoMapperFoundException e) {
                    logger.error(e);
                }
            }
        }
        JdbcUtils.ColumnInfo[] columns = tableInfos.get(mapper.getName());
        if (columns != null){
            updateTable(session, mapper, mapper.getFieldDescription(), columns);
            return true;
        }
        createTable(session, mapper);
        return true;
    }

    private void createTable(Session session, TableDescription mapper) {
        ParameterFactory.functional("CREATE TABLE IF NOT EXISTS :table(:fields);")
                .by("fields", ":name :type ${PRIMARY KEY, UNIQUE,NOT NULL,AUTO_INCREMENT}", mapper.getFieldDescription(), (f, e)->{
                    f.set("name", e.getName())
                            .set("type", e.getType())
                            .setOption("UNIQUE", e.unique())
                            .setOption("NOTNULL", !e.nullable())
                            .setOption("PRIMARYKEY", e.isPrimaryKey())
                            .setOption("AUTO_INCREMENT", e.isAutoIncrement());
                }).by(mapper, (f, e)->{
                    f.set("table", e.getName());
                    session.builder(f.transform()).execute();
                });
        session.commit();
        for (ConstraintDescription constraintDescription : mapper.getConstraintDescriptions()) {
            alterConstraint(session, mapper, constraintDescription);
        }
    }

    private void alterConstraint(Session session, TableDescription mapper, ConstraintDescription description){
        if (description.isCollection())return;
        try {
            TableDescription ref  = findMapper(description.getTypeClass());
            session.builder("ALTER TABLE :table ADD CONSTRAINT :name FOREIGN KEY(:key) REFERENCES :ref(:column)")
                    .parameter(f->{
                f.set("table", mapper.getName())
                .set("name", StringUtil.under("FK", mapper.getName().toUpperCase(), description.getJoinColumn().toUpperCase()))
                .set("key", description.getSqlJoinColumn())
                .set("ref", ref.getName())
                .set("column", ref.getPrimaryKey().getName());
            }).execute();
            session.commit();
        } catch (NoMapperFoundException e) {
            throw new MapperError(mapper.getClazz());
        }

    }

    private void dropConstraint(Session session, TableDescription mapper, ConstraintDescription description){
        session.builder("ALTER TABLE :table DROP FOREIGN KEY :name").parameter(f->{
            f.set("table", mapper.getName())
             .set("name", StringUtil.under("FK", mapper.getName().toUpperCase(), description.getJoinColumn().toUpperCase()));
        }).execute();
    }
}
