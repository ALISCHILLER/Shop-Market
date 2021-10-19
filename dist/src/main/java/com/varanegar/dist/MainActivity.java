package com.varanegar.dist;

import android.os.Bundle;

import com.varanegar.dist.fragment.DistCustomersFragment;
import com.varanegar.dist.fragment.DistLoginFragment;
import com.varanegar.dist.fragment.DistSendTourFragment;
import com.varanegar.dist.jobscheduler.DistJobScheduler;
import com.varanegar.framework.util.jobscheduler.JobSchedulerService;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.user.UserModel;

public class MainActivity extends VasActivity {

    @Override
    protected void onStart() {
        super.onStart();
        JobSchedulerService.start(this, R.mipmap.ic_launcher, DistJobScheduler.class);
    }

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
                DistLoginFragment loginFragment = new DistLoginFragment();
                pushFragment(loginFragment);
            } else if (tourManager.isTourAvailable()) {
                DistCustomersFragment customerFragment = new DistCustomersFragment();
                pushFragment(customerFragment);
            } else if (tourManager.isTourSending()) {
                pushFragment(new DistSendTourFragment());
            } else {
                DistTourReportFragment profileFragment = new DistTourReportFragment();
                pushFragment(profileFragment);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("firstCreation", true);
    }

}