package appFx.models;

import com.avaje.ebean.config.ScalarTypeConverter;
import com.avaje.ebean.text.json.JsonValueAdapter;
import com.avaje.ebeaninternal.server.text.json.WriteJsonBuffer;
import com.avaje.ebeaninternal.server.type.*;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.SQLException;

public class SimpleIntegerPropertyConverter implements ScalarTypeConverter<SimpleIntegerProperty, Integer> {

    @Override
    public SimpleIntegerProperty getNullValue() {
        return null;
    }

    @Override
    public SimpleIntegerProperty wrapValue(Integer integer) {
        return new SimpleIntegerProperty(integer);
    }

    @Override
    public Integer unwrapValue(SimpleIntegerProperty simpleIntegerProperty) {
        return simpleIntegerProperty.getValue();
    }
}
