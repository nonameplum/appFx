package sample;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.skife.jdbi.v2.tweak.ConnectionFactory;

import java.sql.SQLException;
import java.util.Map;

public class Controller {

    @FXML
    private TextField tfSelected;
    @FXML
    private TableView<Map<String, Object>> tableView;

    public ConnectionFactory connectionFactory = null;

    public void setConfiguration() {
        SqlLiteDB sqlDB = new SqlLiteDB();
        connectionFactory = sqlDB.connectionFactory;

        tableView.getSelectionModel().selectedIndexProperty().addListener((a, b, c) -> {
            if ((Integer) a.getValue() > 0) {
                String name = tableView.getSelectionModel().getSelectedItem().get("name").toString();
                tfSelected.setText(name);
            }
        });

        tableView.getSelectionModel().setCellSelectionEnabled(true);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void getData() throws SQLException {
        DataSource dataSource = new DataSource(connectionFactory, "something");
        dataSource.setPrimaryColumnVisible(true);
        tableView.getColumns().clear();
        tableView.getColumns().addAll(dataSource.getTableColumns());
        //dataSource.getTableColumns().remove(dataSource.getColumnWithKey("ID"));
        //dataSource.getColumnWithKey("NAME").setMinWidth(150);
        //dataSource.getColumnWithKey("NAME").setMaxWidth(150);

        tableView.setItems(dataSource.getQueryResult());
    }
}
