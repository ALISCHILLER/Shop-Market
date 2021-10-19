package com.varanegar.vaslibrary.manager.locationmanager.viewmodel;

import com.varanegar.vaslibrary.webapi.tracking.TrackingRequestModel;

/**
 * Created by A.Jafarzadeh on 11/8/2017.
 */

public class DeleteOperationLocationViewModel extends BaseEventLocationViewModel {

    public DeleteEventViewModel eventData;

    @Override
    public void prepareForTracking(TrackingRequestModel trackingRequestModel) {
        trackingRequestModel.pointEvent.add(this);
        PersonnelDailyActivityEventTypeId = EventTypeId.DeleteOperation;
    }

    @Override
    public boolean IsImportant() {
        return true;
    }
}
