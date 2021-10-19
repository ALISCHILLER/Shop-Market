package com.varanegar.framework.util.recycler;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.database.model.BaseModel;

/**
 * Created by atp on 5/6/2017.
 */

public abstract class ItemContextView<T extends BaseModel> {
    private ContextItemDialog dialog;

    public void setDialog(ContextItemDialog contextItemDialog) {
        this.dialog = contextItemDialog;
    }

    public void onDestroy() {

    }

    public void onPause() {

    }

    protected boolean isResumed() {
        return true;
    }

    public interface OnCloseListener {
        void run();
    }

    public OnCloseListener onClose;

    public int getPosition() {
        return position;
    }

    private int position;

    public Context getContext() {
        return context;
    }

    private Context context;
    protected BaseRecyclerAdapter<T> adapter;

    public ItemContextView(BaseRecyclerAdapter<T> adapter, Context context) {
        this.adapter = adapter;
        this.context = context;
    }

    public abstract View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, int position);

    public void onStart() {

    }

    public View getView(ViewGroup viewGroup, int position) {
        this.position = position;
        return onCreateView(LayoutInflater.from(context), viewGroup, position);
    }

    protected void closeContextView() {
        if (onClose != null)
            onClose.run();
    }

    protected FragmentManager getChildFragmentManager() {
        if (dialog != null)
            return dialog.getChildFragmentManager();
        else
            return null;
    }
}
