package com.varanegar.warmsales;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.varanegar.framework.util.jobscheduler.JobSchedulerService;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.warmsales.jobscheduler.WarmSalesJobScheduler;

public class MainActivity extends VasActivity {
    @Override
    protected void onStart() {
        super.onStart();
        JobSchedulerService.start(this, R.mipmap.ic_launcher, WarmSalesJobScheduler.class);
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
                WarmSalesLoginFragment loginFragment = new WarmSalesLoginFragment();
                pushFragment(loginFragment);
            } else if (tourManager.isTourAvailable()) {
                WarmSalesCustomersFragment customerFragment = new WarmSalesCustomersFragment();
                pushFragment(customerFragment);
            } else if (tourManager.isTourSending()) {
                pushFragment(new WarmSalesSendTourFragment());
            } else {
                WarmSalesTourReportFragment tourReportFragment = new WarmSalesTourReportFragment();
                pushFragment(tourReportFragment);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("firstCreation", true);
    }
}