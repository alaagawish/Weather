package eg.gov.iti.jets.kotlin.weather.map

import android.annotation.SuppressLint
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
import eg.gov.iti.jets.kotlin.weather.Constants.LATITUDE
import eg.gov.iti.jets.kotlin.weather.Constants.LONGITUDE
import eg.gov.iti.jets.kotlin.weather.Constants.SOURCE
import eg.gov.iti.jets.kotlin.weather.Constants.TAG
import eg.gov.iti.jets.kotlin.weather.R
import eg.gov.iti.jets.kotlin.weather.databinding.ActivityMapsBinding
import eg.gov.iti.jets.kotlin.weather.db.LocalSource
import eg.gov.iti.jets.kotlin.weather.favourite.viewmodel.FavouriteViewModel
import eg.gov.iti.jets.kotlin.weather.favourite.viewmodel.FavouriteViewModelFactory
import eg.gov.iti.jets.kotlin.weather.home.viewmodel.HomeViewModel
import eg.gov.iti.jets.kotlin.weather.home.viewmodel.HomeViewModelFactory
import eg.gov.iti.jets.kotlin.weather.model.FavouritePlace
import eg.gov.iti.jets.kotlin.weather.model.Repository
import eg.gov.iti.jets.kotlin.weather.network.APIState
import eg.gov.iti.jets.kotlin.weather.network.DayClient
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
                DayClient.getInstance(), LocalSource(this)
            )
        )
        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]
        favouriteViewModelFactory = FavouriteViewModelFactory(
            Repository.getInstance(
                DayClient.getInstance(), LocalSource(this)
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
        }
        mapFragment.getMapAsync { googleMap ->
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(10f))
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.setOnMapClickListener { latLng ->
                googleMap.addMarker(MarkerOptions().position(latLng).title("Chosen place"))
                Log.d(TAG, "Picked location: $latLng")
                this.latLng = latLng
            }
        }
        binding.addToFavButton.setOnClickListener {
            if (intent.getStringExtra(SOURCE) == "fav") {
                addPlaceToFav(latLng.latitude, latLng.longitude)
            } else if (intent.getStringExtra(SOURCE) == "mapSettings") {
                editor.putString(LATITUDE, latLng.latitude.toString())
                editor.putString(LONGITUDE, latLng.longitude.toString())
                editor.apply()
                finish()

            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val sydney = LatLng(
            sharedPreferences.getString(LATITUDE, "1.0")?.toDouble()!!,
            sharedPreferences.getString(LONGITUDE, "1.0")?.toDouble()!!
        )
        latLng = sydney
        mMap.addMarker(MarkerOptions().position(sydney).title("Current Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun addPlaceToFav(lat: Double, lon: Double) {
        homeViewModel.getForecastData(lat, lon)
        lifecycleScope.launch {
            homeViewModel.forecastStateFlow.collectLatest { result ->
                when (result) {
                    is APIState.Waiting -> {
                        Timber.tag(TAG).d("onCreateView: waiting")
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
                        favouriteViewModel.addPlaceToFav(favouritePlace)
                        favouriteViewModel.getAllFavPlaces()
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