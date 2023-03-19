package eg.gov.iti.jets.kotlin.weather

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import eg.gov.iti.jets.kotlin.weather.databinding.ActivityMainBinding
import eg.gov.iti.jets.kotlin.weather.databinding.ActivityOnboardingBinding

val LANGUAGE = "LANGUAGE"
val NAME = "WEATHER"
val UNIT = "UNIT"
val LOCATION = "LOCATION"

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.saveSettingMaterialButton.setOnClickListener {
            finish()
        }
        binding.animation.playAnimation()
        val editor = sharedPreferences.edit()

        editor.putString(LANGUAGE, "en")
        editor.putString(LOCATION, "gps")
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
        binding.locationIntroRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            when (radioButton.text) {
                "Map" -> editor.putString(LOCATION, "map")
                "GPS" -> editor.putString(LOCATION, "gps")
            }
            editor.apply()
        }


    }
}