package eg.gov.iti.jets.kotlin.weather.map

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import eg.gov.iti.jets.kotlin.weather.R
import eg.gov.iti.jets.kotlin.weather.databinding.ActivityMapsBinding
import eg.gov.iti.jets.kotlin.weather.favourite.view.FLAG
import eg.gov.iti.jets.kotlin.weather.sharedPreferences

const val LAT = "LAT"
const val LON = "LON"

class MapsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapsBinding
    lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapView = findViewById<MapView>(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { googleMap ->
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(15f))
            googleMap.setOnMapClickListener { latLng ->
                Log.d("MainActivity", "Picked location: $latLng")
                editor = sharedPreferences.edit()
                editor.putString(LAT, latLng.latitude.toString())
                editor.putString(LON, latLng.longitude.toString())
                editor.putBoolean(FLAG, false)
                editor.apply()


                finish()
            }
        }
    }

}