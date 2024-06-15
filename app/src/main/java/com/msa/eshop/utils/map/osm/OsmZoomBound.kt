package com.msa.eshop.utils.map.osm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint


//-------------------------------------------------------------------------------------------------- OsmZoomBound
@Composable
@OsmAndroidComposable
fun OsmZoomBound(
    geoPoint: List<GeoPoint>,
) {
    val box = getBoundingBoxFromPoints(geoPoint)
    val applier =
        currentComposer.applier as? MapApplier ?: throw IllegalStateException("Invalid Applier")
    val mapView = applier.mapView
    mapView.zoomToBoundingBox(box, true)
}
//-------------------------------------------------------------------------------------------------- OsmZoomBound


//---------------------------------------------------------------------------------------------- getBoundingBoxFromPoints
fun getBoundingBoxFromPoints(points: List<GeoPoint>): BoundingBox {
    var north = 0.0
    var south = 0.0
    var west = 0.0
    var east = 0.0
    for (i in points.indices) {
        val lat = points[i].latitude
        val lon = points[i].longitude
        if (i == 0 || lat > north) north = lat
        if (i == 0 || lat < south) south = lat
        if (i == 0 || lon < west) west = lon
        if (i == 0 || lon > east) east = lon
    }
    north += 0.02
    south -= 0.2
    east += 0.02
    west -= 0.02

    return BoundingBox(north, east, south, west)
}
//---------------------------------------------------------------------------------------------- getBoundingBoxFromPoints
