package com.varanegar.vaslibrary.jobscheduler;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.jobscheduler.Job;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLicense;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingService;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;

/**
 * Created by A.Torabi on 8/13/2017.
 */

public class TrackingServiceJob implements Job {
    private GoogleApiClient googleApiClient;

    @Override
    public Long getInterval() {
        return 600L;
    }

    @Override
    public void run(Context context) {
        if (TrackingLicense.getLicensePolicy(context) == 1)
            return;
        if (SysConfigManager.hasTracking(context)) {
            TrackingLogManager.addLog(context, LogType.LOCATION_SETTINGS, LogLevel.Info, "ردیابی از کنسول فعال است");
            GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
            int result = googleApiAvailability.isGooglePlayServicesAvailable(context);
            if (result == ConnectionResult.SUCCESS) {
                TrackingLogManager.addLog(context, LogType.LOCATION_SETTINGS, LogLevel.Info, "ردیابی و گوگل پلی فعال هستند");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        TrackingLogManager.addLog(context, LogType.LOCATION_SETTINGS, LogLevel.Error, "مجوز دسترسی به موقعیت ندارد");
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                            context.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        TrackingLogManager.addLog(context, LogType.LOCATION_SETTINGS, LogLevel.Error, "مجوز دسترسی به موقعیت در حالت background ندارد");
                }
                /**
                 * mehrdad Ali
                 * فعال کردن سرویس ارسال  لوکیشن
                 */
                    Intent trackingService = new Intent(context, TrackingService.class);
                    context.startService(trackingService);
                    getOnePoint(context);
            } else {
                if (result == ConnectionResult.API_UNAVAILABLE) {
                    TrackingLogManager.addLog(context, LogType.LOCATION_SETTINGS, LogLevel.Error, "Google api client is not available");
                } else if (result == ConnectionResult.SERVICE_DISABLED) {
                    TrackingLogManager.addLog(context, LogType.LOCATION_SETTINGS, LogLevel.Error, "Google api client is disabled");
                } else if (result == ConnectionResult.SERVICE_MISSING) {
                    TrackingLogManager.addLog(context, LogType.LOCATION_SETTINGS, LogLevel.Error, "Google api client is not installed");
                } else if (result == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
                    TrackingLogManager.addLog(context, LogType.LOCATION_SETTINGS, LogLevel.Error, "Google api client version is old. update is needed");
                } else {
                    TrackingLogManager.addLog(context, LogType.LOCATION_SETTINGS, LogLevel.Error, "Google api client error code = " + result);
                }
            }
        }

    }

    private void getOnePoint(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        TrackingLogManager.addLog(context, LogType.LOCATION_SETTINGS, LogLevel.Info, "Google api connected");
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            TrackingLogManager.addLog(context, LogType.LOCATION_SETTINGS, LogLevel.Error, "Permission denied");
                            googleApiClient.disconnect();
                            return;
                        }
                        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
                        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(location -> {
                            if (location == null) {
                                TrackingLogManager.addLog(context, LogType.POINT, LogLevel.Error, "One high accuracy point failed , null location");
                                fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, null).addOnSuccessListener(location2 -> {
                                    googleApiClient.disconnect();
                                    if (location2 != null) {
                                        TrackingLogManager.addLog(context, LogType.POINT, LogLevel.Info, "One low accuracy point received");
                                        TrackingService.savePoint(context, location2);
                                    } else
                                        TrackingLogManager.addLog(context, LogType.POINT, LogLevel.Error, "One low accuracy point failed , null location");
                                }).addOnFailureListener(e -> {
                                    TrackingLogManager.addLog(context, LogType.POINT, LogLevel.Error, "One low accuracy point failed");
                                });
                            } else {
                                TrackingLogManager.addLog(context, LogType.POINT, LogLevel.Info, "One high accuracy point received");
                                googleApiClient.disconnect();
                                TrackingService.savePoint(context, location);
                            }
                        }).addOnFailureListener(e -> {
                            TrackingLogManager.addLog(context, LogType.POINT, LogLevel.Error, "One high accuracy point failed");
                            fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, null).addOnSuccessListener(location2 -> {
                                googleApiClient.disconnect();
                                if (location2 != null) {
                                    TrackingLogManager.addLog(context, LogType.POINT, LogLevel.Info, "One low accuracy point received");
                                    TrackingService.savePoint(context, location2);
                                } else
                                    TrackingLogManager.addLog(context, LogType.POINT, LogLevel.Error, "One low accuracy point failed, null location");
                            }).addOnFailureListener(e1 -> {
                                TrackingLogManager.addLog(context, LogType.POINT, LogLevel.Error, "One low accuracy point failed");
                            });
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                }).build();
        googleApiClient.connect();
    }
}
