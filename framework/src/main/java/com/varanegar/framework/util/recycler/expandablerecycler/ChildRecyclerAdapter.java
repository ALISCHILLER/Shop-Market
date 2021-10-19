package com.varanegar.framework.util.recycler.expandablerecycler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;

import java.util.List;

/**
 * Created by A.Torabi on 6/19/2017.
 */

public abstract class ChildRecyclerAdapter<T extends BaseModel> extends BaseRecyclerAdapter<T> {

    private ExpandableRecyclerAdapter parentAdapter;
    int position;

    public ChildRecyclerAdapter(@NonNull AppCompatActivity activity, ExpandableRecyclerAdapter parentAdapter, int position) {
        super(activity);
        this.parentAdapter = parentAdapter;
        this.position = position;
    }

    public ChildRecyclerAdapter(@NonNull AppCompatActivity activity, ExpandableRecyclerAdapter parentAdapter, int position, @NonNull List<T> items) {
        super(activity, items);
        this.parentAdapter = parentAdapter;
        this.position = position;
    }

    public ChildRecyclerAdapter(@NonNull AppCompatActivity activity, ExpandableRecyclerAdapter parentAdapter, int position, @NonNull BaseRepository<T> repository, @NonNull Query query) {
        super(activity, repository, query);
        this.parentAdapter = parentAdapter;
        this.position = position;
    }

    public int getSelectedItem() {
        if(parentAdapter.selectedItem == null)
            return -1;
        if (parentAdapter.selectedItem.adapter == position)
            return parentAdapter.selectedItem.position;
        return -1;
    }
}
