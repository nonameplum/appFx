package appFx.controllers;

import appFx.datasource.TableViewDS;
import appFx.datasource.SqlLiteDBI;
import appFx.models.Something;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
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
    private TableViewDS tableViewDS;
    private PopOver popOver;

    ObservableList<Something> somethingList;

    @PostConstruct
    public void init() {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setEditable(false);

        makeEbeanTest();
    }

    public void onLoadData() throws SQLException {
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

    public void makeEbeanTest() {
        System.out.println("-- EBEAT JAVAFX BEAN PROPERTIES --");

        somethingList = FXCollections.observableList(Ebean.find(Something.class).findList());
        // Update test
        Something something = somethingList.get(0);
        System.out.println(something.getName()); // Print "Ebean test"
        something.setName("Test");
        Ebean.update(something);
        System.out.println(something.getName()); // Value is setted to "Test"

        somethingList = FXCollections.observableList(Ebean.find(Something.class).findList());
        Something somethingAfterUpdate = somethingList.get(0);
        System.out.println(somethingAfterUpdate.getName()); // Will print Old Value "Ebean test"
    }

}
