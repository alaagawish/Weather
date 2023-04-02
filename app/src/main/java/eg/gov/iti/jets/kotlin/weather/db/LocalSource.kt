package eg.gov.iti.jets.kotlin.weather.db

import eg.gov.iti.jets.kotlin.weather.model.*
import kotlinx.coroutines.flow.Flow

class LocalSource internal constructor(
    private val favDao: FavDao,
    private val dayDao: DayDao,
    private val alertDao: AlertDao,
    private val hourDao: HourDao,
    private val dailyDao: DailyDao
) : LocalSourceInterface {

    private val dayFromDB: Flow<DayDBModel> = dayDao.getDay
    private val dailyFromDB: Flow<List<DailyDBModel>> = dailyDao.getNextDays
    private val hourFromDB: Flow<List<HourlyDBModel>> = hourDao.getDayHours
    private val favFromDB: Flow<List<FavouritePlace>> = favDao.getAllFavPlaces
    private val alertsFromDB: Flow<List<AlertsDB>> = alertDao.getAllAlerts
    override suspend fun addDay(day: DayDBModel) {
        println("local source: $day")
        dayDao.addDay(day)
    }

    override suspend fun deleteAll() {
        dayDao.deleteAll()
    }

    override val getDay: Flow<DayDBModel> = dayFromDB

    override suspend fun addDayHours(hour: HourlyDBModel) {
        hourDao.addDayHours(hour)
    }

    override suspend fun deleteAllHours() {
        hourDao.deleteAllHours()
    }

    override val getDayHours: Flow<List<HourlyDBModel>> = hourFromDB

    override suspend fun addComingDay(day: DailyDBModel) {
        dailyDao.addComingDay(day)
    }

    override suspend fun deleteAllComingDays() {
        dailyDao.deleteAllComingDays()
    }

    override val getNextDays: Flow<List<DailyDBModel>> = dailyFromDB


    override val getAllFavPlaces: Flow<List<FavouritePlace>> = favFromDB

    override suspend fun addPlaceToFav(favouritePlace: FavouritePlace) {
        favDao.addFavPlace(favouritePlace)
    }

    override suspend fun deletePlaceFromFav(favouritePlace: FavouritePlace) {
        favDao.deletePlaceFromFav(favouritePlace)
    }

    override val getAllAlerts: Flow<List<AlertsDB>> = alertsFromDB

    override suspend fun addAlert(alertsDB: AlertsDB) {
        alertDao.addAlert(alertsDB)
    }

    override suspend fun deleteAlert(alert: AlertsDB) {
        alertDao.deleteAlert(alert)
    }

}