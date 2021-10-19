package com.varanegar.supervisor;

import com.varanegar.framework.util.jobscheduler.JobScheduler;
import com.varanegar.vaslibrary.jobscheduler.LocationProviderJob;
import com.varanegar.vaslibrary.jobscheduler.SendTrackingPointsServiceJob;
import com.varanegar.vaslibrary.jobscheduler.TrackingServiceJob;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;

/**
 * Created by A.Torabi on 6/7/2018.
 */

public class SupervisorJobScheduler extends JobScheduler {
    @Override
    public void registerJobs() {
        if (SysConfigManager.hasTracking(getContext())) {
            register(new SendTrackingPointsServiceJob());
            register(new TrackingServiceJob());
            register(new LocationProviderJob());
        }else{
            TrackingLogManager.addLog(getContext(), LogType.CONFIG, LogLevel.Error, "ترکینگ از کنسول غیرفعال است");
        }
    }
}
