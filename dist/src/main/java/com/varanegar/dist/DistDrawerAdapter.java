package com.varanegar.dist;

import android.view.View;

import com.varanegar.dist.report.DeliveryReport;
import com.varanegar.dist.report.DistWarehouseFragment;
import com.varanegar.dist.report.PaymentReport;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.component.drawer.DrawerItem;
import com.varanegar.vaslibrary.ui.drawer.MainDrawerAdapter;

/**
 * Created by s.foroughi on 12/02/2017.
 */

public class DistDrawerAdapter extends MainDrawerAdapter{
    public DistDrawerAdapter(final MainVaranegarActivity activity) {
        super(activity);

        add(1, new DistTourReportDrawerItem(activity));

        reports.addItem(new DrawerItem(activity, R.string.dist_onhandqty).setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DistWarehouseFragment fragment = new DistWarehouseFragment();
                gotoReportFragment(fragment, false);
            }
        }));
        reports.addItem(new DrawerItem(activity, R.string.delivery_report).setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeliveryReport fragment = new DeliveryReport();
                gotoReportFragment(fragment, false);
            }
        }));
        reports.addItem(new DrawerItem(activity, R.string.payment_report).setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaymentReport fragment = new PaymentReport();
                gotoReportFragment(fragment, false);
            }
        }));

    }
}
