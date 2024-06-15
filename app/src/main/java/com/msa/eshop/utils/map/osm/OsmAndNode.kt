package com.msa.eshop.utils.map.osm

internal interface OsmAndNode {
    fun onAttached() {}
    fun onRemoved() {}
    fun onCleared() {}
}

internal object OsmNodeRoot : OsmAndNode