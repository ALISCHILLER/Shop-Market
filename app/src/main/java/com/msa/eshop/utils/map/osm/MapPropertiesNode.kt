package com.msa.eshop.utils.map.osm

import android.util.Log
import com.msa.eshop.utils.map.osm.CameraState
import org.osmdroid.events.DelayedMapListener
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay

internal class MapPropertiesNode(
    val mapViewComposed: OsmMapView,
    val mapListeners: MapListeners,
    private val cameraState: CameraState,
    overlayManagerState: OverlayManagerState
) : OsmAndNode {

    private var delayedMapListener: DelayedMapListener? = null
    private var eventOverlay: MapEventsOverlay? = null

    init {
        overlayManagerState.setMap(mapViewComposed)
        cameraState.setMap(mapViewComposed)
    }

    override fun onAttached() {
        mapViewComposed.controller.setCenter(cameraState.geoPoint)
        mapViewComposed.controller.setZoom(cameraState.zoom)

        delayedMapListener = DelayedMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                temp()
                return false
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                temp()
                return false
            }
        }, 500L)

        mapViewComposed.addMapListener(delayedMapListener)

        val eventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let { mapListeners.onMapClick.invoke(it) }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                p?.let { mapListeners.onMapLongClick.invoke(it) }
                return true
            }
        }

        eventOverlay = MapEventsOverlay(eventsReceiver)

        mapViewComposed.overlayManager.add(eventOverlay)

        if (mapViewComposed.isLayoutOccurred) {
            mapListeners.onFirstLoadListener.invoke("")
        }
    }

    private fun temp() {
        Log.d("meri", "onScroll - ${mapViewComposed.zoomLevelDouble}")
        Log.d("meri", "onScroll cameraState - ${cameraState.zoom}")
        val currentZoom = mapViewComposed.zoomLevelDouble
        val currentGeoPoint =
            mapViewComposed.let { GeoPoint(it.mapCenter.latitude, it.mapCenter.longitude) }
        cameraState.geoPoint = currentGeoPoint
        cameraState.zoom = currentZoom
        Log.d("meri", "onScroll cameraState2 - ${cameraState.zoom}")
    }

    override fun onCleared() {
        super.onCleared()
        delayedMapListener?.let { mapViewComposed.removeMapListener(it) }
        eventOverlay?.let { mapViewComposed.overlayManager.remove(eventOverlay) }
    }

    override fun onRemoved() {
        super.onRemoved()
        delayedMapListener?.let { mapViewComposed.removeMapListener(it) }
        eventOverlay?.let { mapViewComposed.overlayManager.remove(eventOverlay) }
    }
}