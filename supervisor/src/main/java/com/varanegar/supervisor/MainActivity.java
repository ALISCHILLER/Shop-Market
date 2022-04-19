package com.varanegar.supervisor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.framework.util.jobscheduler.JobSchedulerService;
import com.varanegar.supervisor.firebase.MyFirebaseMessagingService;
import com.varanegar.supervisor.fragment.list_notification_Fragment.ListNotification_Fragment;
import com.varanegar.supervisor.fragment.menuTools_Fragment.MenuTools_Fragmnet;
import com.varanegar.supervisor.fragment.news_fragment.News_Fragment;
import com.varanegar.supervisor.getTour_fragment.Get_Tour_Fragment;
import com.varanegar.supervisor.model.VisitorManager;
import com.varanegar.supervisor.model.VisitorModel;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.model.user.UserModel;

import java.util.List;

/**
   * Created by A.Torabi on 6/7/2018.
 */

public class MainActivity extends VasActivity {
    @Override
    protected void onStart() {
        super.onStart();
        JobSchedulerService.start(this, R.mipmap.ic_launcher, SupervisorJobScheduler.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean firstCreation = false;
        if (savedInstanceState != null)
            firstCreation = savedInstanceState.getBoolean("firstCreation", false);
        if (!firstCreation) {
            UserModel userModel = UserManager.readFromFile(this);

            List<VisitorModel>  visitorModels = new VisitorManager(this).getAll();

            if (userModel == null) {
                SupervisorLoginFragment loginFragment = new SupervisorLoginFragment();
                pushFragment(loginFragment);
            } else if (visitorModels.size() == 0) {
                Get_Tour_Fragment get_tour_fragment=new Get_Tour_Fragment();
                pushFragment(get_tour_fragment);
            } else {
                MainFragment mainFragment = new MainFragment();
                pushFragment(mainFragment);
            }
        }

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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("firstCreation", true);

    }

    @Override
    protected void onDestroy() {
        SharedPreferences editor = getSharedPreferences("KeyPairBoolData", Context.MODE_PRIVATE);
        String json = VaranegarGsonBuilder.build().create().toJson(null);
        editor.edit().putString("visitorBoolData", json).commit();
        super.onDestroy();
    }

}
