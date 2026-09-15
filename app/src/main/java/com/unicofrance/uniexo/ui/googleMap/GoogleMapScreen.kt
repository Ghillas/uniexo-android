package com.unicofrance.uniexo.ui.googleMap

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.unicofrance.uniexo.data.local.database.entities.Container
import com.unicofrance.uniexo.ui.container.ContainerScreen

@Composable
fun GoogleMapScreen(
    modifier: Modifier = Modifier,
    viewModel: GoogleMapViewModel
) {
    val context = LocalContext.current

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPositionState = rememberCameraPositionState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    userLocation = latLng
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                    )
                }
            }
        }
    }

    val locations by viewModel.locations.collectAsStateWithLifecycle()

    val containerSelected = remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(locations) {
        locations.firstOrNull()?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                it,
                10f
            )
        }
    }
    val container = remember {
        mutableStateOf<Container?>(null)
    }

    val mapProperties = remember {
        MapProperties(
            isMyLocationEnabled = hasLocationPermission
        )
    }

    when (containerSelected.intValue) {
        0 -> {
            GoogleMap(
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = hasLocationPermission
                ),
                modifier = modifier
            ) {
                locations.forEach { containerLoc ->
                    Marker(
                        state = rememberUpdatedMarkerState(
                            containerLoc
                        ),
                        onClick = {
                            container.value = viewModel.getContainer(containerLoc)
                            containerSelected.intValue = 1
                            false
                        },
                    )
                }
            }
        }
        1 -> {
            container.value?.let {
                ContainerScreen(
                    it,
                    containerSelected
                )
            }
        }
    }
}



