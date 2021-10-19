package com.varanegar.contractor;

import android.content.Context;

import com.varanegar.vaslibrary.manager.updatemanager.TourUpdateFlow;
import com.varanegar.vaslibrary.sync.SyncService;

public class ContractorSyncService extends SyncService {
    @Override
    protected TourUpdateFlow getTourUpdateFlow(Context context) {
        return new ContractTourUpdateFlow(context);
    }
}
