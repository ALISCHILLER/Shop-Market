package com.varanegar.vaslibrary.ui.fragment.productgroup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.productUnit.UnitOfProductModel;
import com.varanegar.vaslibrary.model.unit.UnitModel;

import java.util.List;

public class ProductUnitAdapter extends RecyclerView.Adapter<ProductUnitAdapter.ViewHolder>{

    private List<UnitOfProductModel> units;
    private OnItemQtyChangedHandler onItemQtyChangedHandler;
    private int productPosition;

    public ProductUnitAdapter(List<UnitOfProductModel> units, OnItemQtyChangedHandler onItemQtyChangedHandler, int productPosition) {
        this.units = units;
        this.onItemQtyChangedHandler = onItemQtyChangedHandler;
        this.productPosition = productPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_counting_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(units.get(position), productPosition);
    }

    @Override
    public int getItemCount() {
        return units.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView unit_text_view_large;
        private ImageButton plusImageViewLarge;
        private ImageButton minusImageViewLarge;
        private TextView qtyTextViewLarge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            unit_text_view_large = itemView.findViewById(R.id.unit_text_view_large);
            qtyTextViewLarge = itemView.findViewById(R.id.qty_text_view_large);
            qtyTextViewLarge.setText("0");
            plusImageViewLarge = itemView.findViewById(R.id.plus_image_view_large);
            minusImageViewLarge = itemView.findViewById(R.id.minus_image_view_large);
        }

        public void bind(UnitOfProductModel item, int position) {
            unit_text_view_large.setText(item.UnitName);

            plusImageViewLarge.setOnClickListener(v -> {
                item.Count++;
                qtyTextViewLarge.setText(HelperMethods.doubleToString(item.Count));
                onItemQtyChangedHandler.plusQty(position, productOrderViewModel.UniqueId, unit, otherUnit);
            });

            minusImageViewLarge.setOnClickListener(v -> {
                if (item.Count > 0){
                    item.Count--;
                    qtyTextViewLarge.setText(item.Count);
                }
            });


        }
    }
}
