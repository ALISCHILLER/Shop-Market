package com.varanegar.framework.base.questionnaire;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.varanegar.framework.base.questionnaire.controls.FormControl;

/**
 * Created by A.Torabi on 7/1/2017.
 */

public class FormView extends LinearLayout {
    FormAdapter adapter;


    public void setAdapter(FormAdapter adapter) {
        this.adapter = adapter;
        createItems();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        removeAllViews();
        if (adapter != null)
            createItems();
    }

    private void createItems() {
        for (FormControl control :
                adapter.getControls()) {
            addView(control.getView(adapter.activity, this));
        }

    }

    public FormView(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
    }

    public FormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
    }

    public FormView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.VERTICAL);
    }
}
