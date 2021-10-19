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
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.LackOfOrderLocationViewModel;

import java.util.Locale;

/**
 * Created by A.Torabi on 6/9/2018.
 */

public class LackOfOrderMarker extends TrackingMarker<LackOfOrderLocationViewModel> {

    public LackOfOrderMarker(@NonNull Activity activity, LackOfOrderLocationViewModel locationModel) {
        super(activity, locationModel, true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.marker_base_event, null);
        ImageView iconImageView = view.findViewById(R.id.icon_image_view);
        iconImageView.setImageResource(R.drawable.lackorder);
        LackOfOrderLocationViewModel lackOfOrderLocationViewModel = getLocationViewModel();
        TextView timeTextView = view.findViewById(R.id.time_text_view);
        timeTextView.setText(DateHelper.toString(lackOfOrderLocationViewModel.ActivityDate, DateFormat.Complete, Locale.getDefault()));
        return view;
    }


    @Override
    public View onCreateInfoView(@NonNull LayoutInflater inflater) {
        LackOfOrderLocationViewModel locationViewModel = getLocationViewModel();
        if (locationViewModel != null && locationViewModel.Desc != null) {
            View view = inflater.inflate(R.layout.info_view_layout, null);
            TextView titleTextView = view.findViewById(R.id.title_text_view);
            titleTextView.setText(Html.fromHtml(locationViewModel.Desc));
            return view;
        } else {
            View view = inflater.inflate(R.layout.tracking_lack_of_rder_info_view_layout, null);
            if (locationViewModel.eventData != null) {
                TextView timeTextView = view.findViewById(R.id.time_text_view);
                timeTextView.setText(locationViewModel.eventData.PTime);

                TextView customerCodeTextView = view.findViewById(R.id.customer_code_text_view);
                customerCodeTextView.setText(locationViewModel.eventData.CustomerCode);

                TextView customerNameTextView = view.findViewById(R.id.customer_name_text_view);
                customerNameTextView.setText(locationViewModel.eventData.CustomerName);

                TextView customerAddressTextView = view.findViewById(R.id.customer_address_text_view);
                customerAddressTextView.setText(locationViewModel.eventData.Address);
            }
            return view;
        }
    }

    @Override
    public float zIndex() {
        return 4;
    }

}
