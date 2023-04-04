package eg.gov.iti.jets.kotlin.weather

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import eg.gov.iti.jets.kotlin.weather.databinding.ActivitySplashBinding
import eg.gov.iti.jets.kotlin.weather.onboarding.OnboardingActivity
import eg.gov.iti.jets.kotlin.weather.utils.Constants.BOARDING


class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.splash.playAnimation()

        Handler().postDelayed({

            val isShown = sharedPreferences!!.getBoolean(BOARDING, false)
            if (!isShown) {
                val intent = Intent(this, OnboardingActivity::class.java)
                startActivity(intent)
                with(sharedPreferences!!.edit()) {
                    putBoolean(BOARDING, true)
                    apply()
                }
            } else {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
            }
            finish()


        }, 3000)
    }
}