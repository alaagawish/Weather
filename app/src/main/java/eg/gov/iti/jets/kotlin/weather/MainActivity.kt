package eg.gov.iti.jets.kotlin.weather

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import eg.gov.iti.jets.kotlin.weather.Constants.LANGUAGE
import eg.gov.iti.jets.kotlin.weather.Constants.NAME
//import eg.gov.iti.jets.kotlin.weather.alert.createNotificationChannel
import eg.gov.iti.jets.kotlin.weather.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val locale = sharedPreferences.getString(LANGUAGE, "en")?.let { Locale(it) }
        if (locale != null) {
            Locale.setDefault(locale)
        }

        val res: Resources = this.resources
        val configuration = Configuration(res.configuration)
        configuration.locale = locale
        res.updateConfiguration(configuration, res.displayMetrics)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val sharedPreferences = this.getSharedPreferences(NAME, Context.MODE_PRIVATE)

        val isOnboardingShown = sharedPreferences.getBoolean("isOnboardingShown", false)
//        createNotificationChannel(this@MainActivity)
        if (!isOnboardingShown) {
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
            with(sharedPreferences.edit()) {
                putBoolean("isOnboardingShown", true)
                apply()
            }
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.favouriteFragment, R.id.alertFragment, R.id.settingsFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}