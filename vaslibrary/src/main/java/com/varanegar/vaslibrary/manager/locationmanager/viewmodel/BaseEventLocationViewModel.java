package com.varanegar.vaslibrary.manager.locationmanager.viewmodel;

import com.varanegar.vaslibrary.manager.locationmanager.BaseLocationViewModel;
import com.varanegar.vaslibrary.webapi.tracking.TrackingRequestModel;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 11/13/2017.
 */

public abstract class BaseEventLocationViewModel extends BaseLocationViewModel {
    public UUID PersonnelDailyActivityEventTypeId;

    public abstract void prepareForTracking(TrackingRequestModel trackingRequestModel);

    public abstract boolean IsImportant();
}
