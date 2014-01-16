package appFx.controllers;

import appFx.datasource.TableViewDS;
import appFx.datasource.SqlLiteDBI;
import appFx.datasource.daos.UniversalDAO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.controlsfx.control.PopOver;
import org.datafx.controller.FXMLController;
import org.datafx.controller.context.FXMLViewContext;
import org.datafx.controller.context.ViewContext;

import javax.annotation.PostConstruct;
import java.io.IOException;

@FXMLController("/fxml/main.fxml")
public class Controller {

    @FXML
    private TextField tfSelected;
    @FXML
    private TableView<ObservableMap<String, SimpleObjectProperty<Object>>> tableView;
    //private TableView<Something> tableView;
    @FXML
    private Button btnEdit;
    @FXMLViewContext
    private ViewContext<Controller> context;
    private TableViewDS tableViewDS;
    private PopOver popOver;

    @PostConstruct
    public void init() {

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setEditable(false);
    }

    public void onLoadData() {
        SqlLiteDBI sqlLiteDBI = (SqlLiteDBI) context.getApplicationContext().getRegisteredObject("db");

        tableViewDS = new TableViewDS(sqlLiteDBI.getConnectionFactory(), "something", null);
        tableView.getColumns().clear();
        popOver = new PopOver();

        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() < 2) {
                    popOver.hide();
                    tableView.getSelectionModel().selectedItemProperty();
                }
            }
        });

        tfSelected.textProperty().bind(tableView.getSelectionModel().selectedItemProperty().asString());

        Callback<TableColumn<ObservableMap<String, SimpleObjectProperty<Object>>, Object>, TableCell<ObservableMap<String, SimpleObjectProperty<Object>>, Object>> cellFactory = p -> {
            TableCell<ObservableMap<String, SimpleObjectProperty<Object>>, Object> cell = new TableCell<ObservableMap<String, SimpleObjectProperty<Object>>, Object>() {
                @Override
                public void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : getString());
                    setGraphic(null);
                }

                private String getString() {
                    return getItem() == null ? "" : getItem().toString();
                }
            };

            cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getClickCount() > 1) {
                        TableCell c = (TableCell) event.getSource();
                        popOver.hide();
                        popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_TOP);
                        popOver.setArrowSize(6);
                        popOver.setCornerRadius(6);
                        popOver.setDetachable(false);
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/popcontent.fxml"));
                            Parent root = (Parent) fxmlLoader.load();
                            Label lblName = (Label) root.lookup("#lblName");
                            lblName.setText(c.getText());
                            popOver.setContentNode(root);

                            GridPane grid = new GridPane();
                            grid.setAlignment(Pos.CENTER);
                            grid.setHgap(10);
                            grid.setVgap(10);
                            grid.setPadding(new Insets(25, 25, 25, 25));

                            int i;
                            for (i = 0; i < tableViewDS.getTableColumns().size(); i++) {
                                TableColumn<ObservableMap<String, SimpleObjectProperty<Object>>, ?> tableColumn = tableViewDS.getTableColumns().get(i);

                                Label label = new Label(tableColumn.getText());
                                grid.add(label, 0, i);

                                TextField textField = new TextField();
                                textField.setId(tableColumn.getText().toLowerCase());
                                textField.setText(tableView.getSelectionModel().getSelectedItem().get(tableColumn.getText().toLowerCase()).getValue().toString());
                                if (tableColumn.getText().equalsIgnoreCase("id")) {
                                    textField.setDisable(true);
                                }
                                grid.add(textField, 1, i);
                            }

                            Button btnSave = (Button) root.lookupAll("#btnSave").toArray()[0];
                            btnSave.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent actionEvent) {
                                    for (int j = 0; j < tableViewDS.getTableColumns().size(); j++) {
                                        TableColumn<ObservableMap<String, SimpleObjectProperty<Object>>, ?> tableColumn = tableViewDS.getTableColumns().get(j);
                                        for (Node node : grid.getChildren()) {
                                            if (node.getId() != null && node.getId().equals(tableColumn.getText().toLowerCase())) {
                                                String key = tableColumn.getText().toLowerCase();
                                                String value = ((TextField) node).getText();
                                                Integer id = Integer.valueOf(tableView.getSelectionModel().getSelectedItem().get("id").getValue().toString());
                                                UniversalDAO dao = tableViewDS.getDbi().open(UniversalDAO.class);
                                                dao.update(tableViewDS.getTableName(), key, value, id);
                                                dao.close();
                                                int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
                                                tableView.getItems().get(selectedIndex).get(key).setValue(value);
                                            }
                                        }
                                    }
                                    popOver.hide();
                                }
                            });

                            AnchorPane anchorPane = (AnchorPane) root.lookupAll("#controlsContent").toArray()[0];
                            anchorPane.getChildren().add(grid);

                            Button btnCancel = (Button) root.lookupAll("#btnCancel").toArray()[0];
                            btnCancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    popOver.hide();
                                }
                            });

                            BoxBlur boxBlur = new BoxBlur();
                            boxBlur.setWidth(2);
                            boxBlur.setHeight(2);
                            boxBlur.setIterations(1);

                            c.getTableRow().setEffect(boxBlur);

                            popOver.setOnHidden(new EventHandler<WindowEvent>() {
                                @Override
                                public void handle(WindowEvent windowEvent) {
                                    c.getTableRow().setEffect(null);
                                    tableView.setDisable(false);
                                }
                            });

                            popOver.show(tableView.getParent(), event.getScreenX(), event.getScreenY());
                            popOver.setAutoHide(true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            return cell;
        };

        tableViewDS.getColumnWithKey("name").setCellFactory(cellFactory);

        tableView.getColumns().addAll(tableViewDS.getTableColumns());
        tableView.setItems(tableViewDS.getQueryResult());
    }

    public void onEdit() {
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
            for (i = 0; i < tableViewDS.getTableColumns().size(); i++) {
                TableColumn<ObservableMap<String, SimpleObjectProperty<Object>>, ?> tableColumn = tableViewDS.getTableColumns().get(i);

                Label label = new Label(tableColumn.getText());
                grid.add(label, 0, i);

                TextField textField = new TextField();
                textField.setId(tableColumn.getText().toLowerCase());
                textField.setText(tableView.getSelectionModel().getSelectedItem().get(tableColumn.getText().toLowerCase()).getValue().toString());
                if (tableColumn.getText().equalsIgnoreCase("id")) {
                    textField.setDisable(true);
                }
                grid.add(textField, 1, i);
            }

            Button btnSave = new Button("Save");
            btnSave.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    for (int j = 0; j < tableViewDS.getTableColumns().size(); j++) {
                        TableColumn<ObservableMap<String, SimpleObjectProperty<Object>>, ?> tableColumn = tableViewDS.getTableColumns().get(j);
                        for (Node node : grid.getChildren()) {
                            if (node.getId() != null && node.getId().equals(tableColumn.getText().toLowerCase())) {
                                String key = tableColumn.getText().toLowerCase();
                                String value = ((TextField) node).getText();
                                Integer id = Integer.valueOf(tableView.getSelectionModel().getSelectedItem().get("id").getValue().toString());
                                UniversalDAO dao = tableViewDS.getDbi().open(UniversalDAO.class);
                                dao.update(tableViewDS.getTableName(), key, value, id);
                                dao.close();
                                int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
                                tableView.getItems().get(selectedIndex).get(key).setValue(value);
                            }
                        }
                    }
                }
            });
            grid.add(btnSave, 1, i + 1);

            stage.setScene(new Scene(grid));

            stage.setTitle("Row[ID]: " + tableView.getSelectionModel().getSelectedItem().get("id").getValue());
            stage.show();

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    context.getRootNode().getScene().getRoot().setEffect(null);
                }
            });

            BoxBlur boxBlur = new BoxBlur();
            boxBlur.setWidth(3);
            boxBlur.setHeight(3);
            boxBlur.setIterations(5);

            context.getRootNode().getScene().getRoot().setEffect(boxBlur);
        }
    }

}
