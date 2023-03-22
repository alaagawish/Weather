package eg.gov.iti.jets.kotlin.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import eg.gov.iti.jets.kotlin.weather.databinding.ActivityMainBinding
import eg.gov.iti.jets.kotlin.weather.databinding.ActivityOnboardingBinding
import java.util.*

const val LANGUAGE = "LANGUAGE"
const val NAME = "WEATHER"
const val UNIT = "UNIT"
//const val LOCATION = "LOCATION"
const val LONGITUDE = "LONGITUDE"
const val LATITUDE = "LATITUDE"
const val PERMISSION_ID = 2

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(binding.root)
        binding.saveSettingMaterialButton.setOnClickListener {
//            if (sharedPreferences.getString(LOCATION, "gps") == "gps") {
////                getLocation()
//            }
            finish()
        }
        binding.animation.playAnimation()
        editor = sharedPreferences.edit()

        editor.putString(LANGUAGE, "en")
//        editor.putString(LOCATION, "gps")
        editor.putString(UNIT, "metric")
        editor.apply()
//        val value = sharedPreferences.getString(UNIT, "metric")

        binding.languagesIntroRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            when (radioButton.text) {
                "English" -> editor.putString(LANGUAGE, "en")
                "Arabic" -> editor.putString(LANGUAGE, "ar")
            }
            editor.apply()
        }
        binding.standardIntroRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            when (radioButton.text) {
                "℃ - Meter/Sec" -> editor.putString(UNIT, "metric")
                "℉ - Mile/Hour" -> editor.putString(UNIT, "imperial")
                "K - Meter/Sec" -> editor.putString(UNIT, "standard")
            }
            editor.apply()
        }
//        binding.locationIntroRadioGroup.setOnCheckedChangeListener { group, checkedId ->
//            val radioButton = findViewById<RadioButton>(checkedId)
//            when (radioButton.text) {
//                "Map" -> editor.putString(LOCATION, "map")
//                "GPS" -> editor.putString(LOCATION, "gps")
//            }
//            editor.apply()
//        }


    }

    override fun onStart() {
        super.onStart()
        getLocation()

    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                requestNewLocation()
            } else {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("TAG", "onRequestPermissionsResult: $requestCode")
        if (requestCode == PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getLocation()
        }
    }

    private fun checkPermissions() = ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    fun requestNewLocation() {
        val locationRequest = LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(5)
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation = p0.lastLocation
            if (lastLocation != null) {
                val latitude = lastLocation.latitude
                val longitude = lastLocation.longitude
                editor.putString(LONGITUDE, longitude.toString())
                editor.putString(LATITUDE, latitude.toString())
                editor.apply()
                Log.d("TAG", "onLocationResult: ${lastLocation.latitude}")
//                val myLocation = Geocoder(applicationContext, Locale.getDefault())
//                val addressList =
//                    myLocation.getFromLocation(lastLocation.latitude, lastLocation.longitude, 1)

//                if (addressList != null && addressList.isNotEmpty()) {
//                    val address = addressList[0]
//                    sb = StringBuilder()
//                    for (i in 0 until address.maxAddressLineIndex) {
//                        sb.append(address.getAddressLine(i)).append("\n")
//                    }
//                    if (address.premises != null)
//                        sb.append(address.premises).append(",\n ")
//                    sb.append(address.subAdminArea).append("\n")
//                    sb.append(address.locality).append(",\n ")
//                    sb.append(address.adminArea).append(",\n ")
//                    sb.append(address.countryName).append(",\n ")
//                    sb.append(address.postalCode)
//
//                }
            }
        }
    }

}