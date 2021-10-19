package com.varanegar.supervisor.tracking;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.varanegar.framework.base.VaranegarActivity;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.webapi.StatusType;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by A.Torabi on 6/19/2018.
 */

public class LastStatusConfigTabFragment extends Fragment {
    private PairedItems datePairedItems;
    private TrackingConfig tConfig;
    private ImageView dateImageView;
    private Switch lastPositionSwitch;
    private Switch lastEventSwitch;

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        tConfig = new TrackingConfig(getContext());
        datePairedItems = view.findViewById(R.id.date_paired_items);
        dateImageView = view.findViewById(R.id.date_image_view);
        lastPositionSwitch = view.findViewById(R.id.last_position_switch);
        lastEventSwitch = view.findViewById(R.id.last_event_switch);
        if (tConfig.getStatusDate() != null) {
            datePairedItems.setValue(DateHelper.toString(tConfig.getStatusDate(), DateFormat.Date, Locale.getDefault()));
        }

        dateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateHelper.showDatePicker(getVaranegarActvity(), null, new DateHelper.OnDateSelected() {
                    @Override
                    public void run(Calendar calendar) {
                        tConfig.setStatusDate(calendar.getTime());
                        datePairedItems.setValue(DateHelper.toString(calendar, DateFormat.Date));
                    }
                });
            }
        });


        StatusType defaultStatusType = tConfig.getStatusType();
        if (defaultStatusType == StatusType.Point) {
            lastPositionSwitch.setChecked(true);
            lastEventSwitch.setChecked(false);
        } else {
            lastPositionSwitch.setChecked(false);
            lastEventSwitch.setChecked(true);
        }

        lastPositionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                tConfig.setStatusType(b ? StatusType.Point : StatusType.Event);
                lastEventSwitch.setChecked(!b);
            }
        });


        lastEventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                tConfig.setStatusType(!b ? StatusType.Point : StatusType.Event);
                lastPositionSwitch.setChecked(!b);
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_last_point_config_tab_layout, container, false);
    }

    private AppCompatActivity getVaranegarActvity() {
        return (VaranegarActivity) getActivity();
    }
}
