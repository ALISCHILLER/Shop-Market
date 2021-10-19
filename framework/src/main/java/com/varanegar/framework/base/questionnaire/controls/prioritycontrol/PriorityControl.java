package com.varanegar.framework.base.questionnaire.controls.prioritycontrol;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.varanegar.framework.R;
import com.varanegar.framework.base.questionnaire.controls.FormControl;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.selectionlistadapter.BaseSelectionRecyclerAdapter;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/1/2017.
 */

public class PriorityControl extends FormControl {
    public List<PriorityOption> options;
    private TextView errorTextView;
    private boolean isChanged;
    private int selectedItem = -1;
    private BaseSelectionRecyclerAdapter<PriorityOption> adapter;

    @Override
    public boolean isValueChanged() {
        return isChanged;
    }

    public PriorityControl(@NonNull Context context, @NonNull List<PriorityOption> options, @NonNull String title, @NonNull UUID uniqueId) {
        super(context, title, uniqueId);
        this.options = options;
    }

    @Override
    protected void onCreateContentView(@NonNull AppCompatActivity activity, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_control_priority, parent, true);
        sortItems();
        adapter = new BaseSelectionRecyclerAdapter<PriorityOption>(activity, options, true) {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.priority_option_layout, parent, false);
                BaseViewHolder<PriorityOption> viewHolder = new BaseViewHolder<PriorityOption>(itemView, this, parent.getContext()) {
                    @Override
                    public void bindView(final int position) {
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                adapter.notifyItemClicked(position);
                            }
                        });
                        if (position == selectedItem)
                            itemView.setBackgroundColor(HelperMethods.getColorAttr(getContext(), R.attr.colorPrimaryLight));
                        else
                            itemView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.white));
                        PriorityOption control = recyclerAdapter.get(position);
                        ((TextView) itemView.findViewById(R.id.name_text_view)).setText(control.name);
                        ((TextView) itemView.findViewById(R.id.priority_text_view)).setText(Integer.toString(position + 1));
                    }
                };
                return viewHolder;
            }
        };

        adapter.setOnItemSelectedListener(new BaseSelectionRecyclerAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, boolean selected) {
                if (selected)
                    selectedItem = position;
            }
        });
        BaseRecyclerView recyclerView = view.findViewById(R.id.options_recycler_view);
        recyclerView.setAdapter(adapter);
        errorTextView = view.findViewById(R.id.error_text_view);
        view.findViewById(R.id.up_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItem > -1) {
                    isChanged = true;
                    int s = Math.max(1, options.get(selectedItem).RowIndex - 1);
                    options.get(selectedItem).RowIndex = s;
                    if (selectedItem - 1 >= 0) {
                        options.get(selectedItem - 1).RowIndex = s + 1;
                        selectedItem = selectedItem - 1;
                    }
                    sortItems();
                }
            }
        });
        view.findViewById(R.id.down_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItem > -1) {
                    isChanged = true;
                    int s = Math.min(options.size(), options.get(selectedItem).RowIndex + 1);
                    options.get(selectedItem).RowIndex = s;
                    if (selectedItem + 1 < options.size()) {
                        options.get(selectedItem + 1).RowIndex = s - 1;
                        selectedItem = selectedItem + 1;
                    }
                    sortItems();
                }
            }
        });
    }

    private void sortItems() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
        Linq.sort(options, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                PriorityOption p1 = (PriorityOption) o1;
                PriorityOption p2 = (PriorityOption) o2;
                return p1.RowIndex == p2.RowIndex ? 0 : p1.RowIndex < p2.RowIndex ? -1 : 1;
            }
        });
    }

    @Override
    public void setError(String err) {
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(err);
    }

    @Override
    public void clearError() {
        errorTextView.setText("");
        errorTextView.setVisibility(View.GONE);
    }


    @Override
    public void deserializeValue(@Nullable String value) {
        if (value == null)
            return;
        Gson gson = new GsonBuilder().create();
        List<PriorityOption> controls =
                gson.fromJson(value, new TypeToken<List<PriorityOption>>() {
                }.getType());
        if (controls == null)
            return;
        for (final PriorityOption control :
                controls) {
            PriorityOption option = Linq.findFirst(this.options, new Linq.Criteria<PriorityOption>() {
                @Override
                public boolean run(PriorityOption item) {
                    return item.name.equals(control.name);
                }
            });
            if (option != null)
                option.RowIndex = control.RowIndex;
        }

    }

    @Nullable
    @Override
    public String serializeValue() {
        Gson gson = new GsonBuilder().create();
        String value = gson.toJson(options);
        return value;
    }
}
