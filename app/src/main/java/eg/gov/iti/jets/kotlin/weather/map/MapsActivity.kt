package eg.gov.iti.jets.kotlin.weather.map

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import eg.gov.iti.jets.kotlin.weather.*
import eg.gov.iti.jets.kotlin.weather.utils.Constants.LATITUDE
import eg.gov.iti.jets.kotlin.weather.utils.Constants.LONGITUDE
import eg.gov.iti.jets.kotlin.weather.utils.Constants.SOURCE
import eg.gov.iti.jets.kotlin.weather.utils.Constants.TAG
import eg.gov.iti.jets.kotlin.weather.R
import eg.gov.iti.jets.kotlin.weather.databinding.ActivityMapsBinding
import eg.gov.iti.jets.kotlin.weather.db.DayDatabase
import eg.gov.iti.jets.kotlin.weather.db.LocalSource
import eg.gov.iti.jets.kotlin.weather.favourite.viewmodel.FavouriteViewModel
import eg.gov.iti.jets.kotlin.weather.favourite.viewmodel.FavouriteViewModelFactory
import eg.gov.iti.jets.kotlin.weather.viewmodel.HomeViewModel
import eg.gov.iti.jets.kotlin.weather.viewmodel.HomeViewModelFactory
import eg.gov.iti.jets.kotlin.weather.model.FavouritePlace
import eg.gov.iti.jets.kotlin.weather.model.Repository
import eg.gov.iti.jets.kotlin.weather.model.APIState
import eg.gov.iti.jets.kotlin.weather.network.DayClient
import eg.gov.iti.jets.kotlin.weather.utils.Constants
import eg.gov.iti.jets.kotlin.weather.utils.Constants.LOCATION
import eg.gov.iti.jets.kotlin.weather.utils.Constants.STR_LOCATION
import eg.gov.iti.jets.kotlin.weather.utils.LocationUtils.Companion.getAddress
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var latLng: LatLng
    private lateinit var mMap: GoogleMap
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var favouriteViewModel: FavouriteViewModel
    private lateinit var favouriteViewModelFactory: FavouriteViewModelFactory
    private lateinit var binding: ActivityMapsBinding

    @SuppressLint("LogNotTimber", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        homeViewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                DayClient.getInstance(),
                LocalSource(
                    DayDatabase.getInstance(this).getFavDao(),
                    DayDatabase.getInstance(this).getDayDao(),
                    DayDatabase.getInstance(this).getAlertsDao(),
                    DayDatabase.getInstance(this).getHourDao(),
                    DayDatabase.getInstance(this).getDailyDao()
                )
            )
        )
        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]
        favouriteViewModelFactory = FavouriteViewModelFactory(
            Repository.getInstance(
                DayClient.getInstance(),
                LocalSource(
                    DayDatabase.getInstance(this).getFavDao(),
                    DayDatabase.getInstance(this).getDayDao(),
                    DayDatabase.getInstance(this).getAlertsDao(),
                    DayDatabase.getInstance(this).getHourDao(),
                    DayDatabase.getInstance(this).getDailyDao()
                )
            )
        )

        favouriteViewModel =
            ViewModelProvider(this, favouriteViewModelFactory)[FavouriteViewModel::class.java]

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
        if (intent.getStringExtra(SOURCE) == "fav") {
            binding.addToFavButton.text = "Add to favourite"
        } else if (intent.getStringExtra(SOURCE) == "mapSettings") {
            binding.addToFavButton.text = "Select location"
        } else if (intent.getStringExtra(SOURCE) == Constants.BOARDING) {
            binding.addToFavButton.text = "Confirm Location"
        }
        mapFragment.getMapAsync { googleMap ->
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(10f))
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.setOnMapClickListener { latLng ->

                googleMap.addMarker(MarkerOptions().position(latLng).title("Chosen place"))

                this.latLng = latLng
            }
        }
        binding.addToFavButton.setOnClickListener {
            if (intent.getStringExtra(SOURCE) == "fav") {

                addPlaceToFav(latLng.latitude, latLng.longitude)
            } else if (intent.getStringExtra(SOURCE) == "mapSettings") {
                println("Map Activity map settings ")
                editor.putString(LATITUDE, latLng.latitude.toString())
                editor.putString(LONGITUDE, latLng.longitude.toString())
                editor.putString(LOCATION, "map")
                println(
                    "Map Activity map settings  ${sharedPreferences!!.getString(LATITUDE, "1")}  ${
                        sharedPreferences!!.getString(
                            LONGITUDE, "1.0"
                        )
                    } ${sharedPreferences!!.getString(LOCATION, "")}"
                )

                editor.apply()
                println(
                    "Map Activity map settings 2 ${sharedPreferences!!.getString(LATITUDE, "1")}  ${
                        sharedPreferences!!.getString(
                            LONGITUDE, "1.0"
                        )
                    } ${sharedPreferences!!.getString(LOCATION, "")}"
                )
                finish()

            } else if (intent.getStringExtra(SOURCE) == Constants.BOARDING) {
                editor.putString(LATITUDE, latLng.latitude.toString())
                editor.putString(LONGITUDE, latLng.longitude.toString())
                editor.putString(LOCATION, "map")

                editor.putString(STR_LOCATION, getAddress(this, latLng.latitude, latLng.longitude))
                editor.apply()
                startActivity(Intent(this@MapsActivity, MainActivity::class.java))

            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val sydney = LatLng(
            sharedPreferences!!.getString(LATITUDE, "1.0")?.toDouble()!!,
            sharedPreferences!!.getString(LONGITUDE, "1.0")?.toDouble()!!
        )
        latLng = sydney
        if (intent.getStringExtra(SOURCE) != "fav")
            mMap.addMarker(MarkerOptions().position(sydney).title("Current Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun addPlaceToFav(lat: Double, lon: Double) {
        homeViewModel.getForecastData(lat, lon)
        lifecycleScope.launch {
            homeViewModel.forecastStateFlow.collectLatest { result ->
                when (result) {
                    is APIState.Waiting -> {
                        Timber.tag(TAG).d("Maps onCreateView: waiting")
                    }
                    is APIState.Success -> {

                        val favouritePlace = FavouritePlace(
                            result.oneCall.current.dt,
                            result.oneCall.lat,
                            result.oneCall.lon,
                            result.oneCall.timezone,
                            result.oneCall.current.weather[0].main,
                            result.oneCall.current.weather[0].icon,
                            result.oneCall.current.temp
                        )
                        if (result.oneCall.lat != sharedPreferences?.getString(LATITUDE, "0.0")!!
                                .toDouble() && result.oneCall.lon != sharedPreferences?.getString(
                                LONGITUDE, "0.0"
                            )!!.toDouble()
                        ) {

                            favouriteViewModel.addPlaceToFav(favouritePlace)
                        }
                        finish()

                    }
                    else -> {
                        Snackbar.make(
                            this@MapsActivity.findViewById(android.R.id.content),
                            "Cant add this place to fav",
                            Snackbar.LENGTH_LONG
                        ).show()
                        finish()


                    }
                }

            }

        }
    }
}