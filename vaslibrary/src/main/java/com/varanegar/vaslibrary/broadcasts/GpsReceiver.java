package com.varanegar.vaslibrary.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;

/**
 * Created by A.Jafarzadeh on 11/13/2017.
 */

public class GpsReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, Intent intent) {
        LocationManager.checkGpsProvider(context);
    }


}
