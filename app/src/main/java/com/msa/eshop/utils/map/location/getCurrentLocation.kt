package com.msa.eshop.utils.map.location

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import timber.log.Timber

@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context, onLocationReceived: (String) -> Unit) {
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                onLocationReceived("Lat: ${location.latitude}, Lon: ${location.longitude}")
                Timber.d("getCurrentLocation: Lat: " + location.latitude + ", Lon: " + location.longitude)
            } else {
                onLocationReceived("Location not found")
                Timber.tag(TAG).e("getCurrentLocation: Location not found")
            }
        }
}