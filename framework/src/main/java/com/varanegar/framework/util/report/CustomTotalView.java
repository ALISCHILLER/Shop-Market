package com.varanegar.framework.util.report;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.database.model.BaseModel;

import java.util.List;

/**
 * Created by ٍ E.Hashemzadeh on 8/15/2017.
 */

public abstract class CustomTotalView <T extends BaseModel> {
    public abstract void onBind(View view, List<T> entities);

    public abstract View onCreateView(LayoutInflater inflater, ViewGroup parent);
}