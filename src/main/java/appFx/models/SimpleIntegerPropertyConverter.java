package appFx.models;

import com.avaje.ebean.config.ScalarTypeConverter;
import javafx.beans.property.SimpleIntegerProperty;

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
