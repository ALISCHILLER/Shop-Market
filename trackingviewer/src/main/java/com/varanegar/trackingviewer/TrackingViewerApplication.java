package com.varanegar.trackingviewer;

import android.app.Application;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.DbHandler;
import com.varanegar.framework.database.SQLiteConnectionString;
import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;
import com.varanegar.vaslibrary.model.location.LocationModel;

import java.util.Calendar;
import java.util.List;

import timber.log.Timber;

/**
 * Created by A.Torabi on 10/22/2018.
 */

public class TrackingViewerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SQLiteConnectionString connectionString = null;
        try {
            connectionString = new SQLiteConnectionString(getApplicationContext(), "vndb");
            DbHandler dbHandler = new DbHandler(this, connectionString);
            VaranegarApplication.Init(dbHandler, new VaranegarApplication.AppId("edb10691-4f3d-42cb-8803-d72f83b339c6","edb10691-4f3d-42cb-8803-d72f83b339c6"), null);
            LocationManager locationManager = new LocationManager(this);
            Calendar from = Calendar.getInstance();
            from.set(Calendar.HOUR_OF_DAY, 0);
            from.set(Calendar.MINUTE, 0);
            from.set(Calendar.SECOND, 0);

            Calendar to = (Calendar) from.clone();
            to.add(Calendar.DAY_OF_MONTH, 1);
            List<LocationModel> locationModels = locationManager.getLocations(from.getTime(), to.getTime(),false,false);
            Timber.d("Today points = " + locationModels.size());
        } catch (Exception e) {
            Timber.e(e);
            throw new RuntimeException(e);
        }

    }
}
