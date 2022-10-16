package com.varanegar.hotsales;

import com.varanegar.hotsales.fragment.HotSalesCustomersFragment;
import com.varanegar.vaslibrary.sync.SyncService;
import com.varanegar.vaslibrary.ui.fragment.clean.CustomersFragment;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;

/**
 * Created by A.Jafarzadeh on 3/6/2018.
 */

public class HotSalesTourReportFragment extends TourReportFragment {
    @Override
    protected CustomersFragment getCustomersFragment() {
        return new HotSalesCustomersFragment();
    }

    @Override
    protected Class<? extends SyncService> getSyncService() {
        return HotSalesSyncService.class;
    }

}
