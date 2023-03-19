package eg.gov.iti.jets.kotlin.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import eg.gov.iti.jets.kotlin.weather.databinding.ActivityMainBinding
import eg.gov.iti.jets.kotlin.weather.databinding.ActivityOnboardingBinding


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

    }
}