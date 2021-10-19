package com.varanegar.vaslibrary.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.OnSaveLocation;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLicense;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.BatteryLowLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.DeviceEventViewModel;
import com.varanegar.vaslibrary.model.location.LocationModel;

import java.util.GregorianCalendar;

import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 11/13/2017.
 */

public class PowerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (TrackingLicense.getLicensePolicy(context) == 1)
            return;
        if (!isInitialStickyBroadcast()) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                if (level == 15) {
                    TrackingLogManager.addLog(context, LogType.BATTERY, LogLevel.Warning, "Battery level = 15%");
                    BatteryLowLocationViewModel batteryLowLocationViewModel = new BatteryLowLocationViewModel();
                    batteryLowLocationViewModel.eventData = new DeviceEventViewModel();
                    batteryLowLocationViewModel.eventData.Time = DateHelper.toString(new GregorianCalendar(), DateFormat.MicrosoftDateTime);
                    final LocationManager locationManager = new LocationManager(context);
                    locationManager.addTrackingPoint(batteryLowLocationViewModel, new OnSaveLocation() {
                        @Override
                        public void onSaved(LocationModel location) {
                            locationManager.tryToSendItem(location);
                        }

                        @Override
                        public void onFailed(String error) {
                            Timber.e(error);
                        }
                    });

                }
            }
        }
    }

}
