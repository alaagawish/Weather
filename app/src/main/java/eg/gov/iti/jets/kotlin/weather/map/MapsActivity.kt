package eg.gov.iti.jets.kotlin.weather.map

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import eg.gov.iti.jets.kotlin.weather.*
import eg.gov.iti.jets.kotlin.weather.Constants.FLAG
import eg.gov.iti.jets.kotlin.weather.Constants.LAT
import eg.gov.iti.jets.kotlin.weather.Constants.LON
import eg.gov.iti.jets.kotlin.weather.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapsBinding
    lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
println("jjjjjjjjjdjdkkdkdkdkdkdkdk")
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapView = findViewById<MapView>(R.id.mapView)
        println("hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjmaps")
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { googleMap ->
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(10f))
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