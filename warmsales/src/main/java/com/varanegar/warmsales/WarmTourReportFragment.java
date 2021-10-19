package com.varanegar.warmsales;

import com.varanegar.vaslibrary.sync.SyncService;
import com.varanegar.vaslibrary.ui.fragment.CustomersFragment;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;

public class WarmTourReportFragment extends TourReportFragment {

    @Override
    protected CustomersFragment getCustomersFragment() {
        return new WarmSalesCustomersFragment();
    }

    @Override
    protected Class<? extends SyncService> getSyncService() {
        return WarmSalesSyncService.class;
    }
}