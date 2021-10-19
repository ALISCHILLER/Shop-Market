package com.varanegar.vaslibrary.manager.locationmanager.viewmodel;

import com.varanegar.vaslibrary.webapi.tracking.TrackingRequestModel;

/**
 * Created by A.Torabi on 8/9/2017.
 */

public class EditOrderLocationViewModel extends EventLocationViewModel {
    public EditOrderActivityEventViewModel eventData;

    @Override
    public void prepareForTracking(TrackingRequestModel trackingRequestModel) {
        trackingRequestModel.pointEvent.add(this);
        PersonnelDailyActivityEventTypeId = EventTypeId.EditOrder;
    }

    @Override
    public boolean IsImportant() {
        return true;
    }

}
