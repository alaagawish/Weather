package eg.gov.iti.jets.kotlin.weather.model

import eg.gov.iti.jets.kotlin.weather.db.LocalSourceInterface
import eg.gov.iti.jets.kotlin.weather.network.RemoteSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class Repository private constructor(
    var remoteSource: RemoteSource,
    var localSourceInterface: LocalSourceInterface,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RepositoryInterface {

    companion object {
        private var INSTANCE: Repository? = null
        fun getInstance(
            remoteSource: RemoteSource,
            localSourceInterface: LocalSourceInterface,
            ioDispatcher: CoroutineDispatcher = Dispatchers.IO
        ): Repository {
            return INSTANCE ?: synchronized(this) {
                val i = Repository(remoteSource, localSourceInterface)
                INSTANCE = i
                i
            }
        }
    }

    override suspend fun getOneCallRemote(
        lat: Double, lon: Double, unit: String, lang: String
    ) = flowOf(remoteSource.getOneCallByNetwork(lat, lon, unit, lang))

    override suspend fun addDay(day: DayDBModel) {
        localSourceInterface.deleteAll()
        localSourceInterface.addDay(day)
    }

    override suspend fun deleteAll() {
        localSourceInterface.deleteAll()
    }

    override val getDay = localSourceInterface.getDay

    override suspend fun addDayHours(hour: HourlyDBModel) {
        localSourceInterface.addDayHours(hour)
    }

    override suspend fun deleteAllHours() {
        localSourceInterface.deleteAllHours()
    }

    override val getDayHours = localSourceInterface.getDayHours

    override suspend fun addComingDay(day: DailyDBModel) {
        localSourceInterface.addComingDay(day)
    }

    override suspend fun deleteAllComingDays() {
        localSourceInterface.deleteAllComingDays()
    }

    override val getNextDays = localSourceInterface.getNextDays
    override suspend fun addPlaceToFav(favouritePlace: FavouritePlace) {
        localSourceInterface.addPlaceToFav(favouritePlace)
    }

    override suspend fun deleteFavPlace(favouritePlace: FavouritePlace) {
        localSourceInterface.deletePlaceFromFav(favouritePlace)
    }

    override val getAllFav: Flow<List<FavouritePlace>> = localSourceInterface.getAllFavPlaces

    override suspend fun addAlert(alertsDB: AlertsDB) {
        localSourceInterface.addAlert(alertsDB)
    }

    override suspend fun deleteAlert(alertsDB: AlertsDB) {
        localSourceInterface.deleteAlert(alertsDB)
    }

    override val getAllAlerts: Flow<List<AlertsDB>> = localSourceInterface.getAllAlerts


}