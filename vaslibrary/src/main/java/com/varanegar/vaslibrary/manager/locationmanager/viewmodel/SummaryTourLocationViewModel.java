package com.varanegar.vaslibrary.manager.locationmanager.viewmodel;

import com.varanegar.vaslibrary.webapi.tracking.TrackingRequestModel;

/**
 * Created by A.Torabi on 8/9/2017.
 */

public class SummaryTourLocationViewModel extends EventLocationViewModel {
    public SummaryToutEventViewModel eventData;

    @Override
    public void prepareForTracking(TrackingRequestModel trackingRequestModel) {
        PersonnelDailyActivityEventTypeId = EventTypeId.SummaryTour;
    }

    @Override
    public boolean IsImportant() {
        return true;
    }

}
