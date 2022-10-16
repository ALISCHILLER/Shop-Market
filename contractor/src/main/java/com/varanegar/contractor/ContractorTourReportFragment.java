package com.varanegar.contractor;

import com.varanegar.vaslibrary.sync.SyncService;
import com.varanegar.vaslibrary.ui.fragment.clean.CustomersFragment;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;

public class ContractorTourReportFragment extends TourReportFragment {
    @Override
    protected CustomersFragment getCustomersFragment() {
        return new ContractorCustomersFragment();
    }

    @Override
    protected Class<? extends SyncService> getSyncService() {
        return ContractorSyncService.class;
    }
}
