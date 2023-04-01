package eg.gov.iti.jets.kotlin.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.google.android.gms.location.*
import eg.gov.iti.jets.kotlin.weather.utils.Constants.LANGUAGE
import eg.gov.iti.jets.kotlin.weather.utils.Constants.NAME
//import eg.gov.iti.jets.kotlin.weather.alert.createNotificationChannel
import eg.gov.iti.jets.kotlin.weather.databinding.ActivityMainBinding
import eg.gov.iti.jets.kotlin.weather.onboarding.OnboardingActivity
import eg.gov.iti.jets.kotlin.weather.utils.Constants.LATITUDE
import eg.gov.iti.jets.kotlin.weather.utils.Constants.LOCATION
import eg.gov.iti.jets.kotlin.weather.utils.Constants.LONGITUDE
import eg.gov.iti.jets.kotlin.weather.utils.Constants.PERMISSION_ID
import eg.gov.iti.jets.kotlin.weather.utils.Constants.STR_LOCATION
import eg.gov.iti.jets.kotlin.weather.utils.Constants.TAG
import eg.gov.iti.jets.kotlin.weather.utils.LocationUtils
import eg.gov.iti.jets.kotlin.weather.utils.checkLocationPermissions
import eg.gov.iti.jets.kotlin.weather.utils.isLocationEnabled
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val locale = sharedPreferences.getString(LANGUAGE, "en")?.let { Locale(it) }
        if (locale != null) {
            Locale.setDefault(locale)
        }

        val res: Resources = this.resources
        val configuration = Configuration(res.configuration)
        configuration.locale = locale
        res.updateConfiguration(configuration, res.displayMetrics)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.favouriteFragment, R.id.alertFragment, R.id.settingsFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        finishAffinity()

    }

    override fun onStart() {
        super.onStart()
        if (sharedPreferences.getString(LOCATION, "gps") == "gps") {
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

        }
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

    private fun requestPermissions() {
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
        locationRequest.setInterval(1000)
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
                if (sharedPreferences.getString(LOCATION, "gps") == "gps") {
                    editor.putString(LONGITUDE, longitude.toString())
                    editor.putString(LATITUDE, latitude.toString())
                    Log.d(TAG, "Main Activity onLocationResult: ${lastLocation.latitude}")

                    editor.putString(
                        STR_LOCATION,
                        LocationUtils.getAddress(
                            this@MainActivity,
                            lastLocation.latitude,
                            lastLocation.longitude
                        )
                    )
                    editor.apply()
                }
            }
        }
    }

}
