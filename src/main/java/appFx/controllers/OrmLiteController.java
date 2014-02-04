package appFx.controllers;

import appFx.datasource.SqlLiteDBI;
import appFx.datasource.TableViewDS;
import appFx.models.SomethingOrmLite;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.db.BaseDatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@FXMLController("/fxml/main.fxml")
public class OrmLiteController {

    @FXML
    private TextField tfSelected;
    @FXML
    //private TableView<ObservableMap<String, SimpleObjectProperty<Object>>> tableView;
    private TableView<SomethingOrmLite> tableView;
    @FXML
    private Button btnEdit;
    @FXMLViewContext
    private ViewContext<Controller> context;
    private TableViewDS tableViewDS;
    private PopOver popOver;

    ObservableList<SomethingOrmLite> SomethingOrmLiteList;

    JdbcConnectionSource jdbcConnectionSource;
    Dao<SomethingOrmLite, ?> dao;

    JdbcPooledConnectionSource firebirdConnection;

    @PostConstruct
    public void init() {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setEditable(false);

        try {
            jdbcConnectionSource = new JdbcPooledConnectionSource(SqlLiteDBI.getConnectionUrl());

//            firebirdConnection = new JdbcPooledConnectionSource(
//                    "jdbc:firebirdsql://cbi-sql/Dane/E_FIRMA/E_FIRMA.GDB",
//                    "topsupus",
//                    "topmaspa", new BaseDatabaseType() {
//                @Override
//                protected String getDriverClassName() {
//                    return "org.firebirdsql.jdbc.FBDriver";
//                }
//
//                @Override
//                public boolean isDatabaseUrlThisType(String url, String dbTypePart) {
//                    return false;
//                }
//
//                @Override
//                public String getDatabaseName() {
//                    return null;
//                }
//            });
//
//            Dao<SomethingOrmLite, ?> liteDao = DaoManager.createDao(firebirdConnection, SomethingOrmLite.class);
//            GenericRawResults<Map<String, String>> rawResults =
//                    liteDao.queryRaw(
//                            "select * from EWI_POJAZDY",
//                            new RawRowMapper<Map<String, String>>() {
//                                public Map<String, String> mapRow(String[] columnNames,
//                                                  String[] resultColumns) {
//                                    LinkedHashMap<String, String> map = new LinkedHashMap<>();
//                                    for (int i=0; i<columnNames.length; i++) {
//                                        map.put(columnNames[i], resultColumns[i]);
//                                    }
//                                    return map;
//                                }
//                            });
//
//            List<Map<String, String>> results = rawResults.getResults();
//            for(Map<String, String> map : results) {
//                System.out.println(map);
//            }

            dao = DaoManager.createDao(jdbcConnectionSource, SomethingOrmLite.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onLoadData() throws SQLException {
        SomethingOrmLiteList = FXCollections.observableList(dao.queryForAll());
        tableView.getColumns().clear();

        TableColumn idColumn = new TableColumn("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<SomethingOrmLite, Integer>("id"));
        TableColumn nameColumn = new TableColumn("NAME");
        nameColumn.setCellValueFactory(new PropertyValueFactory<SomethingOrmLite, String>("name"));

        tableView.getColumns().addAll(idColumn, nameColumn);
        tableView.setItems(FXCollections.observableArrayList(SomethingOrmLiteList));

        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SomethingOrmLite>() {
            @Override
            public void changed(ObservableValue<? extends SomethingOrmLite> observableValue, SomethingOrmLite oldValue, SomethingOrmLite newValue) {
                if (oldValue != null) {
                    tfSelected.textProperty().unbindBidirectional(oldValue.nameProperty());
                }
                if (newValue != null) {
                    tfSelected.textProperty().bindBidirectional(newValue.nameProperty());
                }
            }
        });
    }

    public void onEdit() throws SQLException {
        SomethingOrmLite item = SomethingOrmLiteList.get(0);
        item.setName("name test bla");

        int updated = dao.update(item);
        System.out.println(updated);
    }

}