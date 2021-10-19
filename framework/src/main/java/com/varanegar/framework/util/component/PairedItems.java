package com.varanegar.framework.util.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;

import com.varanegar.framework.R;


/**
 * Created by atp on 3/11/2017.
 */

public class PairedItems extends LinearLayout {
    private float textSize;
    private int textPadding;
    private boolean singleLine;
    private String value;
    private String title;
    private int orientation = LinearLayout.HORIZONTAL;
    private int typeface;


    TextView titleTextView;
    TextView valueTextView;
    private boolean inflated = false;
    private int valueColor = -1;

    public void setTitle(String title) {
        this.title = title;
        if (inflated)
            titleTextView.setText(title);
    }

    public void setValue(String value) {
        this.value = value;
        if (inflated)
            valueTextView.setText(value);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = null;
        if (orientation == LinearLayout.HORIZONTAL)
            view = inflate(getContext(), R.layout.item_pair_single_line_layout, this);
        else
            view = inflate(getContext(), R.layout.item_pair_layout, this);
        titleTextView = (TextView) view.findViewById(R.id.title_text_view);
        valueTextView = (TextView) view.findViewById(R.id.value_text_view);
        titleTextView.setText(title);
        valueTextView.setText(value);
        if (valueColor != -1)
            valueTextView.setTextColor(valueColor);
        else
            valueTextView.setTextColor(Color.BLACK);

        valueTextView.setSingleLine(singleLine);
        titleTextView.setSingleLine(singleLine);
        Typeface typeface = Typeface.create(titleTextView.getTypeface(), this.typeface);
        titleTextView.setTypeface(typeface);
        typeface = Typeface.create(valueTextView.getTypeface(), this.typeface);
        valueTextView.setTypeface(typeface);
        titleTextView.setPadding(textPadding, textPadding, textPadding, textPadding);
        valueTextView.setPadding(textPadding, textPadding, textPadding, textPadding);
        if (textSize != -1) {
            titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
        inflated = true;
    }

    public PairedItems(Context context) {
        super(context);
    }

    public PairedItems(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PairedItems, 0, 0);
        try {
            title = a.getString(R.styleable.PairedItems_title);
            value = a.getString(R.styleable.PairedItems_value);
            singleLine = a.getBoolean(R.styleable.PairedItems_singleLine, false);
            orientation = a.getInt(R.styleable.PairedItems_android_orientation, LinearLayout.HORIZONTAL);
            typeface = a.getInt(R.styleable.PairedItems_android_textStyle, Typeface.NORMAL);
            textPadding = a.getDimensionPixelOffset(R.styleable.PairedItems_textPadding, getContext().getResources().getDimensionPixelSize(R.dimen.padding_default));
            textSize = a.getDimension(R.styleable.PairedItems_textSize, -1);
        } finally {
            a.recycle();
        }
    }

    public PairedItems(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PairedItems, 0, 0);
        try {
            title = a.getString(R.styleable.PairedItems_title);
            value = a.getString(R.styleable.PairedItems_value);
            singleLine = a.getBoolean(R.styleable.PairedItems_singleLine, false);
            orientation = a.getInt(R.styleable.PairedItems_android_orientation, LinearLayout.HORIZONTAL);
            typeface = a.getInt(R.styleable.PairedItems_android_textStyle, Typeface.NORMAL);
            textPadding = a.getDimensionPixelOffset(R.styleable.PairedItems_textPadding, getContext().getResources().getDimensionPixelSize(R.dimen.padding_default));
            textSize = a.getDimension(R.styleable.PairedItems_textSize, -1);
        } finally {
            a.recycle();
        }
    }

    public void setValueColor(@ColorRes int color) {
        this.valueColor = color;
        if (valueTextView != null)
            valueTextView.setTextColor(getResources().getColor(color));
    }

    @Nullable
    public String getValue() {
        return value;
    }
}