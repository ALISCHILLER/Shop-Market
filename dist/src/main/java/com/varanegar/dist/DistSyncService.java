package com.varanegar.dist;

import android.content.Context;

import com.varanegar.vaslibrary.manager.updatemanager.TourUpdateFlow;
import com.varanegar.vaslibrary.sync.SyncService;

/**
 * Created by A.Jafarzadeh on 2/19/2018.
 */

public class DistSyncService extends SyncService {
    @Override
    protected TourUpdateFlow getTourUpdateFlow(Context context) {
        return new DistTourUpdateFlow(context);
    }
}
