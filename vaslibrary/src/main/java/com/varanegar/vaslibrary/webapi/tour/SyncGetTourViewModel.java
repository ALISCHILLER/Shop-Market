package com.varanegar.vaslibrary.webapi.tour;

import android.content.Context;
import android.content.pm.PackageManager;

import com.varanegar.framework.validation.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 10/2/2017.
 */

public class SyncGetTourViewModel {
    public SyncGetTourViewModel(Context context, UUID tourId , int tourNo){
        this.TourUniqueId = tourId;
        this.TourNo = tourNo;
        this.SendDate = new Date();
        try {
            this.ApkVersion = context.getPackageManager().getPackageInfo(context.getApplicationInfo().packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
            e.printStackTrace();
        }
    }
    @NotNull
    public UUID TourUniqueId;
    public int TourNo;
    public Date SendDate;
    public String ApkVersion;
    public List<SyncGetCustomerCallViewModel> CustomerCalls = new ArrayList<>();
    public List<SyncGetCancelInvoiceViewModel> CancelInvoices = new ArrayList<>();
    public List<SyncGetCustomerUpdateDataViewModel> CustomerUpdates = new ArrayList<>();
    public List<SyncGetCustomerUpdateLocationViewModel> CustomerLocations = new ArrayList<>();
    public List<SyncGetRequestLineModel> RequestItemLines = new ArrayList<>();

    public List<GpsTrackingsViewModel> GpsTrackings = new ArrayList<>();
}
