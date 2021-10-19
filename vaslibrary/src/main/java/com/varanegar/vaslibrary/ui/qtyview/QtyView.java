package com.varanegar.vaslibrary.ui.qtyview;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;

import java.util.List;

/**
 * Created by A.Torabi on 7/25/2017.
 */

public class QtyView {

    private Color color;
    private UnitsAdapter adapter;


    public enum Color {
        Green,
        Red,
        Orange
    }

    public void setColor(Color color) {
        this.color = color;
    }

    private RecyclerView unitsRecyclerView;
    private List<BaseUnit> units;

    public QtyView build(ViewGroup parent, @NonNull List<BaseUnit> productUnits) {
        this.units = productUnits;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_qty_view, parent);
        unitsRecyclerView = (RecyclerView) view.findViewById(R.id.units_recycler_view);
        adapter = new UnitsAdapter();
        unitsRecyclerView.setAdapter(adapter);
        unitsRecyclerView.setLayoutManager(new LinearLayoutManager(parent.getContext(), LinearLayoutManager.HORIZONTAL, false));

        return this;
    }

    class UnitsAdapter extends RecyclerView.Adapter {


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_unit_item, parent, false);
                return new ProductUnitViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ProductUnitViewHolder) holder).bindView(position);
        }

        @Override
        public int getItemCount() {
            return units.size();
        }
    }

    class ProductUnitViewHolder extends RecyclerView.ViewHolder {

        private final TextView qtyTextView;
        private final TextView unitTextView;

        public ProductUnitViewHolder(View itemView) {
            super(itemView);
            qtyTextView = (TextView) itemView.findViewById(R.id.qty_text_view);
            unitTextView = (TextView) itemView.findViewById(R.id.unit_text_view);
        }

        public void bindView(final int position) {
            final BaseUnit unit = units.get(position);
            qtyTextView.setText(HelperMethods.doubleToString(unit.value));
            unitTextView.setText(unit.Name);
            if (color == Color.Green) {
                qtyTextView.setBackgroundResource(R.drawable.calculator_left_green_corner);
                unitTextView.setBackgroundResource(R.drawable.calculator_right_green_corner);
                qtyTextView.setTextColor(HelperMethods.getColor(qtyTextView.getContext(), R.color.green));
            } else if (color == Color.Orange) {
                qtyTextView.setBackgroundResource(R.drawable.calculator_left_orange_corner);
                unitTextView.setBackgroundResource(R.drawable.calculator_right_orange_corner);
                qtyTextView.setTextColor(HelperMethods.getColor(qtyTextView.getContext(), R.color.orange));
            } else if (color == Color.Red) {
                qtyTextView.setBackgroundResource(R.drawable.calculator_left_red_corner);
                unitTextView.setBackgroundResource(R.drawable.calculator_right_red_corner);
                qtyTextView.setTextColor(HelperMethods.getColor(qtyTextView.getContext(), R.color.red));
            }
        }
    }
}
