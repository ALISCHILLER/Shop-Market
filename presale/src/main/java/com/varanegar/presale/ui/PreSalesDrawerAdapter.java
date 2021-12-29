package com.varanegar.presale.ui;

import android.view.View;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.component.drawer.DrawerItem;
import com.varanegar.presale.R;
import com.varanegar.presale.report.PresalesWarehouseFragment;
import com.varanegar.vaslibrary.ui.drawer.MainDrawerAdapter;
import com.varanegar.vaslibrary.ui.report.SalesOrRequestReportFragment;

/**
 * Created by s.foroughi on 12/02/2017.
 */

public class PreSalesDrawerAdapter extends MainDrawerAdapter {
    public PreSalesDrawerAdapter(final MainVaranegarActivity activity) {
        super(activity);

        /**
         * گزارشات قبلی
         */

                add(1, new PreSalesTourReportDrawerItem(activity));
//        reports.addItem(new DrawerItem(activity, R.string.inventory_qty).setClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PresalesWarehouseFragment fragment = new PresalesWarehouseFragment();
//                gotoReportFragment(fragment,false);
//            }
//        }));
        reports.addItem(new DrawerItem(activity, com.varanegar.vaslibrary.R.string.order_report).setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SalesOrRequestReportFragment fragment = new SalesOrRequestReportFragment();
                gotoReportFragment(fragment,false);
            }
        }));

    }
}
