package com.varanegar.framework.database.mapper;

import android.database.Cursor;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by atp on 8/10/2016.
 */
public abstract class CursorMapper<T extends BaseModel> {

    private HashMap<String, Boolean> fields = new HashMap<>();

    protected boolean isNullable(T model, String fieldName) {
        try {

            if (fields.containsKey(fieldName))
                return fields.get(fieldName);
            else {
                Field field = model.getClass().getField(fieldName);
                Annotation notNull = field.getAnnotation(NotNull.class);
                fields.put(fieldName, notNull == null);
                return notNull == null;
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return true;
        }
    }

    public abstract T map(Cursor cursor) throws RuntimeException;
}
