package eg.gov.iti.jets.kotlin.weather.db

import eg.gov.iti.jets.kotlin.weather.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalSource(
    private val alerts: MutableList<AlertsDB> = mutableListOf(),
    private var day: MutableList<DayDBModel> = mutableListOf(),
    private val days: MutableList<DailyDBModel> = mutableListOf(),
    private val favs: MutableList<FavouritePlace> = mutableListOf(),
    private val hours: MutableList<HourlyDBModel> = mutableListOf()
) : LocalSourceInterface {
    override suspend fun addDay(day: DayDBModel) {
        deleteAll()
        this.day.add(day)
    }

    override suspend fun deleteAll() {
        day.clear()
    }

    override val getDay: Flow<DayDBModel> = flowOf()


    override suspend fun addDayHours(hour: HourlyDBModel) {

        hours.add(hour)
    }

    override suspend fun deleteAllHours() {
        hours.clear()
    }

    override val getDayHours: Flow<List<HourlyDBModel>>
        get() = flowOf(hours)

    override suspend fun addComingDay(day: DailyDBModel) {

        days.add(day)
    }

    override suspend fun deleteAllComingDays() {
        days.clear()
    }

    override val getNextDays: Flow<List<DailyDBModel>>
        get() = flowOf(days)
    override val getAllFavPlaces: Flow<List<FavouritePlace>>
        get() = flowOf(favs)

    override suspend fun addPlaceToFav(favouritePlace: FavouritePlace) {
        favs.add(favouritePlace)
    }

    override suspend fun deletePlaceFromFav(favouritePlace: FavouritePlace) {
        favs.remove(favouritePlace)
    }

    override val getAllAlerts: Flow<List<AlertsDB>>
        get() = flowOf(alerts)

    override suspend fun addAlert(alertsDB: AlertsDB) {
        alerts.add(alertsDB)
    }

    override suspend fun deleteAlert(alert: AlertsDB) {
        alerts.remove(alert)
    }

    fun clear() {
        alerts.clear()
        favs.clear()
        day.clear()
        days.clear()
        hours.clear()
    }
}