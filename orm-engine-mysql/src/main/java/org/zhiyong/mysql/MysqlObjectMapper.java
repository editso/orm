package org.zhiyong.mysql;

import org.zhiyong.orm.api.*;
import org.zhiyong.orm.description.ConnectionDescription;
import org.zhiyong.orm.description.TableDescription;
import org.zhiyong.orm.util.PackageUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MysqlObjectMapper implements ObjectMapper {
    private Configure configure;
    private Connection connection;
    private TableDescription[] tables;
    private Create create;
    private FieldTypeSelector selector;
    private ConnectionDescription connectionDescription;

    private void initTables(){
        List<TableDescription> classes = new ArrayList<>();
        for (String s : configure.mapperPackages()){
            classes.addAll(Arrays.asList(TableDescription.descriptions(PackageUtil.scannerClass(s), selector)));
        }
        tables = classes.toArray(TableDescription[]::new);
        for (TableDescription table : tables) {
            Global.add(table.getClazz(), table);
        }
        Session session = openSession();
        Transaction transaction = session.beginTransaction();
        for (TableDescription table : tables) {
            try {
                create.createTable(openSession(), table);
            } catch (SQLException e) {
                e.printStackTrace();
                transaction.rollback();
            }
        }
    }

    public TableDescription[] getTables() {
        return tables;
    }


    @Override
    public void init(Configure configure) {
        this.configure = configure;
        this.connectionDescription = configure.connect();
        try {
            Class.forName(connectionDescription.getDriver());
            connection = DriverManager.getConnection(connectionDescription.toUri());
            create = new MysqlCreate();
            selector = new MysqlFieldTypeSelector();
            connection.setAutoCommit(false);
            initTables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Session openSession(){
        try {
            return new SessionImpl(connection);
        }catch (Exception e){
            throw new Error(e.getMessage());
        }
    }

}
