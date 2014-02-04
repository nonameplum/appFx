package appFx.datasource;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.tweak.ConnectionFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlLiteDBI {

    private final DBI dbi;
    private final ConnectionFactory connectionFactory;
    private static String datasourceClassName = "org.sqlite.JDBC";
    private static String connectionUrl = "jdbc:sqlite:database.db";

    public SqlLiteDBI() {
        try {
            Class.forName(datasourceClassName);
        } catch (ClassNotFoundException e) {
            System.err.println("No JDBC driver");
            e.printStackTrace();
        }

        connectionFactory = () -> DriverManager.getConnection(connectionUrl);

        dbi = new DBI(connectionFactory);
        createTable();
    }

    private void createTable() {
        Handle h = dbi.open();
        try {
            h.execute("select * from something");
            h.execute("select * from user");
        } catch (Exception e) {
            try {
                h.execute("drop table something");
                h.execute("drop table user");
            } catch (Exception e2) {}
            h.execute("create table something (id int, name varchar(100))");
            h.execute("create table user (id int, name varchar(100))");
            createBootstrapData();
        }
        h.close();
    }

    private void createBootstrapData() {
        Handle h = dbi.open();
        for (Integer i = 0; i < 50; i++) {
            h.execute("insert into something (id, name) values (?, ?)", i, "Name " + i.toString());
        }

        for (Integer i = 0; i < 50; i++) {
            h.execute("insert into user (id, name) values (?, ?)", i, "Name " + i.toString());
        }
        h.close();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionUrl);
    }

    public DBI getDbi() {
        return dbi;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public static String getConnectionUrl() {
        return connectionUrl;
    }
}
