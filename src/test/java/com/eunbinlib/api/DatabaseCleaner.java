package com.eunbinlib.api;

import org.hibernate.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseCleaner implements InitializingBean {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        if (tableNames != null) {
            return;
        }

        entityManager.unwrap(Session.class)
                .doWork(this::extractTableNames);
    }

    private void extractTableNames(Connection conn) throws SQLException {
        List<String> tableNames = new ArrayList<>();

        ResultSet tables = conn
                .getMetaData()
                .getTables(conn.getCatalog(), null, "%", new String[]{"TABLE"});

        while (tables.next()) {
            tableNames.add(tables.getString("table_name"));
        }

        this.tableNames = tableNames;
    }

    public void execute() {
        entityManager.unwrap(Session.class)
                .doWork(this::cleanUpDatabase);
    }

    private void cleanUpDatabase(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        statement.executeUpdate("SET REFERENTIAL_INTEGRITY FALSE");

        for (String tableName : tableNames) {
            statement.executeUpdate("TRUNCATE TABLE " + tableName);
//            statement.executeUpdate("ALTER TABLE " + tableName + " ALTER COLUMN ID RESTART WITH 1"); // NOTE: 지금은 ID가 Sequence 전략이 아닌 Identity 전략임
        }

        statement.executeUpdate("SET REFERENTIAL_INTEGRITY TRUE");
    }
}