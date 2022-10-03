package com.varanegar.vaslibrary.ui.fragment.productgroup;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.model.productUnit.UnitOfProductModel;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderViewModel;
import com.varanegar.vaslibrary.model.unit.UnitModel;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/*
* Creted by mehrdad latifi
* */
public class ProductUnitAdapter extends RecyclerView.Adapter<ProductUnitAdapter.ViewHolder>{

    private List<UnitOfProductModel> units;
    private OnItemQtyChangedHandler onItemQtyChangedHandler;
    private int productPosition;
    private UUID productId;
    private HashMap<UUID, ProductUnitViewManager.ProductUnits> productUnitHashMap;
    private long lastClick = 0;
    private final int delay = 200;
    private final Handler handler;
    private ProductOrderViewModel productOrderViewModel;

    public ProductUnitAdapter(List<UnitOfProductModel> units, OnItemQtyChangedHandler onItemQtyChangedHandler, int productPosition, UUID productId, HashMap<UUID, ProductUnitViewManager.ProductUnits> productUnitHashMap, ProductOrderViewModel productOrderViewModel) {
        this.units = units;
        this.onItemQtyChangedHandler = onItemQtyChangedHandler;
        this.productPosition = productPosition;
        this.productId = productId;
        this.productUnitHashMap = productUnitHashMap;
        this.handler = new Handler();
        this.productOrderViewModel = productOrderViewModel;
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
                ProductUnitViewManager.ProductUnits pu = productUnitHashMap.get(productId);
                qtyTextViewLarge.setText(HelperMethods.doubleToString(item.Count));
                DiscreteUnit unit = new DiscreteUnit();
                unit.ProductUnitId = item.productUnitId;
                unit.Name = item.UnitName;
                unit.ConvertFactor = pu.LargeUnit.ConvertFactor;
                unit.value = item.Count;
//                onItemQtyChangedHandler.plusQty(position, productId, unit, null);
                lastClick = new Date().getTime();
                handler.postDelayed(() -> {
                    if (new Date().getTime() - lastClick < delay)
                        return;
                    if (onItemQtyChangedHandler != null)
                        onItemQtyChangedHandler.start(productOrderViewModel);
                }, delay + 50);
            });

            minusImageViewLarge.setOnClickListener(v -> {
                if (item.Count > 0){
                    item.Count--;
                    qtyTextViewLarge.setText(HelperMethods.doubleToString(item.Count));
                }
            });


        }
    }
}
