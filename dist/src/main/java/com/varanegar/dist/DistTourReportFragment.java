package com.varanegar.dist;

import com.varanegar.dist.fragment.DistCustomersFragment;
import com.varanegar.vaslibrary.sync.SyncService;
import com.varanegar.vaslibrary.ui.fragment.clean.CustomersFragment;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;

/**
 * Created by s.foroughi on 25/03/2017.
 */

public class DistTourReportFragment extends TourReportFragment {

    @Override
    protected CustomersFragment getCustomersFragment() {
        return new DistCustomersFragment();
    }

    @Override
    protected Class<? extends SyncService> getSyncService() {
        return DistSyncService.class;
    }
}