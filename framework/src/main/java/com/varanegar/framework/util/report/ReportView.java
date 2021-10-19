package com.varanegar.framework.util.report;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;

/**
 * Created by atp on 12/26/2016.
 */
public class ReportView extends LinearLayout {
    ReportAdapter adapter;

    public void setAdapter(ReportAdapter adapter) {
        this.adapter = adapter;
        removeAllViews();
        addView(adapter.getReportView(), new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public ReportView(Context context) {
        super(context);
        setOrientation(VERTICAL);
    }

    public ReportView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public ReportView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
    }

}
