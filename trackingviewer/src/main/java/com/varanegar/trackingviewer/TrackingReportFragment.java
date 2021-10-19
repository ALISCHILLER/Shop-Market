package com.varanegar.trackingviewer;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TimePicker;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.varanegar.framework.base.VaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.base.BackupManager;
import com.varanegar.vaslibrary.manager.Area;
import com.varanegar.vaslibrary.manager.RegionAreaPointManager;
import com.varanegar.vaslibrary.manager.locationmanager.BaseLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;
import com.varanegar.vaslibrary.manager.locationmanager.map.BatteryLowMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.GPSOffMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.GPSOnMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.LackOfOrderMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.LackOfVisitMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.MapHelper;
import com.varanegar.vaslibrary.manager.locationmanager.map.OrderMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.SendTourMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.StartTourMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.TrackingMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.TrackingPointMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.TransitionMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.WaitMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.WifiOffMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.WifiOnMarker;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.BatteryLowLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.GpsProviderOffLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.GpsProviderOnLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.LackOfOrderLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.LackOfVisitLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.OrderLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.SendTourLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.StartTourLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.TransitionEventLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.WaitLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.WifiOffLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.WifiOnLocationViewModel;
import com.varanegar.vaslibrary.model.location.LocationModel;
import com.varanegar.vaslibrary.ui.dialog.ImportDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 10/22/2018.
 */

public class TrackingReportFragment extends VaranegarFragment {
    private MapView mMapView;
    private GoogleMap googleMap;
    Geocoder geocoder;
    MapHelper trackingMapHelper;
    public static Date startDate;
    public static Date endDate;
    private PairedItems startDatePairedItems;
    private PairedItems endDatePairedItems;
    private PairedItems startTimePairedItems;
    private PairedItems endTimePairedItems;
    private static List<LocationModel> locations;
    private Switch filterSwitch;
    private List<UUID> outliers;

    public static List<LocationModel> getLocations() {
        return locations;
    }

    private void smoothePoints() {
        outliers = new ArrayList<>();
        mainAccuracy = 0;
        variance = -1;
        timeStamp = 0;
        lat = 0;
        lng = 0;
        for (LocationModel locationModel :
                locations) {
            callman(locationModel.UniqueId, locationModel.Latitude, locationModel.Longitude, locationModel.Accuracy,locationModel.Speed, locationModel.Date.getTime());
            locationModel.Latitude = lat;
            locationModel.Longitude = lng;
        }
        for (final UUID outLier :
                outliers) {
            Linq.removeFirst(locations, new Linq.Criteria<LocationModel>() {
                @Override
                public boolean run(LocationModel item) {
                    return item.UniqueId.equals(outLier);
                }
            });
        }
    }

    float mainAccuracy = 0;
    float variance = -1;
    long timeStamp = 0;
    double lat;
    double lng;

    private void callman(UUID pointId, double newLat, double newLng, float accuracy, float speed, long timeStamp) {
        if (accuracy < mainAccuracy)
            accuracy = mainAccuracy;
        if (variance < 0) {
            lat = newLat;
            lng = newLng;
            variance = accuracy * accuracy;
        } else {
            long TimeInc_milliseconds = timeStamp - this.timeStamp;
            if (TimeInc_milliseconds > 0) {
                variance += TimeInc_milliseconds * speed * speed / 1000;
                this.timeStamp = timeStamp;
            }
            float K = variance / (variance + accuracy * accuracy);
            lat += K * (newLat - lat);
            lng += K * (newLng - lng);
            if (distance(lat, lng, newLat, newLng) > 200)
                outliers.add(pointId);
            variance = (1 - K) * variance;
        }
    }

    private float distance(double lat1, double lng1, double lat2, double lng2) {
        Location loc1 = new Location("location1");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lng1);
        Location loc2 = new Location("location2");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lng2);
        return loc2.distanceTo(loc1);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        Calendar from = Calendar.getInstance();
        from.set(Calendar.HOUR_OF_DAY, 0);
        from.set(Calendar.MINUTE, 0);
        from.set(Calendar.SECOND, 0);
        startDate = from.getTime();
        Calendar to = (Calendar) from.clone();
        to.add(Calendar.DAY_OF_MONTH, 1);
        endDate = to.getTime();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getVaranegarActvity().setDrawerLayout(R.layout.tracking_report_drawer_layout);

        startDatePairedItems = view.findViewById(R.id.start_date_item);
        endDatePairedItems = view.findViewById(R.id.end_date_item);
        view.findViewById(R.id.start_calendar_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateHelper.showDatePicker(getVaranegarActvity(), null, new DateHelper.OnDateSelected() {
                    @Override
                    public void run(Calendar calendar) {
                        if (calendar.getTime().after(new Date())) {
                            showErrorDialog(getString(R.string.date_could_not_be_after_now));
                            return;
                        }
                        if (endDate != null && endDate.before(calendar.getTime())) {
                            showErrorDialog(getString(R.string.end_date_could_not_be_before_start_date));
                            return;
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        startDate = calendar.getTime();
                        startDatePairedItems.setValue(DateHelper.toString(startDate, DateFormat.Date, Locale.getDefault()));
                    }
                });
            }
        });
        view.findViewById(R.id.end_calendar_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateHelper.showDatePicker(getVaranegarActvity(), null, new DateHelper.OnDateSelected() {
                    @Override
                    public void run(Calendar calendar) {
                        if (calendar.getTime().after(new Date())) {
                            showErrorDialog(getString(R.string.date_could_not_be_after_now));
                            return;
                        }
                        if (startDate != null && startDate.after(calendar.getTime())) {
                            showErrorDialog(getString(R.string.start_date_could_not_be_after_end_date));
                            return;
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 55);
                        calendar.set(Calendar.SECOND, 55);
                        endDate = calendar.getTime();
                        endDatePairedItems.setValue(DateHelper.toString(endDate, DateFormat.Date, Locale.getDefault()));
                    }
                });
            }
        });


        startTimePairedItems = view.findViewById(R.id.start_time_item);
        endTimePairedItems = view.findViewById(R.id.end_time_item);
        view.findViewById(R.id.start_time_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startDate != null) {

                    TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(startDate);
                            cal.set(Calendar.HOUR_OF_DAY, i);
                            cal.set(Calendar.MINUTE, i1);
                            startDate = cal.getTime();
                            startTimePairedItems.setValue(DateHelper.toString(startDate, DateFormat.Time, Locale.getDefault()));
                        }
                    }, 0, 0, true);
                    dialog.show();
                }
            }
        });
        view.findViewById(R.id.end_time_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (endDate != null) {
                    TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(endDate);
                            cal.set(Calendar.HOUR_OF_DAY, i);
                            cal.set(Calendar.MINUTE, i1);
                            endDate = cal.getTime();
                            endTimePairedItems.setValue(DateHelper.toString(endDate, DateFormat.Time, Locale.getDefault()));
                        }
                    }, 0, 0, true);
                    dialog.show();
                }
            }
        });


        final Switch isSendSwitch = (Switch) view.findViewById(R.id.show_sent_radio_button);
        filterSwitch = (Switch) view.findViewById(R.id.filter_radio_button);


        RadioButton showPathRadioBtn = (RadioButton) view.findViewById(R.id.show_path_radio_button);
        final RadioButton showTrackingBtn = (RadioButton) view.findViewById(R.id.show_tracking_radio_button);
        final RadioButton showEventsBtn = (RadioButton) view.findViewById(R.id.show_events_radio_button);

        view.findViewById(R.id.ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showEventsBtn.isChecked()) {
                    hideTracking();
                    showEvents(isSendSwitch.isChecked());
                } else {
                    hideTracking();
                    showTracking(showTrackingBtn.isChecked(), isSendSwitch.isChecked());
                }
            }
        });
        view.findViewById(R.id.restore_backup_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BackupManager.getList(getContext(), null).size() > 0) {
                    ImportDialogFragment importDialog = new ImportDialogFragment();
                    importDialog.show(getChildFragmentManager(), "ImportDialogFragment");
                } else {
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setTitle(R.string.error);
                    dialog.setMessage(R.string.there_is_no_backup_file);
                    dialog.setIcon(Icon.Alert);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.show();
                }
            }
        });
    }

    private void showErrorDialog(String error) {
        if (isResumed()) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.error);
            dialog.setMessage(error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tracking_report_layout, container, false);

        view.findViewById(R.id.report_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointsReportFragment reportFragment = new PointsReportFragment();
                reportFragment.show(getChildFragmentManager(), "PointsReportFragment");
            }
        });
        view.findViewById(R.id.drawer_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVaranegarActvity().toggleDrawer();
            }
        });
        mMapView = (MapView) view.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        trackingMapHelper = new MapHelper(getActivity());
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                trackingMapHelper.setGoogleMap(googleMap);
                try {
                    googleMap.setMyLocationEnabled(true);
                    createPolygons();
                } catch (SecurityException ex) {
                    Timber.e(ex);
                }
            }
        });

        return view;
    }

    private void hideTracking() {
        if (trackingMapHelper != null)
            trackingMapHelper.removeMarkers();

    }

    private void showEvents(boolean isSend) {
        VaranegarActivity activity = getVaranegarActvity();
        if (!activity.isFinishing()) {
            locations = new LocationManager(activity).getEventLocations(startDate, endDate, isSend);
            if (filterSwitch.isChecked())
                smoothePoints();
            if (!activity.isFinishing() && googleMap != null) {
                List<TrackingMarker> markers = create(activity, locations, true);
                trackingMapHelper.removeMarkers();
                trackingMapHelper.setMarkers(markers);
                trackingMapHelper.setDrawLines(true);
                trackingMapHelper.moveToArea(markers);
                trackingMapHelper.draw(googleMap.getMaxZoomLevel());
            }
        }
    }

    private void showTracking(boolean isTracking, boolean isSend) {
        VaranegarActivity activity = getVaranegarActvity();
        if (!activity.isFinishing()) {
            locations = new LocationManager(activity).getLocations(startDate, endDate, isTracking, isSend);
            if (filterSwitch.isChecked())
                smoothePoints();
            if (!activity.isFinishing() && googleMap != null) {
                List<TrackingMarker> markers = create(activity, locations, true);
                trackingMapHelper.removeMarkers();
                trackingMapHelper.setMarkers(markers);
                trackingMapHelper.setDrawLines(true);
                trackingMapHelper.moveToArea(markers);
                trackingMapHelper.draw(googleMap.getMaxZoomLevel());
            }
        }
    }

    public static List<TrackingMarker> create(Activity activity, List<LocationModel> locations, boolean showPoints) {
        List<TrackingMarker> markers = new ArrayList<>();
        LocationManager locationManager = new LocationManager(activity);
        int idx = 0;
        for (LocationModel current :
                locations) {
            BaseLocationViewModel locationViewModel = locationManager.convert(current);
            if (locationViewModel != null) {
                TrackingMarker marker = null;
                if (locationViewModel instanceof LackOfOrderLocationViewModel)
                    marker = new LackOfOrderMarker(activity, (LackOfOrderLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof LackOfVisitLocationViewModel)
                    marker = new LackOfVisitMarker(activity, (LackOfVisitLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof OrderLocationViewModel)
                    marker = new OrderMarker(activity, (OrderLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof WifiOffLocationViewModel)
                    marker = new WifiOffMarker(activity, (WifiOffLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof WifiOnLocationViewModel)
                    marker = new WifiOnMarker(activity, (WifiOnLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof BatteryLowLocationViewModel)
                    marker = new BatteryLowMarker(activity, (BatteryLowLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof GpsProviderOffLocationViewModel)
                    marker = new GPSOffMarker(activity, (GpsProviderOffLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof GpsProviderOnLocationViewModel)
                    marker = new GPSOnMarker(activity, (GpsProviderOnLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof WaitLocationViewModel)
                    marker = new WaitMarker(activity, (WaitLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof StartTourLocationViewModel)
                    marker = new StartTourMarker(activity, (StartTourLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof SendTourLocationViewModel)
                    marker = new SendTourMarker(activity, (SendTourLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof TransitionEventLocationViewModel)
                    marker = new TransitionMarker(activity, (TransitionEventLocationViewModel) locationViewModel);
                else {
                    if (idx == 0)
                        marker = new TrackingPointMarker(activity, locationViewModel, TrackingPointMarker.PointType.Start);
                    else if (idx == locations.size() - 1)
                        marker = new TrackingPointMarker(activity, locationViewModel, TrackingPointMarker.PointType.End);
                    else if (showPoints)
                        marker = new TrackingPointMarker(activity, locationViewModel, TrackingPointMarker.PointType.Normal);
                }
                if (marker != null) {
                    markers.add(marker);
                }
                idx++;
            }
        }
        return markers;
    }

    private void createPolygons() {
        RegionAreaPointManager manager = new RegionAreaPointManager(getContext());
        Area regionArea = manager.getRegion();
        if (regionArea != null) {
            List<LatLng> latLongs = regionArea.getLatLongs();
            PolygonOptions rectOptions = new PolygonOptions();
            rectOptions.addAll(latLongs);
            rectOptions.strokeColor(HelperMethods.getColor(getContext(), R.color.red));
            rectOptions.strokeWidth(4);
            googleMap.addPolygon(rectOptions);
        }

        Area dayPath = manager.getDayPath();
        if (dayPath != null) {
            List<LatLng> latLongs = dayPath.getLatLongs();
            PolygonOptions rectOptions = new PolygonOptions();
            rectOptions.addAll(latLongs);
            rectOptions.strokeColor(HelperMethods.getColor(getContext(), R.color.green));
            rectOptions.fillColor(HelperMethods.getColor(getContext(), R.color.green_light_light_transparent));
            rectOptions.strokeWidth(2);
            googleMap.addPolygon(rectOptions);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
