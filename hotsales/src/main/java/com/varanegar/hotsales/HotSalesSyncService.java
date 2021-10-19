package com.varanegar.hotsales;

import android.content.Context;

import com.varanegar.vaslibrary.manager.updatemanager.TourUpdateFlow;
import com.varanegar.vaslibrary.sync.SyncService;

/**
 * Created by A.Jafarzadeh on 3/6/2018.
 */

public class HotSalesSyncService extends SyncService {
    @Override
    protected TourUpdateFlow getTourUpdateFlow(Context context) {
        return new HotSalesTourUpdateFlow(context);
    }
}
