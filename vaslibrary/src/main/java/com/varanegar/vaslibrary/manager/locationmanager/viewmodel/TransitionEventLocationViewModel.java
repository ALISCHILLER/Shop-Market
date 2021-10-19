package com.varanegar.vaslibrary.manager.locationmanager.viewmodel;

import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.manager.RegionAreaPointManager;
import com.varanegar.vaslibrary.manager.TransitionType;
import com.varanegar.vaslibrary.webapi.tracking.TrackingRequestModel;

import java.util.Locale;

/**
 * Created by A.Torabi on 4/29/2018.
 */

public class TransitionEventLocationViewModel extends BaseEventLocationViewModel {


    public TransitionEventViewModel eventData;

    @Override
    public void prepareForTracking(TrackingRequestModel trackingRequestModel) {
        trackingRequestModel.pointEvent.add(this);
        if (eventData.Transition.Region.equals(RegionAreaPointManager.VisitDayPath) && eventData.Transition.Type == TransitionType.Enter)
            PersonnelDailyActivityEventTypeId = EventTypeId.EnterVisitDay;
        else if (eventData.Transition.Region.equals(RegionAreaPointManager.VisitDayPath) && eventData.Transition.Type == TransitionType.Exit)
            PersonnelDailyActivityEventTypeId = EventTypeId.ExitVisitDay;
        else if (eventData.Transition.Region.equals(RegionAreaPointManager.RegionPath) && eventData.Transition.Type == TransitionType.Enter)
            PersonnelDailyActivityEventTypeId = EventTypeId.EnterRegion;
        else if (eventData.Transition.Region.equals(RegionAreaPointManager.RegionPath) && eventData.Transition.Type == TransitionType.Exit)
            PersonnelDailyActivityEventTypeId = EventTypeId.ExitRegion;
        else if (eventData.Transition.Region.equals(RegionAreaPointManager.CompanyPath) && eventData.Transition.Type == TransitionType.Enter)
            PersonnelDailyActivityEventTypeId = EventTypeId.EnterCompany;
        else if (eventData.Transition.Region.equals(RegionAreaPointManager.CompanyPath) && eventData.Transition.Type == TransitionType.Exit)
            PersonnelDailyActivityEventTypeId = EventTypeId.ExitCompany;
    }

    @Override
    public boolean IsImportant() {
        return true;
    }
}
