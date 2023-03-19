package eg.gov.iti.jets.kotlin.weather

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import eg.gov.iti.jets.kotlin.weather.databinding.ActivityOnboardingBinding
import eg.gov.iti.jets.kotlin.weather.databinding.ActivitySplashBinding

lateinit var sharedPreferences: SharedPreferences

class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = this.getSharedPreferences(NAME, Context.MODE_PRIVATE)

        binding.splash.playAnimation()

        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
        }, 3000)
    }
}