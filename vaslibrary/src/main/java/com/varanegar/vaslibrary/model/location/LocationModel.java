package com.varanegar.vaslibrary.model.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Property;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLicense;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.model.user.UserModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 8/9/2017.
 */
@Table
public class LocationModel extends BaseModel {
    @Column
    public long rowid;
    @Column
    public double Longitude;
    @Column
    public double Latitude;
    @Column
    public float Accuracy;
    @Column
    public float Speed;
    @Column
    public Date Date;
    @Column
    public boolean IsSend;
    @Column
    public String Event;
    @Column
    public Date LastRetryTime;
    @Column
    public String EventType;
    @Column
    public boolean Tracking;
    @Column
    public String Address;
    //    @Column
//    public int ActivityType;
//    @Column
//    public boolean IsImportant;
    @Column
    public UUID TourId;
    @Column
    public Integer TourRef;
    @Column
    public UUID CompanyPersonnelId;
    @Column
    public String CompanyPersonnelName;
    @Column
    public UUID CustomerId;
    @Column
    public long TimeOffset;
    @Column
    public String Provider;
    @Column
    public int LicensePolicy;
    @Column
    public String DateAndTime;
    @Column
    public Date UpdateTime;
    @Column
    public Date DeviceTime;
    @Property
    public String EventTypeSimpleName;
//    @Property
//    public String ActivityTypeName;

    @Override
    public void setProperties() {
        super.setProperties();

        if (EventType != null) {
            String[] splits = EventType.split("\\.");
            if (splits.length > 0)
                EventTypeSimpleName = splits[splits.length - 1];
        }
//        switch (ActivityType) {
//            case DetectedActivity.IN_VEHICLE:
//                ActivityTypeName = "IN_VEHICLE";
//                break;
//            case DetectedActivity.ON_BICYCLE:
//                ActivityTypeName = "ON_BICYCLE";
//                break;
//            case DetectedActivity.ON_FOOT:
//                ActivityTypeName = "ON_FOOT";
//                break;
//            case DetectedActivity.STILL:
//                ActivityTypeName = "STILL";
//                break;
//            case DetectedActivity.UNKNOWN:
//                ActivityTypeName = "UNKNOWN";
//                break;
//            case DetectedActivity.TILTING:
//                ActivityTypeName = "TILTING";
//                break;
//            case DetectedActivity.WALKING:
//                ActivityTypeName = "WALKING";
//                break;
//            case DetectedActivity.RUNNING:
//                ActivityTypeName = "RUNNING";
//                break;
//        }
    }

    public static LocationModel convert(@NonNull Context context, @NonNull Location location, @NonNull UserModel userModel, @Nullable TourModel tourModel) {
        LocationModel locationModel = new LocationModel();
        locationModel.UniqueId = UUID.randomUUID();
        locationModel.Accuracy = location.getAccuracy();
        locationModel.Latitude = location.getLatitude();
        locationModel.Longitude = location.getLongitude();
        locationModel.Speed = location.getSpeed();
//        locationModel.Address = getAddress(context, locationModel.Latitude, locationModel.Longitude);
        locationModel.Date = new Date(location.getTime());
        locationModel.DateAndTime = DateHelper.toString(locationModel.Date, DateFormat.Complete, Locale.getDefault());
        locationModel.IsSend = false;
        locationModel.TourId = tourModel != null ? tourModel.UniqueId : null;
        locationModel.TourRef = tourModel != null ? tourModel.TourNo : null;
        locationModel.CompanyPersonnelId = userModel.UniqueId;
        locationModel.CompanyPersonnelName = userModel.UserName;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (location.isFromMockProvider())
                locationModel.Provider = "mock";
            else
                setProvider(context, locationModel, location);
        } else
            setProvider(context, locationModel, location);
        locationModel.LicensePolicy = TrackingLicense.getLicensePolicy(context);
        locationModel.DeviceTime = new Date();
        return locationModel;
    }

    public LocationModel convert(@NonNull Context context, @NonNull UserModel userModel, @Nullable TourModel tourModel) {
        LocationModel locationModel = new LocationModel();
        locationModel.UniqueId = UUID.randomUUID();
        locationModel.Accuracy = Accuracy;
        locationModel.Latitude = Latitude;
        locationModel.Longitude = Longitude;
        locationModel.Speed = Speed;
        locationModel.DeviceTime = new Date();
        locationModel.Date = new Date();
        locationModel.DateAndTime = DateHelper.toString(locationModel.Date, DateFormat.Complete, Locale.getDefault());
        locationModel.Provider = "fused_last";
        locationModel.CustomerId = null;
        locationModel.EventType = null;
        locationModel.Event = null;
        locationModel.EventTypeSimpleName = null;
        locationModel.IsSend = false;
        locationModel.LastRetryTime = null;
        locationModel.TourId = tourModel != null ? tourModel.UniqueId : null;
        locationModel.TourRef = tourModel != null ? tourModel.TourNo : null;
        locationModel.CompanyPersonnelId = userModel.UniqueId;
        locationModel.CompanyPersonnelName = userModel.UserName;
        locationModel.LicensePolicy = TrackingLicense.getLicensePolicy(context);
        return locationModel;
    }

    private static void setProvider(Context context, LocationModel locationModel, Location location) {
        String provider = location.getProvider();
        Boolean isOn = new LocationManager(context).getGpsStatus(location.getTime());
        if (isOn == null)
            locationModel.Provider = provider;
        else if (isOn)
            locationModel.Provider = provider + "_on";
        else
            locationModel.Provider = provider + "_off";
    }

    public static Location convert(@NonNull LocationModel locationModel) {
        Location location = new Location("point A");
        location.setLatitude(locationModel.Latitude);
        location.setLongitude(locationModel.Longitude);
        return location;
    }

    public static String getAddress(@NonNull final Context context, final double latitude, final double Longitude) {
        final List<String> addressFragments = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!Connectivity.isConnected(context)) {
                    Timber.d("Missing internet connection");

                    return;
                }
                try {
                    Geocoder geocoder = new Geocoder(context, VasHelperMethods.getSysConfigLocale(context));
                    final List<android.location.Address> addresses = geocoder.getFromLocation(
                            latitude,
                            Longitude,
                            1);

                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                            addressFragments.add(address.getAddressLine(i));
                        }
                    }
                } catch (Exception ex) {
                    if (ex instanceof IOException)
                        Timber.e("Address is not available: " + ex.getMessage());
                    else
                        Timber.e(ex);
                }
            }
        });
        thread.start();
        try {
            thread.join();
            String address = Linq.merge(addressFragments, new Linq.Merge<String>() {
                @Override
                public String run(String item1, String item2) {
                    return item1 + " " + item2;
                }
            });
            return address;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return " ";
        }
    }

}
