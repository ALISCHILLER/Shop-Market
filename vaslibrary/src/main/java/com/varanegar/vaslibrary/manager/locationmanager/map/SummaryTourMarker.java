package com.varanegar.vaslibrary.manager.locationmanager.map;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.SummaryTourLocationViewModel;

/**
 * Created by A.Torabi on 6/9/2018.
 */

public class SummaryTourMarker extends TrackingMarker<SummaryTourLocationViewModel> {

    public SummaryTourMarker(@NonNull Activity activity, SummaryTourLocationViewModel locationModel) {
        super(activity, locationModel, true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.marker_summary_tour_event, null);
        ImageView greenIconImageView = view.findViewById(R.id.green_icon_image_view);
        ImageView orangeIconImageView = view.findViewById(R.id.orange_icon_image_view);
        ImageView redIconImageView = view.findViewById(R.id.red_icon_image_view);
        SummaryTourLocationViewModel locationViewModel = getLocationViewModel();
        if (locationViewModel.SubType == 0) {
            greenIconImageView.setVisibility(View.VISIBLE);
            orangeIconImageView.setVisibility(View.GONE);
            redIconImageView.setVisibility(View.GONE);
        } else if (locationViewModel.SubType == 1) {
            greenIconImageView.setVisibility(View.GONE);
            orangeIconImageView.setVisibility(View.VISIBLE);
            redIconImageView.setVisibility(View.GONE);
        } else if (locationViewModel.SubType == 2) {
            greenIconImageView.setVisibility(View.GONE);
            orangeIconImageView.setVisibility(View.GONE);
            redIconImageView.setVisibility(View.VISIBLE);
        }
        TextView nameTextView = view.findViewById(R.id.name_text_view);
        nameTextView.setText(locationViewModel.Lable);
        return view;
    }

    @Override
    public float zIndex() {
        return 10;
    }
}
