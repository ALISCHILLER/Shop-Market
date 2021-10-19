package com.varanegar.framework.util.recycler.expandablerecycler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

import com.varanegar.framework.R;
import com.varanegar.framework.util.recycler.BaseRecyclerView;

/**
 * Created by atp on 1/31/2017.
 */

public class ExpandableRecyclerView extends BaseRecyclerView {
    int headerBackgroundColor;
    Drawable collapseIcon;
    Drawable expandIcon;

    public ExpandableRecyclerView(Context context) {
        super(context);
    }

    public ExpandableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableRecyclerView);
        try {
            if (Build.VERSION.SDK_INT >= 23)
                headerBackgroundColor = a.getColor(R.styleable.ExpandableRecyclerView_headerBackgroundColor, context.getColor(R.color.white));
            else
                headerBackgroundColor = a.getColor(R.styleable.ExpandableRecyclerView_headerBackgroundColor, context.getResources().getColor(R.color.white));
            Drawable collapse = a.getDrawable(R.styleable.ExpandableRecyclerView_collapseIcon);
            if (Build.VERSION.SDK_INT >= 21)
                collapseIcon = collapse != null ? collapse : context.getDrawable(R.drawable.ic_unfold_less_black_24dp);
            else
                collapseIcon = collapse != null ? collapse : context.getResources().getDrawable(R.drawable.ic_unfold_less_black_24dp);
            Drawable expand = a.getDrawable(R.styleable.ExpandableRecyclerView_expandIcon);
            if (Build.VERSION.SDK_INT >= 21)
                expandIcon = expand != null ? expand : context.getDrawable(R.drawable.ic_unfold_more_black_24dp);
            else
                expandIcon = expand != null ? expand : context.getResources().getDrawable(R.drawable.ic_unfold_more_black_24dp);
        } finally {
            a.recycle();
        }
    }

    public ExpandableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof ExpandableRecyclerAdapter) {
            ExpandableRecyclerAdapter exAdapter = (ExpandableRecyclerAdapter) adapter;
            exAdapter.setHeaderBackgroundColor(headerBackgroundColor);
            exAdapter.setExpandIcon(expandIcon);
            exAdapter.setCollapseIcon(collapseIcon);
        }

    }
}
