package appFx.models;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;
import javafx.beans.property.SimpleIntegerProperty;

import java.sql.SQLException;

public class SimpleIntegerPropertyPresister extends BaseDataType {

    private static final SimpleIntegerPropertyPresister singleTon = new SimpleIntegerPropertyPresister();

    private SimpleIntegerPropertyPresister() {
        super(SqlType.INTEGER, new Class<?>[]{Integer.class});
    }

    @Override
    public Object parseDefaultString(FieldType fieldType, String defaultStr) {
        return new SimpleIntegerProperty(Integer.parseInt(defaultStr));
    }

    public static SimpleIntegerPropertyPresister getSingleton() {
        return singleTon;
    }

    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        int resultsInt = results.getInt(columnPos);
        return resultsInt;
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        return new SimpleIntegerProperty((Integer) sqlArg);
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        SimpleIntegerProperty integerProperty = (SimpleIntegerProperty) javaObject;
        return integerProperty.getValue();
    }

}
