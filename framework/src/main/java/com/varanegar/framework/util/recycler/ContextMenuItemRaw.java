package com.varanegar.framework.util.recycler;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

/**
 * Created by A.Torabi on 7/25/2017.
 */
public abstract class ContextMenuItemRaw {
    public abstract boolean isAvailable(int position);

    @Nullable
    protected abstract View onCreateView(int position, View convertView, ViewGroup parent);

    protected void setContextItemDialog(ContextItemDialog contextItemDialog) {
        this.contextItemDialog = contextItemDialog;
    }

    private ContextItemDialog contextItemDialog;

    protected ContextItemDialog getContextItemDialog() {
        return contextItemDialog;
    }
}
