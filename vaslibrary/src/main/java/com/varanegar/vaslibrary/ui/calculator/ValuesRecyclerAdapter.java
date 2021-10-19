package com.varanegar.vaslibrary.ui.calculator;

import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.vaslibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by atp on 2/20/2017.
 */

class ValuesRecyclerAdapter extends RecyclerView.Adapter {
    private List<DiscreteUnit> items = new ArrayList<>();
    private Calculator calculator;

    public ValuesRecyclerAdapter(Calculator calculator) {
        this.calculator = calculator;
    }

    void deselect() {
        selectedPosition = -1;
        notifyDataSetChanged();
    }

    public DiscreteUnit select(int i) {
        if (i < items.size() && i >= 0) {
            selectedPosition = i;
            notifyDataSetChanged();
            return items.get(i);
        }
        return null;
    }

    public DiscreteUnit get(int position) {
        if (items == null)
            return null;
        if (items.size() > position && position >= 0)
            return items.get(position);
        return null;
    }

    void setItems(@NonNull List<DiscreteUnit> items) {
        this.items = items;
    }

    @NonNull
    List<DiscreteUnit> getItems() {
        return items;
    }

    @Nullable
    public BaseUnit selectDefault() {
        if (items.size() == 0)
            return null;
        int p = Linq.findFirstIndex(items, new Linq.Criteria<DiscreteUnit>() {
            @Override
            public boolean run(DiscreteUnit item) {
                return item.IsDefault;
            }
        });
        p = (p <= 0) ? 0 : p;
        if (items.get(p).value == 0) {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).value > 0) {
                    p = i;
                    break;
                }
            }
        }
        select(p);
        return items.get(p);
    }

    public interface OnItemClick {
        void onClick(int position);
    }

    OnItemClick onItemClick;

    private int selectedPosition = 0;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calculator_unit_view, parent, false);
        view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new UnitValueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UnitValueViewHolder viewHolder = (UnitValueViewHolder) holder;

        if (position == items.size()) {
            BaseUnit unit = calculator.getBulkUnit();
            viewHolder.valueTextView.setText(Double.toString(unit.value));
            viewHolder.convertFactorTextView.setText(unit.Unit);
            viewHolder.titleTextView.setText(unit.Name);
            viewHolder.operatorTextView.setVisibility(View.GONE);
            viewHolder.imageView.setVisibility(View.INVISIBLE);
            viewHolder.unitLinearLayout.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.unitLinearLayout.setVisibility(View.VISIBLE);
            viewHolder.imageView.setVisibility(View.VISIBLE);
            DiscreteUnit unit = items.get(position);
            viewHolder.titleTextView.setText(unit.Name);
            viewHolder.operatorTextView.setText("+");
            viewHolder.valueTextView.setText(Integer.toString((int) unit.value));

            if (position == items.size() - 1 && calculator.getBulkUnit() == null)
                viewHolder.operatorTextView.setVisibility(View.GONE);
            else if (position == items.size() - 1 && calculator.getBulkUnit() != null) {
                viewHolder.operatorTextView.setVisibility(View.VISIBLE);
                viewHolder.operatorTextView.setText("=");
            }
            viewHolder.convertFactorTextView.setText(HelperMethods.doubleToString(unit.ConvertFactor));
            viewHolder.imageView.setVisibility(View.VISIBLE);

        }
        if (selectedPosition == position) {
            viewHolder.iconLinearLayout.setBackgroundResource(R.drawable.calculator_right_green_corner);
            viewHolder.convertFactorTextView.setBackgroundResource(R.drawable.calculator_left_green_corner);
            viewHolder.titleTextView.setTypeface(null, Typeface.BOLD);
            viewHolder.valueTextView.setTypeface(null, Typeface.BOLD);
            viewHolder.convertFactorTextView.setTypeface(null, Typeface.BOLD);
        } else {
            viewHolder.iconLinearLayout.setBackgroundResource(R.drawable.calculator_right_gray_corner);
            viewHolder.convertFactorTextView.setBackgroundResource(R.drawable.calculator_left_gray_corner);
            viewHolder.titleTextView.setTypeface(null, Typeface.NORMAL);
            viewHolder.valueTextView.setTypeface(null, Typeface.NORMAL);
            viewHolder.convertFactorTextView.setTypeface(null, Typeface.NORMAL);
        }
    }

    @Override
    public int getItemCount() {
        return calculator.getBulkUnit() == null ? items.size() : items.size() + 1;
    }

    private class UnitValueViewHolder extends RecyclerView.ViewHolder {
        TextView convertFactorTextView;
        TextView titleTextView;
        TextView valueTextView;
        TextView operatorTextView;
        LinearLayout iconLinearLayout;
        LinearLayout unitLinearLayout;
        ImageView imageView;

        UnitValueViewHolder(final View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.title_text_view);
            valueTextView = (TextView) itemView.findViewById(R.id.value_text_view);
            operatorTextView = (TextView) itemView.findViewById(R.id.operator_text_view);
            convertFactorTextView = (TextView) itemView.findViewById(R.id.convert_factor_text_view);
            iconLinearLayout = (LinearLayout) itemView.findViewById(R.id.icon_linear_layout);
            imageView = (ImageView) itemView.findViewById(R.id.icon_image_view);
            unitLinearLayout = (LinearLayout) itemView.findViewById(R.id.unit_linear_layout);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() == items.size()) {
                        BaseUnit bulkUnit = calculator.getBulkUnit();
                        if (onItemClick != null && bulkUnit != null && !bulkUnit.Readonly) {
                            onItemClick.onClick(getAdapterPosition());
                            selectedPosition = getAdapterPosition();
                            notifyDataSetChanged();
                        }
                    } else {
                        DiscreteUnit unit = items.get(getAdapterPosition());
                        if (onItemClick != null && !unit.Readonly) {
                            onItemClick.onClick(getAdapterPosition());
                            selectedPosition = getAdapterPosition();
                            notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }
}
