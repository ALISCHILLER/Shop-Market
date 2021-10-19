package com.varanegar.vaslibrary.catalogue.groupcatalog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.product.ProductModel;

/**
 * Created by A.Torabi on 8/5/2017.
 */

public class ProductGroupCatalogProductViewHolder extends BaseViewHolder<ProductModel> {
    private final TextView productCodeTextView;

    public ProductGroupCatalogProductViewHolder(View itemView, BaseRecyclerAdapter<ProductModel> recyclerAdapter, Context context) {
        super(itemView, recyclerAdapter, context);
        productCodeTextView = (TextView) itemView.findViewById(R.id.product_code_text_view);
    }

    @Override
    public void bindView(int position) {
        final ProductModel productOrderViewModel = recyclerAdapter.get(position);
        productCodeTextView.setText(productOrderViewModel.ProductCode);
    }

}
