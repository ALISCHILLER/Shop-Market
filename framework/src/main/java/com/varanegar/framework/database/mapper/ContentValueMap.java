package com.varanegar.framework.database.mapper;

import android.content.ContentValues;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Created by atp on 8/10/2016.
 */
public class ContentValueMap<T2, T extends BaseModel> {
    private String tblName;
    private ContentValues cv = new ContentValues();

    public ContentValueMap(Class<T> clazz) {
        String simpleModelName = clazz.getSimpleName();
        tblName = simpleModelName.replace("Model", "");
        Table tblAnnotation = clazz.getAnnotation(Table.class);
        if (tblAnnotation != null) {
            if (!tblAnnotation.name().isEmpty()) {
                tblName = tblAnnotation.name();
            }
        }
    }

    public ContentValueMap<T2, T> map(UUID field, ModelProjection<T> projection) {
        if (field != null)
            cv.put(projection.getSimpleName(), field.toString());
        else
            cv.putNull(projection.getSimpleName());
        return this;
    }

    public ContentValueMap<T2, T> map(Date field, ModelProjection<T> projection) {
        if (field != null)
            cv.put(projection.getSimpleName(), field.getTime());
        else
            cv.putNull(projection.getSimpleName());
        return this;
    }

    public ContentValueMap<T2, T> map(BigDecimal field, ModelProjection<T> projection) {
        if (field != null)
            cv.put(projection.getSimpleName(), field.doubleValue());
        else
            cv.putNull(projection.getSimpleName());
        return this;
    }

    public ContentValueMap<T2, T> map(Currency field, ModelProjection<T> projection) {
        if (field != null)
            cv.put(projection.getSimpleName(), field.doubleValue());
        else
            cv.putNull(projection.getSimpleName());
        return this;
    }

    public ContentValueMap<T2, T> map(Boolean field, ModelProjection<T> projection) {
        if (field != null)
            cv.put(projection.getSimpleName(), field);
        else
            cv.putNull(projection.getSimpleName());
        return this;
    }

    public ContentValueMap<T2, T> map(String field, ModelProjection<T> projection) {
        if (field != null)
            cv.put(projection.getSimpleName(), field);
        else
            cv.putNull(projection.getSimpleName());
        return this;
    }

    public ContentValueMap<T2, T> map(Integer field, ModelProjection<T> projection) {
        if (field != null)
            cv.put(projection.getSimpleName(), field);
        else
            cv.putNull(projection.getSimpleName());
        return this;
    }

    public ContentValueMap<T2, T> map(Double field, ModelProjection<T> projection) {
        if (field != null)
            cv.put(projection.getSimpleName(), field);
        else
            cv.putNull(projection.getSimpleName());
        return this;
    }

    public String getTblName() {
        return tblName;
    }

    public ContentValues getContentValues() {
        return cv;
    }


}
