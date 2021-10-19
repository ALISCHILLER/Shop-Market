package com.varanegar.dist;

import android.content.Context;
import android.view.View;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.vaslibrary.ui.drawer.TourReportDrawerItem;

/**
 * Created by s.foroughi on 25/03/2017.
 */

public class DistTourReportDrawerItem extends TourReportDrawerItem {

    public DistTourReportDrawerItem(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
        ((MainVaranegarActivity)getContext()).pushFragment(new DistTourReportFragment());
    }


}
