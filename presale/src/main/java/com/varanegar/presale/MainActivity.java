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
import com.varanegar.vaslibrary.manager.news.NewsZarManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.ui.dialog.News.NewsDialog;
import com.varanegar.vaslibrary.ui.fragment.news_fragment.model.NewsData_Model;

import java.util.List;


public class MainActivity extends VasActivity {


    //---------------------------------------------------------------------------------------------- onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkUserLogin();
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
        else if (tourManager.isTourSending())
            pushFragment(new PreSalesSendTourFragment());
        else
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
            } catch (Exception ignored) {
            }
        }
    }
    //---------------------------------------------------------------------------------------------- showDialogNews


}
