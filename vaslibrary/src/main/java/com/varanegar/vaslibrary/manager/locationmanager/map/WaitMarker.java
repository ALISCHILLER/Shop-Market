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
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.WaitLocationViewModel;

import java.util.Date;
import java.util.Locale;

/**
 * Created by A.Torabi on 6/9/2018.
 */

public class WaitMarker extends TrackingMarker<WaitLocationViewModel> {
    public WaitMarker(@NonNull Activity activity, WaitLocationViewModel locationModel) {
        super(activity, locationModel, true);
    }

    @Override
    public View onCreateInfoView(@NonNull LayoutInflater inflater) {
        WaitLocationViewModel waitLocationViewModel = getLocationViewModel();
        if (waitLocationViewModel.Desc != null)
            return super.onCreateInfoView(inflater);
        View view = inflater.inflate(R.layout.info_view_layout, null);
        TextView titleTextView = view.findViewById(R.id.title_text_view);
        if (waitLocationViewModel.eventData != null) {
            Date endTime = waitLocationViewModel.eventData.EndTime;
            endTime = endTime == null ? new Date() : endTime;
            Date startTime = waitLocationViewModel.eventData.StartTime;
            long timeSpan = (endTime.getTime() - startTime.getTime()) / 1000;
            titleTextView.setText(getActivity().getString(R.string.halt_for) + " " + DateHelper.getTimeSpanString(timeSpan) + "  " + getActivity().getString(R.string.start_time) + ": " + DateHelper.toString(waitLocationViewModel.eventData.StartTime, DateFormat.Complete, Locale.getDefault()));
        }
        return view;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.marker_base_event, null);
        ImageView iconImageView = view.findViewById(R.id.icon_image_view);
        iconImageView.setImageResource(R.drawable.wait);
        WaitLocationViewModel waitLocationViewModel = getLocationViewModel();
        TextView timeTextView = view.findViewById(R.id.time_text_view);
        timeTextView.setText(DateHelper.toString(waitLocationViewModel.ActivityDate, DateFormat.Time, Locale.getDefault()));
        return view;
    }

    @Override
    public float zIndex() {
        return 3;
    }
}
