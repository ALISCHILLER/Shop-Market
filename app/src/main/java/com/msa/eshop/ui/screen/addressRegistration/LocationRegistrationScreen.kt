package com.msa.eshop.ui.screen.addressRegistration

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msa.eshop.R
import com.msa.eshop.utils.map.osm.OpenStreetMap
import com.msa.eshop.ui.common.topBar.TopBarDetails
import com.msa.eshop.utils.map.osm.Marker
import com.msa.eshop.utils.map.osm.MarkerLabeled
import com.msa.eshop.utils.map.osm.model.LabelProperties
import com.msa.eshop.utils.map.osm.rememberCameraState
import com.msa.eshop.utils.map.osm.rememberMarkerState
import android.graphics.Paint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.ui.theme.barcolorlight2
import com.msa.eshop.utils.map.location.RequestLocationPermission
import com.msa.eshop.utils.map.location.getCurrentLocation

import com.utsman.osmapp.Coordinates
import org.osmdroid.util.GeoPoint

@Composable
@Preview
fun LocationRegistrationScreen(
    modifier: Modifier = Modifier,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val locationState = remember { mutableStateOf("Unknown") }
    val permissionGranted = remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()



    LaunchedEffect(state) {
        viewModel.startLocationUpdates()
    }

    val location by viewModel.location.collectAsState()


    RequestLocationPermission { granted ->
        permissionGranted.value = granted
    }


      getCurrentLocation(context) { location -> locationState.value = location }

    val cameraState = rememberCameraState {
        geoPoint = Coordinates.iran
        zoom = 6.0
    }
    val markerLocation = rememberMarkerState(
        geoPoint = Coordinates.iran,
        rotation = 90f
    )

    location?.let {
        cameraState.geoPoint = GeoPoint(it.latitude, it.longitude)
        cameraState. zoom = 15.0
        markerLocation.geoPoint = GeoPoint(it.latitude, it.longitude)
    }

    val depokIcon: Drawable? by remember {
        mutableStateOf(context.getDrawable(R.drawable.round_eject_24))
    }

    val jakartaLabelProperties = remember {
        mutableStateOf(
            LabelProperties(
                labelColor = android.graphics.Color.RED,
                labelTextSize = 40f,
                labelAlign = Paint.Align.CENTER,
                labelTextOffset = 30f
            )
        )
    }
    Scaffold(
        modifier = modifier
            .background(color = Color.White),
        topBar = {
            TopBarDetails("ثبت لوکیشن")
        },
    ) {



        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = modifier
                    .padding(it)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    Modifier
                        .padding(vertical = 5.dp, horizontal = 5.dp)
                        .shadow(5.dp)
                        .background(color = MaterialTheme.colors.surface)
                        .weight(1.0f)
                        .fillMaxSize()
                        .padding(horizontal = 5.dp)
                ) {

                    OpenStreetMap(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize(),
                        onMapClick = {
                            markerLocation.geoPoint= it
                        },
                        cameraState = cameraState
                    ) {
                        Marker(
                            state = markerLocation,
                            icon = depokIcon,
                            title = "Depok",
                            snippet = "Jawa barat"
                        ) {

                            Column(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(
                                        color = Color.Gray,
                                        shape = RoundedCornerShape(7.dp)
                                    ),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = it.title)
                                Text(text = it.snippet, fontSize = 10.sp)
                            }
                        }

                    }
                }


                Box(
                    Modifier
                        .padding(vertical = 5.dp, horizontal = 5.dp)
                        .shadow(5.dp)
                        .background(color = MaterialTheme.colors.surface)
                        .height(64.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp)
                ) {
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 3.dp, vertical = 3.dp),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Button(
                                onClick = {
                                },
                                modifier = Modifier
                                    .padding(5.dp)
                                    .fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    stringResource(id = R.string.address_registration),
                                    style = Typography.titleSmall,
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 3.dp, vertical = 3.dp),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Button(
                                onClick = {
                                    viewModel.navigateToAddressRegistration()
                                },
                                modifier = Modifier
                                    .padding(5.dp)
                                    .fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, color = barcolorlight2)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "LocationOn",
                                    tint = barcolorlight2
                                )
                                Text(
                                    stringResource(id = R.string.new_address),
                                    style = Typography.titleSmall,
                                    color = barcolorlight2
                                )
                            }
                        }

                    }
                }
            }
        }


    }
}

