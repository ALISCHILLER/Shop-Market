package com.varanegar.framework.util.recycler;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.varanegar.framework.database.model.BaseModel;

/**
 * Created by atp on 12/11/2016.
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    protected BaseRecyclerAdapter<T> recyclerAdapter;
    protected View itemView;
    private Context context;

    public BaseViewHolder(View itemView, BaseRecyclerAdapter<T> recyclerAdapter , Context context) {
        super(itemView);
        this.recyclerAdapter = recyclerAdapter;
        this.itemView = itemView;
        this.context = context;
    }
    public abstract void bindView(int position);

    public View getItemView() {
        return itemView;
    }

    public Context getContext() {
        return context;
    }
}
