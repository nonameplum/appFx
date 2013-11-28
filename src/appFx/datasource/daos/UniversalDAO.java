package appFx.datasource.daos;

import appFx.datasource.mappers.MapMapper;
import appFx.datasource.mappers.MetaDataMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Map;

@UseStringTemplate3StatementLocator
public interface UniversalDAO {
    @SqlQuery("select * from <table> order by ID")
    @Mapper(MapMapper.class)
    public List<Map<String, Object>> sqlQuery(@Define("table") String table);

    @SqlUpdate("update <table> set <property> = :value where id = :id")
    int update(@Define("table") String table, @Define("property") String property, @Bind("value") String value, @Bind("id") int id);

    @SqlQuery("select * from <table> limit 1")
    @Mapper(MetaDataMapper.class)
    public ResultSetMetaData getMetaData(@Define("table") String table);

    public void close();
}

