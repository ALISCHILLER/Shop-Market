package com.varanegar.framework.util.filter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.varanegar.framework.R;
import com.varanegar.framework.util.Linq;

/**
 * Created by A.Jafarzadeh on 6/27/2017.
 */
public class OptionViewHolder extends RecyclerView.ViewHolder {
    private TextView optionValueTextView;
    private TextView selectedOptionValueTextView;
    private FiltersAdapter adapter;

    public OptionViewHolder(View itemView, FiltersAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        optionValueTextView = (TextView) itemView.findViewById(R.id.option_value_text_view);
        selectedOptionValueTextView = (TextView) itemView.findViewById(R.id.selected_option_value_text_view);
    }

    public void bind(final Filter option) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (option.selected && !Linq.exists(adapter.getFilters(), new Linq.Criteria<Filter>() {
                    @Override
                    public boolean run(Filter item) {
                        return !item.name.equals(option.name) && item.selected;
                    }
                })) {
                    return;
                }
                option.selected = !option.selected;
                if (option.khafan && option.selected)
                    Linq.forEach(adapter.getFilters(), new Linq.Consumer<Filter>() {
                        @Override
                        public void run(Filter item) {
                            if (!item.name.equals(option.name))
                                item.selected = false;
                        }
                    });
                else if (!option.khafan && option.selected) {
                    Linq.forEach(adapter.getFilters(), new Linq.Consumer<Filter>() {
                        @Override
                        public void run(Filter item) {
                            if (item.khafan)
                                item.selected = false;
                        }
                    });
                }
                adapter.notifyDataSetChanged();
                adapter.runOnItemClickListener(getAdapterPosition());
            }
        });
        optionValueTextView.setText(option.name);
        selectedOptionValueTextView.setText(option.name);
        if (option.selected) {
            optionValueTextView.setVisibility(View.INVISIBLE);
            selectedOptionValueTextView.setVisibility(View.VISIBLE);
        } else {
            optionValueTextView.setVisibility(View.VISIBLE);
            selectedOptionValueTextView.setVisibility(View.INVISIBLE);
        }
    }
}
