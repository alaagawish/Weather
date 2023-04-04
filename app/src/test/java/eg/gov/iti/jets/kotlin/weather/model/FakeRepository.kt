package eg.gov.iti.jets.kotlin.weather.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeRepository(
    private val dayy: MutableList<DayDBModel> = mutableListOf(),
    private val fav: MutableList<FavouritePlace> = mutableListOf(),
    private val alerts: MutableList<AlertsDB> = mutableListOf(),
    private val hours: MutableList<HourlyDBModel> = mutableListOf(),
    private val days: MutableList<DailyDBModel> = mutableListOf()
) : RepositoryInterface {
    override suspend fun getOneCallRemote(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): Flow<OneCall> {
        return flowOf(
            OneCall(
                lat, lon, "",
                Current(
                    11,
                    22,
                    33,
                    3.2,
                    1.2,
                    1,
                    2,
                    2.3,
                    1,
                    22,
                    1.2,
                    mutableListOf(WeatherObject("", "", ""))
                ),
                mutableListOf(),
                mutableListOf(),
                mutableListOf()
            )
        )

    }

    override suspend fun addDay(day: DayDBModel) {
        dayy.clear()
        dayy.add(day)
    }

    override suspend fun deleteAll() {
        dayy.clear()
    }

    override val getDay: Flow<DayDBModel>
        get() = if (dayy.size > 0) flowOf(dayy[0]) else flowOf()

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

    override suspend fun addPlaceToFav(favouritePlace: FavouritePlace) {
        fav.add(favouritePlace)
    }

    override suspend fun deleteFavPlace(favouritePlace: FavouritePlace) {
        fav.remove(favouritePlace)
    }

    override val getAllFav: Flow<List<FavouritePlace>>
        get() = flowOf(fav)

    override suspend fun addAlert(alertsDB: AlertsDB) {
        alerts.add(alertsDB)
    }

    override suspend fun deleteAlert(alertsDB: AlertsDB) {
        alerts.remove(alertsDB)
    }

    override val getAllAlerts: Flow<List<AlertsDB>>
        get() = flowOf(alerts)

    fun close() {
        alerts.clear()
        fav.clear()
        dayy.clear()
        days.clear()
        hours.clear()
    }
}