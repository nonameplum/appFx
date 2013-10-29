package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.tweak.ConnectionFactory;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataSource {

    @UseStringTemplate3StatementLocator
    public interface SqlInterface {
        @SqlQuery("select * from <table> order by ID")
        @Mapper(MapMapper.class)
        public List<Map<String, Object>> sqlQuery(@Define("table") String table);

        @SqlUpdate("update <table> set <property> = :value where id = :id")
        int update(@Define("table") String table, @Define("property") String property, @Bind("value") String value, @Bind("id") int id);

        @SqlQuery("select * from <table> limit 1")
        @Mapper(MetaDataMapper.class)
        public ResultSetMetaData getMetaData(@Define("table") String table);

        public void close();
    }

    private ConnectionFactory connectionFactory;
    private DBI dbi;
    private ObservableList<Map<String, Object>> queryResult;
    private DatabaseMetaData databaseMetaData;
    private String tableName;
    private String primaryColumnName;

    private Boolean isPrimaryColumnVisible = false;
    List<TableColumn<Map<String, Object>, ?>> tableColumns;

    public DataSource(ConnectionFactory connectionFactory, String tableName) {
        this.connectionFactory = connectionFactory;
        try {
            databaseMetaData = this.connectionFactory.openConnection().getMetaData();
            ResultSet rs = databaseMetaData.getPrimaryKeys(null, null, tableName);
            while (rs.next()) {
                primaryColumnName = rs.getString("COLUMN_NAME");
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        this.dbi = new DBI(this.connectionFactory);
        this.tableName = tableName;
        select();
    }

    public void select() {
        SqlInterface dao = dbi.open(SqlInterface.class);
        List<Map<String, Object>> result = dao.sqlQuery(tableName);
        dao.close();
        this.queryResult = FXCollections.observableList(result);
        if (queryResult.size() > 0) {
            createColumns(queryResult.get(0));
        }
    }

    private void createColumns(Map<String, Object> row) {
        if (this.tableColumns == null) {
            this.tableColumns = new ArrayList();
        } else {
            this.tableColumns.clear();
        }

        Callback<TableColumn<Map, Object>, TableCell<Map, Object>> cellFactory = p -> new EditingCell();

        for (int i = 0; i < row.keySet().size(); i++) {
            Boolean isPrimaryKeyColumn = false;
            if (primaryColumnName != null) {
                isPrimaryKeyColumn = primaryColumnName.equalsIgnoreCase(row.keySet().toArray()[i].toString());
            }

            if ((isPrimaryKeyColumn && this.isPrimaryColumnVisible) || (!isPrimaryKeyColumn)) {
                TableColumn tableColumn = new TableColumn();
                tableColumn.setText(row.keySet().toArray()[i].toString().toUpperCase());
                tableColumn.setCellValueFactory(new MapValueFactoryKeys(row.keySet().toArray()[i].toString()));
                tableColumn.setCellFactory(cellFactory);
                tableColumns.add(tableColumn);

                tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent cellEditEvent) {
                        String key = ((MapValueFactoryKeys) cellEditEvent.getTableColumn().getCellValueFactory()).getKey().toString();
                        String value = cellEditEvent.getNewValue().toString();
                        int id = (int) ((LinkedHashMap<String, Object>) cellEditEvent.getRowValue()).get("id");
                        SqlInterface dao = dbi.open(SqlInterface.class);
                        dao.update(tableName, key, value, id);
                        dao.close();
                        ((LinkedHashMap<String, Object>) cellEditEvent.getTableView().getItems().get(cellEditEvent.getTablePosition().getRow())).put(key, cellEditEvent.getNewValue());
                    }
                });
            }
        }
    }

    public ObservableList<Map<String, Object>> getQueryResult() {
        return queryResult;
    }

    public List<TableColumn<Map<String, Object>, ?>> getTableColumns() {
        return tableColumns;
    }

    public TableColumn getColumnWithKey(String key) {
        for (int i = 0; i < tableColumns.size(); i++) {
            if (key.equalsIgnoreCase(((MapValueFactoryKeys) tableColumns.get(i).getCellValueFactory()).getKey().toString())) {
                return tableColumns.get(i);
            }
        }
        return null;
    }

    public String getTableName() {
        return tableName;
    }

    public Boolean getPrimaryColumnVisible() {
        return isPrimaryColumnVisible;
    }

    public void setPrimaryColumnVisible(Boolean primaryColumnVisible) {
        isPrimaryColumnVisible = primaryColumnVisible;
        createColumns(queryResult.get(0));
    }
}
