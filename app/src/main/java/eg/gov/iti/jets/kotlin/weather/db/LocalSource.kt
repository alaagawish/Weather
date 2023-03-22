package eg.gov.iti.jets.kotlin.weather.db

import android.content.Context
import eg.gov.iti.jets.kotlin.weather.home.view.HomeFragment
import eg.gov.iti.jets.kotlin.weather.model.DailyDBModel
import eg.gov.iti.jets.kotlin.weather.model.DayDBModel
import eg.gov.iti.jets.kotlin.weather.model.HourlyDBModel
import kotlinx.coroutines.flow.Flow

class LocalSource(context: Context) : LocalSourceInterface {
    private val dayDatabase: DayDatabase = DayDatabase.getInstance(context)

    private val dayDao: DayDao by lazy {
        dayDatabase.getDayDao()
    }
    private val dailyDao: DailyDao by lazy {
        dayDatabase.getDailyDao()
    }
    private val hourDao: HourDao by lazy {
        dayDatabase.getHourDao()
    }
    private val dayFromDB: Flow<DayDBModel> = dayDao.getDay
    private val dailyFromDB: Flow<List<DailyDBModel>> = dailyDao.getNextDays
    private val hourFromDB: Flow<List<HourlyDBModel>> = hourDao.getDayHours
    override suspend fun addDay(day: DayDBModel) {
        dayDao.addDay(day)
    }

    override suspend fun deleteAll() {
        dayDao.deleteAll()
    }

    override val getDay: Flow<DayDBModel>
        get() = dayFromDB

    override suspend fun addDayHours(hour: HourlyDBModel) {
        hourDao.addDayHours(hour)
    }

    override suspend fun deleteAllHours() {
        hourDao.deleteAllHours()
    }

    override val getDayHours: Flow<List<HourlyDBModel>>
        get() = hourFromDB

    override suspend fun addComingDay(day: DailyDBModel) {
        dailyDao.addComingDay(day)
    }

    override suspend fun deleteAllComingDays() {
        dailyDao.deleteAllComingDays()
    }

    override val getNextDays: Flow<List<DailyDBModel>>
        get() = dailyFromDB

}