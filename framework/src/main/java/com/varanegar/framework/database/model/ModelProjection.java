package com.varanegar.framework.database.model;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by atp on 12/27/2016.
 */
public abstract class ModelProjection<T extends BaseModel> {
    protected ModelProjection(String name) {
        this.name = name;
    }

    String name;

    public String getName() {
        return name;
    }

    public String getSimpleName() {
        if (name.contains("."))
            return name.split("\\.")[1];
        else
            return name;
    }

    public static ModelProjection All = new ModelProjection("*") {
        @Override
        public String getName() {
            return super.getName();
        }
    };


    public HashMap<String, ModelProjection<T>> getColumns() {

        Field[] fields = getClass().getDeclaredFields();

        HashMap<String, ModelProjection<T>> results = new HashMap<>();
        for (Field f : fields) {
            Class clazz = f.getType();
            String name1 = clazz.getSimpleName();
            String name2 = getClass().getSimpleName();
            if (name1.equals(name2)) {
                String fieldName = f.getName();
                if (!fieldName.equals("All") && !fieldName.endsWith("Tbl") && !fieldName.endsWith("All"))
                    try {
                        ModelProjection<T> projection = (ModelProjection<T>) f.get(this);
                        results.put(projection.getSimpleName(), projection);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
            }
        }
        return results;
    }
}

