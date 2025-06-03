package com.example.jsonplaceholderapi.ui.screen

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.jsonplaceholderapi.data.local.DataStoreManager
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.android.gms.location.LocationServices
import android.Manifest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MyProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        scope.launch {
            dataStoreManager.getName()?.let { name = it }
            dataStoreManager.getSurname()?.let { surname = it }
            dataStoreManager.getImagePath()?.let { imagePath = it }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val path = it.toString()
            imagePath = path
            scope.launch { dataStoreManager.saveImagePath(path) }
        }
    }

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val latLng = LatLng(it.latitude, it.longitude)
                        currentLocation = latLng
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 14f)
                    }
                }
            }
        } else {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    Column(
        modifier = Modifier
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Imię") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text("Nazwisko") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { galleryLauncher.launch("image/*") }) {
            Text("Wybierz zdjęcie")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            scope.launch {
                dataStoreManager.saveName(name)
                dataStoreManager.saveSurname(surname)
                dataStoreManager.saveImagePath(imagePath)
            }
        }) {
            Text("Zapisz dane")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (imagePath.isNotEmpty()) {
            Image(
                painter = rememberImagePainter(imagePath),
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Twoja lokalizacja:")
        Spacer(modifier = Modifier.height(8.dp))

        if (locationPermissionsState.allPermissionsGranted) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true)
                ) {
                    currentLocation?.let { location ->
                        Marker(
                            state = MarkerState(position = location),
                            title = "Tu jesteś"
                        )
                    }
                }
            }
        } else {
            Text("Proszę, nadaj uprawnienia lokalizacji aby zobaczyć mapę.")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { locationPermissionsState.launchMultiplePermissionRequest() }) {
                Text("Poproś o uprawnienia")
            }
        }
        Button(onClick = { navController.popBackStack() }) {
            Text("Wróć")
        }
    }
}