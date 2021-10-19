package com.varanegar.framework.util.report;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * Created by A.Torabi on 5/21/2018.
 */

public class ReportHorizontalScrollView extends HorizontalScrollView {
    private Integer fl;

    public ReportHorizontalScrollView(Context context) {
        super(context);
    }

    public ReportHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReportHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    enum Status {
        Scrolled,
        Start
    }

    interface HorizontalStatusChangeListener {
        void onChanged(Status status);
    }

    private HorizontalStatusChangeListener horizontalStatusChanged;

    public void setOnHorizontalStatusChangeListener(@NonNull HorizontalStatusChangeListener listener) {
        this.horizontalStatusChanged = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (fl == null)
            fl = oldl;
        if (Math.abs(fl - l) < 200) {
            if (horizontalStatusChanged != null)
                horizontalStatusChanged.onChanged(Status.Start);
        } else {
            if (horizontalStatusChanged != null)
                horizontalStatusChanged.onChanged(Status.Scrolled);
        }

    }
}
