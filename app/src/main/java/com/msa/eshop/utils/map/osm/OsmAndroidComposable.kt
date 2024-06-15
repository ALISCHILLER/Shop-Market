package com.msa.eshop.utils.map.osm

import androidx.compose.runtime.ComposableTargetMarker

@Retention(AnnotationRetention.BINARY)
@ComposableTargetMarker(description = "OsmAnd Composable")
@Target(
    AnnotationTarget.FILE,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER,
)
annotation class OsmAndroidComposable