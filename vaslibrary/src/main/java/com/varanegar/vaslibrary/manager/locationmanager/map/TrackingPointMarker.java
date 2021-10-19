package com.varanegar.vaslibrary.manager.locationmanager.map;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.locationmanager.BaseLocationViewModel;

import java.util.Locale;

/**
 * Created by A.Torabi on 6/9/2018.
 */

public class TrackingPointMarker extends TrackingMarker<BaseLocationViewModel> {

    private final String label;
    PointType pointType;
    private TextView infoTextView;

    public TrackingPointMarker(@NonNull Activity activity, BaseLocationViewModel locationModel, PointType pointType, String label) {
        super(activity, locationModel, false);
        this.pointType = pointType;
        this.label = label;
    }

    public TrackingPointMarker(@NonNull Activity activity, BaseLocationViewModel locationModel, PointType pointType) {
        this(activity, locationModel, pointType, null);
    }

    @Override
    public View onCreateInfoView(@NonNull LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.info_view_layout, null);
        BaseLocationViewModel locationViewModel = getLocationViewModel();
        TextView titleTextView = view.findViewById(R.id.title_text_view);
        String time = inflater.getContext().getString(R.string.time);
        String location = inflater.getContext().getString(R.string.location);
        if (locationViewModel.Desc != null)
            titleTextView.setText(Html.fromHtml(locationViewModel.Desc));
        else if (locationViewModel.ActivityDate != null) {
            titleTextView.setText(time + ": " + DateHelper.toString(locationViewModel.ActivityDate, DateFormat.Complete, Locale.getDefault()) + "\n"
                    + location + ": " + locationViewModel.Latitude + " " + locationViewModel.Longitude);
        } else {
            titleTextView.setText(location + ": " + locationViewModel.Latitude + "," + locationViewModel.Longitude);
        }
        return view;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.marker_tracking_point, null);
        ImageView iconImageView = view.findViewById(R.id.icon_image_view);
        TextView labelTextView = view.findViewById(R.id.label_text_view);
        if (label != null && !label.isEmpty())
            labelTextView.setText(label);
        else
            labelTextView.setVisibility(View.GONE);
        if (pointType == PointType.Start)
            iconImageView.setImageResource(R.drawable.start);
        else if (pointType == PointType.End)
            iconImageView.setImageResource(R.drawable.end);
        else
            iconImageView.setImageResource(R.drawable.point);
        return view;
    }

    @Override
    public float zIndex() {
        return 1;
    }

    public enum PointType {
        Normal,
        Start,
        End
    }
}

