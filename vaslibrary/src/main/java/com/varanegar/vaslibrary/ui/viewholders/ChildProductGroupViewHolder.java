package com.varanegar.vaslibrary.ui.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.expandablerecycler.ChildRecyclerAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.productGroup.ProductGroupModel;

/**
 * Created by s.foroughi on 14/02/2017.
 */

public class ChildProductGroupViewHolder extends BaseViewHolder<ProductGroupModel> {
    public TextView productNameTextView;
    public TextView productNameTextViewSelected;

    public ChildProductGroupViewHolder(View itemView, ChildRecyclerAdapter<ProductGroupModel> recyclerAdapter, Context context) {
        super(itemView, recyclerAdapter, context);
        productNameTextView = (TextView) itemView.findViewById(R.id.product_name_text_view);
        productNameTextViewSelected = (TextView) itemView.findViewById(R.id.product_name_text_view_selected);
    }

    @Override
    public void bindView(final int position) {
        itemView.setBackgroundColor(HelperMethods.getColor(getContext(),R.color.grey_light_light_light));
        int selectedItem = ((ChildRecyclerAdapter) recyclerAdapter).getSelectedItem();
        if (selectedItem == position) {
            productNameTextViewSelected.setVisibility(View.VISIBLE);
            productNameTextView.setVisibility(View.INVISIBLE);
        } else {
            productNameTextViewSelected.setVisibility(View.INVISIBLE);
            productNameTextView.setVisibility(View.VISIBLE);
        }

        productNameTextView.setText(recyclerAdapter.get(position).ProductGroupName);
        productNameTextViewSelected.setText(recyclerAdapter.get(position).ProductGroupName);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerAdapter.runItemClickListener(position);
            }
        });
    }
}
