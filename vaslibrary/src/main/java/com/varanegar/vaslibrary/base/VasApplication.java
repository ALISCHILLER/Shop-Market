package com.varanegar.vaslibrary.base;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.multidex.MultiDexApplication;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.logging.LogConfig;
import com.varanegar.framework.database.DbHandler;
import com.varanegar.framework.database.SQLiteConnectionString;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.OnSaveLocation;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLicense;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.DeviceEventViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.OpenApplicationLocationViewModel;
import com.varanegar.vaslibrary.model.location.LocationModel;

import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by A.Torabi on 5/16/2018.
 */

public abstract class VasApplication extends MultiDexApplication {

    public static LogConfig createLogConfig(Context context) {
        try {
            return VasLogConfig.getInstance(context, VasLogConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    abstract protected VaranegarApplication.AppId appId();

    abstract protected VaranegarApplication.GRSAppId grsAppId();

    abstract protected String getDatabaseName();

    @Override
    public void onCreate() {
        super.onCreate();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int storagePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (storagePermission == PackageManager.PERMISSION_GRANTED) {
                createLogConfig(this);
            }
        } else {
            createLogConfig(this);
        }

        SQLiteConnectionString connectionString = null;
        try {
            connectionString = new SQLiteConnectionString(getApplicationContext(), getDatabaseName());
            DbHandler dbHandler = new DbHandler(this, connectionString);
            VaranegarApplication.Init(dbHandler, appId(), grsAppId());
            TrackingLogManager.addLog(this, LogType.APP_OPEN, LogLevel.Info);
            TrackingLicense.logLicense(this);
            OpenApplicationLocationViewModel locationViewModel = new OpenApplicationLocationViewModel();
            locationViewModel.eventData = new DeviceEventViewModel();
            locationViewModel.eventData.Time = DateHelper.toString(new Date(), DateFormat.MicrosoftDateTime, Locale.US);
            final LocationManager locationManager = new LocationManager(this);
            locationManager.addTrackingPoint(locationViewModel, new OnSaveLocation() {
                @Override
                public void onSaved(LocationModel location) {
                    locationManager.tryToSendItem(location);
                }

                @Override
                public void onFailed(String error) {
                    Timber.d(error);
                }
            });
        } catch (Exception e) {
            Timber.e(e);
            throw new RuntimeException(e);
        }

    }
}
