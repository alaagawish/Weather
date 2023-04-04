package eg.gov.iti.jets.kotlin.weather.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import eg.gov.iti.jets.kotlin.weather.MainActivity
import eg.gov.iti.jets.kotlin.weather.utils.Constants.LANGUAGE
import eg.gov.iti.jets.kotlin.weather.utils.Constants.UNIT
import eg.gov.iti.jets.kotlin.weather.databinding.ActivityOnboardingBinding
import eg.gov.iti.jets.kotlin.weather.editor
import eg.gov.iti.jets.kotlin.weather.map.MapsActivity
import eg.gov.iti.jets.kotlin.weather.sharedPreferences
import eg.gov.iti.jets.kotlin.weather.utils.Constants
import eg.gov.iti.jets.kotlin.weather.utils.Constants.BOARDING
import eg.gov.iti.jets.kotlin.weather.utils.Constants.SOURCE


class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.animation.playAnimation()


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

            if (sharedPreferences!!.getString(Constants.LOCATION, "gps") == "gps") {
                val intent = Intent(this@OnboardingActivity, MainActivity::class.java)
                startActivity(intent)


            } else if (sharedPreferences!!.getString(Constants.LOCATION, "gps") == "map") {
                val intent = Intent(this@OnboardingActivity, MapsActivity::class.java)
                intent.putExtra(SOURCE, BOARDING)
                startActivity(intent)
            }

        }

    }
}