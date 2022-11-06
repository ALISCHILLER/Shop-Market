package com.varanegar.presale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.jobscheduler.JobSchedulerService;
import com.varanegar.presale.firebase.MyFirebaseMessagingService;
import com.varanegar.presale.fragment.PreSalesCustomersFragment;
import com.varanegar.presale.fragment.PreSalesSendTourFragment;
import com.varanegar.presale.fragment.PresalesLoginFragment;
import com.varanegar.presale.jobscheduler.PresalesJobScheduler;
import com.varanegar.presale.ui.PreSalesTourReportFragment;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.news.NewsZarManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.ui.dialog.News.NewsDialog;
import com.varanegar.vaslibrary.ui.fragment.news_fragment.model.NewsData_Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

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


    //---------------------------------------------------------------------------------------------- onStart
    @Override
    protected void onStart() {
        super.onStart();
        JobSchedulerService.start(this, R.mipmap.ic_launcher, PresalesJobScheduler.class);
    }
    //---------------------------------------------------------------------------------------------- onStart


    //---------------------------------------------------------------------------------------------- onSaveInstanceState
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("firstCreation", true);
    }
    //---------------------------------------------------------------------------------------------- onSaveInstanceState


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
            pushFragment(new PresalesLoginFragment());
        else
            checkTourAvailable();
    }
    //---------------------------------------------------------------------------------------------- checkUserLogin


    //---------------------------------------------------------------------------------------------- checkTourAvailable
    private void checkTourAvailable() {
        requestNews();
        TourManager tourManager = new TourManager(this);
        if (tourManager.isTourAvailable())
            pushFragment(new PreSalesCustomersFragment());
        else if (tourManager.isTourSending()) {
            pushFragment(new PreSalesSendTourFragment());
        } else
            pushFragment(new PreSalesTourReportFragment());
    }
    //---------------------------------------------------------------------------------------------- checkTourAvailable




    //---------------------------------------------------------------------------------------------- requestNews
    private void requestNews() {
        NewsZarManager manager = new NewsZarManager(this);
        manager.sync(new UpdateCall() {
            @Override
            protected void onFinish() {
                super.onFinish();
            }

            @Override
            protected void onSuccess() {
                super.onSuccess();
                showDialogNews();
            }

            @Override
            protected void onFailure(String error) {
                super.onFailure(error);
            }

            @Override
            protected void onError(String error) {
                super.onError(error);
            }
        });
    }
    //---------------------------------------------------------------------------------------------- requestNews


    //---------------------------------------------------------------------------------------------- showDialogNews
    private void showDialogNews() {
        NewsZarManager manager = new NewsZarManager(this);
        List<NewsData_Model> news = manager.getUnReadNews();
        if (news.size() > 0) {
            manager.readNews(news);
            try {
                NewsDialog dialog = new NewsDialog(news);
                dialog.setTitle(getString(com.varanegar.vaslibrary.R.string.news_zar));
                dialog.setCancelable(true);
                dialog.setClosable(true);
                dialog.show(getSupportFragmentManager(), "NewsDialog");
            } catch (Exception ignored) {}
        }
    }
    //---------------------------------------------------------------------------------------------- showDialogNews


}
