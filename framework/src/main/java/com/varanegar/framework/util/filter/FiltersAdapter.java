package com.varanegar.framework.util.filter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.R;
import com.varanegar.framework.util.Linq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 6/27/2017.
 */
public class FiltersAdapter extends RecyclerView.Adapter {

    private final Context context;
    private final UUID uniqueId;
    private int defaultFilter = -1;

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
        setup();
    }

    private List<Filter> filters = new ArrayList<>();

    public List<Filter> getSelectedFilters() {
        return Linq.findAll(filters, new Linq.Criteria<Filter>() {
            @Override
            public boolean run(Filter item) {
                return item.selected;
            }
        });
    }

    public FiltersAdapter(@Nullable Context context, UUID uniqueId, int defaultOption, List<Filter> filters) {
        this.context = context;
        this.uniqueId = uniqueId;
        this.defaultFilter = defaultOption;
        this.filters = filters;
        setup();
    }

    public FiltersAdapter(@Nullable Context context, UUID uniqueId, int defaultOption, Filter... filters) {
        this.context = context;
        this.uniqueId = uniqueId;
        this.defaultFilter = defaultOption;
        this.filters = new ArrayList<>();
        Collections.addAll(this.filters, filters);
        setup();
    }

    private void setup() {
        if (context == null)
            return;
        for (Filter filter :
                filters) {
            filter.selected = false;
        }
        Set<String> selectedFiltersSet = context.getSharedPreferences("FILTERS_ADAPTER", Context.MODE_PRIVATE).getStringSet(uniqueId.toString(), null);
        if (selectedFiltersSet == null) {
            if (defaultFilter >= 0) {
                filters.get(defaultFilter).selected = true;
            }
            return;
        }
        String[] selectedFilters = selectedFiltersSet.toArray(new String[0]);
        if (selectedFilters.length > 0) {
            for (final String f :
                    selectedFilters) {
                Filter selectedFilter = Linq.findFirst(filters, new Linq.Criteria<Filter>() {
                    @Override
                    public boolean run(Filter item) {
                        return item.name.equals(f);
                    }
                });
                if (selectedFilter != null)
                    selectedFilter.selected = true;
            }
        } else if (defaultFilter >= 0) {
            filters.get(defaultFilter).selected = true;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.option_item_view, parent, false);
        return new OptionViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OptionViewHolder optionViewHolder = (OptionViewHolder) holder;
        optionViewHolder.bind(filters.get(position));
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void runOnItemClickListener(int position) {
        if (onItemClickListener != null) {
            onItemClickListener.onClick(position);
            saveState();
        }
    }

    private void saveState() {
        if (context == null)
            return;
        List<Filter> selectedFilters = Linq.findAll(filters, new Linq.Criteria<Filter>() {
            @Override
            public boolean run(Filter item) {
                return item.selected;
            }
        });
        if (selectedFilters.size() > 0) {
            SharedPreferences.Editor editor = context.getSharedPreferences("FILTERS_ADAPTER", Context.MODE_PRIVATE).edit();
            List<String> filters = Linq.map(selectedFilters, new Linq.Map<Filter, String>() {
                @Override
                public String run(Filter item) {
                    return item.name;
                }
            });
            Set<String> set = new HashSet<>();
            set.addAll(filters);
            editor.putStringSet(uniqueId.toString(), set).apply();
        }
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }
}
