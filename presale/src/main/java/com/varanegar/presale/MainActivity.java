package com.varanegar.presale;

import android.os.Bundle;

import com.varanegar.framework.util.jobscheduler.JobSchedulerService;
import com.varanegar.presale.fragment.PreSalesCustomersFragment;
import com.varanegar.presale.fragment.PreSalesSendTourFragment;
import com.varanegar.presale.fragment.PresalesLoginFragment;
import com.varanegar.presale.jobscheduler.PresalesJobScheduler;
import com.varanegar.presale.ui.PreSalesTourReportFragment;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.user.UserModel;


public class MainActivity extends VasActivity {

    @Override
    protected void onStart() {
        super.onStart();
        JobSchedulerService.start(this, R.mipmap.ic_launcher, PresalesJobScheduler.class);
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
                PresalesLoginFragment loginFragment = new PresalesLoginFragment();
                pushFragment(loginFragment);
            } else if (tourManager.isTourAvailable()) {
                PreSalesCustomersFragment customerFragment = new PreSalesCustomersFragment();
                pushFragment(customerFragment);
            } else if (tourManager.isTourSending()) {
                pushFragment(new PreSalesSendTourFragment());
            } else {
                PreSalesTourReportFragment tourReportFragment = new PreSalesTourReportFragment();
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
