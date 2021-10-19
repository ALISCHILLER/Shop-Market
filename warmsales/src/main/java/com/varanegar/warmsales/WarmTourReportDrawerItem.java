package com.varanegar.warmsales;

import android.content.Context;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.component.drawer.BaseDrawerItem;
import com.varanegar.vaslibrary.ui.drawer.TourReportDrawerItem;

public class WarmTourReportDrawerItem  extends TourReportDrawerItem {

    public WarmTourReportDrawerItem(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
        ((MainVaranegarActivity)getContext()).pushFragment(new WarmTourReportFragment());
    }


}
