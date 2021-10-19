package com.varanegar.contractor;

import android.view.View;

import com.varanegar.contractor.ui.ContractorTourReportDrawerItem;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.component.drawer.DrawerItem;
import com.varanegar.vaslibrary.ui.drawer.MainDrawerAdapter;
import com.varanegar.vaslibrary.ui.report.SalesOrRequestReportFragment;

class ContractorDrawerAdapter extends MainDrawerAdapter {
    public ContractorDrawerAdapter(final MainVaranegarActivity activity) {
        super(activity);
        add(1, new ContractorTourReportDrawerItem(activity));
        reports.addItem(new DrawerItem(activity, com.varanegar.vaslibrary.R.string.order_report).setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SalesOrRequestReportFragment fragment = new SalesOrRequestReportFragment();
                gotoReportFragment(fragment,false);
            }
        }));

    }
}
