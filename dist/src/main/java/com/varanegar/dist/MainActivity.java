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
        boolean firstCreation = false;
        if (savedInstanceState != null)
            firstCreation = savedInstanceState.getBoolean("firstCreation", false);
        if (!firstCreation)
            checkVersionIsUpdated();

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


    //---------------------------------------------------------------------------------------------- checkVersionIsUpdated
    private void checkVersionIsUpdated() {
        try {
            int currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            SharedPreferences sharedPreferences = getApplicationContext()
                    .getSharedPreferences("ApplicationVersion", Context.MODE_PRIVATE);
            int saveVersion = sharedPreferences.getInt("SaveVersion", 0);
            if (currentVersion != saveVersion && saveVersion > 0) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("SaveVersion", currentVersion);
                editor.apply();
                showDialogNewFeatures();
            } else
                checkUserLogin();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            checkUserLogin();
        }

    }
    //---------------------------------------------------------------------------------------------- checkVersionIsUpdated


    //---------------------------------------------------------------------------------------------- showDialogNewFeatures
    private void showDialogNewFeatures() {
        StringBuilder text = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getAssets().open("newFeature.txt")))) {
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                text.append(mLine);
                text.append('\n');
            }
            CuteMessageDialog dialog = new CuteMessageDialog(this);
            dialog.setIcon(Icon.Info);
            dialog.setCancelable(false);
            dialog.setTitle(com.varanegar.vaslibrary.R.string.newFeatures);
            dialog.setMessage(text.toString());
            dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.iUnderstood, view -> checkUserLogin());
            dialog.show();
        } catch (IOException e) {
            Timber.e("Error reading file new feature " + e.getMessage());
            checkUserLogin();
        }
    }
    //---------------------------------------------------------------------------------------------- showDialogNewFeatures


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