package com.varanegar.vaslibrary.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.WaitLocationViewModel;
import com.varanegar.vaslibrary.model.location.LocationModel;

import java.util.Date;

public class WaitAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LocationManager locationManager = new LocationManager(context);
        locationManager.stopWait();
    }
}
