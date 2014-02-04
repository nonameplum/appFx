package appFx.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

@DatabaseTable(tableName = "something")
public class SomethingOrmLite {

    @DatabaseField(id = true, persisterClass = SimpleIntegerPropertyPresister.class)
    private SimpleIntegerProperty id;
    @DatabaseField(persisterClass = SimpleStringPropertyPresister.class)
    private SimpleStringProperty name;

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

}
