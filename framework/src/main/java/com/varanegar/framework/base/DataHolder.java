package com.varanegar.framework.base;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;

/**
 * Created by atp on 2/25/2017.
 */

class DataHolder {
    private Map<String, DataItem> map = new HashMap<>();

    <T> void save(@NonNull String id, @NonNull T object) {
        if (map.containsKey(id)) {
            DataItem item = map.get(id);
            if (item.getData() != null) {
                if (item.getData().getClass() != object.getClass())
                    throw new InputMismatchException("Type mismatch. You already have saved an object with different type with the same id.");
            }
        }
        map.put(id, new DataItem<T>(object));
    }

    @NonNull
    <T> T retrieve(@NonNull String id, boolean remove) {
        if (!map.containsKey(id))
            throw new NullPointerException("Id does not exist in the Data Holder");
        DataItem<T> item;
        try {
            item = map.get(id);
        } catch (Exception ex) {
            throw new ClassCastException("Type of the object saved in the data holder is different.");
        }
        if (remove)
            map.remove(id);
        return item.getData();
    }

    public void remove(@NonNull String id) {
        if (map.containsKey(id))
            map.remove(id);
    }

    private class DataItem<T> {
        private T data;

        DataItem(T object) {
            data = object;
        }

        T getData() {
            return data;
        }
    }
}