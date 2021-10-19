package com.varanegar.vaslibrary.manager;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.varanegar.vaslibrary.model.RegionAreaPointModel;

import java.util.Arrays;
import java.util.List;

/**
 * Created by A.Torabi on 4/29/2018.
 */

public class Area {
    private LatLng[] points;

    public Area(@NonNull List<RegionAreaPointModel> p) {
        int count = p.size();
        this.points = new LatLng[count];
        for (int i = 0; i < count; i++) {
            points[i] = new LatLng(p.get(i).Latitude, p.get(i).Longitude);
        }
    }

    public boolean pointIsInRegion(double latitude, double longitude) {
        int crossings = 0;
        LatLng point = new LatLng(latitude, longitude);
        int count = points.length;
        // for each edge
        for (int i = 0; i < count; i++) {
            LatLng a = points[i];
            int j = i + 1;
            if (j >= count) {
                j = 0;
            }
            LatLng b = points[j];
            if (rayCrossesSegment(point, a, b)) {
                crossings++;
            }
        }
        // odd number of crossings?
        return (crossings % 2 == 1);
    }

    private boolean rayCrossesSegment(LatLng point, LatLng a, LatLng b) {
        double px = point.longitude;
        double py = point.latitude;
        double ax = a.longitude;
        double ay = a.latitude;
        double bx = b.longitude;
        double by = b.latitude;
        if (ay > by) {
            ax = b.longitude;
            ay = b.latitude;
            bx = a.longitude;
            by = a.latitude;
        }
        // alter longitude to cater for 180 degree crossings
        if (px < 0) {
            px += 360;
        }
        if (ax < 0) {
            ax += 360;
        }
        if (bx < 0) {
            bx += 360;
        }

        if (py == ay || py == by) py += 0.00000001;
        if ((py > by || py < ay) || (px > Math.max(ax, bx))) return false;
        if (px < Math.min(ax, bx)) return true;

        double red = (ax != bx) ? ((by - ay) / (bx - ax)) : Double.MAX_VALUE;
        double blue = (ax != px) ? ((py - ay) / (px - ax)) : Double.MAX_VALUE;
        return (blue >= red);
    }

    public List<LatLng> getLatLongs() {
        return Arrays.asList(points);
    }
}
