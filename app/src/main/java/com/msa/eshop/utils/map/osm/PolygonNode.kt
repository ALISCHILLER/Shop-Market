package com.msa.eshop.utils.map.osm

import com.msa.eshop.utils.map.osm.OsmAndNode
import com.msa.eshop.utils.map.osm.OsmMapView
import org.osmdroid.views.overlay.Polygon

internal class PolygonNode(
    private val mapView: OsmMapView,
    val polygon: Polygon,
    var onPolylineClick: (Polygon) -> Unit
) : OsmAndNode {

    override fun onRemoved() {
        super.onRemoved()
        mapView.overlayManager.remove(polygon)
    }

    fun setupListeners() {
        polygon.setOnClickListener { polygon, _, _ ->
            onPolylineClick.invoke(polygon)
            if (polygon.isInfoWindowOpen) {
                polygon.closeInfoWindow()
            } else {
                polygon.showInfoWindow()
            }
            true
        }
    }
}