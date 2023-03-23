package eg.gov.iti.jets.kotlin.weather

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import java.util.*

lateinit var sharedPreferences: SharedPreferences


class MyApplication : Application() {


    override fun attachBaseContext(base: Context) {
        sharedPreferences = base.getSharedPreferences(NAME, Context.MODE_PRIVATE)

        super.attachBaseContext(updateBaseContextLocale(base))

    }

    private fun updateBaseContextLocale(context: Context): Context {
//        val language = "ar"
        val language = sharedPreferences.getString(LANGUAGE, "en")
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res: Resources = context.resources
        val configuration = Configuration(res.configuration)
        configuration.locale = locale
        res.updateConfiguration(configuration, res.displayMetrics)

        return context.createConfigurationContext(configuration)
    }
}
