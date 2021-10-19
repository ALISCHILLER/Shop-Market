package com.varanegar.framework.util.recycler.selectionlistadapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.varanegar.framework.R;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseViewHolder;

/**
 * Created by atp on 4/12/2017.
 */

class SelectionListItemViewHolder extends BaseViewHolder {
    ImageView iconImageView;
    TextView nameTextView;

    public SelectionListItemViewHolder(View itemView, BaseRecyclerAdapter recyclerAdapter, Context context) {
        super(itemView, recyclerAdapter, context);
        nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
        iconImageView = (ImageView) itemView.findViewById(R.id.icon_image_view);
    }

    @Override
    public void bindView(final int position) {
        boolean selected = false;
        final SelectionRecyclerAdapter adapter = (SelectionRecyclerAdapter) recyclerAdapter;
        if (Linq.exists(adapter.getSelectedPositions(), new Linq.Criteria<Integer>() {
            @Override
            public boolean run(Integer item) {
                return item.equals(getAdapterPosition());
            }
        })) {
            selected = true;
        }
        if (adapter.isMultiSelect() && selected) {
            iconImageView.setImageResource(R.drawable.ic_check_box_black_24dp);
        } else if (adapter.isMultiSelect() && !selected) {
            iconImageView.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        } else if (!adapter.isMultiSelect() && selected) {
            iconImageView.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
        } else if (!adapter.isMultiSelect() && !selected) {
            iconImageView.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
        }
        nameTextView.setText(adapter.get(position).toString());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.notifyItemClicked(position);
                adapter.runItemClickListener(position);
            }
        });
        if (adapter.forGround) {
            nameTextView.setTextColor(adapter.forGroundColor);
            iconImageView.setColorFilter(adapter.forGroundColor);
        }
    }
}
