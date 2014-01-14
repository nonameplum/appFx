package appFx.datasource.mappers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapMapper implements ResultSetMapper<Map<String, SimpleObjectProperty<Object>>> {

    @Override
    public ObservableMap<String, SimpleObjectProperty<Object>> map(int index, ResultSet r, StatementContext ctx) throws SQLException {
//        Map<String, Object> obj = new LinkedHashMap();
        ObservableMap<String, SimpleObjectProperty<Object>> obj = FXCollections.observableMap(new LinkedHashMap<String, SimpleObjectProperty<Object>>());
        for (int i = 1; i <= r.getMetaData().getColumnCount(); i++) {
            Object rsObj = r.getObject(i);
            obj.put(r.getMetaData().getColumnLabel(i), new SimpleObjectProperty<Object>(rsObj));
        }
        return obj;
    }
}
