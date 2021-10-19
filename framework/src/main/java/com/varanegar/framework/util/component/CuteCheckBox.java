package com.varanegar.framework.util.component;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.varanegar.framework.R;

/**
 * Created by atp on 3/30/2017.
 */

public class CuteCheckBox extends RelativeLayout {
    String text;
    CheckBox checkBox;
    TextView textView;
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
    boolean isChecked;

    private void setup() {
        inflate(getContext(), R.layout.cute_check_box, this);
        checkBox = (CheckBox) findViewById(R.id.check_box);
        textView = (TextView) findViewById(R.id.text_view);
        textView.setText(text);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onCheckedChangeListener != null)
                    onCheckedChangeListener.onCheckedChanged(checkBox, checkBox.isChecked());
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setup();
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        if (checkBox != null)
            checkBox.setChecked(checked);
    }

    public boolean isChecked() {
        return checkBox.isChecked();
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setText(String text) {
        this.text = text;
        if (textView != null)
            textView.setText(text);
    }

    public CuteCheckBox(Context context) {
        super(context);
    }

    public CuteCheckBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CuteCheckBox, 0, 0);
        try {
            isChecked = a.getBoolean(R.styleable.CuteCheckBox_isChecked, false);
            text = a.getString(R.styleable.CuteCheckBox_text);
        } finally {
            a.recycle();
        }
    }

    public CuteCheckBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CuteCheckBox, 0, 0);
        try {
            isChecked = a.getBoolean(R.styleable.CuteCheckBox_isChecked, false);
            text = a.getString(R.styleable.CuteCheckBox_text);
        } finally {
            a.recycle();
        }
    }
}
