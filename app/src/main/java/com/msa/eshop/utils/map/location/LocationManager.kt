package com.msa.eshop.utils.map.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.msa.eshop.utils.map.location.util.GpsUtils
import com.msa.runningtrackinglocation.util.checkLocationPermission
import com.msa.runningtrackinglocation.util.isNetworkOrGPSEnabled
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import com.msa.eshop.R
class PiLocationException(message: String) : Exception(message)

@Singleton
class PiLocationManager @Inject constructor(@ApplicationContext val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    private var activity: Activity? = null
    var GPS_SETTINGS_REQUEST_CODE = 9234

    fun setActivity(activity: Activity) {
        this.activity = activity
    }

    @SuppressLint("MissingPermission")
    fun locationUpdates(intervalInMillis: Long): Flow<Location> {
        return callbackFlow {
            if (!context.checkLocationPermission()) {
                throw PiLocationException(context.getString(R.string.missing_location_permission))
            }

            if (!context.isNetworkOrGPSEnabled()) {
                turnOnGPS()
                throw PiLocationException(context.getString(R.string.network_or_gps_is_not_available))
            }

            // make the request
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalInMillis)
                .setWaitForAccurateLocation(false)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location: Location ->
                        trySend(location).isSuccess
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                Log.d("PiLocationManager", "fusedLocationClient: removing location updates")
                Timber.d("Producer coroutine is about to close")
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }
    }

    fun turnOnGPS() {
        val gpsUtils = GpsUtils(context)

        gpsUtils.checkGpsSettings(
            onSuccess = {
                // GPS is already enabled
            },
            onFailure = { exception ->
                try {
                    // Show the dialog to enable GPS
                    activity?.let {
                        exception.startResolutionForResult(it, GPS_SETTINGS_REQUEST_CODE)
                    }
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            }
        )
    }
}
