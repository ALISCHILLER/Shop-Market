package com.varanegar.vaslibrary.manager.locationmanager.map;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.locationmanager.BaseLocationViewModel;

import java.util.Locale;

/**
 * Created by A.Torabi on 6/9/2018.
 */

public abstract class TrackingMarker<T extends BaseLocationViewModel> extends CustomMarker {

    private final boolean isEvent;

    public T getLocationViewModel() {
        return location;
    }

    private final T location;

    public TrackingMarker(@NonNull Activity activity, T locationModel, boolean isEvent) {
        super(activity);
        this.location = locationModel;
        this.isEvent = isEvent;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater) {
        return null;
    }

    @Override
    public View onCreateInfoView(@NonNull LayoutInflater inflater) {
        BaseLocationViewModel locationViewModel = getLocationViewModel();
        View view = inflater.inflate(R.layout.info_view_layout, null);
        TextView titleTextView = view.findViewById(R.id.title_text_view);
        if (locationViewModel != null && locationViewModel.Desc != null) {
            titleTextView.setText(Html.fromHtml(locationViewModel.Desc));
        } else {
            String time = inflater.getContext().getString(R.string.time);
            String location = inflater.getContext().getString(R.string.location);
            if (locationViewModel.ActivityDate != null) {
                titleTextView.setText(time + ": " + DateHelper.toString(locationViewModel.ActivityDate, DateFormat.Time, Locale.getDefault()) + "\n"
                        + location + ": " + locationViewModel.Latitude + " " + locationViewModel.Longitude);
            } else {
                titleTextView.setText(location + ": " + locationViewModel.Latitude + " " + locationViewModel.Longitude);
            }
        }
        return view;
    }

    public boolean isEvent() {
        return isEvent;
    }
}
