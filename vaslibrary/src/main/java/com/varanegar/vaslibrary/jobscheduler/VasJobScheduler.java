package com.varanegar.vaslibrary.jobscheduler;

import android.app.ActivityManager;
import android.content.Context;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.jobscheduler.JobScheduler;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import timber.log.Timber;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by A.Torabi on 12/20/2017.
 */

public abstract class VasJobScheduler extends JobScheduler {

    public static void registerVasJobs(Context context){
        if (context != null) {
            if (SysConfigManager.hasTracking(context)) {
                try {
                    ActivityManager.MemoryInfo memoryInfo = getAvailableMemory(context);
                    if (!memoryInfo.lowMemory) {
                        register(new SendTrackingPointsServiceJob());
                        register(new TrackingServiceJob());
                        register(new LocationProviderJob());
                    } else
                        TrackingLogManager.addLog(context, LogType.LOCATION_SETTINGS, LogLevel.Error, "Restarting Tracking skipped because of Low Memory!");
                } catch (Error error) {
                    Timber.e(error);
                }
            } else {
                TrackingLogManager.addLog(context, LogType.CONFIG, LogLevel.Error, "ترکینگ از کنسول غیرفعال است");
            }
            SysConfigModel silentSend = new SysConfigManager(context).read(ConfigKey.AutoSynch, SysConfigManager.cloud);
            if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales) && SysConfigManager.compare(silentSend, true))
                register(new SilentSendTourJob());
        }

    }

    @Override
    public void registerJobs() {
        registerVasJobs(getContext());
    }

    private static ActivityManager.MemoryInfo getAvailableMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }
}
