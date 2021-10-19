package com.varanegar.supervisor;

import android.os.Bundle;

import com.varanegar.framework.util.jobscheduler.JobSchedulerService;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.model.user.UserModel;

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
            if (userModel == null) {
                SupervisorLoginFragment loginFragment = new SupervisorLoginFragment();
                pushFragment(loginFragment);
            } else {
                MainFragment mainFragment = new MainFragment();
                pushFragment(mainFragment);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("firstCreation", true);
    }

}
