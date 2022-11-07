package com.varanegar.dist;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.varanegar.dist.firebase.MyFirebaseMessagingService;
import com.varanegar.dist.fragment.DistCustomersFragment;
import com.varanegar.dist.fragment.DistLoginFragment;
import com.varanegar.dist.fragment.DistSendTourFragment;
import com.varanegar.dist.jobscheduler.DistJobScheduler;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.jobscheduler.JobSchedulerService;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.user.UserModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import timber.log.Timber;

public class MainActivity extends VasActivity {

    //---------------------------------------------------------------------------------------------- onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkUserLogin();

        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences("Firebase_Token", Context.MODE_PRIVATE);
        String oldToken = sharedPreferences
                .getString("172F4321-16BB-4415-85D1-DD88FF04234C", "");
        if (oldToken.isEmpty())
            MyFirebaseMessagingService.refreshToken(this,
                    new MyFirebaseMessagingService.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(String error) {

                        }
                    });
    }
    //---------------------------------------------------------------------------------------------- onCreate



    //---------------------------------------------------------------------------------------------- checkUserLogin
    private void checkUserLogin() {
        UserModel userModel = UserManager.readFromFile(this);
        if (userModel == null)
            pushFragment(new DistLoginFragment());
        else
            checkTourAvailable();
    }
    //---------------------------------------------------------------------------------------------- checkUserLogin


    //---------------------------------------------------------------------------------------------- checkTourAvailable
    private void checkTourAvailable() {
        TourManager tourManager = new TourManager(this);
        if (tourManager.isTourAvailable())
            pushFragment(new DistCustomersFragment());
        else if (tourManager.isTourSending())
            pushFragment(new DistSendTourFragment());
        else
            pushFragment(new DistTourReportFragment());
    }
    //---------------------------------------------------------------------------------------------- checkTourAvailable


    //---------------------------------------------------------------------------------------------- onStart
    @Override
    protected void onStart() {
        super.onStart();
        JobSchedulerService.start(this, R.mipmap.ic_launcher, DistJobScheduler.class);
    }
    //---------------------------------------------------------------------------------------------- onStart


    //---------------------------------------------------------------------------------------------- onSaveInstanceState
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("firstCreation", true);
    }
    //---------------------------------------------------------------------------------------------- onSaveInstanceState

}