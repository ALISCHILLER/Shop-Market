package com.varanegar.vaslibrary.manager.locationmanager;

import android.Manifest;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.Area;
import com.varanegar.vaslibrary.manager.RegionAreaPointManager;
import com.varanegar.vaslibrary.manager.Transition;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.locationmanager.newtacking.RemoteSignalREmitter;
import com.varanegar.vaslibrary.manager.locationmanager.newtacking.SignalRListener;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.TransitionEventLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.TransitionEventViewModel;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.location.LocationModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.timezone.TimeApi;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by A.Torabi on 8/9/2017.
 */

public class TrackingService extends IntentService{


    private LocationManager locationManager;
    private static Area dayPath;
    private static Area region;
    private static Area company;
    private static ArrayDeque<String> lastLocations = new ArrayDeque<>(500);
    private GoogleApiClient googleApiClient;
    private static boolean isConnected;
    private SignalRListener signalRListener;


    public TrackingService() {
        super("TrackingService");
    }

    private void init() {
        if (TrackingLicense.getLicensePolicy(this) == 1)
            return;
        RegionAreaPointManager regionAreaPointManager = new RegionAreaPointManager(this);
        dayPath = regionAreaPointManager.getDayPath();
        region = regionAreaPointManager.getRegion();
        company = regionAreaPointManager.getCompanyPath();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int locationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int bgLocationPermission = PackageManager.PERMISSION_GRANTED;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                bgLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            if (locationPermission == PackageManager.PERMISSION_GRANTED && bgLocationPermission == PackageManager.PERMISSION_GRANTED) {
                start();
            } else {
                if (locationPermission != PackageManager.PERMISSION_GRANTED)
                    TrackingLogManager.addLog(this, LogType.LOCATION_SETTINGS, LogLevel.Error, "Permission denied");
                else
                    TrackingLogManager.addLog(this, LogType.LOCATION_SETTINGS, LogLevel.Error, "Background Permission denied");
            }
        } else {

            start();

        }
    }


    public static void savePoint(Context context, Location location) {

        TrackingLogManager.addLog(context, LogType.POINT, LogLevel.Info, "{" + location.getLatitude() + "  " + location.getLongitude() + "} Provider=" + location.getProvider() + " Gps time=" + DateHelper.toString(new Date(location.getTime()), DateFormat.Time, Locale.getDefault()) + " Tablet time=" + DateHelper.toString(new Date(), DateFormat.Time, Locale.getDefault()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (location.isFromMockProvider()) {
                TrackingLogManager.addLog(context, LogType.MOCK_PROVIDER, LogLevel.Error, "پوینت تقلبی!");
            }
        }
        if (location.getAccuracy() > 100) {
            TrackingLogManager.addLog(context, LogType.INACCURATE_POINT, LogLevel.Warning, " دقت " + location.getAccuracy());
        }

        if (location.getTime() < 20000L) {
            TrackingLogManager.addLog(context, LogType.POINT_TIME, LogLevel.Error, "{" +
                    location.getLatitude() + "  " + location.getLongitude() + "} Gps Time is wrong : " + DateHelper.toString(new Date(location.getTime()), DateFormat.Complete, Locale.getDefault()));
            location.setTime(new Date().getTime());
        }

        if (Math.abs(location.getTime() - new Date().getTime()) / 3000 > 2800) {
            TrackingLogManager.addLog(context, LogType.POINT_TIME, LogLevel.Error, "{" + location.getLatitude() + "  "
                    + location.getLongitude()
                    + "} Gps Time is different from Tablet time. Gps time = "
                    + DateHelper.toString(new Date(location.getTime()), DateFormat.Complete, Locale.getDefault())
                    + " Tablet Time : "
                    + DateHelper.toString(new Date(), DateFormat.Complete, Locale.getDefault()));
            TimeApi api = new TimeApi(context);
           // api.checkTime(message -> TrackingLogManager.addLog(context, LogType.INVALID_TIME, LogLevel.Info, message));
          // location.setTime(new Date().getTime());
        }

        TourModel tourModel = new TourManager(context).loadTour();
        UserModel userModel = UserManager.readFromFile(context);
        if (userModel != null) {
            LocationModel locationModel = LocationModel.convert(context, location, userModel, tourModel);
            locationModel.Tracking = isLicenseValid(context, location, SysConfigManager.hasTracking(context));
            try {
                new LocationManager(context).insert(locationModel);
//                locationManager.refreshSomePoints(location);
            } catch (Exception e) {
                Timber.e(e);
                TrackingLogManager.addLog(context, LogType.POINT, LogLevel.Error, "{" + location.getLatitude() + "  " + location.getLongitude() + "} failed to save", e.getMessage());
            }

            ActivityManager.MemoryInfo memoryInfo = getAvailableMemory(context);

            if (!memoryInfo.lowMemory) {
                RegionAreaPointManager regionAreaPointManager = new RegionAreaPointManager(context);
                Transition transition = regionAreaPointManager.isTransition(location, dayPath, region, company);
                if (transition != null) {
                    Timber.d("Transition detected: " + "    Area=" + transition.Region + "    Type=" + transition.Type.name());
                    TrackingLogManager.addLog(context, LogType.TRANSITION, LogLevel.Info, "Transition detected: " + "    Area=" + transition.Region + "    Type=" + transition.Type.name() + "  gps time =" + DateHelper.toString(new Date(location.getTime()), DateFormat.Time, Locale.getDefault()));
                    final TransitionEventLocationViewModel locationViewModel = new TransitionEventLocationViewModel();
                    locationViewModel.eventData = new TransitionEventViewModel();
                    locationViewModel.eventData.Time = new Date(location.getTime());
                    locationViewModel.eventData.Transition = transition;
                    new LocationManager(context).addTrackingPoint(locationViewModel, locationModel, null);
                }
            }

        } else {
            TrackingLogManager.addLog(context, LogType.POINT, LogLevel.Info, "{" + location.getLatitude() + "  " + location.getLongitude() + "} skipped! user is not signed in");
        }
    }

    // Get a MemoryInfo object for the device's current memory status.
    private static ActivityManager.MemoryInfo getAvailableMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    private void start() {
        Log.e("Tracking Service", "start: ");
        locationManager = new LocationManager(this);
        if (SysConfigManager.hasTracking(this)) {
            try {
                TrackingLogManager.addLog(this, LogType.PROVIDER, LogLevel.Info, "starting location provider");
                TourManager tourManager = new TourManager(this);
                TourModel tourModel = tourManager.loadTour();
                if (googleApiClient != null && !googleApiClient.isConnected())
                    isConnected = false;

                if (!isConnected || googleApiClient == null) {
                    TrackingLogManager.addLog(this, LogType.PROVIDER, LogLevel.Info, "starting google api client");
                    googleApiClient = new GoogleApiClient.Builder(this)
                            .addApi(LocationServices.API)
                            .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(@Nullable Bundle bundle) {
                                    isConnected = googleApiClient.isConnected();
                                    if (googleApiClient != null && isConnected) {
                                        TrackingLogManager.addLog(TrackingService.this, LogType.PROVIDER, LogLevel.Info, "google api connected");
                                        try {
                                            if (ActivityCompat.checkSelfPermission(TrackingService.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackingService.this,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                TrackingLogManager.addLog(TrackingService.this, LogType.LOCATION_SETTINGS, LogLevel.Error,
                                                        "Permission denied");
                                                return;
                                            }
                                            FusedLocationProviderClient fusedLocationProviderClient =
                                                    LocationServices.getFusedLocationProviderClient(TrackingService.this);
                                            fusedLocationProviderClient.requestLocationUpdates(LocationManager.getLocationRequest(TrackingService.this),
                                                    new LocationCallback() {
                                                        @Override
                                                        public void onLocationResult(LocationResult locationResult) {
                                                            super.onLocationResult(locationResult);
                                                            List<Location> locations = locationResult.getLocations();
                                                            for (Location location :
                                                                    locations) {
                                                                String key = location.getLatitude() + "-" + location.getLongitude();
                                                                if (!lastLocations.contains(key)) {
                                                                    try {
                                                                        savePoint(TrackingService.this, location);
                                                                        if (lastLocations.size() >= 500)
                                                                            lastLocations.removeLast();
                                                                        lastLocations.addFirst(key);
                                                                        Long w = locationManager.getWaitTime();
                                                                        if (w != null && location.getSpeed() > 0 && locationManager.isWait() && location.getTime() > w)
                                                                            locationManager.stopWait();
                                                                    } catch (Exception ignored) {

                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }, Looper.myLooper())
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Timber.e(e);
                                                            TrackingLogManager.addLog(TrackingService.this, LogType.PROVIDER, LogLevel.Error, "requesting location updates failed", e.getMessage());

                                                        }
                                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    SysConfigModel waitingControl = new SysConfigManager(TrackingService.this)
                                                            .read(ConfigKey.WaitingControl, SysConfigManager.cloud);
                                                    if (SysConfigManager.compare(waitingControl, true)) {
                                                        Intent intent = new Intent(TrackingService.this, TransitionReceiverBroadcast.class);
                                                        intent.setAction(TransitionReceiverBroadcast.INTENT_ACTION);
                                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(TrackingService.this, 0, intent, 0);

                                                        List<ActivityTransition> activityTransitions = new ArrayList<>();
                                                        activityTransitions.add(new ActivityTransition.Builder()
                                                                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                                                                .setActivityType(DetectedActivity.STILL).build());
                                                        activityTransitions.add(new ActivityTransition.Builder()
                                                                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                                                                .setActivityType(DetectedActivity.STILL).build());
                                                        ActivityTransitionRequest request = new ActivityTransitionRequest(activityTransitions);
                                                        ActivityRecognition.getClient(TrackingService.this).requestActivityTransitionUpdates(request, pendingIntent);
                                                    }
                                                }
                                            });
                                        } catch (Error e) {
                                            Timber.e(e);
                                            TrackingLogManager.addLog(TrackingService.this, LogType.PROVIDER, LogLevel.Error, "starting location provider failed", e.getMessage());
                                        }
                                    } else
                                        TrackingLogManager.addLog(TrackingService.this, LogType.PROVIDER, LogLevel.Error, "google api connection failed");
                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    TrackingLogManager.addLog(TrackingService.this, LogType.PROVIDER, LogLevel.Error, "google api connection suspended");
                                    isConnected = false;
                                }
                            }).build();
                    googleApiClient.connect();
                } else
                    TrackingLogManager.addLog(TrackingService.this, LogType.PROVIDER, LogLevel.Info, "google api is already connected");
            } catch (Error error) {
                Timber.e(error);
                TrackingLogManager.addLog(this, LogType.PROVIDER, LogLevel.Error, "خطا در راه اندازی ردیابی", error.getMessage());
            }
        }
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        init();
    }


    private static boolean isLicenseValid(Context context, Location location, boolean hasTracking) {
        try {
            return TrackingLicense.isValid(context, location);
        } catch (TrackingLicenseNotFoundEception trackingLicenseNotFoundEception) {
            if (hasTracking) {
                Toast.makeText(context, R.string.there_is_no_tracking_license, Toast.LENGTH_SHORT).show();
            }
            return false;
        } catch (TrackingLicenseExpiredEception trackingLicenseExpiredEception) {
            if (hasTracking) {
                Toast.makeText(context, R.string.tracking_license_is_expired, Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }

}
