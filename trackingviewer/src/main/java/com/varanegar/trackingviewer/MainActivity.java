package com.varanegar.trackingviewer;

import android.os.Bundle;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.logging.LogConfig;
import com.varanegar.vaslibrary.base.VasLogConfig;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.user.UserModel;

public class MainActivity extends MainVaranegarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean firstCreation = false;
        if (savedInstanceState != null)
            firstCreation = savedInstanceState.getBoolean("firstCreation", false);
        if (!firstCreation) {
            TourManager tourManager = new TourManager(this);
            UserModel userModel = UserManager.readFromFile(this);
            if (userModel == null) {
                ImportBackupFragment fragment = new ImportBackupFragment();
                pushFragment(fragment);
            } else {
                TrackingReportFragment trackingReportFragment = new TrackingReportFragment();
                pushFragment(trackingReportFragment);
            }
        }
    }

    @Override
    protected LogConfig createLogConfig() {
        try {
            return VasLogConfig.getInstance(this, VasLogConfig.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("firstCreation", true);
    }

    @Override
    protected boolean checkStoragePermission() {
        return true;
    }

    @Override
    protected boolean checkCameraPermission() {
        return true;
    }

    @Override
    protected boolean checkLocationPermission() {
        return true;
    }
}
