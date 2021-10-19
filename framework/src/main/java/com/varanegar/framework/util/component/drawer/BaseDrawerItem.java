package com.varanegar.framework.util.component.drawer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by atp on 1/29/2017.
 */

public abstract class BaseDrawerItem extends LinearLayout {

    public OnClickListener onClickListener;

    void setup() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null)
                    onClickListener.onClick(BaseDrawerItem.this);
            }
        });
        onCreateView();
    }

    public BaseDrawerItem(Context context) {
        super(context);
        setup();
    }

    public BaseDrawerItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public BaseDrawerItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    /**
     * You should override this method to provide a view for drawer item.
     */
    protected abstract void onCreateView();
}
