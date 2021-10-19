package com.varanegar.vaslibrary.manager.locationmanager.map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.varanegar.vaslibrary.manager.locationmanager.BaseLocationViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 6/11/2018.
 */

public class MapHelper {
    private final Activity activity;
    private GoogleMap googleMap;
    private List<TrackingMarker> markers;
    private boolean drawLines;

    public MapHelper(Activity activity) {
        this.activity = activity;
    }

    private List<Marker> pointMarkers = new ArrayList<>();
    private HashMap<UUID, TrackingMarker> pointersMap = new HashMap<>();
    private List<Marker> arrowMarkers = new ArrayList<>();
    private Polyline polyline;

    public void removeMarkers() {
        for (Marker marker :
                pointMarkers) {
            marker.remove();
        }
        for (Marker marker :
                arrowMarkers) {
            marker.remove();
        }
        if (polyline != null)
            polyline.remove();
        pointMarkers.clear();
        arrowMarkers.clear();
        pointersMap.clear();
        polyline = null;
        googleMap.clear();
    }

    public void draw(@Nullable Float zoom) {
        removeMarkers();
        if (zoom == null)
            zoom = googleMap.getCameraPosition().zoom;
        float max = googleMap.getMaxZoomLevel();
        float min = googleMap.getMinZoomLevel();
        int count = 20;
        float z = (max - min) / count;
        float[] h = new float[count];
        float[] dd = new float[]{4300, 4000, 3700, 3400, 3200, 3000, 2800, 2600, 2400, 2200, 2000, 1600, 1300, 1000, 700, 250, 140, 70, 20, 5};
        for (int i = 0; i < count; i++) {
            h[i] = (i + 1) * z;
        }
        googleMap.setInfoWindowAdapter(new InfoWindowAdapter(activity, markers));

        Bitmap originalArrow = BitmapFactory.decodeResource(activity.getResources(), com.varanegar.vaslibrary.R.drawable.ic_keyboard_arrow_up_black_18dp);
        Map<UUID, List<TrackingMarker>> markersOfAllCustomers = new HashMap<>();
        for (TrackingMarker marker :
                markers) {
            UUID companyPersonnelId = marker.getLocationViewModel().CompanyPersonnelId;
            if (markersOfAllCustomers.containsKey(companyPersonnelId))
                markersOfAllCustomers.get(companyPersonnelId).add(marker);
            else {
                List<TrackingMarker> markers = new ArrayList<>();
                markers.add(marker);
                markersOfAllCustomers.put(companyPersonnelId, markers);
            }
        }
        float d = h[0];
        for (int i = 0; i < count; i++) {
            if (zoom < h[i]) {
                d = dd[i];
                break;
            }
        }
        for (List<TrackingMarker> customerMarkers :
                markersOfAllCustomers.values()) {
            TrackingMarker lastMarker = null;
            PolylineOptions rectOptions = new PolylineOptions();
            rectOptions.width(2);
            for (TrackingMarker currentMarker :
                    customerMarkers) {
                float distance = 100000;
                if (lastMarker != null && !currentMarker.isEvent()) {
                    distance = distance(lastMarker.getLocationViewModel(), currentMarker.getLocationViewModel());
                }
                if (distance > d || currentMarker.isEvent()) {
                    LatLng latLng = new LatLng(currentMarker.getLocationViewModel().Latitude, currentMarker.getLocationViewModel().Longitude);
                    Marker m = googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .anchor(0.5f, 0.5f)
                            .zIndex(currentMarker.zIndex())
                            .icon(BitmapDescriptorFactory.fromBitmap(currentMarker.createBitMap())));
                    m.setTag(currentMarker.getUniqueId());
                    pointMarkers.add(m);
                    pointersMap.put(currentMarker.getUniqueId(), currentMarker);


                    if (drawLines && lastMarker != null && distance(lastMarker.getLocationViewModel(), currentMarker.getLocationViewModel()) > 5) {
                        float rotationDegrees =
                                (float) Math.toDegrees(Math.atan2(
                                        currentMarker.getLocationViewModel().Longitude - lastMarker.getLocationViewModel().Longitude,
                                        currentMarker.getLocationViewModel().Latitude - lastMarker.getLocationViewModel().Latitude));

                        Matrix matrix = new Matrix();
                        matrix.setRotate(rotationDegrees, originalArrow.getWidth() / 2, originalArrow.getHeight() / 2);
                        Bitmap rotatedArrow = rotateBitmap(originalArrow, rotationDegrees);
                        Marker mArrowhead = googleMap.addMarker(new MarkerOptions()
                                .position(computeCentroid(getLatLng(lastMarker.getLocationViewModel()), getLatLng(currentMarker.getLocationViewModel())))
                                .icon(BitmapDescriptorFactory.fromBitmap(rotatedArrow))
                                .anchor(0.5f, 0.5f));
                        arrowMarkers.add(mArrowhead);

                        rectOptions = rectOptions.add(latLng);

                        if (drawLines)
                            polyline = googleMap.addPolyline(rectOptions);
                    }

                    lastMarker = currentMarker;
                }
            }
        }


    }

    private float distance(BaseLocationViewModel location1, BaseLocationViewModel location2) {
        android.location.Location loc1 = new android.location.Location("location1");
        loc1.setLatitude(location1.Latitude);
        loc1.setLongitude(location1.Longitude);
        android.location.Location loc2 = new android.location.Location("location2");
        loc2.setLatitude(location2.Latitude);
        loc2.setLongitude(location2.Longitude);
        return loc1.distanceTo(loc2);
    }

    private static LatLng getLatLng(@NonNull BaseLocationViewModel locationModel) {
        return new LatLng(locationModel.Latitude, locationModel.Longitude);
    }

    private static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private static LatLng computeCentroid(LatLng... points) {
        List<LatLng> list = new ArrayList();
        Collections.addAll(list, points);
        return computeCentroid(list);
    }

    private static LatLng computeCentroid(List<LatLng> points) {
        double latitude = 0;
        double longitude = 0;
        int n = points.size();

        for (LatLng point : points) {
            latitude += point.latitude;
            longitude += point.longitude;
        }

        return new LatLng(latitude / n, longitude / n);
    }

    public void moveToArea(List<TrackingMarker> points) {
        if (points.size() > 0) {
            LatLngBounds.Builder b = new LatLngBounds.Builder();
            for (TrackingMarker p : points) {
                BaseLocationViewModel locationViewModel = p.getLocationViewModel();
                if (locationViewModel != null)
                    b.include(new LatLng(locationViewModel.Latitude, locationViewModel.Longitude));
            }
            LatLngBounds bounds = b.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 10);
            googleMap.animateCamera(cu);
        }
    }

    public interface OnMarkerInfoViewClickListener {
        void onClick(TrackingMarker marker);
    }

    public void setOnMarkerInfoViewClickListener(final OnMarkerInfoViewClickListener listener) {
        if (googleMap != null) {
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    listener.onClick(pointersMap.get(marker.getTag()));
                }
            });
        }
    }

    public void removeOnMarkerInfoViewClickListener(){
        if (googleMap != null)
            googleMap.setOnInfoWindowClickListener(null);
    }

    public void setMarkers(List<TrackingMarker> markers) {
        this.markers = markers;
    }

    public void setDrawLines(boolean drawLines) {
        this.drawLines = drawLines;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}
