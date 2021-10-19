package com.varanegar.framework.util.report;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.database.model.BaseModel;

/**
 * Created by A.Torabi on 8/15/2017.
 */
public abstract class CustomViewHolder<T extends BaseModel> {
    public abstract void onBind(View view, T entity);

    public abstract View onCreateView(LayoutInflater inflater, ViewGroup parent);
}
