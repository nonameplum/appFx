package appFx.datasource.mappers;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class MetaDataMapper implements ResultSetMapper<ResultSetMetaData> {

    @Override
    public ResultSetMetaData map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return r.getMetaData();
    }
}