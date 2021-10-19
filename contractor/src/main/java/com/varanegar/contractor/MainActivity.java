package com.varanegar.contractor;

import android.os.Bundle;

import com.varanegar.contractor.jobscheduler.ContractorJobScheduler;
import com.varanegar.framework.util.jobscheduler.JobSchedulerService;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.user.UserModel;

public class MainActivity extends VasActivity {
    @Override
    protected void onStart() {
        super.onStart();
        JobSchedulerService.start(this, R.mipmap.ic_launcher, ContractorJobScheduler.class);
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
                ContractorLoginFragment loginFragment = new ContractorLoginFragment();
                pushFragment(loginFragment);
            } else if (tourManager.isTourAvailable()) {
                ContractorCustomersFragment customerFragment = new ContractorCustomersFragment();
                pushFragment(customerFragment);
            } else if (tourManager.isTourSending()) {
                pushFragment(new ContractorSendTourFragment());
            } else {
                ContractorTourReportFragment tourReportFragment = new ContractorTourReportFragment();
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
