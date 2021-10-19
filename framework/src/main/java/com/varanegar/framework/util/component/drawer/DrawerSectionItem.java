package com.varanegar.framework.util.component.drawer;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.varanegar.framework.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A.Torabi on 8/30/2017.
 */

public class DrawerSectionItem extends BaseDrawerItem {
    private DrawerAdapter parent;
    private ImageView expandImageView;
    private List<BaseDrawerItem> items;
    private TextView titleTextview;
    private LinearLayout itemsLinearlayout;
    private ImageView iconImageView;

    public DrawerSectionItem(Context context, DrawerAdapter parent, String title) {
        super(context);
        titleTextview.setText(title);
        this.parent = parent;
    }

    public DrawerSectionItem(Context context, DrawerAdapter parent, String title , @DrawableRes int icon) {
        super(context);
        titleTextview.setText(title);
        this.parent = parent;
        iconImageView.setVisibility(VISIBLE);
        iconImageView.setImageResource(icon);
    }

    public DrawerSectionItem(Context context, DrawerAdapter parent, @StringRes int title) {
        this(context, parent, context.getString(title));
        iconImageView.setVisibility(GONE);
    }
    public DrawerSectionItem(Context context, DrawerAdapter parent, @StringRes int title, @DrawableRes int icon) {
        this(context, parent, context.getString(title), icon);
    }

    public DrawerSectionItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawerSectionItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DrawerSectionItem setItems(List<BaseDrawerItem> items) {
        this.items = items;
        processItems();
        return this;
    }

    public DrawerSectionItem addItem(BaseDrawerItem item) {
        if (this.items == null)
            this.items = new ArrayList<>();
        this.items.add(item);
        processItems();
        return this;
    }

    @Override
    protected void onCreateView() {
        View view = inflate(getContext(), R.layout.section_drawer_item, this);
        iconImageView = (ImageView) view.findViewById(R.id.icon_image_view);
        titleTextview = (TextView) view.findViewById(R.id.title_text_view);
        itemsLinearlayout = (LinearLayout) view.findViewById(R.id.items_linear_layout);
        expandImageView = (ImageView) view.findViewById(R.id.expand_image_view);
        expandImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleItems();
            }
        });
        processItems();
        openItems();
    }

    private void processItems() {
        itemsLinearlayout.removeAllViews();
        if (items != null) {
            for (View itemView :
                    items) {
                itemsLinearlayout.addView(itemView);
            }
        }
        if(parent != null)
            parent.notifyDataSetInvalidated();
    }

    private void openItems() {
        expandImageView.setImageResource(R.drawable.ic_expand_less_black_24dp);
        itemsLinearlayout.setVisibility(VISIBLE);
        if (parent != null)
            parent.notifyDataSetInvalidated();
    }

    private void closeItems() {
        expandImageView.setImageResource(R.drawable.ic_expand_more_black_24dp);
        itemsLinearlayout.setVisibility(GONE);
        if (parent != null)
            parent.notifyDataSetInvalidated();
    }

    private void toggleItems() {
        if (itemsLinearlayout.getVisibility() == VISIBLE)
            closeItems();
        else
            openItems();
    }
}
