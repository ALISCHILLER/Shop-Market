package com.varanegar.vaslibrary.ui.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.expandablerecycler.ExpandableRecyclerAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.productGroup.ProductGroupModel;

/**
 * Created by s.foroughi on 14/02/2017.
 */

public class ProductGroupViewHolder extends BaseViewHolder<ProductGroupModel> {
    public TextView ProductNameTextView;

    public ProductGroupViewHolder(View itemView, BaseRecyclerAdapter<ProductGroupModel> recyclerAdapter, Context context) {
        super(itemView, recyclerAdapter, context);
        ProductNameTextView = (TextView) itemView.findViewById(R.id.product_name_text_view);
    }

    @Override
    public void bindView(final int position) {
        ProductNameTextView.setText(recyclerAdapter.get(position).ProductGroupName);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerAdapter.runItemClickListener(position);
            }
        });
        if (recyclerAdapter instanceof ExpandableRecyclerAdapter) {
            ExpandableRecyclerAdapter adapter = (ExpandableRecyclerAdapter) recyclerAdapter;
            int selectedPosition = adapter.getSelectedPosition();
            if (selectedPosition == position)
                itemView.setBackgroundColor(HelperMethods.getColorAttr(getContext(), R.attr.colorPrimaryLight));
            else
                itemView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.white));

        }
    }
}
