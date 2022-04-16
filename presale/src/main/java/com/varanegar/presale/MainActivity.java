package com.varanegar.presale;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.varanegar.framework.util.jobscheduler.JobSchedulerService;
import com.varanegar.presale.firebase.MyFirebaseMessagingService;
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
}
