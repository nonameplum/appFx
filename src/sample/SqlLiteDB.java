package sample;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.tweak.ConnectionFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlLiteDB {

    public final DBI dbi;
    public final ConnectionFactory connectionFactory;

    public SqlLiteDB() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("No JDBC driver");
            e.printStackTrace();
        }

        connectionFactory = new ConnectionFactory() {
            @Override
            public Connection openConnection() throws SQLException {
                return DriverManager.getConnection("jdbc:sqlite:database.db");
            }
        };

        dbi = new DBI(connectionFactory);
        createTable();
    }

    public void createTable() {
        Handle h = dbi.open();
        try {
            h.execute("select * from something");
        } catch (Exception e) {
            h.execute("create table something (id int, name varchar(100))");
            createBootstrapData();
        }
        h.close();
    }

    public void createBootstrapData() {
        Handle h = dbi.open();
        for (Integer i = 0; i < 100; i++) {
            h.execute("insert into something (id, name) values (?, ?)", i, "Name " + i.toString());
        }
        h.close();
    }
}
