package eg.gov.iti.jets.kotlin.weather.onboarding

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.RadioButton
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import eg.gov.iti.jets.kotlin.weather.MainActivity
import eg.gov.iti.jets.kotlin.weather.R
import eg.gov.iti.jets.kotlin.weather.utils.Constants.LANGUAGE
import eg.gov.iti.jets.kotlin.weather.utils.Constants.LATITUDE
import eg.gov.iti.jets.kotlin.weather.utils.Constants.LONGITUDE
import eg.gov.iti.jets.kotlin.weather.utils.Constants.PERMISSION_ID
import eg.gov.iti.jets.kotlin.weather.utils.Constants.TAG
import eg.gov.iti.jets.kotlin.weather.utils.Constants.UNIT
import eg.gov.iti.jets.kotlin.weather.databinding.ActivityOnboardingBinding
import eg.gov.iti.jets.kotlin.weather.editor
import eg.gov.iti.jets.kotlin.weather.map.MapsActivity
import eg.gov.iti.jets.kotlin.weather.sharedPreferences
import eg.gov.iti.jets.kotlin.weather.utils.Constants
import eg.gov.iti.jets.kotlin.weather.utils.Constants.BOARDING
import eg.gov.iti.jets.kotlin.weather.utils.Constants.SOURCE
import eg.gov.iti.jets.kotlin.weather.utils.Constants.STR_LOCATION
import eg.gov.iti.jets.kotlin.weather.utils.LocationUtils
import eg.gov.iti.jets.kotlin.weather.utils.checkLocationPermissions
import eg.gov.iti.jets.kotlin.weather.utils.isLocationEnabled
import java.util.*


class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(binding.root)
        binding.saveSettingMaterialButton.setOnClickListener {
            if (binding.arabicIntroRadioButton.isChecked)
                editor.putString(LANGUAGE, "ar")
            else if (binding.englishIntroRadioButton.isChecked)
                editor.putString(LANGUAGE, "en")
            if (binding.gpsIntroRadioButton.isChecked)
                editor.putString(Constants.LOCATION, "gps")
            else if (binding.mapIntroRadioButton.isChecked)
                editor.putString(Constants.LOCATION, "map")
            if (binding.metricIntroRadioButton.isChecked)
                editor.putString(UNIT, "metric")
            else if (binding.imperialIntroRadioButton.isChecked)
                editor.putString(UNIT, "imperial")
            else if (binding.kelvinIntroRadioButton.isChecked)
                editor.putString(UNIT, "standard")
            editor.apply()




            if (sharedPreferences.getString(Constants.LOCATION, "gps") == "gps") {
                if (checkLocationPermissions(this)) {
                    if (isLocationEnabled(this)) {
                        requestNewLocation()
                        getLocation()

                    } else {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        getLocation()

                    }
                } else {
                    requestPermissions()
                }
                getLocation()

            } else {
                val intent = Intent(this@OnboardingActivity, MapsActivity::class.java)
                intent.putExtra(SOURCE, BOARDING)
                startActivity(intent)
            }

        }
        binding.animation.playAnimation()

//        binding.languagesIntroRadioGroup.setOnCheckedChangeListener { _, checkedId ->
//            val radioButton = findViewById<RadioButton>(checkedId)
//            when (radioButton.text) {
//                this.getString(R.string.english) -> editor.putString(LANGUAGE, "en")
//                this.getString(R.string.arabic) -> editor.putString(LANGUAGE, "ar")
//            }
//            editor.apply()
//        }
//        binding.standardIntroRadioGroup.setOnCheckedChangeListener { _, checkedId ->
//            val radioButton = findViewById<RadioButton>(checkedId)
//            when (radioButton.text) {
//                this.getString(R.string.celsius) -> editor.putString(UNIT, "metric")
//                this.getString(R.string.fahrenheit) -> editor.putString(UNIT, "imperial")
//                this.getString(R.string.kelvin) -> editor.putString(UNIT, "standard")
//            }
//            editor.apply()
//        }
//        binding.locationIntroRadioGroup.setOnCheckedChangeListener { _, checkedId ->
//            val radioButton = findViewById<RadioButton>(checkedId)
//            when (radioButton.text) {
//                this.getString(R.string.map) -> editor.putString(Constants.LOCATION, "map")
//                this.getString(R.string.gps) -> editor.putString(Constants.LOCATION, "gps")
//
//            }
//            editor.apply()
//        }

    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        if (checkLocationPermissions(this)) {
            if (isLocationEnabled(this)) {
                requestNewLocation()
            } else {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

            }
        } else {
            requestPermissions()
        }
    }

    fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
    }

    @SuppressLint("LogNotTimber")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult: $requestCode")
        if (requestCode == PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) getLocation()
        }
    }


    @SuppressLint("MissingPermission")
    fun requestNewLocation() {
        val locationRequest = LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(5)
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        @SuppressLint("LogNotTimber")
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation = p0.lastLocation
            if (lastLocation != null) {
                val latitude = lastLocation.latitude
                val longitude = lastLocation.longitude
                if (sharedPreferences.getString(Constants.LOCATION, "gps") == "gps") {
                    editor.putString(LONGITUDE, longitude.toString())
                    editor.putString(LATITUDE, latitude.toString())
                    Log.d(TAG, "On Boarding onLocationResult: ${lastLocation.latitude}")

                    editor.putString(
                        STR_LOCATION,
                        LocationUtils.getAddress(
                            this@OnboardingActivity,
                            lastLocation.latitude,
                            lastLocation.longitude
                        )
                    )
                    editor.apply()
                }
//                editor.putString(LONGITUDE, longitude.toString())
//                editor.putString(LATITUDE, latitude.toString())
//                editor.apply()
//                Log.d(TAG, "On Boarding onLocationResult: ${lastLocation.latitude}")
//                val myLocation = Geocoder(applicationContext, Locale.getDefault())
//                val addressList =
//                    myLocation.getFromLocation(lastLocation.latitude, lastLocation.longitude, 1)
//
//                if (addressList != null && addressList.isNotEmpty()) {
//                    val address = addressList[0]
//                    val sb = StringBuilder()
//
//                    sb.append(address.countryName)
//                    println("countryyyyyyyyyyyyyyyyyy: ${address.countryName}")

//                    editor.putString(STR_LOCATION, sb.toString())
//                    editor.apply()

                val intent = Intent(this@OnboardingActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
//            }
            }
        }
    }

}