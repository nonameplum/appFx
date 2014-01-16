package appFx.models;

import com.avaje.ebean.config.ScalarTypeConverter;
import javafx.beans.property.SimpleStringProperty;

public class SimpleStringPropertyConverter implements ScalarTypeConverter<SimpleStringProperty, String> {

    @Override
    public SimpleStringProperty getNullValue() {
        return null;
    }

    @Override
    public SimpleStringProperty wrapValue(String s) {
        return new SimpleStringProperty(s);
    }

    @Override
    public String unwrapValue(SimpleStringProperty simpleStringProperty) {
        return simpleStringProperty.getValue();
    }

}
