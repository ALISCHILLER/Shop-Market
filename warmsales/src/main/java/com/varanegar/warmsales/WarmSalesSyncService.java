package com.varanegar.warmsales;

import android.content.Context;

import com.varanegar.vaslibrary.manager.updatemanager.TourUpdateFlow;
import com.varanegar.vaslibrary.sync.SyncService;

public class WarmSalesSyncService extends SyncService {
    @Override
    protected TourUpdateFlow getTourUpdateFlow(Context context) {
        return new WarmSalesTourUpdateFlow(context);
    }
}
