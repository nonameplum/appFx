package appFx.datasource;

import appFx.datasource.daos.UniversalDAO;
import appFx.datasource.helpers.EditingCell;
import appFx.datasource.helpers.MapValueFactoryKeys;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.tweak.ConnectionFactory;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataSource {

    List<TableColumn<ObservableMap<String, SimpleObjectProperty<Object>>, ?>> tableColumns;
    private ConnectionFactory connectionFactory;
    private DBI dbi;
    private ObservableList<ObservableMap<String, SimpleObjectProperty<Object>>> queryResult;
    private DatabaseMetaData databaseMetaData;
    private String tableName;

    public DataSource(ConnectionFactory connectionFactory, String tableName, Boolean createColumns) {
        this.connectionFactory = connectionFactory;
        this.dbi = new DBI(this.connectionFactory);
        this.tableName = tableName;
        select();

        if (createColumns == null) {
            createColumns = true;
        }
        if (this.queryResult.size() > 0 && createColumns) {
            createColumns(this.queryResult.get(0));
        }
    }

    public void select() {
        UniversalDAO dao = dbi.open(UniversalDAO.class);
        List<ObservableMap<String, SimpleObjectProperty<Object>>> result = dao.sqlQuery(tableName);
        dao.close();
        this.queryResult = FXCollections.observableList(result);
    }

    private void createColumns(ObservableMap<String, SimpleObjectProperty<Object>> row) {
        if (this.tableColumns == null) {
            this.tableColumns = new ArrayList<TableColumn<ObservableMap<String, SimpleObjectProperty<Object>>, ?>>();
        } else {
            this.tableColumns.clear();
        }

        Callback<TableColumn<Map, Object>, TableCell<Map, Object>> cellFactory = p -> new EditingCell();

        for (int i = 0; i < row.keySet().size(); i++) {
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
                    Integer id = Integer.valueOf(((ObservableMap<String, SimpleObjectProperty<Object>>) cellEditEvent.getRowValue()).get("id").getValue().toString());
                    UniversalDAO dao = dbi.open(UniversalDAO.class);
                    dao.update(tableName, key, value, id);
                    dao.close();
                    int selectedRowIndex = cellEditEvent.getTablePosition().getRow();
                    ((ObservableMap<String, SimpleObjectProperty<Object>>) cellEditEvent.getTableView().getItems().get(selectedRowIndex)).get(key).setValue(cellEditEvent.getNewValue());
                }
            });
        }
    }

    public ObservableList<ObservableMap<String, SimpleObjectProperty<Object>>> getQueryResult() {
        return queryResult;
    }

    public List<TableColumn<ObservableMap<String, SimpleObjectProperty<Object>>, ?>> getTableColumns() {
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

    public DBI getDbi() {
        return dbi;
    }
}
