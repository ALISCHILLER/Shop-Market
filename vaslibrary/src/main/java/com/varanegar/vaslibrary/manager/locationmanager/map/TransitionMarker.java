package com.varanegar.vaslibrary.manager.locationmanager.map;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.Area;
import com.varanegar.vaslibrary.manager.RegionAreaPointManager;
import com.varanegar.vaslibrary.manager.TransitionType;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.EventLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.EventTypeId;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.TransitionEventLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.TransitionEventViewModel;

import java.util.Locale;

import timber.log.Timber;

/**
 * Created by A.Torabi on 6/9/2018.
 */

public class TransitionMarker extends TrackingMarker<TransitionEventLocationViewModel> {
    public TransitionMarker(@NonNull Activity activity, TransitionEventLocationViewModel locationModel) {
        super(activity, locationModel, true);

        TransitionEventViewModel eventData = locationModel.eventData;
        if (eventData.Transition.Region.equals(RegionAreaPointManager.VisitDayPath) && eventData.Transition.Type == TransitionType.Enter)
            locationModel.PersonnelDailyActivityEventTypeId = EventTypeId.EnterVisitDay;
        else if (eventData.Transition.Region.equals(RegionAreaPointManager.VisitDayPath) && eventData.Transition.Type == TransitionType.Exit)
            locationModel.PersonnelDailyActivityEventTypeId = EventTypeId.ExitVisitDay;
        else if (eventData.Transition.Region.equals(RegionAreaPointManager.RegionPath) && eventData.Transition.Type == TransitionType.Enter)
            locationModel.PersonnelDailyActivityEventTypeId = EventTypeId.EnterRegion;
        else if (eventData.Transition.Region.equals(RegionAreaPointManager.RegionPath) && eventData.Transition.Type == TransitionType.Exit)
            locationModel.PersonnelDailyActivityEventTypeId = EventTypeId.ExitRegion;
        else if (eventData.Transition.Region.equals(RegionAreaPointManager.CompanyPath) && eventData.Transition.Type == TransitionType.Enter)
            locationModel.PersonnelDailyActivityEventTypeId = EventTypeId.EnterCompany;
        else if (eventData.Transition.Region.equals(RegionAreaPointManager.CompanyPath) && eventData.Transition.Type == TransitionType.Exit)
            locationModel.PersonnelDailyActivityEventTypeId = EventTypeId.ExitCompany;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.marker_base_event, null);
        ImageView iconImageView = view.findViewById(R.id.icon_image_view);
        final TransitionEventLocationViewModel transitionEventLocationViewModel = getLocationViewModel();
        if (transitionEventLocationViewModel.PersonnelDailyActivityEventTypeId.equals(EventTypeId.EnterCompany))
            iconImageView.setImageResource(R.drawable.enter_company);
        else if (transitionEventLocationViewModel.PersonnelDailyActivityEventTypeId.equals(EventTypeId.ExitCompany))
            iconImageView.setImageResource(R.drawable.exit_company);
        else if (transitionEventLocationViewModel.PersonnelDailyActivityEventTypeId.equals(EventTypeId.EnterRegion))
            iconImageView.setImageResource(R.drawable.enter_region);
        else if (transitionEventLocationViewModel.PersonnelDailyActivityEventTypeId.equals(EventTypeId.ExitRegion))
            iconImageView.setImageResource(R.drawable.exit_region);
        else if (transitionEventLocationViewModel.PersonnelDailyActivityEventTypeId.equals(EventTypeId.EnterVisitDay))
            iconImageView.setImageResource(R.drawable.enter_path);
        else if (transitionEventLocationViewModel.PersonnelDailyActivityEventTypeId.equals(EventTypeId.ExitVisitDay))
            iconImageView.setImageResource(R.drawable.exit_path);
        TextView timeTextView = view.findViewById(R.id.time_text_view);
        timeTextView.setText(DateHelper.toString(transitionEventLocationViewModel.ActivityDate, DateFormat.Complete, Locale.getDefault()));
        RegionAreaPointManager manager = new RegionAreaPointManager(inflater.getContext());
        Area day = manager.getDayPath();
        if (day.pointIsInRegion(transitionEventLocationViewModel.Latitude,transitionEventLocationViewModel.Longitude))
            Timber.d(transitionEventLocationViewModel.Latitude + "," + transitionEventLocationViewModel.Longitude + " point is in path");
        else
            Timber.d(transitionEventLocationViewModel.Latitude + "," + transitionEventLocationViewModel.Longitude + " point is not in path");
        return view;
    }

    @Override
    public float zIndex() {
        return 3;
    }
}
