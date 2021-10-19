package com.varanegar.vaslibrary.base;

import android.content.Context;
import androidx.annotation.StringRes;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.database.model.ModelProjection;

/**
 * Created by A.Torabi on 5/15/2018.
 */

public class OrderOption<T extends BaseModel>{
    private final Context context;

    public OrderOption(Context context){
        this.context = context;
    }

    public ModelProjection<T> getProjection() {
        return projection;
    }

    private ModelProjection<T> projection;
    private String name;

    @Override
    public String toString() {
        return name;
    }
    public OrderOption<T> setProjection(ModelProjection<T> projection){
        this.projection = projection;
        return this;
    }
    public OrderOption<T> setName(@StringRes int name){
        this.name = context.getString(name);
        return this;
    }
}
