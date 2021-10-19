package com.varanegar.hotsales.ui;

import android.content.Context;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.hotsales.HotSalesTourReportFragment;
import com.varanegar.vaslibrary.ui.drawer.TourReportDrawerItem;

/**
 * Created by A.Jafarzadeh on 3/11/2018.
 */

public class HotSalesTourReportDrawerItem extends TourReportDrawerItem {
    public HotSalesTourReportDrawerItem(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
        ((MainVaranegarActivity) getContext()).pushFragment(new HotSalesTourReportFragment());
    }
}
