package com.varanegar.vaslibrary.manager.locationmanager.map;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.StartTourLocationViewModel;

import java.util.Locale;

/**
 * Created by A.Torabi on 6/9/2018.
 */

public class StartTourMarker extends TrackingMarker<StartTourLocationViewModel> {
    public StartTourMarker(@NonNull Activity activity, StartTourLocationViewModel locationModel) {
        super(activity, locationModel, true);
    }

    @Override
    public View onCreateInfoView(@NonNull LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.info_view_layout, null);
        StartTourLocationViewModel startTourLocationViewModel = getLocationViewModel();
        TextView titleTextView = view.findViewById(R.id.title_text_view);
        titleTextView.setText(String.valueOf(startTourLocationViewModel.eventData.TourNo));
        return view;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.marker_base_event, null);
        ImageView iconImageView = view.findViewById(R.id.icon_image_view);
        iconImageView.setImageResource(R.drawable.opentour);
        StartTourLocationViewModel startTourLocationViewModel = getLocationViewModel();
        TextView timeTextView = view.findViewById(R.id.time_text_view);
        timeTextView.setText(DateHelper.toString(startTourLocationViewModel.ActivityDate, DateFormat.Complete, Locale.getDefault()));
        return view;
    }

    @Override
    public float zIndex() {
        return 3;
    }
}
