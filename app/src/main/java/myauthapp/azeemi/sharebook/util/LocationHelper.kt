package myauthapp.azeemi.sharebook.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LocationHelper(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    
    suspend fun getCurrentLocation(): Result<Location> {
        return try {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return Result.failure(Exception("Location permission not granted"))
            }
            
            val location = suspendCancellableCoroutine<Location> { continuation ->
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            continuation.resumeWith(Result.success(location))
                        } else {
                            continuation.resumeWithException(Exception("Location not available"))
                        }
                    }
                    .addOnFailureListener { e ->
                        continuation.resumeWithException(e)
                    }
            }
            
            Result.success(location)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAddressFromLocation(location: Location): Result<String> {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            
            if (addresses.isNullOrEmpty()) {
                return Result.failure(Exception("No address found"))
            }
            
            val address = addresses[0]
            val addressText = buildString {
                append(address.getAddressLine(0))
            }
            
            Result.success(addressText)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}