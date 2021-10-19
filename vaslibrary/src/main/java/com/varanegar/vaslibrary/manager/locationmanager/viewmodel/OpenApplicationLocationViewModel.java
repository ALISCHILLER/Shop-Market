package com.varanegar.vaslibrary.manager.locationmanager.viewmodel;

import com.varanegar.vaslibrary.webapi.tracking.TrackingRequestModel;

/**
 * Created by A.Torabi on 5/14/2018.
 */

public class OpenApplicationLocationViewModel extends BaseEventLocationViewModel {
    public DeviceEventViewModel eventData;

    @Override
    public void prepareForTracking(TrackingRequestModel trackingRequestModel) {
        trackingRequestModel.pointEvent.add(this);
        PersonnelDailyActivityEventTypeId = EventTypeId.OpenApp;
    }

    @Override
    public boolean IsImportant() {
        return true;
    }
}
