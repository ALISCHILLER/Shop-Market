package com.varanegar.vaslibrary.manager.locationmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.WaitEventViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.WaitLocationViewModel;
import com.varanegar.vaslibrary.model.location.LocationModel;

import java.util.Date;
import java.util.List;

public class TransitionReceiverBroadcast extends BroadcastReceiver {
    public static final String INTENT_ACTION = "com.varanegar.vaslibrary.activity_transition";
    LocationManager locationManager;


    @Override
    public void onReceive(final Context context, Intent intent) {
        if (TrackingLicense.getLicensePolicy(context) == 1)
            return;
        locationManager = new LocationManager(context);
        if (intent == null || !INTENT_ACTION.equals(intent.getAction()))
            return;
        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            if (result == null)
                return;
            List<ActivityTransitionEvent> transitionEvents = result.getTransitionEvents();
            ActivityTransitionEvent event = transitionEvents.get(transitionEvents.size() - 1);
            String tr = (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER) ? "ENTER" : "EXIT";
            String ac = (event.getActivityType() == DetectedActivity.STILL) ? "STILL" : "OTHER";
            TrackingLogManager.addLog(context, LogType.ACTIVITY, LogLevel.Info, ac + "  " + tr);
            if (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER && event.getActivityType() == DetectedActivity.STILL && !locationManager.isWait()) {
                locationManager.saveWait(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (locationManager.isWait()) {
                            final LocationModel oldLocation = locationManager.getLastLocation(WaitLocationViewModel.class);
                            if (oldLocation == null) {
                                saveNewWait(context);
                                return;
                            }
                            final WaitLocationViewModel waitLocationViewModel = locationManager.convert(oldLocation, WaitLocationViewModel.class);
                            if (waitLocationViewModel != null && waitLocationViewModel.eventData != null) {
                                locationManager.getLocation(new LocationManager.OnLocationUpdated() {
                                    @Override
                                    public void onSucceeded(LocationModel newLocation) {
                                        float distance = locationManager.distance(newLocation, oldLocation);
                                        if (distance < 10 && waitLocationViewModel.eventData.EndTime != null && (new Date().getTime() - waitLocationViewModel.eventData.EndTime.getTime()) < 10 * 60 * 1000) {
                                            waitLocationViewModel.eventData.EndTime = null;
                                            locationManager.tryToSendItem(locationManager.updateTrackingPoint(waitLocationViewModel, oldLocation));
                                        } else {
                                            saveNewWait(context);
                                        }
                                    }

                                    @Override
                                    public void onFailed(String error) {
                                        saveNewWait(context);
                                    }
                                });
                            } else
                                saveNewWait(context);
                        }
                    }
                }, 10 * 60 * 1000);
            } else if (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_EXIT && event.getActivityType() == DetectedActivity.STILL && locationManager.isWait()) {
                locationManager.stopWait();
            }

        }
    }

    private void saveNewWait(Context context) {
        TrackingLogManager.addLog(context, LogType.START_WAIT, LogLevel.Info);
        LocationModel locationModel = locationManager.getLastLocation(WaitLocationViewModel.class);
        WaitLocationViewModel oldWaitLocationViewModel = locationManager.convert(locationModel, WaitLocationViewModel.class);
        if (oldWaitLocationViewModel != null && oldWaitLocationViewModel.eventData != null && oldWaitLocationViewModel.eventData.EndTime == null) {
            oldWaitLocationViewModel.eventData.EndTime = new Date();
            locationModel = locationManager.updateTrackingPoint(oldWaitLocationViewModel, locationModel);
            locationManager.tryToSendItem(locationModel);
        }

        final WaitLocationViewModel waitLocationViewModel = new WaitLocationViewModel();
        waitLocationViewModel.eventData = new WaitEventViewModel();
        waitLocationViewModel.eventData.StartTime = new Date();
        locationManager.addTrackingPoint(waitLocationViewModel, new OnSaveLocation() {
            @Override
            public void onSaved(LocationModel location) {
                locationManager.tryToSendItem(location);
            }

            @Override
            public void onFailed(String error) {

            }
        });
    }
}
