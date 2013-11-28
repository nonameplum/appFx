package appFx.controllers;

import appFx.datasource.DataSource;
import appFx.datasource.SqlLiteDB;
import appFx.datasource.daos.UniversalDAO;
import appFx.datasource.helpers.EditingCell;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.skife.jdbi.v2.tweak.ConnectionFactory;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Controller {

    @FXML
    private TextField tfSelected;
    @FXML
    private TableView<Map<String, Object>> tableView;

    public Stage primaryStage = null;

    public ConnectionFactory connectionFactory = null;
    DataSource dataSource = null;

    public void setConfiguration(Stage primaryStage) {
        this.primaryStage = primaryStage;

        SqlLiteDB sqlDB = new SqlLiteDB();
        connectionFactory = sqlDB.connectionFactory;

        tableView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                if ((Integer) observableValue.getValue() >= 0) {
                    String name = tableView.getSelectionModel().getSelectedItem().get("name").toString();
                    tfSelected.setText(name);
                }
            }
        });

        tableView.getSelectionModel().setCellSelectionEnabled(true);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void getData() throws SQLException {
        dataSource = new DataSource(connectionFactory, "something");
        dataSource.setPrimaryColumnVisible(true);
        tableView.getColumns().clear();
        tableView.getColumns().addAll(dataSource.getTableColumns());
        //dataSource.getTableColumns().remove(dataSource.getColumnWithKey("ID"));
        //dataSource.getColumnWithKey("NAME").setMinWidth(150);
        //dataSource.getColumnWithKey("NAME").setMaxWidth(150);

        tableView.setItems(dataSource.getQueryResult());
    }

    public void updateSelectedRecord() {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setWidth(300);
            stage.setHeight(300);

            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));

            int i;
            for (i = 0; i < dataSource.getTableColumns().size(); i++) {
                TableColumn<Map<String, Object>, ?> tableColumn = dataSource.getTableColumns().get(i);

                Label label = new Label(tableColumn.getText());
                grid.add(label, 0, i);

                TextField textField = new TextField();
                textField.setId(tableColumn.getText().toLowerCase());
                textField.setText(tableView.getSelectionModel().getSelectedItem().get(tableColumn.getText().toLowerCase()).toString());
                if (tableColumn.getText().equalsIgnoreCase("id")) {
                    textField.setDisable(true);
                }
                grid.add(textField, 1, i);
            }

            Button btnSave = new Button("Save");
            btnSave.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    for (int j = 0; j < dataSource.getTableColumns().size(); j++) {
                        TableColumn<Map<String, Object>, ?> tableColumn = dataSource.getTableColumns().get(j);
                        for (Node node : grid.getChildren()) {
                            if (node.getId() != null && node.getId().equals(tableColumn.getText().toLowerCase())) {
                                String key = tableColumn.getText().toLowerCase();
                                String value = ((TextField) node).getText();
                                Integer id = Integer.valueOf(tableView.getSelectionModel().getSelectedItem().get("id").toString());
                                UniversalDAO dao = dataSource.getDbi().open(UniversalDAO.class);
                                dao.update(dataSource.getTableName(), key, value, id);
                                dao.close();
                                tableView.getItems().get(tableView.getSelectionModel().getSelectedIndex()).put(key, value);
                                tableColumn.setVisible(false);
                                tableColumn.setVisible(true);
                            }
                        }
                    }
                }
            });
            grid.add(btnSave, 1, i + 1);

            stage.setScene(new Scene(grid));

            stage.setTitle("Edit: " + tableView.getSelectionModel().getSelectedItem().get("name"));
            stage.show();

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    primaryStage.getScene().getRoot().setEffect(null);
                }
            });

            BoxBlur boxBlur = new BoxBlur();
            boxBlur.setWidth(10);
            boxBlur.setHeight(3);
            boxBlur.setIterations(3);

            primaryStage.getScene().getRoot().setEffect(boxBlur);
        }
    }
}