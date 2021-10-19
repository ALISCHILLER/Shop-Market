package com.varanegar.vaslibrary.manager.locationmanager.viewmodel;

import com.varanegar.vaslibrary.webapi.tracking.TrackingRequestModel;

/**
 * Created by A.Torabi on 8/9/2017.
 */

public class OrderLocationViewModel extends EventLocationViewModel {
    public OrderActivityEventViewModel eventData;

    @Override
    public void prepareForTracking(TrackingRequestModel trackingRequestModel) {
        trackingRequestModel.orderEvent.add(this);
        PersonnelDailyActivityEventTypeId = EventTypeId.Order;
    }

    @Override
    public boolean IsImportant() {
        return true;
    }

}
