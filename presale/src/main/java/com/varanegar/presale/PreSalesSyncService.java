package com.varanegar.presale;

import android.content.Context;

import com.varanegar.vaslibrary.manager.updatemanager.TourUpdateFlow;
import com.varanegar.vaslibrary.sync.SyncService;

/**
 * Created by A.Torabi on 2/17/2018.
 */

public class PreSalesSyncService extends SyncService {
    @Override
    protected TourUpdateFlow getTourUpdateFlow(Context context) {
        return new PreSalesTourUpdateFlow(context);
    }
}
