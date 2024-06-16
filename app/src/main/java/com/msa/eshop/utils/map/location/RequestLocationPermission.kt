package com.msa.eshop.utils.map.location

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun RequestLocationPermission(onPermissionResult: (Boolean) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> onPermissionResult(isGranted) }
    )

    val permissionCheckResult = remember {
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
        onPermissionResult(true)
    } else {
        LaunchedEffect(Unit) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}
