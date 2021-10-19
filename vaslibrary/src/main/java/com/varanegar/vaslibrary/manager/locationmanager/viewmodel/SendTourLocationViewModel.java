package com.varanegar.vaslibrary.manager.locationmanager.viewmodel;

import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.webapi.tracking.TrackingRequestModel;

import java.util.Locale;

/**
 * Created by A.Torabi on 12/17/2017.
 */

public class SendTourLocationViewModel extends BaseEventLocationViewModel {

    public SendTourEventViewModel eventData;

    @Override
    public void prepareForTracking(TrackingRequestModel trackingRequestModel) {
        trackingRequestModel.pointEvent.add(this);
        PersonnelDailyActivityEventTypeId = EventTypeId.SendTour;
    }

    @Override
    public boolean IsImportant() {
        return true;
    }
}
