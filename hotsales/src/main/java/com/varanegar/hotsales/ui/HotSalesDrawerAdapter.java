package com.varanegar.hotsales.ui;

import android.view.View;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.component.drawer.DrawerItem;
import com.varanegar.hotsales.R;
import com.varanegar.hotsales.report.OperationReport.OperationReportFragment;
import com.varanegar.hotsales.fragment.RequestProductFragment;
import com.varanegar.hotsales.report.HotSalesWarehouseFragment;
import com.varanegar.vaslibrary.ui.drawer.MainDrawerAdapter;
import com.varanegar.vaslibrary.ui.report.SalesOrRequestReportFragment;

/**
 * Created by A.Jafarzadeh on 3/11/2018.
 */

public class HotSalesDrawerAdapter extends MainDrawerAdapter {

    public HotSalesDrawerAdapter(final MainVaranegarActivity activity) {
        super(activity);

        add(1, new HotSalesTourReportDrawerItem(activity));
        add(2, new DrawerItem(getActivity(), R.string.request_product, com.varanegar.vaslibrary.R.drawable.ic_request_product_black_24dp).setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestProductFragment fragment = new RequestProductFragment();
                gotoReportFragment(fragment,false);
            }
        }));
        reports.addItem(new DrawerItem(activity, R.string.inventory_qty).setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HotSalesWarehouseFragment fragment = new HotSalesWarehouseFragment();
                gotoReportFragment(fragment,false);
            }
        }));
        reports.addItem(new DrawerItem(activity, R.string.sales_report).setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SalesOrRequestReportFragment fragment = new SalesOrRequestReportFragment();
                gotoReportFragment(fragment,false);
            }
        }));
        reports.addItem(new DrawerItem(activity, R.string.operation_report).setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OperationReportFragment fragment = new OperationReportFragment();
                gotoReportFragment(fragment,false);
            }
        }));
    }
}
