package com.varanegar.vaslibrary.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.jobscheduler.JobScheduler;
import com.varanegar.vaslibrary.jobscheduler.SendTrackingPointsServiceJob;
import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.OnSaveLocation;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLicense;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.BaseEventLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.DeviceEventViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.MobileDataOffLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.MobileDataOnLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.WifiOffLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.WifiOnLocationViewModel;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.location.LocationModel;
import com.varanegar.vaslibrary.webapi.ping.PingApi;

import java.util.Date;
import java.util.GregorianCalendar;

import timber.log.Timber;

public class ConnectivityReceiver extends BroadcastReceiver {

    private static Long lastWifiOn;
    private static Long lastWifiOff;

    private static Long lastDataOn;
    private static Long lastDataOff;


    @Override
    public void onReceive(final Context context, Intent intent) {
        if (TrackingLicense.getLicensePolicy(context) == 1)
            return;
        if (!isInitialStickyBroadcast()) {
            String action = intent.getAction();
            if (action == null)
                return;
            SharedPreferences sharedPreferences = context.getSharedPreferences("WIFI_STATUS", Context.MODE_PRIVATE);
            SharedPreferences sharedPreferences2 = context.getSharedPreferences("DATA_STATUS", Context.MODE_PRIVATE);
            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) || action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                BaseEventLocationViewModel locationViewModel = null;
                if (Connectivity.isConnectedWifi(context) && !sharedPreferences.getBoolean("IS_ON", false)) {
                    long now = new Date().getTime();
                    if (lastWifiOn != null && now - lastWifiOn < 60000)
                        return;
                    lastWifiOn = new Date().getTime();
                    locationViewModel = new WifiOnLocationViewModel();
                    WifiOnLocationViewModel wifiOnLocationViewModel = ((WifiOnLocationViewModel) locationViewModel);
                    wifiOnLocationViewModel.eventData = new DeviceEventViewModel();
                    wifiOnLocationViewModel.eventData.Time = DateHelper.toString(new GregorianCalendar(), DateFormat.MicrosoftDateTime);
                    TrackingLogManager.addLog(context, LogType.WIFI_ON, LogLevel.Info);
                    sharedPreferences.edit().putBoolean("IS_ON", true).apply();
                    tryToSendTrackingPoints(context);
                    new PingApi().refreshBaseServerUrl(context, null);
                } else if (!Connectivity.isConnectedWifi(context) && sharedPreferences.getBoolean("IS_ON", true)) {
                    long now = new Date().getTime();
                    if (lastWifiOff != null && now - lastWifiOff < 60000)
                        return;
                    lastWifiOff = new Date().getTime();
                    locationViewModel = new WifiOffLocationViewModel();
                    WifiOffLocationViewModel wifiOffLocationViewModel = (WifiOffLocationViewModel) locationViewModel;
                    wifiOffLocationViewModel.eventData = new DeviceEventViewModel();
                    wifiOffLocationViewModel.eventData.Time = DateHelper.toString(new GregorianCalendar(), DateFormat.MicrosoftDateTime);
                    TrackingLogManager.addLog(context, LogType.WIFI_OFF, LogLevel.Info);
                    sharedPreferences.edit().putBoolean("IS_ON", false).apply();
                } else if (Connectivity.isConnectedMobile(context) && !sharedPreferences2.getBoolean("IS_ON", false)) {
                    long now = new Date().getTime();
                    if (lastDataOn != null && now - lastDataOn < 60000)
                        return;
                    lastDataOn = new Date().getTime();
                    locationViewModel = new MobileDataOnLocationViewModel();
                    MobileDataOnLocationViewModel mobileDataOnLocationViewModel = ((MobileDataOnLocationViewModel) locationViewModel);
                    mobileDataOnLocationViewModel.eventData = new DeviceEventViewModel();
                    mobileDataOnLocationViewModel.eventData.Time = DateHelper.toString(new GregorianCalendar(), DateFormat.MicrosoftDateTime);
                    TrackingLogManager.addLog(context, LogType.DATA_ON, LogLevel.Info);
                    sharedPreferences2.edit().putBoolean("IS_ON", true).apply();
                    tryToSendTrackingPoints(context);
                    new PingApi().refreshBaseServerUrl(context, null);
                } else if (!Connectivity.isConnectedMobile(context) && sharedPreferences2.getBoolean("IS_ON", true)) {
                    long now = new Date().getTime();
                    if (lastDataOff != null && now - lastDataOff < 60000)
                        return;
                    lastDataOff = new Date().getTime();
                    locationViewModel = new MobileDataOffLocationViewModel();
                    MobileDataOffLocationViewModel mobileDataOffLocationViewModel = (MobileDataOffLocationViewModel) locationViewModel;
                    mobileDataOffLocationViewModel.eventData = new DeviceEventViewModel();
                    mobileDataOffLocationViewModel.eventData.Time = DateHelper.toString(new GregorianCalendar(), DateFormat.MicrosoftDateTime);
                    TrackingLogManager.addLog(context, LogType.DATA_OFF, LogLevel.Info);
                    sharedPreferences2.edit().putBoolean("IS_ON", false).apply();
                }

                if (locationViewModel != null) {
                    final LocationManager locationManager = new LocationManager(context);
                    locationManager.addTrackingPoint(locationViewModel, new OnSaveLocation() {
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

    private void tryToSendTrackingPoints(Context context) {
        if (SysConfigManager.hasTracking(context))
            JobScheduler.resetJob(SendTrackingPointsServiceJob.class, context);
    }
}
