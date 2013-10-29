package sample;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapMapper implements ResultSetMapper<Map<String, Object>> {

    @Override
    public Map<String, Object> map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Map<String, Object> obj = new LinkedHashMap();
        for (int i = 1; i <= r.getMetaData().getColumnCount(); i++) {
            Object rsObj = r.getObject(i);
            obj.put(r.getMetaData().getColumnLabel(i), rsObj);
        }
        return obj;
    }
}
