package com.varanegar.framework.util.fragment.extendedlist;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.util.filter.Filter;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseViewHolder;

import java.util.List;

/**
 * Created by atp on 1/14/2017.
 */

public abstract class DbListFragment<DataModel extends BaseModel, Repository extends BaseRepository<DataModel>> extends ExtendedListFragment<DataModel> {
    protected abstract BaseViewHolder<DataModel> createListItemViewHolder(ViewGroup parent, int viewType);


    @NonNull
    protected abstract Repository getRepository();

    @NonNull
    protected abstract Query createFiltersQuery(@Nullable String text, @Nullable List<Filter> filters, @Nullable Object spinnerFilterItem);

    @Override
    protected BaseRecyclerAdapter<DataModel> createRecyclerAdapter() {
        return new BaseRecyclerAdapter<DataModel>(getVaranegarActvity(), getRepository(), createFiltersQuery(null, getFilters(), null)) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return createListItemViewHolder(parent, viewType);
            }
        };
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void runFilter(String text, List<Filter> filters, Object spinnerFilterItem) {
        getAdapter().refresh(createFiltersQuery(text, filters, spinnerFilterItem));
    }
}
