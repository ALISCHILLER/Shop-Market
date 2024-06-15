package com.msa.eshop.utils.map.osm

internal class MapCircleNode(
    private val mapView: OsmMapView,
    val circleOverlay: CircleOverlay
) : OsmAndNode {

    override fun onRemoved() {
        super.onRemoved()
        mapView.overlayManager.remove(circleOverlay)
    }
}