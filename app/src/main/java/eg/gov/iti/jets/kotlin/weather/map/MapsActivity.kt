package eg.gov.iti.jets.kotlin.weather.map

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
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
import eg.gov.iti.jets.kotlin.weather.utils.LocationUtils
import eg.gov.iti.jets.kotlin.weather.utils.LocationUtils.Companion.getAddress
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mutableSharedFlow = MutableSharedFlow<String>()

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
            binding.addToFavButton.text = getString(R.string.add_to_fav)
        } else if (intent.getStringExtra(SOURCE) == "mapSettings") {
            binding.addToFavButton.text = getString(R.string.select_loc)
        } else if (intent.getStringExtra(SOURCE) == Constants.BOARDING) {
            binding.addToFavButton.text = getString(R.string.confirm_location)
        }

        mapFragment.getMapAsync { googleMap ->
            if (binding.searchLocationEditText.text.isNullOrBlank()) {
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(10f))
                googleMap.uiSettings.isZoomControlsEnabled = true
                googleMap.setOnMapClickListener { latLng ->
                    googleMap.clear()
                    googleMap.addMarker(MarkerOptions().position(latLng).title("Chosen place"))

                    this.latLng = latLng
                }
            }
        }
        lifecycleScope.launch {
            mutableSharedFlow.collectLatest {
                delay(100)
                if (it.length >= 5) {
                    val latLngUsingString = LocationUtils.getLatLng(this@MapsActivity, it)
                    if (latLngUsingString.first != null && latLngUsingString.second != null)
                        mapFragment.getMapAsync { googleMap ->
                            googleMap.clear()
                            val latLngg = LatLng(
                                latLngUsingString.first!!,
                                latLngUsingString.second!!
                            )
                            val cameraPosition = CameraPosition.Builder()
                                .target(latLngg)
                                .zoom(7f)
                                .build()
                            googleMap.animateCamera(
                                CameraUpdateFactory.newCameraPosition(
                                    cameraPosition
                                )
                            )

                            googleMap.addMarker(
                                MarkerOptions().position(latLngg).title("Chosen place")
                            )
                            this@MapsActivity.latLng = latLngg
                            googleMap.setOnMapClickListener { latLng ->
                                googleMap.clear()
                                googleMap.addMarker(
                                    MarkerOptions().position(latLng).title("Chosen place")
                                )

                                this@MapsActivity.latLng = latLng
                            }
                            googleMap.uiSettings.isZoomControlsEnabled = true
                        }

                }
            }
        }
        binding.searchLocationEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (p0 != null)
                    lifecycleScope.launch {
                        mutableSharedFlow.emit(p0.toString())
                    }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.addToFavButton.setOnClickListener {
            if (intent.getStringExtra(SOURCE) == "fav") {

                addPlaceToFav(latLng.latitude, latLng.longitude)
            } else if (intent.getStringExtra(SOURCE) == "mapSettings") {
                editor.putString(LATITUDE, latLng.latitude.toString())
                editor.putString(LONGITUDE, latLng.longitude.toString())
                editor.putString(LOCATION, "map")
                editor.apply()
                finish()

            } else if (intent.getStringExtra(SOURCE) == Constants.BOARDING) {
                editor.putString(LATITUDE, latLng.latitude.toString())
                editor.putString(LONGITUDE, latLng.longitude.toString())
                editor.putString(LOCATION, "map")
                var cityy = getAddress(this, latLng.latitude, latLng.longitude)
                if (cityy != "") {
                    cityy = getAddress(this, latLng.latitude, latLng.longitude)
                }
                editor.putString(STR_LOCATION, cityy)

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
                        val city = getAddress(
                            this@MapsActivity,
                            result.oneCall.lat,
                            result.oneCall.lon
                        )
                        val favouritePlace = FavouritePlace(
                            dt = result.oneCall.current.dt,
                            lat = result.oneCall.lat,
                            lon = result.oneCall.lon,
                            timezone = if (city == "") result.oneCall.timezone else city,
                            main = result.oneCall.current.weather[0].main,
                            icon = result.oneCall.current.weather[0].icon,
                            temp = result.oneCall.current.temp
                        )
                        favouriteViewModel.addPlaceToFav(favouritePlace)
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