package com.varanegar.contractor.ui;

import com.varanegar.contractor.ContractorCustomersFragment;
import com.varanegar.contractor.ContractorSyncService;
import com.varanegar.vaslibrary.sync.SyncService;
import com.varanegar.vaslibrary.ui.fragment.CustomersFragment;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;

/**
 * Created by A.Jafarzadeh on 8/10/2019.
 */

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
