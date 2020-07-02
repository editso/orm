package org.zhiyong.orm.base;

import org.apache.log4j.Logger;
import org.zhiyong.orm.api.*;
import org.zhiyong.orm.description.ConnectionDescription;
import org.zhiyong.orm.description.TableDescription;
import org.zhiyong.orm.exceptions.DriverError;
import org.zhiyong.orm.exceptions.FieldSelectError;
import org.zhiyong.orm.exceptions.NoMapperFoundException;
import org.zhiyong.orm.exceptions.OrmError;
import org.zhiyong.orm.util.JdbcUtils;
import org.zhiyong.orm.util.PackageUtil;
import org.zhiyong.orm.util.ReflectionUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public abstract class BaseObjectMapper implements ObjectMapper {
    protected final Logger logger;
    protected boolean autoCreateTable = false;
    protected final Map<Class<?>, TableDescription> tableDescription = new HashMap<>();
    private FieldTypeSelector fieldTypeSelector;
    protected ConnectionDescription connectionDescription;
    protected Configure configure;

    public BaseObjectMapper(){
        logger = Logger.getLogger(this.getClass());
    }

    @Override
    public ObjectMapper init(Configure configure) {
        logger.debug("初始化映射器");
        this.configure = configure;
        this.connectionDescription = configure.connectionDescription();
        try {
            Class.forName(connectionDescription.getDriver());
        } catch (ClassNotFoundException e) {
            throw new DriverError();
        }
        if (getFieldTypeSelector() == null)
            throw new FieldSelectError();
        fieldTypeSelector = ReflectionUtil.newInstance(getFieldTypeSelector());
        TableDescription description;
        logger.debug("扫描指定包下映射器: "+ Arrays.toString(configure.mapperPackages()));
        for (String pkg : configure.mapperPackages()){
            for (Class<?> aClass : PackageUtil.scannerClass(pkg)) {
                description = TableDescription.from(aClass, fieldTypeSelector);
                if (Objects.isNull(description))continue;
                tableDescription.put(aClass, description);
            }
        }
        logger.debug("扫描到映射器: " + tableDescription.keySet());
        if (isAutoCreateTable())
            createAll();
        return this;
    }

    @Override
    public Configure getConfigure() {
        return configure;
    }

    @Override
    public boolean reCreate(Class<?> mapper) {
        return drop(mapper) && create(mapper);
    }

    public abstract boolean autoCommit();

    public abstract Class<? extends FieldTypeSelector> getFieldTypeSelector();

    @Override
    public final ObjectMapper setAutoCreateTable(boolean autoCreateTable) {
        this.autoCreateTable = autoCreateTable;
        return this;
    }

    @Override
    public final boolean isAutoCreateTable() {
        return autoCreateTable;
    }

    @Override
    public TableDescription findMapper(Class<?> mapper) throws NoMapperFoundException {
        TableDescription description = tableDescription.get(mapper);
        if (Objects.isNull(description))
            throw new NoMapperFoundException();
        return description;
    }

    @Override
    public TableDescription[] getMappers() {
        return tableDescription.values().toArray(TableDescription[]::new);
    }

    @Override
    public Connection connection() throws SQLException {
        return DriverManager.getConnection(connectionDescription.toUri());
    }

    public Session openSession(){
        try {
            Connection connection = connection();
            connection.setAutoCommit(autoCommit());
            return openSession(connection);
        }catch (SQLException e){
            throw new OrmError("连接的到数据库失败: " + e.getMessage());
        }
    }
    protected abstract Session openSession(Connection connection);

    @Override
    public String mapperInfo() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{\n");
        for (TableDescription mapper : getMappers()) {
            buffer.append("\t")
                    .append("<Table: ")
                    .append(mapper.getName())
                    .append(">")
                    .append(" --> ")
                    .append(mapper.getClazz().getName())
                    .append("\n");
        }
        buffer.append("}");
        return buffer.toString();
    }

    @Override
    public void createAll() {
        Session session = openSession();
        try {
            Map<String, JdbcUtils.ColumnInfo[]> columnInfos = JdbcUtils.tableInfo(
                    connectionDescription.getDb(),
                    session.getConnection());
            HashSet<String> tables = new HashSet<>();
            for (TableDescription mapper : getMappers()) {
                create(session, mapper, columnInfos, tables);
            }
        } catch (SQLException e) {
            throw new OrmError("数据库连接失败", e);
        }finally {
            session.close();
        }

    }

    @Override
    public void dropAll() {
        Session session = openSession();
        try {
            HashSet<String> list = new HashSet<>();
            for (TableDescription mapper : getMappers()) {
                drop(session, mapper, list);
            }
        }catch (Exception e){
            throw new OrmError(e);
        }finally {
            session.close();
        }
    }

    @Override
    public boolean drop(Class<?> mapper) {
        Session session = openSession();
        try {
            return drop(session, findMapper(mapper), new HashSet<>());
        } catch (NoMapperFoundException e) {
            return false;
        } catch (SQLException e) {
            throw new OrmError(e);
        } finally {
            session.close();
        }
    }

    protected abstract boolean drop(Session session, TableDescription mapper, Set<String> es)throws SQLException;

    @Override
    public boolean create(Class<?> mapper) {
        Session session = openSession();
        try {
            Map<String, JdbcUtils.ColumnInfo[]> columnInfos = JdbcUtils.tableInfo(
                    connectionDescription.getDb(),
                    session.getConnection());
            return create(session, findMapper(mapper), columnInfos, new HashSet<>());
        } catch (NoMapperFoundException e) {
            logger.error(e + "/" + mapper.getName());
            return false;
        } catch (SQLException e) {
            throw new OrmError(e);
        }finally {
            session.close();
        }
    }

    protected abstract boolean create(Session session,
                                      TableDescription mapper,
                                      Map<String, JdbcUtils.ColumnInfo[]> columnInfos,
                                      Set<String> es);


}
