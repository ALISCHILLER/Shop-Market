package com.varanegar.vaslibrary.jobscheduler;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.varanegar.framework.util.jobscheduler.Job;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;

import timber.log.Timber;

/**
 * Created by A.Torabi on 7/24/2017.
 */

public class SendTrackingPointsServiceJob implements Job {

    @Override
    public Long getInterval() {
        return 120L;
    }

    @Override
    public void run(Context context) {
        if (SysConfigManager.hasTracking(context)) {
            Intent intent = new Intent(context, SendTrackingPointsService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(intent);
            else
                context.startService(intent);
        } else {
            Timber.d("Tracking config is false");
            TrackingLogManager.addLog(context, LogType.CONFIG, LogLevel.Error, "ترکینگ از کنسول غیرفعال است");
        }
    }
}
