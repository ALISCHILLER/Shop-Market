package com.varanegar.vaslibrary.jobscheduler;

import android.content.Context;
import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.varanegar.framework.util.jobscheduler.Job;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLicense;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.webapi.timezone.TimeApi;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import timber.log.Timber;

/**
 * Created by A.Torabi on 10/21/2018.
 */

public class LocationProviderJob implements Job {
    @Override
    public Long getInterval() {
        return 900L;
    }

    @Override
    public void run(final Context context) {
        if (TrackingLicense.getLicensePolicy(context) == 1)
            return;
        TrackingLicense.logLicense(context);
        LocationSettingsRequest.Builder request = new LocationSettingsRequest.Builder();
        request.addLocationRequest(LocationManager.getLocationRequest(context));
        LocationServices.getSettingsClient(context).checkLocationSettings(request.build()).addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                LocationSettingsStates states = locationSettingsResponse.getLocationSettingsStates();
                if (!states.isLocationPresent()) {
                    Timber.d("Location Services is not present");
                    TrackingLogManager.addLog(context, LogType.PROVIDER, LogLevel.Error, "NotEnabled");
                }
                String providers = "";
                if (states.isGpsPresent() && states.isGpsUsable())
                    providers += " GPS = usable ";
                if (states.isGpsPresent() && !states.isGpsUsable())
                    providers += " GPS = not usable ";
                if (!states.isGpsPresent())
                    providers += " GPS = not present ";
                if (states.isNetworkLocationPresent() && states.isNetworkLocationUsable())
                    providers += " NETWORK = usable ";
                if (states.isNetworkLocationPresent() && !states.isNetworkLocationUsable())
                    providers += " NETWORK = not usable ";
                if (!states.isNetworkLocationPresent())
                    providers += " NETWORK = not present ";
                if (providers.isEmpty()) {
                    Timber.d("Location provider is not present!");
                    TrackingLogManager.addLog(context, LogType.PROVIDER, LogLevel.Error, "NotAvailable");
                } else {
                    Timber.d(providers + "location provider is present.");
                    TrackingLogManager.addLog(context, LogType.PROVIDER, LogLevel.Info, providers);
                }
                checkTime(context);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                TrackingLogManager.addLog(context, LogType.PROVIDER, LogLevel.Error, "NotEnabled");
            }
        });
    }

    private void checkTime(final Context context) {
        TimeApi timeApi = new TimeApi(context);
        timeApi.checkTime((String log) -> TrackingLogManager.addLog(context, LogType.INVALID_TIME, LogLevel.Info,log ));
    }
}
