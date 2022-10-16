package com.varanegar.presale.ui;

import com.varanegar.presale.PreSalesSyncService;
import com.varanegar.presale.fragment.PreSalesCustomersFragment;
import com.varanegar.vaslibrary.sync.SyncService;
import com.varanegar.vaslibrary.ui.fragment.clean.CustomersFragment;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;

/**
 * Created by s.foroughi on 25/03/2017.
 */

public class PreSalesTourReportFragment extends TourReportFragment {

    @Override
    protected CustomersFragment getCustomersFragment() {
        return new PreSalesCustomersFragment();
    }

    @Override
    protected Class<? extends SyncService> getSyncService() {
        return PreSalesSyncService.class;
    }
}