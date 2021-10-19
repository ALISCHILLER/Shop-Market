package com.varanegar.framework.util.recycler;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.varanegar.framework.R;

/**
 * Created by atp on 12/19/2016.
 */
public class BaseRecyclerViewWrapped extends BaseRecyclerView {

    public BaseRecyclerViewWrapped(Context context) {
        super(context);
    }

    public BaseRecyclerViewWrapped(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseRecyclerViewWrapped(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View inflate() {
        return inflate(getContext(), R.layout.base_recycler_view_wrapped_layout, this);
    }
}
