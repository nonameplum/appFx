package appFx.controllers;

import appFx.datasource.DataSource;
import appFx.datasource.SqlLiteDB;
import appFx.models.Something;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    private TableView<ObservableMap<String, SimpleObjectProperty<Object>>> tableView;
    //private TableView<Something> tableView;
    @FXML
    private Button btnEdit;
    @FXMLViewContext
    private ViewContext<Controller> context;
    private DataSource dataSource;
    private PopOver popOver;

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

        List<Something> somethingList = Ebean.find(Something.class).findList();
        System.out.println(somethingList);
    }

    public void onEdit() {

    }

}
