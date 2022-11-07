package com.varanegar.supervisor;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.jobscheduler.JobSchedulerService;
import com.varanegar.supervisor.firebase.MyFirebaseMessagingService;
import com.varanegar.supervisor.fragment.list_notification_Fragment.ListNotification_Fragment;
import com.varanegar.supervisor.getTour_fragment.Get_Tour_Fragment;
import com.varanegar.supervisor.model.VisitorManager;
import com.varanegar.supervisor.model.VisitorModel;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.model.user.UserModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import timber.log.Timber;

/**
   * Created by A.Torabi on 6/7/2018.
 */

public class MainActivity extends VasActivity {


    //---------------------------------------------------------------------------------------------- onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkVersionIsUpdated();

        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("pin_code");
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("pin_code");
        }


        if (newString!=null){
            if (newString.equals("pin_layout")){
                ListNotification_Fragment notification_fragment=new ListNotification_Fragment();
                pushFragment(notification_fragment);
            }
        }

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
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("SaveVersion", currentVersion);
            editor.apply();
            if (currentVersion != saveVersion && saveVersion > 0) {
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
            pushFragment(new SupervisorLoginFragment());
        else
            checkVisitors();
    }
    //---------------------------------------------------------------------------------------------- checkUserLogin



    //---------------------------------------------------------------------------------------------- checkVisitors
    private void checkVisitors() {
        List<VisitorModel>  visitorModels = new VisitorManager(this).getAll();
        if (visitorModels.size() == 0) {
            Get_Tour_Fragment get_tour_fragment=new Get_Tour_Fragment();
            pushFragment(get_tour_fragment);
        } else {
            MainFragment mainFragment = new MainFragment();
            pushFragment(mainFragment);
        }
    }
    //---------------------------------------------------------------------------------------------- checkVisitors


    //---------------------------------------------------------------------------------------------- onStart
    @Override
    protected void onStart() {
        super.onStart();
        JobSchedulerService.start(this, R.mipmap.ic_launcher, SupervisorJobScheduler.class);
    }
    //---------------------------------------------------------------------------------------------- onStart


    //---------------------------------------------------------------------------------------------- onSaveInstanceState
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("firstCreation", true);

    }
    //---------------------------------------------------------------------------------------------- onSaveInstanceState


    //---------------------------------------------------------------------------------------------- onDestroy
    @Override
    protected void onDestroy() {
        SharedPreferences editor = getSharedPreferences("KeyPairBoolData", Context.MODE_PRIVATE);
        String json = VaranegarGsonBuilder.build().create().toJson(null);
        editor.edit().putString("visitorBoolData", json).apply();
        super.onDestroy();
    }
    //---------------------------------------------------------------------------------------------- onDestroy

}
