package eg.gov.iti.jets.kotlin.weather.settings.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import eg.gov.iti.jets.kotlin.weather.*
import eg.gov.iti.jets.kotlin.weather.utils.Constants.LANGUAGE
import eg.gov.iti.jets.kotlin.weather.utils.Constants.LATITUDE
import eg.gov.iti.jets.kotlin.weather.utils.Constants.LONGITUDE
import eg.gov.iti.jets.kotlin.weather.utils.Constants.NOTIFICATION
import eg.gov.iti.jets.kotlin.weather.utils.Constants.PERMISSION_ID
import eg.gov.iti.jets.kotlin.weather.utils.Constants.TAG
import eg.gov.iti.jets.kotlin.weather.utils.Constants.UNIT
import eg.gov.iti.jets.kotlin.weather.R
import eg.gov.iti.jets.kotlin.weather.databinding.FragmentSettingsBinding
import eg.gov.iti.jets.kotlin.weather.db.LocalSource
import eg.gov.iti.jets.kotlin.weather.map.MapsActivity
import eg.gov.iti.jets.kotlin.weather.model.Repository
import eg.gov.iti.jets.kotlin.weather.network.DayClient
import eg.gov.iti.jets.kotlin.weather.settings.viewmodel.SettingsViewModel
import eg.gov.iti.jets.kotlin.weather.settings.viewmodel.SettingsViewModelFactory
import eg.gov.iti.jets.kotlin.weather.utils.Constants
import eg.gov.iti.jets.kotlin.weather.utils.Constants.STR_LOCATION
import java.util.*

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var settingsViewModelFactory: SettingsViewModelFactory
    lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsViewModelFactory = SettingsViewModelFactory(
            Repository.getInstance(
                DayClient.getInstance(),
                LocalSource(requireContext())
            )
        )
        settingsViewModel = ViewModelProvider(
            this, settingsViewModelFactory
        )[SettingsViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (sharedPreferences.getString(LANGUAGE, null) == "en") {
            binding.englishRadioButton.isChecked = true
        } else
            binding.arabicRadioButton.isChecked = true

        if (sharedPreferences.getString(Constants.LOCATION, null) == "map") {
            binding.mapRadioButton.isChecked = true
        } else
            binding.gpsRadioButton.isChecked = true

        binding.enableNotificationsSwitch.isChecked =
            sharedPreferences.getString(NOTIFICATION, null) == "enable"

        if (sharedPreferences.getString(UNIT, null) == "metric") {
            binding.celsiusRadioButton.isChecked = true
        } else if (sharedPreferences.getString(UNIT, null) == "imperial")
            binding.fahrenheitRadioButton.isChecked = true
        else
            binding.kelvinRadioButton.isChecked = true

        val notificationManager = NotificationManagerCompat.from(requireContext())


        binding.languagesRadioGroup.setOnCheckedChangeListener { _, i ->
            val radioButton = view.findViewById<RadioButton>(i)
            when (radioButton.text) {
                context?.getString(R.string.english) -> editor.putString(LANGUAGE, "en")
                context?.getString(R.string.arabic) -> editor.putString(LANGUAGE, "ar")
            }
            editor.apply()

            val locale = sharedPreferences.getString(LANGUAGE, "en")?.let { Locale(it) }
            if (locale != null) {
                Locale.setDefault(locale)
            }
            val res: Resources = context?.resources!!
            val configuration = Configuration(res.configuration)
            configuration.locale = locale
            res.updateConfiguration(configuration, res.displayMetrics)

            val mainActivity = activity as MainActivity
            mainActivity.navController.navigate(R.id.settingsFragment)

        }
        binding.locationRadioGroup.setOnCheckedChangeListener { _, i ->
            val radioButton = view.findViewById<RadioButton>(i)
            when (radioButton.text) {
                context?.getString(R.string.map) -> {
                    editor.putString(Constants.LOCATION, "map")
                    editor.apply()

                    val intent = Intent(requireContext(), MapsActivity::class.java)
                    intent.putExtra(Constants.SOURCE, "mapSettings")
                    startActivity(intent)
                }
                context?.getString(R.string.gps) -> {
                    editor.putString(Constants.LOCATION, "gps")
                    getLocation()
                }

            }
            editor.apply()
        }

        binding.enableNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {


                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    println("enable notification permission")
                    if (!notificationManager.areNotificationsEnabled()) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.parse("package:eg.gov.iti.jets.kotlin.weather")
                        startActivity(intent)

                    }

                }
                if (notificationManager.areNotificationsEnabled()) {
                    editor.putString(NOTIFICATION, "enable")
                    editor.apply()
                }

            } else {
                editor.putString(NOTIFICATION, "disable")
                editor.apply()
                notificationManager.cancelAll()


            }
        }


        binding.unitsGroup.setOnCheckedChangeListener { _, i ->
            val radioButton = view.findViewById<RadioButton>(i)
            when (radioButton.text) {

                context?.getString(R.string.celsius) -> editor.putString(UNIT, "metric")
                context?.getString(R.string.fahrenheit) -> editor.putString(UNIT, "imperial")
                context?.getString(R.string.kelvin) -> editor.putString(UNIT, "standard")
            }
            editor.apply()

        }

    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
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
            requireActivity(),
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
        Log.d(TAG, "onRequestPermissionsResult: $requestCode")
        println("done or not kkkkkkkk ")
//        binding.enableNotificationsSwitch.isChecked =
//            requestCode == 123 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED

        if (requestCode == PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getLocation()
        }
    }

    private fun checkPermissions() = ActivityCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    private fun isLocationEnabled(): Boolean {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocation() {
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

                if (sharedPreferences.getString(Constants.LOCATION, "gps") == "gps") {
                    editor.putString(LONGITUDE, longitude.toString())
                    editor.putString(LATITUDE, latitude.toString())
                    Log.d(TAG, "Settings onLocationResult: ${lastLocation.latitude}")

                    editor.putString(
                        STR_LOCATION,
                        "settings geocoder error"
//                        LocationUtils.getAddress(
//                            requireContext(),
//                            activity?.applicationContext!!,
//                            lastLocation.latitude,
//                            lastLocation.longitude
//                        )
                    )
                    editor.apply()
                }
//                editor.putString(LONGITUDE, longitude.toString())
//                editor.putString(LATITUDE, latitude.toString())
//                editor.apply()
//                Log.d(TAG, " Settings onLocationResult: ${lastLocation.latitude}")
//                val myLocation = Geocoder(applicationContext, Locale.getDefault())
//                val addressList =
//                    myLocation.getFromLocation(lastLocation.latitude, lastLocation.longitude, 1)
//
//                if (addressList != null && addressList.isNotEmpty()) {
//                    val address = addressList[0]
//                    val sb = StringBuilder()
//                    for (i in 0 until address.maxAddressLineIndex) {
//                        sb.append(address.getAddressLine(i)).append("\n")
//                    }
//                    sb.append(address.countryName).append(",")
//                    if (address.premises != null)
//                        sb.append(address.premises).append(", ")
//                    sb.append(address.adminArea).append(", ")
//                    sb.append(address.locality).append(", ")
//                    sb.append(address.subAdminArea)
//  //                  sb.append(address.postalCode)
//
//                    editor.putString(STRLOCATION, sb.toString())
//
//                    editor.apply()
//                }
            }
        }
    }

}