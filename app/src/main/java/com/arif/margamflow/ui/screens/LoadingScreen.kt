package com.arif.margamflow.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.opencsv.CSVWriter
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LocationScreen() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val coroutineScope = rememberCoroutineScope()

    var isTracking by remember { mutableStateOf(false) }
    val locationList = remember { mutableStateListOf<LocationData>() }
    var tripStartTime by remember { mutableStateOf(0L) }

    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 5000L
    ).build()

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                for (location in result.locations) {

                    coroutineScope.launch {
                        val placeName = getPlaceNameAsync(context, location.latitude, location.longitude)
                        locationList.add(
                            LocationData(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                timestamp = System.currentTimeMillis(),
                                placeName = placeName
                            )
                        )
                    }
                }
            }
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        if (!granted) {
            Toast.makeText(context, "Location permission denied!", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    return@Button
                }

                if (!isTracking) {
                    locationList.clear()
                    tripStartTime = System.currentTimeMillis()

                    try {
                        fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            context.mainLooper
                        )
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                    Toast.makeText(context, "Trip started", Toast.LENGTH_SHORT).show()
                } else {
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                    coroutineScope.launch {
                        delay(1000L) // wait 1 second for location processing to finish
                        saveTripToCSV(context, locationList)
                    }
                }
                isTracking = !isTracking
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(if (isTracking) "End Trip" else "Start Trip")
        }
    }
}

private suspend fun getPlaceNameAsync(context: android.content.Context, lat: Double, lon: Double): String {
    return try {
        val geocoder = Geocoder(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // New async geocoder method for API 33+
            withContext(Dispatchers.IO) {
                val deferred = CompletableDeferred<List<Address>>()
                geocoder.getFromLocation(lat, lon, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: List<Address>) {
                        deferred.complete(addresses)
                    }

                    override fun onError(errorMessage: String?) {
                        deferred.complete(emptyList())
                    }
                })
                val addresses = deferred.await()
                if (addresses.isNotEmpty()) addresses[0].getAddressLine(0) ?: "Unknown" else "Unknown"
            }
        } else {
            // Deprecated synchronous method for API < 33
            val addresses = withContext(Dispatchers.IO) {
                geocoder.getFromLocation(lat, lon, 1)
            }
            if (!addresses.isNullOrEmpty()) addresses[0].getAddressLine(0) ?: "Unknown" else "Unknown"
        }
    } catch (e: Exception) {
        "Unknown"
    }
}

private fun saveTripToCSV(context: android.content.Context, locations: List<LocationData>) {
    try {
        if (locations.isEmpty()) {
            Toast.makeText(context, "No locations captured!", Toast.LENGTH_SHORT).show()
            return
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
        val fileName = "trip_${sdf.format(Date())}.csv"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        val writer = CSVWriter(FileWriter(file))

        writer.writeNext(arrayOf("Latitude", "Longitude", "Timestamp", "Place Name"))
        locations.forEach {
            writer.writeNext(
                arrayOf(
                    it.latitude.toString(),
                    it.longitude.toString(),
                    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(it.timestamp)),
                    it.placeName
                )
            )
        }
        writer.close()

        Toast.makeText(context, "Trip saved at:\n${file.absolutePath}", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save trip", Toast.LENGTH_LONG).show()
    }
}

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val placeName: String
)
