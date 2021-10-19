package com.varanegar.vaslibrary.manager.locationmanager.viewmodel;

import com.varanegar.vaslibrary.webapi.tracking.TrackingRequestModel;

import java.util.UUID;

/**
 * Created by A.Torabi on 8/9/2017.
 */

public class LackOfVisitLocationViewModel extends EventLocationViewModel {
    public LackOfVisitLocationViewModel() {
        eventData = new LackOfVisitActivityEventViewModel();
    }

    public LackOfVisitActivityEventViewModel eventData;

    @Override
    public void prepareForTracking(TrackingRequestModel trackingRequestModel) {
        PersonnelDailyActivityEventTypeId = EventTypeId.LackOfVisit;
        trackingRequestModel.lackOfVisitEvent.add(this);
    }

    @Override
    public boolean IsImportant() {
        return true;
    }

    public class LackOfVisitActivityEventViewModel {
        public UUID CustomerId;
        public String CustomerName;
        public String CustomerCode;
        public String StoreName;
        public String Phone;
        public String Address;
        public String PTime;
        public String Time;
        public String Description;
        public boolean IsInVisitDayPath;
    }
}
