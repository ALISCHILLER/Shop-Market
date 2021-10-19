package com.varanegar.vaslibrary.manager.locationmanager.map;

import android.app.Activity;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 6/11/2018.
 */

    class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final HashMap<UUID, TrackingMarker> markers = new HashMap<>();
    private final Activity activity;

    public InfoWindowAdapter(Activity activity, List<TrackingMarker> markers) {
        this.activity = activity;
        for (TrackingMarker marker :
                markers) {
            this.markers.put(marker.getUniqueId(), marker);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Object obj = marker.getTag();
        if (obj != null) {
            UUID tag = (UUID) obj;
            TrackingMarker trackingMarker = markers.get(tag);
            if (trackingMarker != null)
                return trackingMarker.onCreateInfoView(activity.getLayoutInflater());
        }
        return null;
    }
}
