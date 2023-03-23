package eg.gov.iti.jets.kotlin.weather.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import java.util.*
//
//@SuppressLint("MissingPermission")
//private fun getLocation() {
//    if (checkPermissions()) {
//        if (isLocationEnabled()) {
//            requestNewLocation()
//        } else {
//            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
//        }
//    } else {
//        requestPermissions()
//    }
//}
//
//private fun requestPermissions() {
//    ActivityCompat.requestPermissions(
//        this,
//        arrayOf(
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ),
//        PERMISSION_ID
//    )
//}
//
//override fun onRequestPermissionsResult(
//    requestCode: Int,
//    permissions: Array<out String>,
//    grantResults: IntArray
//) {
//    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    Log.d("TAG", "onRequestPermissionsResult: $requestCode")
//    if (requestCode == PERMISSION_ID) {
//        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
//            getLocation()
//    }
//}
//
//private fun checkPermissions() = ActivityCompat.checkSelfPermission(
//    this,
//    Manifest.permission.ACCESS_COARSE_LOCATION
//) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
//    this,
//    Manifest.permission.ACCESS_FINE_LOCATION
//) == PackageManager.PERMISSION_GRANTED
//
//
//private fun isLocationEnabled(): Boolean {
//    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
//        LocationManager.NETWORK_PROVIDER
//    )
//}
//
//@SuppressLint("MissingPermission")
//fun requestNewLocation() {
//    val locationRequest = LocationRequest()
//    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//    locationRequest.setInterval(0)
//    fusedLocationClient.requestLocationUpdates(
//        locationRequest,
//        locationCallback,
//        Looper.myLooper()
//    )
//}
//
//private val locationCallback = object : LocationCallback() {
//    override fun onLocationResult(p0: LocationResult) {
//        val lastLocation = p0.lastLocation
//        if (lastLocation != null) {
//            l = lastLocation.latitude
//            lg = lastLocation.longitude
//            latitude.text = lastLocation.latitude.toString()
//            longitude.text = lastLocation.longitude.toString()
//            Log.d("TAG", "onLocationResult: ${lastLocation.latitude}")
//            val myLocation = Geocoder(applicationContext, Locale.getDefault())
//            val addressList =
//                myLocation.getFromLocation(lastLocation.latitude, lastLocation.longitude, 1)
//
//            if (addressList != null && addressList.isNotEmpty()) {
//                val address = addressList[0]
//                sb = StringBuilder()
//                for (i in 0 until address.maxAddressLineIndex) {
//                    sb.append(address.getAddressLine(i)).append("\n")
//                }
//                if (address.premises != null)
//                    sb.append(address.premises).append(",\n ")
//                sb.append(address.subAdminArea).append("\n")
//                sb.append(address.locality).append(",\n ")
//                sb.append(address.adminArea).append(",\n ")
//                sb.append(address.countryName).append(",\n ")
//                sb.append(address.postalCode)
//                location.text = sb.toString()
//
//            }
//        }
//    }
//}
