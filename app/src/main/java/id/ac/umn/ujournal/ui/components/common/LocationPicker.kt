package id.ac.umn.ujournal.ui.components.common

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import id.ac.umn.ujournal.ui.constant.INDONESIA_LAT_LONG
import java.io.IOException
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun LocationPicker(
    onLocationSelect: (coordinates: LatLng) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val defaultLatLong = INDONESIA_LAT_LONG
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLatLong, 10f)
    }
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fetchCurrentLatLong(fusedLocationClient){ location: Location? ->
                // TODO: remove log in production
                Log.d("LocationPicker", "lat long: $location")

                if (location != null) {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        LatLng(location.latitude, location.longitude),
                        10f
                    )
                }
            }
        } else {
            // TODO: improve error handling
            Log.d("LocationPicker", "Permission not granted")
        }
    }

    // Check if permission is granted, otherwise request it
    LaunchedEffect(Unit) {
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            PermissionChecker.PERMISSION_GRANTED -> {
                fetchCurrentLatLong(fusedLocationClient){ location: Location? ->
                    // TODO: remove log in production
                    Log.d("LocationPicker", "lat long: $location")

                    if (location != null) {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            LatLng(location.latitude, location.longitude),
                            10f
                        )
                    }
                }
            }
            else -> locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            UJournalTopAppBar(
                title = {
                    Text(
                        text = "Choose Location",
                    )
                },
                onBackButtonClick = onDismiss
            )
        },
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = {
                        onLocationSelect(cameraPositionState.position.target)
                    },
                ) {
                    Icon(Icons.Filled.Check, "Submit geolocation")
                }
                Spacer(Modifier.padding(bottom = 80.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ){
            Marker(
                state = MarkerState(position = cameraPositionState.position.target),
            )
        }
    }

}

@SuppressLint("MissingPermission")
private fun fetchCurrentLatLong(
    fusedLocationClient: FusedLocationProviderClient,
    onLatLongFetched: (Location?) -> Unit
) {
    val getCurrentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
        PRIORITY_HIGH_ACCURACY,
        CancellationTokenSource().token
    )

    getCurrentLocationTask.addOnSuccessListener { location ->
        Log.d("LocationPicker.fetchCurrentLatLong", "lat long: $location")
        location?.let {
            onLatLongFetched(location)
        }
    }.addOnFailureListener {
        // TODO: improve error handling
        Log.d("LocationPicker", "Error fetching Current Lat Long: ${it.message}")
    }
}

private fun getAddressFromLatLong(
    lat: Double,
    lon: Double,
    context: Context,
): Address? {
    var address: Address? = null
    val geocoder = Geocoder(context, Locale.getDefault())

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        geocoder.getFromLocation(lat, lon, 1) { list ->
            if (list.size != 0) {
                address = list[0]
            }
        }
    } else {
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                address = list[0]
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return address
}