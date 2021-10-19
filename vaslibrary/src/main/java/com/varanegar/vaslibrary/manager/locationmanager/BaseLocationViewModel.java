package com.varanegar.vaslibrary.manager.locationmanager;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.manager.locationmanager.map.TrackingMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.TrackingPointMarker;
import com.varanegar.vaslibrary.model.location.LocationModel;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by A.Torabi on 8/9/2017.
 */

public class BaseLocationViewModel {
    public long RowId;
    public UUID UniqueId;
    public UUID CompanyPersonnelId;
    public double Latitude;
    public double Longitude;
    public long TimeOffset;
    public Date ActivityDate;
    public float Speed;
    public float Accurancy;
    public String Address;
    public UUID TourId;
    public Integer TourRef;
    public String CompanyPersonnelName;
    public int SubType;
    public String Lable;
    private int LicensePolicy;
    private String Provider;
    private Date DeviceTime;
    public String Desc;

    protected void setBaseInfo(LocationModel location) {
        this.CompanyPersonnelId = location.CompanyPersonnelId;
        this.CompanyPersonnelName = location.CompanyPersonnelName;
        this.Latitude = location.Latitude;
        this.Longitude = location.Longitude;
        this.TimeOffset = location.TimeOffset;
        this.Speed = location.Speed;
        this.Accurancy = location.Accuracy;
        this.Address = location.Address;
        this.TourId = location.TourId;
        this.TourRef = location.TourRef;
        this.UniqueId = location.UniqueId;
        this.Provider = location.Provider;
        this.LicensePolicy = location.LicensePolicy;
        this.ActivityDate = location.Date;
        this.DeviceTime = location.DeviceTime;
        this.RowId = location.rowid;
    }
}