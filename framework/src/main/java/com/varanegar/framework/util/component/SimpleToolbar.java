package com.varanegar.framework.util.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.varanegar.framework.R;


/**
 * Created by atp on 2/12/2017.
 */

public class SimpleToolbar extends Toolbar {
    private int backgroundColor;
    private int colorPrimary;
    ImageView drawerImageView;
    ImageView backImageView;
    private String title;
    private int colorIcons;
    TextView titleTextView1;
    TextView titleTextView2;

    private OnClickListener drawerListener;
    private OnClickListener backIconListener;
    private Integer menuImageId;

    public void setOnMenuClickListener(OnClickListener listener) {
        this.drawerListener = listener;
        drawerImageView.setVisibility(VISIBLE);
        if (backIconListener != null) {
            titleTextView1.setVisibility(GONE);
            titleTextView2.setVisibility(VISIBLE);
        }

    }

    public void setOnMenuClickListener(@DrawableRes int imageId, OnClickListener listener) {
        setOnMenuClickListener(listener);
        this.menuImageId = imageId;
        if (this.drawerImageView != null)
            this.drawerImageView.setImageResource(imageId);
    }

    public void removeOnMenuClickListener() {
        this.drawerListener = null;
        drawerImageView.setVisibility(GONE);
        titleTextView1.setVisibility(VISIBLE);
        titleTextView2.setVisibility(GONE);
    }

    public void setOnBackClickListener(OnClickListener listener) {
        this.backIconListener = listener;
        backImageView.setVisibility(VISIBLE);
        if (backIconListener != null) {
            titleTextView1.setVisibility(GONE);
            titleTextView2.setVisibility(VISIBLE);
        }
    }

    public void removeOnBackClickListener() {
        this.backIconListener = null;
        backImageView.setVisibility(GONE);
        titleTextView1.setVisibility(VISIBLE);
        titleTextView2.setVisibility(GONE);
    }

    public void setTitle(String title) {
        this.title = title;
        if (titleTextView1 != null) {
            titleTextView1.setText(title);
            titleTextView2.setText(title);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setup();
        if (Build.VERSION.SDK_INT > 21)
            setElevation(getResources().getDimension(R.dimen.elevation_default));
    }

    private void setup() {
        View view = inflate(getContext(), R.layout.toolbar_simple, null);
        if (backgroundColor == -1)
            setBackgroundColor(colorPrimary);
        else
            setBackgroundColor(backgroundColor);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(view);
        titleTextView1 = (TextView) view.findViewById(R.id.title_text_view1);
        titleTextView2 = (TextView) view.findViewById(R.id.title_text_view2);
        if (title != null && !title.isEmpty()) {
            titleTextView1.setText(title);
            titleTextView2.setText(title);
        }
        drawerImageView = (ImageView) view.findViewById(R.id.drawer_image_view);
        drawerImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerListener != null)
                    drawerListener.onClick(view);
            }
        });
        if (menuImageId != null)
            drawerImageView.setImageResource(menuImageId);

        if (colorIcons != -1)
            drawerImageView.setColorFilter(colorIcons, PorterDuff.Mode.SRC_ATOP);

        backImageView = (ImageView) view.findViewById(R.id.back_image_view);
        backImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (backIconListener != null)
                    backIconListener.onClick(view);
            }
        });
        if (colorIcons != -1)
            backImageView.setColorFilter(colorIcons, PorterDuff.Mode.SRC_ATOP);


    }

    public SimpleToolbar(Context context) {
        super(context);
    }

    public SimpleToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        int[] attr = new int[]{0, 1};
        //مهرداد جون دوست دارم
        TypedArray a = context.obtainStyledAttributes(new int[]{R.attr.colorIcons, R.attr.colorPrimary});
        TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.SimpleToolbar, 0, 0);
        try {
            title = a2.getString(R.styleable.SimpleToolbar_title);
            backgroundColor = a2.getColor(R.styleable.SimpleToolbar_backgroundColor, -1);
            colorIcons = a.getColor(attr[0], -1);
            colorPrimary = a.getColor(attr[1], -1);
        } finally {
            a.recycle();
            a2.recycle();
        }
    }

    public SimpleToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
