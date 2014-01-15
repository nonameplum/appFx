package appFx.datasource.mappers;

import appFx.models.Something;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SomethingMapper implements ResultSetMapper<Something> {

    @Override
    public Something map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Something something = new Something();
        //something.id = new SimpleIntegerProperty(r.getInt("id"));
        //something.name = new SimpleStringProperty(r.getString("name"));
        return something;
    }
}
