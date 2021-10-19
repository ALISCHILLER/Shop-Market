package com.varanegar.presale.ui;

import android.content.Context;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.vaslibrary.ui.drawer.TourReportDrawerItem;

/**
 * Created by s.foroughi on 25/03/2017.
 */

public class PreSalesTourReportDrawerItem extends TourReportDrawerItem {

    public PreSalesTourReportDrawerItem(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
        ((MainVaranegarActivity) getContext()).pushFragment(new PreSalesTourReportFragment());

    }


}
