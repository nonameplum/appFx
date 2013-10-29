package sample;

import javafx.scene.control.cell.MapValueFactory;

public class MapValueFactoryKeys<T> extends MapValueFactory<T> {
    private Object key;

    public MapValueFactoryKeys(Object o) {
        super(o);
        key = o;
    }

    public Object getKey() {
        return key;
    }
}
