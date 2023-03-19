package eg.gov.iti.jets.kotlin.weather.db

import android.content.Context
import eg.gov.iti.jets.kotlin.weather.home.view.HomeFragment

class LocalSource(context: Context) : LocalSourceInterface {
    private val dayDao: DayDao by lazy {
        val dayDatabase: DayDatabase = DayDatabase.getInstance(context)
        dayDatabase.getDayDao()
    }

    init {

    }
}