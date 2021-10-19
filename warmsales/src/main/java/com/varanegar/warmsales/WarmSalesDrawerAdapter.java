package com.varanegar.warmsales;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.component.drawer.DrawerItem;
import com.varanegar.vaslibrary.ui.drawer.MainDrawerAdapter;

public class WarmSalesDrawerAdapter extends MainDrawerAdapter {
    public WarmSalesDrawerAdapter(final MainVaranegarActivity activity) {
        super(activity);

        add(1, new WarmTourReportDrawerItem(activity));

        reports.addItem(new DrawerItem(activity, R.string.car_onhandqty).setClickListener(view -> {
            WarmWarehouseFragment fragment = new WarmWarehouseFragment();
            gotoReportFragment(fragment, false);
        }));
        reports.addItem(new DrawerItem(activity, R.string.delivery_report).setClickListener(view -> {
            DeliveryReport fragment = new DeliveryReport();
            gotoReportFragment(fragment, false);
        }));
        reports.addItem(new DrawerItem(activity, R.string.payment_report).setClickListener(view -> {
            PaymentReport fragment = new PaymentReport();
            gotoReportFragment(fragment, false);
        }));

    }
}
