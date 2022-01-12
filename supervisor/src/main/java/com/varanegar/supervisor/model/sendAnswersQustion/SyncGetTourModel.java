package com.varanegar.supervisor.model.sendAnswersQustion;

import android.content.Context;
import android.content.pm.PackageManager;

import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class SyncGetTourModel {

    public SyncGetTourModel(Context context, UUID tourId){
        this.TourUniqueId = tourId;
        this.SendDate = new Date();
        try {
            this.ApkVersion = context.getPackageManager().getPackageInfo(context.getApplicationInfo().packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
            e.printStackTrace();
        }
    }
    public UUID TourUniqueId;
    public Date SendDate;
    public String ApkVersion;
    public List<SyncGetCustomerCallModel> CustomerCalls = new ArrayList<>();
}
