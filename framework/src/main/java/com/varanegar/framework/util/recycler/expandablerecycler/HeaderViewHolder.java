package com.varanegar.framework.util.recycler.expandablerecycler;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.varanegar.framework.R;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;

/**
 * Created by atp on 1/22/2017.
 */

public class HeaderViewHolder extends RecyclerView.ViewHolder {
    ImageView expandImageView;
    BaseViewHolder customHeaderViewHolder;
    BaseRecyclerView itemsRecyclerView;
    LinearLayout headerLinearLayout;

    public <T1 extends BaseModel> HeaderViewHolder(View headerView, BaseViewHolder<T1> customHeaderViewHolder) {
        super(headerView);
        expandImageView = (ImageView) itemView.findViewById(R.id.expand_image_view);
        itemsRecyclerView = (BaseRecyclerView) itemView.findViewById(R.id.items_recycler_view);
        headerLinearLayout = (LinearLayout) itemView.findViewById(R.id.header_linear_layout);
        this.customHeaderViewHolder = customHeaderViewHolder;
    }
}
