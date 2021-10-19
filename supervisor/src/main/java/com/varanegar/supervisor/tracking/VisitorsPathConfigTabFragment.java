package com.varanegar.supervisor.tracking;

import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.varanegar.framework.base.VaranegarActivity;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.selectionlistadapter.BaseSelectionRecyclerAdapter;
import com.varanegar.framework.util.recycler.selectionlistadapter.SelectionRecyclerAdapter;
import com.varanegar.supervisor.R;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.EventTypeId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Created by A.Torabi on 6/19/2018.
 */

public class VisitorsPathConfigTabFragment extends Fragment {
    private PairedItems datePairedItems;
    private ImageView dateImageView;
    private TrackingConfig tConfig;
    private PairedItems fromTimePairedItems;
    private PairedItems toTimePairedItems;
    private ImageView fromTimeImageView;
    private ImageView toTimeImageView;
    private TextView waitTimeTextView;

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        tConfig = new TrackingConfig(getContext());


        /// Activities

        BaseRecyclerView activitiesRecyclerView = view.findViewById(R.id.activities_recycler_view);
        final SelectionRecyclerAdapter<PointType> activitiesAdapter = new SelectionRecyclerAdapter<>(getVaranegarActvity(), getActivities(), true);
        activitiesAdapter.setOnItemSelectedListener(new BaseSelectionRecyclerAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, boolean selected) {
                if (selected)
                    tConfig.setTrackingActivityType(activitiesAdapter.get(position));
                else
                    tConfig.removeTrackingActivityType(activitiesAdapter.get(position));
            }
        });
        Set<UUID> activityTypeIds = tConfig.getTrackingActivityTypes();
        for (final UUID id :
                activityTypeIds) {
            activitiesAdapter.select(new Linq.Criteria<PointType>() {
                @Override
                public boolean run(PointType item) {
                    return item.TypeId.equals(id);
                }
            });
        }
        activitiesRecyclerView.setAdapter(activitiesAdapter);
        CheckBox allActivitiesCheckBox = view.findViewById(R.id.all_activites_check_box);
        if (activitiesAdapter.getSelectedItems().size() == activitiesAdapter.getItemCount())
            allActivitiesCheckBox.setChecked(true);
        allActivitiesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    for (PointType pointType :
                            activitiesAdapter.getItems()) {
                        tConfig.setTrackingActivityType(pointType);
                    }
                    activitiesAdapter.selectAll();
                } else {
                    for (PointType pointType :
                            activitiesAdapter.getItems()) {
                        tConfig.removeTrackingActivityType(pointType);
                    }
                    activitiesAdapter.deselectAll();
                }
            }
        });


        /// Events

        BaseRecyclerView eventsRecyclerView = view.findViewById(R.id.events_recycler_view);
        final SelectionRecyclerAdapter<PointType> eventsAdapter = new SelectionRecyclerAdapter<>(getVaranegarActvity(), getDeviceEvents(), true);
        eventsAdapter.setOnItemSelectedListener(new BaseSelectionRecyclerAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, boolean selected) {
                if (selected)
                    tConfig.setTrackingActivityType(eventsAdapter.get(position));
                else
                    tConfig.removeTrackingActivityType(eventsAdapter.get(position));
            }
        });
        for (final UUID id :
                activityTypeIds) {
            eventsAdapter.select(new Linq.Criteria<PointType>() {
                @Override
                public boolean run(PointType item) {
                    return item.TypeId.equals(id);
                }
            });
        }
        eventsRecyclerView.setAdapter(eventsAdapter);
        CheckBox allEventsCheckBox = view.findViewById(R.id.all_events_check_box);
        if (eventsAdapter.getSelectedItems().size() == eventsAdapter.getItemCount())
            allEventsCheckBox.setChecked(true);
        allEventsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    for (PointType pointType :
                            eventsAdapter.getItems()) {
                        tConfig.setTrackingActivityType(pointType);
                    }
                    eventsAdapter.selectAll();
                } else {
                    for (PointType pointType :
                            eventsAdapter.getItems()) {
                        tConfig.removeTrackingActivityType(pointType);
                    }
                    eventsAdapter.deselectAll();
                }
            }
        });


        ////

        datePairedItems = view.findViewById(R.id.date_paired_items);
        fromTimePairedItems = view.findViewById(R.id.from_time_paired_items);
        toTimePairedItems = view.findViewById(R.id.to_time_paired_items);
        dateImageView = view.findViewById(R.id.date_image_view);
        fromTimeImageView = view.findViewById(R.id.from_time_image_view);
        toTimeImageView = view.findViewById(R.id.to_time_image_view);

        if (tConfig.getTrackingDate() != null) {
            datePairedItems.setValue(DateHelper.toString(tConfig.getTrackingDate(), DateFormat.Date, Locale.getDefault()));
        }

        if (tConfig.getFromTime() != null)
            fromTimePairedItems.setValue(tConfig.getFromTime());


        if (tConfig.getToTime() != null)
            toTimePairedItems.setValue(tConfig.getToTime());



        dateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateHelper.showDatePicker(getVaranegarActvity(), null, new DateHelper.OnDateSelected() {
                    @Override
                    public void run(Calendar calendar) {
                        tConfig.setTrackingDate(calendar.getTime());
                        datePairedItems.setValue(DateHelper.toString(calendar, DateFormat.Date));
                    }
                });
            }
        });




        fromTimeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        tConfig.setFromTime(i, i1);
                        fromTimePairedItems.setValue(tConfig.getFromTime());
                    }
                }, 0, 0, true);
                dialog.show();
            }
        });


        toTimeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        tConfig.setToTime(i, i1);
                        toTimePairedItems.setValue(tConfig.getToTime());
                    }
                }, 0, 0, true);
                dialog.show();
            }
        });

        waitTimeTextView = view.findViewById(R.id.wait_time_text_view);
        waitTimeTextView.setText(intToHour(tConfig.getWaitTime()));

        SeekBar waitSeekBar = view.findViewById(R.id.wait_time_seek_bar);
        waitSeekBar.setProgress(tConfig.getWaitTime());
        waitSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tConfig.setWaitTime(i);
                waitTimeTextView.setText(intToHour(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    String intToHour(int i) {
        if (i < 60)
            return i + " " + getString(R.string.minutes);
        else if (i % 60 == 0)
            return i / 60 + " " + getString(R.string.hour);
        else
            return i / 60 + " " + getString(R.string.hour) + " " + i % 60 + " " + getString(R.string.minutes);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracking_config_tab_layout, container, false);
    }


    private List<PointType> getDeviceEvents() {
        PointType wifiOff = new PointType(EventTypeId.WifiOff, getString(R.string.wifi_off));
        PointType wifiOn = new PointType(EventTypeId.WifiOn, getString(R.string.wifi_on));
        PointType dataOff = new PointType(EventTypeId.MobileDataOff, getString(R.string.data_off));
        PointType dataOn = new PointType(EventTypeId.MobileDataOn, getString(R.string.data_on));
        PointType gpsOff = new PointType(EventTypeId.GpsOff, getString(R.string.gps_off));
        PointType gpsOn = new PointType(EventTypeId.GpsOn, getString(R.string.gps_on));
        PointType batteryLow = new PointType(EventTypeId.BatteryLow, getString(R.string.battery_low));
        List<PointType> pointTypes = new ArrayList<>();
        Collections.addAll(pointTypes, wifiOff, wifiOn, dataOff, dataOn, gpsOff, gpsOn, batteryLow);
        return pointTypes;
    }

    private List<PointType> getActivities() {
        PointType order = new PointType(EventTypeId.Order, getString(R.string.order));
        PointType lackOfOrder = new PointType(EventTypeId.LackOfOrder, getString(R.string.lack_of_order));
        PointType lackOfVisit = new PointType(EventTypeId.LackOfVisit, getString(R.string.lack_of_visit));
        PointType sendTour = new PointType(EventTypeId.SendTour, getString(R.string.send_tour));
        PointType startTour = new PointType(EventTypeId.StartTour, getString(R.string.start_tour));
        PointType wait = new PointType(EventTypeId.Wait, getString(R.string.wait));
        PointType enterCompany = new PointType(EventTypeId.EnterCompany, getString(R.string.enter_company));
        PointType exitCompany = new PointType(EventTypeId.ExitCompany, getString(R.string.exit_company));
        PointType enterRegion = new PointType(EventTypeId.EnterRegion, getString(R.string.enter_region));
        PointType exitRegion = new PointType(EventTypeId.ExitRegion, getString(R.string.exit_region));
        PointType enterVisitDay = new PointType(EventTypeId.EnterVisitDay, getString(R.string.enter_visit_day));
        PointType exitVisitDay = new PointType(EventTypeId.ExitVisitDay, getString(R.string.exit_visit_day));
        PointType openApp = new PointType(EventTypeId.OpenApp, getString(R.string.open_app));
        List<PointType> pointTypes = new ArrayList<>();
        Collections.addAll(pointTypes,
                order, lackOfOrder, lackOfVisit, sendTour, startTour, wait, enterCompany, exitCompany, enterRegion,
                exitRegion, enterVisitDay, exitVisitDay, openApp);
        return pointTypes;
    }

    class PointType {
        public PointType(UUID typeId, String name) {
            this.TypeId = typeId;
            this.Name = name;
        }

        public UUID TypeId;
        public String Name;

        @Override
        public String toString() {
            return Name;
        }
    }

    private AppCompatActivity getVaranegarActvity() {
        return (VaranegarActivity) getActivity();
    }
}
