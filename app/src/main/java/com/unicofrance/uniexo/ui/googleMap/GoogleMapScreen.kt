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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.unicofrance.uniexo.data.local.database.entities.Container
import com.unicofrance.uniexo.ui.container.ContainerScreen

/*
*  Map screen that load only the visible container on the map
*  Require and display user position on the map
*
* */
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
                        CameraUpdateFactory.newLatLngZoom(USER_POSITION_MOCK, 15f) // I use a mock for the position to simplify test
                    )
                    viewModel.getVisibleContainer(
                        cameraPositionState.projection?.visibleRegion?.latLngBounds
                    )
                }
            }
        }
    }

    val locations by viewModel.locations.collectAsStateWithLifecycle()

    val visibleLocations by viewModel.visiblePoint.collectAsStateWithLifecycle()

    val containerSelected = remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(cameraPositionState) {
        snapshotFlow {
            Pair(
                cameraPositionState.isMoving,
                cameraPositionState?.projection?.visibleRegion?.latLngBounds
            )
        }.collect { (isMoving, bound) ->
            if(!isMoving && bound != null) {
                viewModel.getVisibleContainer(
                    cameraPositionState.projection?.visibleRegion?.latLngBounds
                )
            }
        }
    }
    val container = remember {
        mutableStateOf<Container?>(null)
    }

    val mapProperties = remember(hasLocationPermission) {
        MapProperties(
            isMyLocationEnabled = hasLocationPermission
        )
    }

    val mapUiSettings = remember(hasLocationPermission) {
        MapUiSettings(
            myLocationButtonEnabled = hasLocationPermission
        )
    }

    when (containerSelected.intValue) {
        0 -> {
            GoogleMap(
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = mapUiSettings,
                modifier = modifier
            ) {
                visibleLocations.forEach { containerLoc ->
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
                Marker (
                    state = rememberUpdatedMarkerState(
                        USER_POSITION_MOCK
                    ),
                    title = "User position Mock",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )
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

val USER_POSITION_MOCK = LatLng(
    43.335833,
    3.225556
)
// I use mock for the start position on the map because i try the app on my personal phone