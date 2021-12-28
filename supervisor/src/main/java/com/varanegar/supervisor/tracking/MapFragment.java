package com.varanegar.supervisor.tracking;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.varanegar.framework.base.ProgressFragment;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.datetime.JalaliCalendar;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.VisitorViewModel;
import com.varanegar.supervisor.webapi.EventViewModel;
import com.varanegar.supervisor.webapi.LastPointsParam;
import com.varanegar.supervisor.webapi.MasterEventViewModel;
import com.varanegar.supervisor.webapi.PersonnelPointsParam;
import com.varanegar.supervisor.webapi.StatusType;
import com.varanegar.supervisor.webapi.SupervisorApi;
import com.varanegar.vaslibrary.manager.locationmanager.BaseLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.map.BatteryLowMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.GPSOffMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.GPSOnMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.LackOfOrderMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.LackOfVisitMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.MapHelper;
import com.varanegar.vaslibrary.manager.locationmanager.map.OrderMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.SendTourMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.StartTourMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.SummaryTourMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.TrackingMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.TrackingPointMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.WaitMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.WifiOffMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.WifiOnMarker;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.BatteryLowLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.EventTypeId;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.GpsProviderOffLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.GpsProviderOnLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.LackOfOrderLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.LackOfVisitLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.OrderLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.SendTourLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.StartTourLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.SummaryTourLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.WaitLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.WifiOffLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.WifiOnLocationViewModel;
import com.varanegar.vaslibrary.model.customerpathview.CustomerPathViewModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 6/7/2018.
 */

public class MapFragment extends ProgressFragment {
    private MapView mapView;
    private GoogleMap googleMap;
    private boolean locationUpdated;
    private boolean myLocationClicked;
    private boolean isDragging;
    private GoogleApiClient client;
    private Location lastLocation;
    private Geocoder geocoder;
    private MapHelper mapHelper;
    private List<TrackingMarker> _markers = new ArrayList<>();
    List<Marker> markers = new ArrayList<>();
    private float zoom;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        mapHelper = new MapHelper(getActivity());
    }


    private List<TrackingMarker> createMarkers(List<EventViewModel> eventViewModels, boolean showEvents, boolean visitorName) {

        List<TrackingMarker> markers = new ArrayList<>();
        FragmentActivity activity = getActivity();
        if (activity == null)
            return markers;

        for (final EventViewModel current :
                eventViewModels) {
            TrackingMarker marker;
            if (showEvents) {
                if (EventTypeId.LackOfOrder.equals(current.PointType))
                    marker = new LackOfOrderMarker(activity, (LackOfOrderLocationViewModel) SupervisorApi.convert(current));
                else if (EventTypeId.LackOfVisit.equals(current.PointType))
                    marker = new LackOfVisitMarker(activity, (LackOfVisitLocationViewModel) SupervisorApi.convert(current));
                else if (EventTypeId.Order.equals(current.PointType))
                    marker = new OrderMarker(activity, (OrderLocationViewModel) SupervisorApi.convert(current));
                else if (EventTypeId.WifiOff.equals(current.PointType))
                    marker = new WifiOffMarker(activity, (WifiOffLocationViewModel) SupervisorApi.convert(current));
                else if (EventTypeId.WifiOn.equals(current.PointType))
                    marker = new WifiOnMarker(activity, (WifiOnLocationViewModel) SupervisorApi.convert(current));
                else if (EventTypeId.BatteryLow.equals(current.PointType))
                    marker = new BatteryLowMarker(activity, (BatteryLowLocationViewModel) SupervisorApi.convert(current));
                else if (EventTypeId.GpsOff.equals(current.PointType))
                    marker = new GPSOffMarker(activity, (GpsProviderOffLocationViewModel) SupervisorApi.convert(current));
                else if (EventTypeId.GpsOn.equals(current.PointType))
                    marker = new GPSOnMarker(activity, (GpsProviderOnLocationViewModel) SupervisorApi.convert(current));
                else if (EventTypeId.Wait.equals(current.PointType))
                    marker = new WaitMarker(activity, (WaitLocationViewModel) SupervisorApi.convert(current));
                else if (EventTypeId.StartTour.equals(current.PointType))
                    marker = new StartTourMarker(activity, (StartTourLocationViewModel) SupervisorApi.convert(current));
                else if (EventTypeId.SendTour.equals(current.PointType))
                    marker = new SendTourMarker(activity, (SendTourLocationViewModel) SupervisorApi.convert(current));
                else {
                    BaseLocationViewModel locationViewModel = SupervisorApi.convert(current);
                    marker = new TrackingPointMarker(activity, locationViewModel, TrackingPointMarker.PointType.Normal, visitorName ? locationViewModel.Lable : null);
                }
            } else {
                marker = new SummaryTourMarker(activity, (SummaryTourLocationViewModel) SupervisorApi.convert(current));
            }
            markers.add(marker);
        }
        return markers;
    }


    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracking_map_layout, container, false);
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // needed to getUnits the map to display immediately

        try {
            MapsInitializer.initialize(getContext());
        } catch (Exception e) {
            Timber.e(e);
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                try {
                    mapHelper.setGoogleMap(googleMap);
                    googleMap.setMyLocationEnabled(true);
                    googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                        @Override
                        public void onCameraMoveStarted(int i) {
                            hideVisitorInfo();
                            if (!myLocationClicked && !locationUpdated) {
                                isDragging = true;
                            } else {
                                isDragging = false;
                            }
                            myLocationClicked = false;
                            if (googleMap.getCameraPosition().zoom != zoom) {
                                zoom = googleMap.getCameraPosition().zoom;
                                refreshMarkers();
                            }
                        }
                    });
                    googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                        @Override
                        public boolean onMyLocationButtonClick() {
                            myLocationClicked = true;
                            hideVisitorInfo();
                            return false;
                        }
                    });
                } catch (SecurityException ex) {
                    Timber.e(ex);
                }
            }
        });
        return view;
    }

    private void refreshMarkers() {
        if (_markers.size() > 0) {
            mapHelper.setMarkers(_markers);
            mapHelper.draw(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
            startLocationUpdate();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
            client.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
            client.disconnect();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
            client.disconnect();
        }
    }

    void startLocationUpdate() {
        Context context = getContext();
        if (context != null) {
            client = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            if (isResumed()) {
                                try {
                                    LocationRequest request = LocationRequest.create();
                                    request.setInterval(1000);
                                    request.setSmallestDisplacement(1);
                                    LocationServices.FusedLocationApi.requestLocationUpdates(client, request, new LocationListener() {
                                        @Override
                                        public void onLocationChanged(Location location) {
                                            lastLocation = location;
                                            updateCurrentAddress();
                                            gotoCurrentLocation();
                                        }
                                    });
                                } catch (SecurityException ex) {
                                    Timber.e(ex);
                                }
                            }
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Timber.d("location connection suspended");
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Timber.e(connectionResult.getErrorMessage());
                        }
                    })
                    .build();
            client.connect();
        }
    }

    public class AddressThread extends Thread {
        @Override
        public void run() {
            super.run();
            if (lastLocation != null) {
                Context context = getContext();
                if (context != null) {
                    if (!Connectivity.isConnected(getContext())) {
                        Timber.d("Missing internet connection");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), R.string.device_is_diconnected, Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    try {
                        final List<Address> addresses = geocoder.getFromLocation(
                                lastLocation.getLatitude(),
                                lastLocation.getLongitude(),
                                1);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (addresses != null && addresses.size() > 0) {
                                    Address address = addresses.get(0);
                                    List<String> addressFragments = new ArrayList<>();
                                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                                        addressFragments.add(address.getAddressLine(i));
                                    }
                                    Toast.makeText(getContext(), TextUtils.join(", ",
                                            addressFragments), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    } catch (Exception ex) {
                        if (ex instanceof IOException)
                            Timber.e("Address is not available: " + ex.getMessage());
                        else
                            Timber.e(ex);
                    }
                }
            }
        }
    }

    void updateCurrentAddress() {
        AddressThread thread = new AddressThread();
        thread.start();
    }

    void gotoCurrentLocation() {
        if (lastLocation != null) {
            LatLng myLatLong = new LatLng(lastLocation.getLatitude(),
                    lastLocation.getLongitude());
            if (!isDragging) {
                // For zooming automatically to the location of the marker
                locationUpdated = true;
                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLatLong).zoom(17).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                locationUpdated = false;
            }
        } else {
            Toast.makeText(getContext(), com.varanegar.vaslibrary.R.string.location_is_not_available, Toast.LENGTH_LONG).show();
        }
    }

    public void showMarkers() {
        if (mapHelper == null)
            return;
        mapHelper.removeMarkers();
        final TrackingConfig trackingConfig = new TrackingConfig(getContext());
        final SupervisorApi api = new SupervisorApi(getContext());
        if (!trackingConfig.isTracking()) {
            final PersonnelPointsParam param = new PersonnelPointsParam();
            param.mDate = trackingConfig.getTrackingDate();
            param.FromTime = trackingConfig.getFromTime();
            param.ToTime = trackingConfig.getToTime();
            param.PersonelIds = trackingConfig.getPersonnelIds();
            param.StopTime = trackingConfig.getWaitTime();


            Set<UUID> trackingActivityTypes = trackingConfig.getTrackingActivityTypes();
            param.Order = trackingActivityTypes.contains(EventTypeId.Order);
            param.LackOrder = trackingActivityTypes.contains(EventTypeId.LackOfOrder);
            param.LackVisit = trackingActivityTypes.contains(EventTypeId.LackOfVisit);
            param.Wait = trackingActivityTypes.contains(EventTypeId.Wait);
            param.GpsPowerOff = trackingActivityTypes.contains(EventTypeId.GpsOff);
            param.GpsPowerOn = trackingActivityTypes.contains(EventTypeId.GpsOn);
            param.BattryLow = trackingActivityTypes.contains(EventTypeId.BatteryLow);
            param.EnterPath = trackingActivityTypes.contains(EventTypeId.EnterVisitDay);
            param.ExitPath = trackingActivityTypes.contains(EventTypeId.ExitVisitDay);
            param.EnterRigion = trackingActivityTypes.contains(EventTypeId.EnterRegion);
            param.ExitRigion = trackingActivityTypes.contains(EventTypeId.ExitRegion);
            param.OpernTour = trackingActivityTypes.contains(EventTypeId.StartTour);
            param.CloseTour = trackingActivityTypes.contains(EventTypeId.SendTour);
            param.ExitCompany = trackingActivityTypes.contains(EventTypeId.ExitCompany);
            param.WifiPowerOff = trackingActivityTypes.contains(EventTypeId.WifiOff);
            param.WifiPowerOn = trackingActivityTypes.contains(EventTypeId.WifiOn);
            param.EnterCompany = trackingActivityTypes.contains(EventTypeId.EnterCompany);
            param.MobileDataOff = trackingActivityTypes.contains(EventTypeId.MobileDataOff);
            param.MobileDataOn = trackingActivityTypes.contains(EventTypeId.MobileDataOn);

            startProgress(R.string.please_wait, R.string.downloading_data);
            api.runWebRequest(api.loadPersonnelPath(param), new WebCallBack<List<MasterEventViewModel>>() {
                @Override
                protected void onFinish() {
                    finishProgress();
                }

                @Override
                protected void onSuccess(final List<MasterEventViewModel> paths, Request request) {
                    api.runWebRequest(api.loadPersonnelEvents(param), new WebCallBack<List<EventViewModel>>() {
                        @Override
                        protected void onFinish() {

                        }

                        @Override
                        protected void onSuccess(List<EventViewModel> events, Request request) {
                            List<EventViewModel> allPoints = new ArrayList<>();
                            for (MasterEventViewModel master :
                                    paths) {
                                if (master.points != null)
                                    allPoints.addAll(master.points);
                            }
                            allPoints.addAll(events);
                            _markers = createMarkers(allPoints, true , false);
                            if (_markers.size() > 0) {
                                mapHelper.setMarkers(_markers);
                                mapHelper.setDrawLines(true);
                                mapHelper.moveToArea(_markers);
                                mapHelper.draw(null);
                                mapHelper.removeOnMarkerInfoViewClickListener();
                            }
                        }

                        @Override
                        protected void onApiFailure(ApiError error, Request request) {
                            Activity activity = getActivity();
                            if (activity != null && !activity.isFinishing() && isResumed()) {
                                String err = WebApiErrorBody.log(error, getContext());
                                showError(err);
                            }
                        }

                        @Override
                        protected void onNetworkFailure(Throwable t, Request request) {
                            Activity activity = getActivity();
                            if (activity != null && !activity.isFinishing() && isResumed()) {
                                Timber.e(t);
                                showError(R.string.network_error);
                            }
                        }
                    });

                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    Activity activity = getActivity();
                    if (activity != null && !activity.isFinishing() && isResumed()) {
                        Timber.e(error.getMessage());
                        showError(error.getMessage());
                    }
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    Activity activity = getActivity();
                    if (activity != null && !activity.isFinishing() && isResumed()) {
                        Timber.e(t);
                        showError(R.string.network_error);
                    }
                }
            });
        } else {
            /**
             * اخرین موقعیت
             */
            JalaliCalendar calendar = new JalaliCalendar();
            Date d=new Date();
            String date = DateHelper.toString(d, DateFormat.Date, Locale.getDefault());

            LastPointsParam param = new LastPointsParam();
            param.date = date;
            param.LaststatusType = trackingConfig.getStatusType().ordinal();
            param.PersonelIds = trackingConfig.getPersonnelIds();
            startProgress(R.string.please_wait, R.string.downloading_data);
            api.runWebRequest(api.loadLastPoints(param), new WebCallBack<List<EventViewModel>>() {
                @Override
                protected void onFinish() {
                    finishProgress();
                }

                @Override
                protected void onSuccess(List<EventViewModel> result, Request request) {


                        createMarkers(result);



//                    _markers = createMarkers(result, new TrackingConfig(getContext()).getStatusType() == StatusType.Event , true);
//                    if (_markers.size() > 0) {
//                        mapHelper.setMarkers(_markers);
//                        mapHelper.setDrawLines(false);
//                        mapHelper.moveToArea(_markers);
//                        mapHelper.draw(null);
//                        mapHelper.setOnMarkerInfoViewClickListener(new MapHelper.OnMarkerInfoViewClickListener() {
//                            @Override
//                            public void onClick(TrackingMarker marker) {
//                                BaseLocationViewModel locationViewModel = marker.getLocationViewModel();
//                                Activity activity = getActivity();
//                                if (activity != null && !activity.isFinishing() && isResumed()) {
//                                    TrackingConfig trackingConfig = new TrackingConfig(activity);
//                                    trackingConfig.isTracking(true);
//                                    trackingConfig.setFromTime(6, 0);
//                                    trackingConfig.setToTime(23, 55);
//                                    trackingConfig.isMap(true);
//                                    List<UUID> customersIds = new ArrayList<>();
//                                    customersIds.add(locationViewModel.CompanyPersonnelId);
//                                    trackingConfig.removePersonnelIds();
//                                    trackingConfig.setPersonnelIds2(customersIds);
//                                    trackingConfig.setTrackingDate(new Date());
//                                    showMarkers();
//                                }
//                            }
//                        });
//                    }
                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    Activity activity = getActivity();
                    if (activity != null && !activity.isFinishing() && isResumed()) {
                        String err = WebApiErrorBody.log(error, activity);
                        showError(err);
                    }
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    Activity activity = getActivity();
                    if (activity != null && !activity.isFinishing() && isResumed()) {
                        showError(R.string.network_error);
                        Timber.e(t);
                    }
                }
            });
        }

    }
    private void createMarkers(List<EventViewModel> result){
        Linq.forEach(markers, new Linq.Consumer<Marker>() {
            @Override
            public void run(Marker item) {
                item.remove();
            }
        });
        markers = new ArrayList<>();


        for (EventViewModel eventViewModel:result){
            createMarker(eventViewModel);
        }
    }
    private void createMarker(EventViewModel eventViewModel){
        Context context = getContext();

        if ( eventViewModel.Latitude != 0 && eventViewModel.Longitude != 0 && context != null) {
            LatLng customerPosition = new LatLng(eventViewModel.Latitude, eventViewModel.Longitude);
            MarkerOptions options = new MarkerOptions().position(customerPosition);
            Marker marker = googleMap.addMarker(options);
            int icon = R.drawable.ic_location_supervisor;
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(icon);
            Bitmap b = BitmapFactory.decodeResource(getContext().getResources(), icon);
            marker.setTitle(eventViewModel.Lable);
            marker.setTag(eventViewModel.Lable);
            marker.setIcon(bitmap);
            markers.add(marker);
        }
    }

    private void showError(String str) {
        Context context = getContext();
        if (context != null) {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setIcon(Icon.Error);
            dialog.setMessage(str);
            dialog.setTitle(R.string.error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }

    private void showError(@StringRes int str) {
        if (getContext() != null)
            showError(getContext().getString(str));
    }

    private void hideVisitorInfo() {

    }

    private void showVisitorInfo(VisitorViewModel visitor) {

    }

}
