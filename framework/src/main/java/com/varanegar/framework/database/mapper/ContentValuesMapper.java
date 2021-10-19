package com.varanegar.framework.database.mapper;

import android.content.ContentValues;

import com.varanegar.framework.database.model.BaseModel;

/**
 * Created by atp on 8/10/2016.
 */
public interface ContentValuesMapper<T extends BaseModel> {
    ContentValues map(T item) throws RuntimeException;

    String getTblName();
    String getUniqueIdGenerationPolicy();
}
