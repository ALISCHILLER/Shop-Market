package com.varanegar.framework.util.recycler.selectionlistadapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by atp on 4/16/2017.
 */

public abstract class BaseSelectionRecyclerAdapter<T> extends BaseRecyclerAdapter<T> {
    private boolean multiSelect;
    private ArrayList<Integer> selectedPositions = new ArrayList<>();
    private boolean selectAll;

    public BaseSelectionRecyclerAdapter(@NonNull AppCompatActivity activity, boolean multiSelect) {
        super(activity);
        this.multiSelect = multiSelect;
    }

    public BaseSelectionRecyclerAdapter(@NonNull AppCompatActivity activity, @NonNull List<T> items, boolean multiSelect) {
        super(activity, items);
        this.multiSelect = multiSelect;
    }

    public BaseSelectionRecyclerAdapter(@NonNull AppCompatActivity activity, @NonNull BaseRepository repository, @NonNull Query query, boolean multiSelect) {
        super(activity, repository, query);
        this.multiSelect = multiSelect;
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }

    /*
    Call this method, when multi selection is disabled
     */
    public int getSelectedPosition() {
        if (selectedPositions.size() == 0)
            return -1;
        else
            return selectedPositions.get(0);
    }

    /*
    Call this method when multi select is disabled.
     */
    @Nullable
    public T getSelectedItem() {
        int i = getSelectedPosition();
        if (i >= 0)
            return get(i);
        else
            return null;
    }

    @NonNull
    public List<Integer> getSelectedPositions() {
        if (selectAll) {
            List<Integer> s = new ArrayList<>();
            int count = getItemCount();
            for (int i = 0; i < count; i++)
                s.add(i);
            return s;
        }
        return selectedPositions;
    }

    @NonNull
    public List<T> getSelectedItems() {
        final ArrayList<T> items = new ArrayList<>();
        Linq.forEach(selectedPositions, new Linq.Consumer<Integer>() {
            @Override
            public void run(Integer item) {
                items.add(get(item));
            }
        });
        return items;
    }

    /**
     * Call this method whenever you need to select and notify the adapter to update itself and run selection listener;
     * * @param position
     */
    public void notifyItemClicked(int position) {
        if (isEnabled()) {
            if (isMultiSelect() && selectedPositions.contains(position)) {
                selectedPositions.remove((Object) position);
            } else if (isMultiSelect() && !selectedPositions.contains(position)) {
                selectedPositions.add(position);
            } else if (!isMultiSelect()) {
                selectedPositions = new ArrayList<>();
                selectedPositions.add(position);
            }
            notifyDataSetChanged();
            if (onItemSelectedListener != null)
                onItemSelectedListener.onItemSelected(position, selectedPositions.contains(position));
        }
    }

    public void selectAll() {
        if (multiSelect) {
            selectAll = true;
            notifyDataSetChanged();
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position, boolean selected);
    }

    private OnItemSelectedListener onItemSelectedListener;

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    /**
     * Call this method whenever you need to select items but do not need to run a click listener the database.
     * Best use case is the initialization, when you only want to have some items selected at first.
     *
     * @param item
     */
    public void select(@NonNull final T item) {
        int i = Linq.findFirstIndex(getItems(), new Linq.Criteria() {
            @Override
            public boolean run(Object it) {
                return it.equals(item);
            }
        });
        if (i >= 0) {
            if (!isMultiSelect())
                selectedPositions = new ArrayList<>();
            selectedPositions.add(i);
            notifyDataSetChanged();
        }
    }

    /**
     * Call this method whenever you need to select items but do not need to run a click listener the database.
     * Best use case is the initialization, when you only want to have some items selected at first.
     *
     * @param criteria
     */
    public void select(@NonNull Linq.Criteria<T> criteria) {
        int i = Linq.findFirstIndex(getItems(), criteria);
        if (i >= 0) {
            if (!isMultiSelect())
                selectedPositions = new ArrayList<>();
            selectedPositions.add(i);
            notifyDataSetChanged();
        }
    }

    /**
     * Call this method whenever you need to select items but do not need to run a click listener the database.
     * Best use case is the initialization, when you only want to have some items selected at first.
     *
     * @param selectedPosition
     */
    public void select(int selectedPosition) {
        if (!isMultiSelect())
            selectedPositions = new ArrayList<>();
        selectedPositions.add(selectedPosition);
        notifyDataSetChanged();
    }

    /**
     * Call this method whenever you need to select items but do not need to run a click listener the database.
     * Best use case is the initialization, when you only want to have some items selected at first.
     *
     * @param positions
     */
    public void select(@NonNull List<Integer> positions) {
        if (multiSelect)
            selectedPositions.addAll(positions);
        notifyDataSetChanged();
    }

    public void deselectAll() {
        selectAll = false;
        selectedPositions = new ArrayList<>();
        notifyDataSetChanged();
    }

}
