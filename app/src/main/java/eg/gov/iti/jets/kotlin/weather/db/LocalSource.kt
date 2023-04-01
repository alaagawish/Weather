package eg.gov.iti.jets.kotlin.weather.db

import android.content.Context
import eg.gov.iti.jets.kotlin.weather.model.*
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
    private val favDao: FavDao by lazy {
        dayDatabase.getFavDao()
    }
    private val alertDao: AlertDao by lazy {
        dayDatabase.getAlertsDao()
    }
    private val dayFromDB: Flow<DayDBModel> = dayDao.getDay
    private val dailyFromDB: Flow<List<DailyDBModel>> = dailyDao.getNextDays
    private val hourFromDB: Flow<List<HourlyDBModel>> = hourDao.getDayHours
    private val favFromDB: Flow<List<FavouritePlace>> = favDao.getAllFavPlaces
    private val alertsFromDB: Flow<List<AlertsDB>> = alertDao.getAllAlerts
    override suspend fun addDay(day: DayDBModel) {
        println("localsource: $day")
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


    override val getAllFavPlaces: Flow<List<FavouritePlace>>
        get() = favFromDB

    override suspend fun addPlaceToFav(favouritePlace: FavouritePlace) {
        favDao.addFavPlace(favouritePlace)
    }

    override suspend fun deletePlaceFromFav(favouritePlace: FavouritePlace) {
        favDao.deletePlaceFromFav(favouritePlace)
    }

    override val getAllAlerts: Flow<List<AlertsDB>>
        get() = alertsFromDB

    override suspend fun addAlert(alertsDB: AlertsDB) {
        alertDao.addAlert(alertsDB)
    }

    override suspend fun deleteAlert(alert: AlertsDB) {
        alertDao.deleteAlert(alert)
    }

}