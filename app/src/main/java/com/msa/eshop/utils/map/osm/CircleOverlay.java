package com.msa.eshop.utils.map.osm;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.OverlayWithIW;

public class CircleOverlay extends OverlayWithIW {

    private final GeoPoint geo;
    private final float radius;

    public CircleOverlay(GeoPoint geo, float radius) {
        this.geo = geo;
        this.radius = radius;
    }


    @Override
    public void draw(Canvas pCanvas, MapView pMapView, boolean pShadow) {
        super.draw(pCanvas, pMapView, pShadow);

        Projection projection = pMapView.getProjection();

        Point pt = new Point();

        projection.toPixels(geo ,pt);
        float circleRadius = (float) (projection.metersToEquatorPixels(radius) * (1/ Math.cos((float) Math.toRadians(geo.getLatitude()))));

        Paint innerCirclePaint;

        innerCirclePaint = new Paint();
        innerCirclePaint.setColor(Color.BLUE);
        innerCirclePaint.setAlpha(40);
        innerCirclePaint.setAntiAlias(true);

        innerCirclePaint.setStyle(Paint.Style.FILL);

        pCanvas.drawCircle((float)pt.x, (float)pt.y, circleRadius, innerCirclePaint);
    }
}
