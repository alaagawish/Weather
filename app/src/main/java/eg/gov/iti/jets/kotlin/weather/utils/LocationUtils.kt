package eg.gov.iti.jets.kotlin.weather.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import eg.gov.iti.jets.kotlin.weather.sharedPreferences
import java.util.*

class LocationUtils {
    companion object {
        fun getAddress(context: Context, latitude: Double, longitude: Double): String {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(latitude, longitude, 1) as List<Address>

            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
//                return address.getAddressLine(0)
                if (address.locality.isNullOrEmpty())
                    return address.getAddressLine(0)
                return address.locality
            }

            return ""
        }

        fun getLatLng(context: Context, location: String): Pair<Double?, Double?> {

            val geocoder = Geocoder(context, Locale.getDefault())
            val addressList = geocoder.getFromLocationName(location, 1)
            println("ffffffffff $location")
            var longitude: Double =
                sharedPreferences!!.getString(Constants.LONGITUDE, "1.0")!!.toDouble()
            var latitude: Double =
                sharedPreferences!!.getString(Constants.LATITUDE, "1.0")!!.toDouble()
            val address: Address
            if (addressList != null) {
                address = addressList[0]
                latitude = address.latitude
                longitude = address.longitude
            }
            return Pair(
                latitude,
                longitude
            )
        }
    }

}

fun checkLocationPermissions(context: Context) = ActivityCompat.checkSelfPermission(
    context, Manifest.permission.ACCESS_COARSE_LOCATION
) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
    context, Manifest.permission.ACCESS_FINE_LOCATION
) == PackageManager.PERMISSION_GRANTED

fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
    )
}


