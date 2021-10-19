package com.varanegar.framework.util.component.drawer;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.varanegar.framework.R;
import com.varanegar.framework.base.MainVaranegarActivity;

/**
 * Created by atp on 1/29/2017.
 */

public class DrawerItem extends BaseDrawerItem {
    String title;
    int iconResourceId;
    TextView textView;
    ImageView iconImageView;

    public DrawerItem(MainVaranegarActivity activity, @StringRes int title) {
        this(activity, activity.getString(title));
    }

    public DrawerItem(MainVaranegarActivity activity, @StringRes int title, @DrawableRes int iconResourceId) {
        this(activity, activity.getString(title), iconResourceId);
    }

    public DrawerItem setClickListener(OnClickListener onItemClick) {
        this.onClickListener = onItemClick;
        return this;
    }

    public DrawerItem(Context context, @StringRes int title, @DrawableRes int iconResourceId) {
        this(context, context.getString(title), iconResourceId);
    }

    public DrawerItem(Context context, String title, @DrawableRes int iconResourceId) {
        super(context);
        this.iconResourceId = iconResourceId;
        this.title = title;
        textView.setText(title);
        if (iconResourceId != -1) {
            iconImageView.setImageResource(iconResourceId);
            iconImageView.setVisibility(VISIBLE);
        }
    }

    public DrawerItem(Context context, String title) {
        super(context);
        this.iconResourceId = -1;
        this.title = title;
        textView.setText(title);
        iconImageView.setVisibility(GONE);
    }

    public DrawerItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DrawerItem, 0, 0);
        try {
            title = a.getString(R.styleable.DrawerItem_title);
            iconResourceId = a.getResourceId(R.styleable.DrawerItem_icon, -1);
        } finally {
            a.recycle();
            textView.setText(title);
            if (iconResourceId != -1) {
                iconImageView.setImageResource(iconResourceId);
                iconImageView.setVisibility(VISIBLE);
            }
        }
    }

    /**
     * You can override this method to provide a different view for drawer item.
     */
    protected void onCreateView() {
        View view = inflate(getContext(), R.layout.drawer_item, this);
        textView = (TextView) view.findViewById(R.id.menu_name_text_view);
        iconImageView = (ImageView) view.findViewById(R.id.menu_icon_image_view);
    }

    protected OnClickListener onClick() {
        return null;
    }
}
