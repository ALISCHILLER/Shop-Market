package com.msa.eshop.utils.map.osm.model

import android.graphics.Paint


//class LabelProperties for Marker with label
//With default parameters that can be customized
data class LabelProperties(
    val labelColor: Int = android.graphics.Color.BLACK,
    val labelTextSize: Float = 40f,
    val labelAntiAlias: Boolean = true,
    val labelAlign: Paint.Align = Paint.Align.CENTER,
    val labelTextOffset: Float = 30f
)
