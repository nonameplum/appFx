package appFx.controllers;

import appFx.datasource.DataSource;
import appFx.datasource.SqlLiteDB;
import appFx.models.Something;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.PopOver;
import org.datafx.controller.FXMLController;
import org.datafx.controller.context.FXMLViewContext;
import org.datafx.controller.context.ViewContext;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.List;

@FXMLController("/fxml/main.fxml")
public class TestController {

    @FXML
    private TextField tfSelected;
    @FXML
    //private TableView<ObservableMap<String, SimpleObjectProperty<Object>>> tableView;
    private TableView<Something> tableView;
    @FXML
    private Button btnEdit;
    @FXMLViewContext
    private ViewContext<Controller> context;
    private DataSource dataSource;
    private PopOver popOver;

    ObservableList<Something> somethingList;

    @PostConstruct
    public void init() {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setEditable(false);
    }

    public void onLoadData() throws SQLException {
        SqlLiteDB sqlLiteDB = (SqlLiteDB) context.getApplicationContext().getRegisteredObject("db");

        String sql = "select count(*) from something";
        SqlRow row = Ebean.createSqlQuery(sql).findUnique();
        System.out.println(row);

        somethingList = FXCollections.observableList(Ebean.find(Something.class).findList());
        System.out.println(somethingList);

        TableColumn idColumn = new TableColumn("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<Something, Integer>("id"));
        TableColumn nameColumn = new TableColumn("NAME");
        nameColumn.setCellValueFactory(new PropertyValueFactory<Something, String>("name"));

        tableView.getColumns().addAll(idColumn, nameColumn);
        tableView.setItems(FXCollections.observableArrayList(somethingList));

    }

    public void onEdit() {
        Something something = somethingList.get(0);
        something.setName("Test");
        Ebean.update(something);
    }

}
