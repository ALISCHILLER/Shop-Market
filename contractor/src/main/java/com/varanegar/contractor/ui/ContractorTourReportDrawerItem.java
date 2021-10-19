package com.varanegar.contractor.ui;

import android.content.Context;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.vaslibrary.ui.drawer.TourReportDrawerItem;

/**
 * Created by A.Jafarzadeh on 8/10/2019.
 */

public class ContractorTourReportDrawerItem extends TourReportDrawerItem {
    public ContractorTourReportDrawerItem(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
        ((MainVaranegarActivity) getContext()).pushFragment(new ContractorTourReportFragment());
    }
}
