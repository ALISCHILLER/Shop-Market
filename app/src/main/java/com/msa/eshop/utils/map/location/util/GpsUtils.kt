package com.msa.eshop.utils.map.location.util

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

class GpsUtils(private val context: Context) {

    fun checkGpsSettings(
        onSuccess: () -> Unit,
        onFailure: (exception: ResolvableApiException) -> Unit
    ) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 5000
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(context)
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // GPS is already enabled
            onSuccess.invoke()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // GPS is not enabled, but we can prompt the user to enable it
                onFailure.invoke(exception)
            }
        }
    }
}
