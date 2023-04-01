package eg.gov.iti.jets.kotlin.weather.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
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
    }
}
