package com.varanegar.framework.util.recycler.selectionlistadapter;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.R;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.database.querybuilder.Query;

import java.util.List;

/**
 * Created by atp on 4/12/2017.
 */

public class SelectionRecyclerAdapter<T> extends BaseSelectionRecyclerAdapter<T> {
    int forGroundColor = -1;
    boolean forGround = false;

    public SelectionRecyclerAdapter(@NonNull AppCompatActivity activity, boolean multiSelect) {
        super(activity, multiSelect);
    }

    public SelectionRecyclerAdapter(@NonNull AppCompatActivity activity, @NonNull List<T> items, boolean multiSelect) {
        super(activity, items, multiSelect);
    }

    public SelectionRecyclerAdapter(@NonNull AppCompatActivity activity, @NonNull BaseRepository repository, @NonNull Query query, boolean multiSelect) {
        super(activity, repository, query, multiSelect);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selection_list_item, parent, false);
        return new SelectionListItemViewHolder(view, this, getActivity());
    }


    public void setForGroundColor(@ColorInt int color) {
        forGroundColor = color;
        forGround = true;
    }
}
