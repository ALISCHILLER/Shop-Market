package com.varanegar.vaslibrary.ui.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.image.ImageManager;
import com.varanegar.vaslibrary.manager.image.ImageType;
import com.varanegar.vaslibrary.model.catalog.CatalogModel;

import java.io.File;

/**
 * Created by A.Torabi on 6/21/2017.
 */

public class CatalogViewHolder extends BaseViewHolder<CatalogModel> {
    private TextView catalogTextView;
    private ImageView catalogImageView;

    public CatalogViewHolder(View itemView, BaseRecyclerAdapter<CatalogModel> recyclerAdapter, Context context) {
        super(itemView, recyclerAdapter, context);
        catalogImageView = (ImageView) itemView.findViewById(R.id.catalog_image_view);
        catalogTextView = (TextView) itemView.findViewById(R.id.catalog_name_text_view);
    }

    @Override
    public void bindView(final int position) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerAdapter.runItemClickListener(position);
            }
        });
        CatalogModel catalogModel = recyclerAdapter.get(position);
        catalogTextView.setText(catalogModel.CatalogName);
        String path = new ImageManager(getContext()).getImagePath(catalogModel.CatalogId, ImageType.CatalogSmall);
        if (path != null) {
            catalogImageView.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(new File(path)).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.product_no_image).into(catalogImageView);
        }
        else
            catalogImageView.setVisibility(View.INVISIBLE);
    }
}
