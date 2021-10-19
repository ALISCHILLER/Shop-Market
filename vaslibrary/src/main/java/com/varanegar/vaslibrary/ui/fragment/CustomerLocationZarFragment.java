package com.varanegar.vaslibrary.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.vaslibrary.R;

import java.util.Locale;

import timber.log.Timber;

/**
 * Created by A.Torabi on 7/17/2017.
 */

public class CustomerLocationZarFragment extends VaranegarFragment {
    private MapView mMapView;
    private GoogleMap googleMap;
    Geocoder geocoder;
    GoogleApiClient client;
    private Location lastLocation;

    private boolean gotoLocation = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_location, container, false);
        view.findViewById(R.id.marker_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (googleMap != null) {
                    MainVaranegarActivity activity = getVaranegarActvity();
                    final CameraPosition cameraPosition = googleMap.getCameraPosition();
                    Location cameraLocation = new Location("camera");
                    cameraLocation.setLatitude(cameraPosition.target.latitude);
                    cameraLocation.setLongitude(cameraPosition.target.longitude);
                    if (lastLocation == null) {
                        if (activity != null && !activity.isFinishing())
                            activity.showSnackBar(R.string.your_location_is_not_available, MainVaranegarActivity.Duration.Short);
                        return;
                    }
                    AlertDialog.Builder aBuilder = new AlertDialog.Builder(getContext());
                    aBuilder.setMessage(R.string.are_you_sure);
                    aBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainVaranegarActivity activity = getVaranegarActvity();
                            if (activity != null) {
                                VaranegarApplication.getInstance().save("ZAR_CUST_LAT", cameraPosition.target.latitude);
                                VaranegarApplication.getInstance().save("ZAR_CUST_LNG", cameraPosition.target.longitude);
                                activity.popFragment();
                            }

                        }
                    });
                    aBuilder.setNegativeButton(R.string.no, null);
                    aBuilder.show();
                }
            }
        });
        view.findViewById(R.id.back_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing())
                    activity.popFragment();
            }
        });
        mMapView = (MapView) view.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to getUnits the map to display immediately

        try {
            Activity activity = getActivity();
            if (activity != null)
                MapsInitializer.initialize(activity);
        } catch (Exception e) {
            Timber.e(e);
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                try {
                    googleMap.setMyLocationEnabled(true);
                } catch (SecurityException ignored) {

                }
            }
        });

        return view;
    }

    void gotoCurrentLocation() {
        // For dropping a marker at a point on the Map
        if (lastLocation != null) {
            LatLng myLatLong = new LatLng(lastLocation.getLatitude(),
                    lastLocation.getLongitude());
            // For zooming automatically to the location of the marker
            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLatLong).zoom(17).build();
            if (googleMap != null)
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {
            Toast.makeText(getContext(), R.string.location_is_not_available, Toast.LENGTH_LONG).show();
        }
    }

    void startLocationUpdate() {
        client = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        try {
                            LocationRequest request = LocationRequest.create();
                            request.setInterval(1000);
                            request.setSmallestDisplacement(1);
                            LocationServices.FusedLocationApi.requestLocationUpdates(client, request, new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    lastLocation = location;
                                    if (gotoLocation) {
                                        gotoCurrentLocation();
                                        gotoLocation = false;
                                    }

                                }
                            });
                        } catch (SecurityException ex) {
                            Timber.e(ex);
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

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        startLocationUpdate();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        client.disconnect();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        client.disconnect();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
        client.disconnect();
        Runtime.getRuntime().gc();
    }
}
