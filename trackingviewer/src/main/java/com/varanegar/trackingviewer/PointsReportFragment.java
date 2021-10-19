package com.varanegar.trackingviewer;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.SlidingDialog;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.model.TrackingLog;
import com.varanegar.vaslibrary.model.TrackingLogModel;
import com.varanegar.vaslibrary.model.location.Location;
import com.varanegar.vaslibrary.model.location.LocationModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A.Torabi on 10/22/2018.
 */

public class PointsReportFragment extends SlidingDialog {
    private TabLayout tabLayout;
    private SimpleReportAdapter<LocationModel> locationsReportAdapter;
    private SimpleReportAdapter<TrackingLogModel> logsReportAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.points_report_fragment, container, false);
        return view;
    }

    private void refresh(int position, View view, @Nullable final Bundle savedInstanceState) {
        final ReportView reportView = view.findViewById(R.id.report_view);
        if (position == 0) {
            if (TrackingReportFragment.getLocations() != null) {
                reportView.setVisibility(View.VISIBLE);
                locationsReportAdapter = new SimpleReportAdapter<LocationModel>(getVaranegarActvity(), LocationModel.class) {
                    @Override
                    public void bind(ReportColumns columns, LocationModel entity) {
                        bindRowNumber(columns);
                        columns.add(bind(entity, Location.DateAndTime, getString(R.string.date)).setSortable().setWeight(1.5f).setFrizzed());
                        columns.add(bind(entity, Location.Latitude, getString(R.string.latitude)).setDecimalFormat("##.#######"));
                        columns.add(bind(entity, Location.Longitude, getString(R.string.longitude)).setDecimalFormat("##.#######"));
                        columns.add(bind(entity, Location.Accuracy, getString(R.string.accuracy)).setSortable());
                        columns.add(bind(entity, Location.Tracking, getString(R.string.tracking)).setSortable().setWeight(0.8f));
                        columns.add(bind(entity, Location.IsSend, getString(R.string.is_sent)).setSortable().setWeight(0.8f));
                        columns.add(bind(entity, Location.TimeOffset, "Time offset").setSortable().setWeight(1));
                        columns.add(bind(entity, Location.EventTypeSimpleName, getString(R.string.event_type)).setSortable().setWeight(2));
                    }
                };
                locationsReportAdapter.create(TrackingReportFragment.getLocations(), savedInstanceState);
                reportView.setAdapter(locationsReportAdapter);
            } else
                reportView.setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.log_options_layout).setVisibility(View.VISIBLE);

            logsReportAdapter = new SimpleReportAdapter<TrackingLogModel>(getVaranegarActvity(), TrackingLogModel.class) {
                @Override
                public void bind(ReportColumns columns, TrackingLogModel entity) {
                    bindRowNumber(columns);
                    columns.add(bind(entity, TrackingLog.FATime, getString(R.string.time)).setSortable().setWeight(1.5f).setFrizzed());
                    columns.add(bind(entity, TrackingLog.EventType, getString(R.string.event_type)));
                    columns.add(bind(entity, TrackingLog.Level, "level"));
                    columns.add(bind(entity, TrackingLog.Description, getString(R.string.description)).setWeight(6f));
                }
            };


            final PairedItemsSpinner<String> logTypesPairedItemsSpinner = view.findViewById(R.id.log_type_spinner);
            List<String> logTypes = new ArrayList<>();
            logTypes.add("*");
            logTypes.add(LogType.WIFI_ON.toString());
            logTypes.add(LogType.WIFI_OFF.toString());
            logTypes.add("WIFI");
            logTypes.add(LogType.GPS_ON.toString());
            logTypes.add(LogType.GPS_OFF.toString());
            logTypes.add("GPS");
            logTypes.add(LogType.SUBMIT_POINT.toString());
            logTypes.add(LogType.LOCATION_SETTINGS.toString());
            logTypes.add(LogType.APP_OPEN.toString());
            logTypes.add(LogType.LICENSE.toString());
            logTypes.add(LogType.BATTERY.toString());
            logTypes.add(LogType.ORDER_EVENT.toString());
            logTypes.add(LogType.START_WAIT.toString());
            logTypes.add(LogType.END_WAIT.toString());
            logTypes.add("WAIT");
            logTypes.add(LogType.MOCK_PROVIDER.toString());
            logTypes.add(LogType.INACCURATE_POINT.toString());
            logTypes.add(LogType.TRANSITION.toString());
            logTypes.add(LogType.POINT.toString());
            logTypes.add(LogType.PROVIDER.toString());
            logTypes.add(LogType.DATA_OFF.toString());
            logTypes.add(LogType.DATA_ON.toString());
            logTypes.add("DATA");
            logTypes.add(LogType.CONFIG.toString());
            logTypes.add(LogType.ACTIVITY.toString());
            logTypesPairedItemsSpinner.setup(getChildFragmentManager(), logTypes, null);


            final PairedItemsSpinner<String> logLevelsPairedItemsSpinner = view.findViewById(R.id.log_level_spinner);
            List<String> logLevels = new ArrayList<>();
            logLevels.add("*");
            logLevels.add(LogLevel.Info.toString());
            logLevels.add(LogLevel.Warning.toString());
            logLevels.add(LogLevel.Error.toString());
            logLevelsPairedItemsSpinner.setup(getChildFragmentManager(), logLevels, null);


            logTypesPairedItemsSpinner.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener<String>() {
                @Override
                public void onItemSelected(int position, String item) {
                    String level = logLevelsPairedItemsSpinner.getSelectedItem();
                    logsReportAdapter.create(new TrackingLogManager(getContext()).getLogs(item, level , TrackingReportFragment.startDate , TrackingReportFragment.endDate ), savedInstanceState);
                    reportView.setAdapter(logsReportAdapter);
                }
            });


            logLevelsPairedItemsSpinner.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener<String>() {
                @Override
                public void onItemSelected(int position, String item) {
                    String type = logTypesPairedItemsSpinner.getSelectedItem();
                    logsReportAdapter.create(new TrackingLogManager(getContext()).getLogs(type, item, TrackingReportFragment.startDate , TrackingReportFragment.endDate ), savedInstanceState);
                    reportView.setAdapter(logsReportAdapter);
                }
            });


            logsReportAdapter.create(new TrackingLogManager(getContext()).getLogs(null, null, TrackingReportFragment.startDate , TrackingReportFragment.endDate ), savedInstanceState);
            reportView.setAdapter(logsReportAdapter);

        }
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        Integer count = VaranegarApplication.getInstance().getDbHandler().getIntegerSingle(new Query().from(Location.LocationTbl).select(Projection.countRows()));
        ((TextView) view.findViewById(R.id.all_points_text_view)).setText(count == null ? "0" : String.valueOf(count));

        tabLayout = view.findViewById(R.id.tab_layout);
        refresh(0, view, savedInstanceState);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                refresh(tab.getPosition(), view, savedInstanceState);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}
