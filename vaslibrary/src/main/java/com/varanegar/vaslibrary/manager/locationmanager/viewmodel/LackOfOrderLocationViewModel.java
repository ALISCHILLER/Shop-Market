package com.varanegar.vaslibrary.manager.locationmanager.viewmodel;

import com.varanegar.vaslibrary.webapi.tracking.TrackingRequestModel;

/**
 * Created by A.Torabi on 8/9/2017.
 */

public class LackOfOrderLocationViewModel extends EventLocationViewModel {
    public LackOfOrderActivityEventViewModel eventData;

    @Override
    public void prepareForTracking(TrackingRequestModel trackingRequestModel) {
        PersonnelDailyActivityEventTypeId = EventTypeId.LackOfOrder;
        trackingRequestModel.lackOfOrderEvent.add(this);
    }

    @Override
    public boolean IsImportant() {
        return true;
    }
}
